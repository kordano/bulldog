(ns bulldog.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn article [data]
  (om/component
   (html [:li.article-entry
          [:h1.article-title (:title data)]
          [:small.article-date (.toDateString (:date data))]
          [:p.article-abstract (:abstract data)]])))


(defn article-list [data owner]
  (let [recent-articles (->> (:articles data)
                             (sort-by :date >)
                             (take 10))]
    (om/component
     (html
      [:ul#article-list
       (map #(om/build article %) recent-articles)]))))
