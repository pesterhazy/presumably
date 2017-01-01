(ns example.refs
  (:require [clojure.pprint]
            [reagent.core :as r]))

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

(defn video-ui []
  (let [!video (atom nil)] ;; clojure.core/atom
    (fn [{:keys [src]}]
      [:div
       [:div
        [:video {:src src
                 :style {:width 400}
                 :ref (fn [el]
                        (reset! !video el))}]]
       [:div
        [:button {:on-click (fn []
                              (when-let [video @!video]
                                (if (.-paused video)
                                  (.play video)
                                  (.pause video))))}
         "Toogle"]]])))

(defn root []
  (let [!visible (r/atom true)]
    (fn []
      [:div
       (when @!visible
         [:div
          [video-ui {:src "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"}]])
       #_[:div
          [:button {:on-click #(swap! !visible not)}
           "Collapse/expand"]]])))
