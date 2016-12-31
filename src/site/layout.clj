(ns site.layout
  (:require [hiccup.page :as hp]
            [site.common :as common]))

(defn layout [{:keys [development?]} {:keys [title body]}]
  (hp/html5
   [:head
    [:title (or title common/default-title)]
    (hp/include-css "/css/style.css")
    (hp/include-css "/vendor/basscss@8.0.1.min.css")
    (hp/include-css "/vendor/highlight.css")
    (hp/include-css "https://fonts.googleapis.com/css?family=Josefin+Sans")

    (hp/include-js "/vendor/highlight.js")
    (when development?
      (hp/include-js "/js/app.js"))
    [:link {:rel "alternate"
            :type "application/atom+xml"
            :href "/atom.xml?type=news"}]
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
      [:div.header [:a {:href "/"} "presumably for side-effects"]
       [:br]
       "a blog about clojure &c."]
      [:div body]
      [:hr.rule]
      [:p.mt2 "This is " [:i "presumably for side-effects"]
       ", a blog by Paulus Esterhazy. "
       "Don't forget to say hello " [:a {:href "https://twitter.com/pesterhazy"} "on twitter"] " or " [:a {:href "mailto:pesterhazy@gmail.com"} "by email"]]]]])
  )
