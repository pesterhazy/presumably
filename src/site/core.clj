(ns site.core
  (:require [hiccup.page :as hp]))


(defn page [data]
  (hp/html5
   [:head
    (hp/include-css "/css/style.css")
    (hp/include-css "/vendor/basscss@8.0.1.min.css")
    (hp/include-css "/vendor/highlight.css")

    (hp/include-js "/vendor/highlight.js")]
   [:body
    [:div.max-width-3.mx-auto
     [:div.clearfix
      (-> data :entry :content)]]

    [:script "hljs.initHighlightingOnLoad();"]]))
