(set-env!
 :source-paths #{"src" "posts"}
 :resource-paths #{"resources"}
 :dependencies '[[perun "0.3.0" :scope "test"]
                 [org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [pandeiro/boot-http "0.7.6"]
                 [confetti/confetti "0.1.4"]
                 [fipp "0.6.7" :scope "provided"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]]
         '[confetti.boot-confetti :refer [sync-bucket]])

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
  (comp (serve :resource-root "public")
        (repl :server true)
        (watch)
        (build)))
