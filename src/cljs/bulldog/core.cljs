(ns bulldog.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [bulldog.components :refer [article-list]]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [secretary.core :as sec :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [sablono.core :as html :refer-macros [html]])
  (:import goog.History))

(enable-console-print!)

(defn now
  "Create current date"
  []
  (js/Date.))

(def app-state (atom {:articles [{:title "bulldog"
                                  :date (now)
                                  :id 345
                                  :abstract "Simple blogging engine"}
                                 {:title "replikativ"
                                  :date (js/Date. 2015 0 1)
                                  :id 234
                                  :abstract "Replication microservice based on Javascript and JVM"}
                                 {:title "lese"
                                  :date (js/Date. 2015 11 24)
                                  :id 123
                                  :abstract "Bookmarking management and sharing"}]}))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(-> % .-token sec/dispatch!))
  (doto h (.setEnabled true)))

(defroute frontpage "/" []
  (om/root
   article-list
   app-state
   {:target (.getElementById js/document "app")}))

(defroute aboutpage "/about" []
  (om/root
   (fn [_ _]
     (om/component
      (dom/h1 nil "About")))
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/articles/:id" {:as params}
  (js/console.log (str "Article: " (:id params))))

(om/root
   article-list
   app-state
   {:target (.getElementById js/document "app")})
