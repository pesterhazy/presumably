(ns site.core
  (:require [hiccup.page :as hp]
            [clj-time.core :as tc]
            [clj-time.coerce :as to]
            [clj-time.format :as tf]
            [site.layout :as layout]))

(defn fmt-date [date]
  (tf/unparse (tf/formatter "MMM dd, YYYY") (to/from-date date)))

(defn page-content [{{:keys [title subtitle content draft
                             date-published]} :entry
                     :as data}]
  (when (and (not draft) (not date-published))
    (println "WARNING: non-draft entry is lacking :date-published key"))
  [:div
   (when title
     [:h1 title])
   (when subtitle
     [:h2 subtitle])
   (when date-published
     [:div.date "published " (fmt-date date-published)])
   [:div content]])

(defn page [opts {{:keys [title]} :entry :as data}]
  (layout/layout opts
                 {:title title :body (page-content data)}))

(defn page-dev [m]
  (page {:development? true} m))

(defn page-prod [m]
  (page {:development? false} m))

(defn index-content [{:keys [entries] :as data}]
  [:div
   [:h2 "Contents"]
   [:ul
    (doall (map (fn [{:keys [title permalink date-published] :as entry}]
                  [:li [:a {:href permalink} title] " " [:span "(" (fmt-date date-published) ")"]])
                entries))
    [:li [:a {:href "/reagent-2.html"} "Reagent Mysteries (2): Reloading"] " " [:span "(Dec 30, 2016)"]]
    [:li [:a {:href "/reagent.html"} "Reagent Mysteries (1): Vectors and Sequences"] " " [:span "(Dec 26, 2016)"]]
    [:li [:a {:href "/boot-react-native.html"} "Getting Started with Boot React Native"] " " [:span "(Aug 2, 2016)"]]]])

(defn index [opts data]
  (layout/layout opts
                 {:body (index-content data)}))

(defn index-dev [data]
  (index {:development? true} data))

(defn index-prod [data]
  (index {:development? false} data))
