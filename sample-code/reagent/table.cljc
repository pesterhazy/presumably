(ns example.table
  (:require [clojure.pprint]))

(comment
  [:table
   [:thead
    [:th "name"] [:th "country"] [:th "date"]]
   [:tbody
    [:tr
     [:td "Descartes"] [:td "France"] [:td "1596"]]
    [:td
     ;; ...
     ]]])

(defn row-ui [cols m]
  [:tr (map (fn [col] [:td (get m col)]) cols)])

(defn table-ui [cols rel]
  [:table
   [:thead
    (map (fn [col] [:th (name col)]) cols)]
   [:tbody
    (map (partial row-ui cols) rel)]])

(def philosopher-cols
  [:name :country :date])

(def philosophers
  [{:name "Descartes"
    :country "France"
    :date 1596}
   {:name "Quine"
    :country "U.S.A."
    :date 1908}])

(defn gadget2 []
  (clojure.pprint/print-table philosopher-cols philosophers))

(defn gadget []
  (clojure.pprint/pprint (table-ui philosopher-cols
                                   philosophers)))
