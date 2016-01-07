(ns bulldog.components.about
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn about-view
  "Fetch content data and render about-page"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.container
        [:h1.header "About me"]
        [:div.about-me-body (:about-me (om/props this))]]))))
