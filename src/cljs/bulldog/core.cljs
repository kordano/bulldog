(ns bulldog.core
  (:require [goog.dom :as gdom]
            [sablono.core :as html :refer-macros [html]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defn time-diff [date]
  (let [diff (- (.getTime (js/Date.))
                (.getTime date))
        minutes (js/Math.floor (/ diff (* 1000 60)))]
    (cond
      (< minutes 2) "now"
      (<= 2 minutes 60) (str minutes " minutes ago")
      :else
      (let [days (js/Math.floor (/ minutes (* 60 24)))]
        (cond
          (< days 1.0) "today"
          (<= 1 days 2) "yesterday"
          :else (str days " days ago"))))))

(enable-console-print!)

(def app-state
  {:title "Welcome"
   :articles [{:title "bulldog"
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
               :abstract "Bookmarking management and sharing"}]})

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
  Object
  (render [this]
    (let [sorted-articles (->> (get (om/props this) :articles)
                               vec
                               (sort-by :date >)
                               (map #(assoc % :date-diff (time-diff (:date %)))))]
      (html
       [:div
        [:h2 (get (om/props this) :title)]
        [:h4 "Recent Articles"]
        [:ul (map article sorted-articles)]]))))

(def frontpage (om/factory Frontpage))

(js/ReactDOM.render
 (frontpage app-state)
 (gdom/getElement "app"))
