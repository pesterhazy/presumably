(ns site.core
  (:require [hiccup.page :as hp]
            [clj-time.core :as tc]
            [clj-time.coerce :as to]
            [clj-time.format :as tf]
            [site.layout :as layout]))

(defn fmt-date [date]
  (tf/unparse (tf/formatter "MMM dd, YYYY") (to/from-date date)))

(defn page-content [{{:keys [main-title subtitle content draft
                             date-published]} :entry
                     :as data}]
  (when (and (not draft) (not date-published))
    (println "WARNING: non-draft entry is lacking :date-published key"))
  [:div
   (when main-title
     [:h1 main-title])
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
    (doall (map (fn [{:keys [draft title permalink date-published] :as entry}]
                  [:li
                   [:a {:href permalink} title]
                   " "
                   (if draft
                     [:span "(draft)"]
                     [:span "(" (fmt-date date-published) ")"])])
                entries))]])

(defn index [opts data]
  (layout/layout opts
                 {:body (index-content data)}))

(defn index-dev [data]
  (index {:development? true} data))

(defn index-prod [data]
  (index {:development? false} data))
