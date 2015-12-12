(ns bulldog.database
  (:require [hasch.core :refer [uuid]]
            [endophile.core :refer [mp]]
            [endophile.hiccup :refer [to-hiccup]]
            [clojure.core.async :refer [go <!!]]
            [konserve.core :as k]))
            
(def test-articles
  {#uuid "2fa45746-fed9-4598-b93c-953f8dbf8aaf"
        {:title "bulldog"
         :date #inst "2015-10-14T08:58:35.036-00:00"
         :content (to-hiccup (mp "This article describes the development process and the internals of a simple blogging engine written in Clojure and Clojurescript."))
         :abstract "Simple blogging engine"}
        #uuid "eaf5ff82-3911-4dd6-96be-c283db3283d5"
        {:title "replikativ"
         :date #inst "2015-10-14T08:58:54.451-00:00"
         :content (to-hiccup (mp "In the following paragraphs the motivation and structure of a replication microservice is described in-depth."))
         :abstract "Replication microservice based on Javascript and JVM"}
        #uuid "a6d77d7f-8676-42c5-b57b-f407cc853659"
        {:title "lese"
         :date #inst "2015-10-14T08:59:19.233-00:00"
         :content (to-hiccup (mp "By following the mainstream trend of developing full-stack Javascript we share in the upcoming paragraphs the development process of a basic bookmarking application."))
         :abstract "Bookmarking management and sharing"}})

(defn now [] (new java.util.Date))

(defn init-db [state password]
  (-> state deref :store (k/assoc-in [:articles] test-articles) <!!)
  (-> state deref :store (k/assoc-in [:admin :password] password) <!!))

(defn get-init [store]
  (->> (<!! (k/get-in store [:articles]))
       (map (fn [[k v]] (assoc (dissoc v :content) :id k)))))

(defn add-article
  [store data]
  (if (vector? data)
    (doall (map (partial add-article store) data))
    (let [new-id (uuid)]
      (<!! (k/assoc-in store [:articles new-id] (update-in data [:content] (comp to-hiccup mp))))))
  (->> (<!! (k/get-in store [:articles]))
       (map (fn [[k v]] (assoc (dissoc v :content) :id k)))))

(defn get-article
  [store data]
  (<!! (k/get-in store [:articles (java.util.UUID/fromString data)])))

(defn login [store data]
  (= data (<!! (k/get-in store [:admin :password]))))

