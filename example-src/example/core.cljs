(ns example.core
  (:require [clojure.pprint]
            [reagent.core :as r]
            [weasel.repl :as repl]
            [example.reagent]))

(enable-console-print!)

(defn ^:export repl []
  (when-not (repl/alive?)
    (repl/connect "ws://localhost:9753" :verbose true)))

(defn init []
  (r/render-component [example.reagent/root]
                      (.getElementById js/document "container")))

(defn on-jsload []
  (init))