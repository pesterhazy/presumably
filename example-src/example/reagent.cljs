(ns example.reagent
  (:require [clojure.pprint]
            [reagent.core :as r]))

(enable-console-print!)

(comment
  [:table
   [:thead
    [:tr
     [:th "name"]] [:th "country"] [:th "date"]]
   [:tbody
    [:tr
     [:td "Descartes"] [:td "France"] [:td "1596"]]
    [:tr
     ;; ...
     ]]])

(defn row-ui [cols m]
  [:tr {:key (:name m)} (map (fn [col] [:td {:key col} (get m col)]) cols)])

(defn table-ui [cols rel]
  [:table
   [:thead
    [:tr (map (fn [col] [:th {:key col} (name col)]) cols)]]
   [:tbody
    (map (partial row-ui cols) rel)]])

(def philosopher-cols
  [:name :country :date])

(def philosophers
  [{:name "Descartes"
    :country "France"
    :date 1596}
   {:name "Kant"
    :country "Prussia"
    :date 1724}
   {:name "Quine"
    :country "U.S.A."
    :date 1908}])

(defn root []
  [:div
   [:h1 "philosophers"]
   [table-ui philosopher-cols philosophers]])

(defn gadget2 []
  (clojure.pprint/print-table philosopher-cols philosophers))

(defn gadget []
  (clojure.pprint/pprint (table-ui philosopher-cols
                                   philosophers)))
