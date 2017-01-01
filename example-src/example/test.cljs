(ns example.test
  (:require [reagent.core :as r]))

(defn test-ui []
  (r/create-class
   {:get-initial-state
    (fn []
      #js {"foo" "bar"})
    :component-did-mount
    #(println "component-did-mount")

    :component-will-mount
    #(println "component-will-mount")

    :component-will-unmount
    #(println "component-will-unmount")

    :display-name  "my-component"

    :reagent-render
    (fn []
      [:div "state: " (pr-str (.-state (r/current-component)))])}))

(def test-ui* (r/reactify-component test-ui))
