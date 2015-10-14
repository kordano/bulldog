(ns bulldog.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [bulldog.components :refer [front-view post-view]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [cljs.core.async :refer [<! >! put! close!]]
            [secretary.core :as sec :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.core.async :refer [<!]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:import goog.History))

(enable-console-print!)

(defn now
  "Create current date"
  []
  (js/Date.))

(def app-state (atom {}))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(-> % .-token sec/dispatch!))
  (doto h (.setEnabled true)))

(defroute "/" []
  (om/root
   front-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/about" []
  (om/root
   (fn [_ _]
     (om/component
      (dom/h1 nil "About")))
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/articles/:id" {:as params}
  (go
    (>! (:socket @app-state)
          {:type :get-article
           :data (:id params)})
    (om/root
     post-view
     app-state
     {:target (.getElementById js/document "app")})))

(om/root
   front-view
   app-state
   {:target (.getElementById js/document "app")})
