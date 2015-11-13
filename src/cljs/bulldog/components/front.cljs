(ns bulldog.components.front
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! >! timeout]]
            [bulldog.helpers :refer [time-diff open-channel]]
            [bulldog.svg :as svg]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn- front-entry [data]
  (html [:li.front-entry
         [:a {:href (str "#/articles/" (:id data))}
          [:div.front-entry-header
           [:h1.front-entry-title (:title data)]
           [:small.front-entry-date (:date-diff data)]]
          [:p.front-entry-abstract (:abstract data)]]]))

(defn front-view [app owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (when-not (:socket app)
        (open-channel app))
      (go-loop []
        (om/transact!
         app :articles
         (fn [old]
           (map #(assoc % :date-diff (time-diff (:date %))) old)))
        (<! (timeout 60000))
        (recur)))
    om/IRender
    (render [_]
      (html
       [:div.container
        [:ul#front-entry-list
         (let [sorted-articles (->> (:articles app)
                                    vec
                                    (sort-by :date >)
                                    (take 7)
                                    (map #(assoc % :date-diff (time-diff (:date %)))))]
           (map front-entry sorted-articles))]
        (when (:admin? app) [:div.circle-menu [:div.circle-btn [:a {:href "#/compose"} "+"]]])]))))
