(ns bulldog.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [hasch.core :refer [uuid]]
            [bulldog.components :refer [article-list]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [chord.client :refer [ws-ch]]
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
   article-list
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
  (js/console.log (str "Article: " (:id params))))


(go
  (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:8080/ws"))]
    (if-not error
      (do
        (swap! app-state assoc-in [:socket] ws-channel)
        (>! ws-channel {:type :init :data nil}))
      (println "Channel error" error))
    (println "Connecting...")
    (go-loop [{:keys [message]} (<! ws-channel)]
      (when message
        (swap! app-state assoc-in [:articles] message)
        (recur (<! ws-channel))))))

(om/root
   article-list
   app-state
   {:target (.getElementById js/document "app")})
