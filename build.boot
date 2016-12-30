(set-env!
 :source-paths #{"src" "posts" "example-src"}
 :resource-paths #{"resources"}
 :dependencies '[[perun "0.3.0" :scope "test"]
                 [org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/tools.nrepl "0.2.12"] ;; why do we need this?
                 [org.clojure/clojurescript "1.9.293"]
                 [pandeiro/boot-http "0.7.3"]
                 [adzerk/boot-cljs "1.7.228-2"]
                 [adzerk/boot-reload "0.4.13"]
                 [samestep/boot-refresh "0.1.0" :scope "test"]
                 [weasel "0.7.0"]
                 [confetti/confetti "0.1.5-alpha"]
                 [fipp "0.6.7" :scope "provided"]
                 [reagent "0.6.0"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer [markdown render draft
                            collection print-meta]]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [sync-bucket]])
(require '[adzerk.boot-reload :refer [reload]])
(require '[adzerk.boot-cljs :refer [cljs]])
(require '[samestep.boot-refresh :refer [refresh]])

(deftask build
  [i include-drafts bool "Include drafts?"
   d development? bool "Dev mode?"]
  (comp (markdown)
        (if include-drafts identity (draft))
        (render :renderer (if development? 'site.core/page-dev 'site.core/page-prod))
        (collection :renderer (if development? 'site.core/index-dev 'site.core/index-prod)
                    :page "index.html")))

(deftask publish-local
  "Publish to target/"
  []
  (comp (build)
        (target)))

(deftask publish
  "Publish to S3"
  []
  (comp (build)
        (sift :include #{#"^public/"})
        (sift :move {#"^public/" ""})
        (sync-bucket :bucket "presumably-de-sitebucket-i2nzci1gpkw3"
                     :prune true ;; careful when setting this to true
                     :cloudfront-id (or (System/getenv "CLOUDFRONT_ID") (throw (ex-info "Set CLOUDFRONT_ID env var" {})))
                     :access-key (System/getenv "AWS_ACCESS_KEY_ID")
                     :secret-key (System/getenv "AWS_SECRET_KEY"))))

(deftask dev
  []
  (task-options! build {:include-drafts true
                        :development? true})
  (comp
   (repl :server true)
   (watch)
   (notify :audible true)
   (reload :asset-path "public" :on-jsload 'example.core/on-jsload)
   (refresh)
   (build)
   (cljs)
   (serve :resource-root "public")))

(deftask weasel
  []
  (with-pass-thru _
    (require 'cljs.repl)
    (require 'weasel.repl.websocket)
    ((resolve 'cljs.repl/repl) ((resolve 'weasel.repl.websocket/repl-env) :ip "0.0.0.0" :port 9753))))
