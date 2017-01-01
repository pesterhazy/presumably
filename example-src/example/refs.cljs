(ns example.refs
  (:require [clojure.pprint]
            [reagent.core :as r]))

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
  [video-ui {:src "http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"}])
