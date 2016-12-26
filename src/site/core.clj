(ns site.core
  (:require [hiccup.page :as hp]))

(def default-title "Presumably for side-effects")

(defn page [{:keys [development?]}
            {{:keys [title subtitle content draft published]} :entry
             :as data}]
  (when (and (not draft) (not published))
    (println "WARNING: non-draft entry is lacking published key"))
  (hp/html5
   [:head
    [:title (or title default-title)]
    (hp/include-css "/css/style.css")
    (hp/include-css "/vendor/basscss@8.0.1.min.css")
    (hp/include-css "/vendor/highlight.css")
    (hp/include-css "https://fonts.googleapis.com/css?family=Josefin+Sans")

    (hp/include-js "/vendor/highlight.js")
    (when development?
      (hp/include-js "/js/app.js"))
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
      [:div.header "presumably for side effects" [:br] "a blog about clojure &c."]
      (when title
        [:h1 title])
      (when subtitle
        [:h2 subtitle])
      (when published
        [:div.date "published " published])
      [:div content]
      [:hr.rule]
      [:p.mt2 "This is " [:i "presumably for side-effects"]
       ", a blog by Paulus Esterhazy. "
       "Don't forget to say hello " [:a {:href "https://twitter.com/pesterhazy"} "on twitter"] " or " [:a {:href "mailto:pesterhazy@gmail.com"} "by email"]]]]]))


(defn page-dev [m]
  (page {:development? true} m))

(defn page-prod [m]
  (page {:development? false} m))
