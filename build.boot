(set-env!
 :source-paths #{"src" "posts"}
 :dependencies '[[perun "0.3.0" :scope "test"]
                 [org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]])

(require '[io.perun :refer :all])

(deftask build
  []
  (comp (markdown)
        (render :renderer 'site.core/page)
        (target)))
