(ns bulldog.core
  (:require [goog.dom :as gdom]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [sablono.core :as html :refer-macros [html]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [bulldog.parser :refer [read]]
            [bulldog.components :refer [Frontpage]])
  (:import goog.History))

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
  (doto h (.setEnabled true)))

(def initial-state
  (atom {:articles [{:title "bulldog"
                              :id #uuid "2fa45746-fed9-4598-b93c-953f8dbf8aaf"
                              :date #inst "2015-10-14T08:58:35.036-00:00"
                              :content [[:p "This article describes the development process and the internals of a simple blogging engine written in Clojure and Clojurescript."]]
                              :abstract "Simple blogging engine"}
                             {:title "replikativ"
                              :id #uuid "eaf5ff82-3911-4dd6-96be-c283db3283d5"
                              :date #inst "2015-10-14T08:58:54.451-00:00"
                              :content [[:p "In the following paragraphs the motivation and structure of a replication microservice is described in-depth."]]
                              :abstract "Replication microservice based on Javascript and JVM"}
                             {:title "lese"
                              :id #uuid "a6d77d7f-8676-42c5-b57b-f407cc853659"
                              :date #inst "2015-10-14T08:59:19.233-00:00"
                              :content [[:p "By following the mainstream trend of developing full-stack Javascript we share in the upcoming paragraphs the development process of a basic bookmarking application."]]
                              :abstract "Bookmarking management and sharing"}]
                  :content/title "Welcome"}))

(def reconciler
  (om/reconciler
   {:state initial-state
    :parser (om/parser {:read read})}))

(enable-console-print!)

(defroute "/" []
  (om/add-root!
   reconciler
   Frontpage
   (gdom/getElement "app")))

(defroute "/articles/:id" {:as params}
  (println (str "Article ID: " (:id params))))

