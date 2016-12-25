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
                 [confetti/confetti "0.1.4"]
                 [fipp "0.6.7" :scope "provided"]
                 [reagent "0.6.0"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [sync-bucket]])
(require '[adzerk.boot-reload :refer [reload]])
(require '[adzerk.boot-cljs :refer [cljs]])

(deftask build
  [i include-drafts bool "Include drafts?"]
  (comp (markdown)
        (if include-drafts identity (draft))
        (render :renderer 'site.core/page)))

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
  (task-options! build {:include-drafts true})
  (comp
   (repl :server true)
   (watch)
   (reload :asset-path "public" :on-jsload 'example.core/on-jsload)
   (build)
   (cljs)
   (serve :resource-root "public")))
