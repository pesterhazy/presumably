(ns site.core
  (:require [hiccup.page :as hp]))

(def default-title "Presumably for side-effects")

(defn page [{{:keys [title subtitle content]} :entry
             :as data}]
  (hp/html5
   [:head
    [:title (or title default-title)]
    (hp/include-css "/css/style.css")
    (hp/include-css "/vendor/basscss@8.0.1.min.css")
    (hp/include-css "/vendor/highlight.css")

    (hp/include-js "/vendor/highlight.js")]
   [:body
    [:div.max-width-3.mx-auto
     [:div.clearfix
      (when title
        [:h1 title])
      (when subtitle
        [:h2 subtitle])
      [:div content]]]

    [:script "hljs.initHighlightingOnLoad();"]]))
