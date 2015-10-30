(ns bulldog.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [bulldog.components.front :refer [front-view]]
            [bulldog.components.compose :refer [compose-view
                                         compose-article-view
                                         compose-project-view]]
            [bulldog.components.admin :refer [admin-view]]
            [bulldog.components.article :refer [article-view]]
            [bulldog.components.portfolio :refer [portfolio-view]]
            [bulldog.components.nav :refer [navbar]]
            [bulldog.helpers :refer [open-channel]]
            [secretary.core :as sec :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.core.async :refer [<! >!]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import goog.History))

(enable-console-print!)

(println "Greetings puny humans!")

(defn now
  "Create current date"
  []
  (js/Date.))

(def app-state (atom {:admin? true}))

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
      (dom/div #js {:className "container"}
               (dom/h1 #js {:className "header"} "About"))))
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/compose" []
  (om/root
   compose-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/compose/article" []
  (om/root
   compose-article-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/compose/project" []
  (om/root
   compose-project-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/portfolio" []
  (om/root
   portfolio-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/admin" []
  (om/root
   admin-view
   app-state
   {:target (.getElementById js/document "app")}))

(defroute "/articles/:id" {:as params}
  (go
    (>! (:socket @app-state)
          {:type :get-article
           :data (:id params)})
    (om/root
     article-view
     app-state
     {:target (.getElementById js/document "app")})))

(om/root
 navbar
 app-state
 {:target (.getElementById js/document "nav-container")})

#_(-> js/document
      .-location
      (set! "#/"))
