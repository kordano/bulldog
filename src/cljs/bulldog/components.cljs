(ns bulldog.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn article [data]
  (om/component
   (html [:li.article-entry
          [:a {:href (str "#/" (:id data))}
           [:h1.article-title (:title data)]
           [:small.article-date (.toDateString (:date data))]
           [:p.article-abstract (:abstract data)]]])))


(defn article-list [data owner]
  (let [recent-articles (->> (:articles data)
                             vals
                             (sort-by :date >)
                             (take 10))]
    (om/component
     (html
      [:ul#article-list
       (map #(om/build article %) recent-articles)]))))

(defn post-view [data]
  (om/component
   (html
    [:div.post-view
     [:h2.post-title (:title data)]
     [:small.post-date (:date data)]
     [:p.post-content (:content data)]])))
