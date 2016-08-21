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
    (hp/include-css "https://fonts.googleapis.com/css?family=Josefin+Sans")

    (hp/include-js "/vendor/highlight.js")
    [:script "hljs.initHighlightingOnLoad();"]
    [:script "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-81846608-1', 'auto');
  ga('send', 'pageview');"]]
   [:body
    [:div.content.mx-auto
     [:div.clearfix
      [:div.header "presumably for side effects" [:br] "a blog about clojure"]
      (when title
        [:h1 title])
      (when subtitle
        [:h2 subtitle])
      [:div content]
      [:hr]
      [:p.mt2 "This is " [:i "presumably for side-effects"] ", a blog by Paulus Esterhazy about Clojure and more."]
      [:p "Don't forget to say hello on twitter: " [:a {:href "https://twitter.com/pesterhazy"} "@pesterhazy"]]]]]))
