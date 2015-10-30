(ns bulldog.components.article
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn- article [data]
  (om/component
   (html
    [:div.article-view
     [:h1.header (:title data)]
     [:small.article-date (.toDateString (:date data))]
     [:p.article-content (:content data)]])))

(defn article-view [app owner]
  (reify
      om/IRender
    (render [state]
      (om/build article (:current-article app)))))

