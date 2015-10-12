(ns bulldog.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn now
  "Create current date"
  []
  (js/Date.))

(defn article-comp [{:keys [title date abstract]}]
  (om/component
   (html [:h1 title]
         [:small date]
         [:p abstract])))
