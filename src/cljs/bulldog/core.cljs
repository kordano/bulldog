(ns bulldog.core
  (:require [goog.dom :as gdom]
            [sablono.core :as html :refer-macros [html]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [om.next :as om :refer-macros [defui]]
            [bulldog.parser :refer [read]]
            [om.dom :as dom]))

(def reconciler
  (om/reconciler
   {:state (atom {:articles [{:title "bulldog"
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
                  :content/title "Welcome"})
    :parser (om/parser {:read read})}))


(enable-console-print!)

(defroute "/articles/:id" {:as params}
  (js/console.log (str "Article ID: " (:id params))))

(defui Article
  Object
  (render [this]
    (let [{:keys [title abstract date-diff id]} (om/props this)]
      (html
       [:li
        [:a {:href (str "#/articles/" id)}
         [:div [:h3 title] [:span date-diff]]
         [:p abstract]]]))))

(def article (om/factory Article))

(defui Frontpage
  static om/IQuery
  (query [this]
    '[(:articles/recent nil) (:content/title nil)])
  Object
  (render [this]
    (let [{:keys [:articles/recent :content/title] :as props} (om/props this)]
      (println "Rendering Frontpage: " props)
      (html
       [:div
        [:h2 title]
        [:h4 "Recent Articles"]
        [:ul (map article recent)]]))))

(def frontpage (om/factory Frontpage))

(om/add-root!
 reconciler
 Frontpage
 (gdom/getElement "app"))
