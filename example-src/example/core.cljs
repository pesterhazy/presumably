(ns example.core
  (:require [clojure.pprint]
            [reagent.core :as r]
            [weasel.repl :as repl]
            [example.reagent]
            [example.props]
            [example.refs]))

(enable-console-print!)

(defn ^:export repl []
  (when-not (repl/alive?)
    (repl/connect "ws://localhost:9753" :verbose true)))

(defn init []
  (r/render-component [example.props/root]
                      (.getElementById js/document "container")))

(defn on-jsload []
  (init))
