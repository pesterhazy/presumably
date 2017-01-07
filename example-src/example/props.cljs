(ns example.props
  (:require [clojure.pprint]
            [reagent.core :as r]))

(extend-protocol IPrintWithWriter js/Symbol (-pr-writer [obj writer opts] (write-all writer "#object[Symbol \"" (.toString obj) "\"]")))

(defn props-ui [m]
  [:div
   [:h3 "argv"]
   [:pre (pr-str (r/argv (r/current-component)))]
   [:h3 "props"]
   [:pre (pr-str (r/props (r/current-component)))]
   [:h3 "passed"]
   [:pre (pr-str m)]])

(defn title-ul-ui [{:keys [title]}]
  [:div
   [:section
    [:h3 title]
    (into [:ul] (r/children (r/current-component)))]])

(def title-ul-ui* (r/reactify-component title-ul-ui))

(defn root []
  [:div
   [:> (r/reactify-component title-ul-ui) {:title "people"}
    [:li "Smith"]
    [:li "Hinz"]]])
