(ns site.core
  (:require [hiccup.page :as hp]))


(defn page [{{:keys [name content]} :entry
             :as data}]
  (hp/html5
   [:head
    [:title (or name "Presumably for side-effects")]
    (hp/include-css "/css/style.css")
    (hp/include-css "/vendor/basscss@8.0.1.min.css")
    (hp/include-css "/vendor/highlight.css")

    (hp/include-js "/vendor/highlight.js")]
   [:body
    [:div.max-width-3.mx-auto
     [:div.clearfix content]]

    [:script "hljs.initHighlightingOnLoad();"]]))
