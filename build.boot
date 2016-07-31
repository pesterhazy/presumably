(set-env!
 :source-paths #{"src" "posts"}
 :resource-paths #{"resources"}
 :dependencies '[[perun "0.3.0" :scope "test"]
                 [org.clojure/clojure "1.8.0"]
                 [pandeiro/boot-http "0.7.0"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer :all]
         '[pandeiro.boot-http :refer [serve]])

(deftask dev
  []
  (comp (serve :resource-root "public")
        (watch)
        (markdown)
        (render :renderer 'site.core/page)))
