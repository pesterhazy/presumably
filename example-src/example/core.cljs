(ns example.core
  (:require [clojure.pprint]
            [reagent.core :as r]
            [clojure.browser.repl :as repl]
            [example.reagent]))

(enable-console-print!)

(defn init []
  (r/render-component [example.reagent/root]
                      (.getElementById js/document "container")))

(defn on-jsload []
  (init))

;; (defonce conn2
;;   (do
;;     (js/console.log "Conencting to REPL")
;;     (repl/connect "http://localhost:9595/repl")))
