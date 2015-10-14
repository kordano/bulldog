(ns bulldog.core
  (gen-class :main true)
  (:require [konserve.filestore :refer [new-fs-store]]
            [konserve.memory :refer [new-mem-store]]
            [konserve.protocols :refer [-get-in -assoc-in -update-in -exists?]]
            [hasch.core :refer [uuid]]
            [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [send! with-channel on-close on-receive run-server]]
            [clojure.core.async :refer [go <!!]]))

(defn now [] (new java.util.Date))

(defn dispatch 
  "Reduces incoming actions"
  [store {:keys [type meta data] :as msg}]
  (letfn [(set-articles [store data]
            (<!! (-assoc-in store [:articles]
                            (if (vector? data)
                              data
                              (vector data))))
            (<!! (-get-in store [:articles])))
          (add-article [store data]
            (if (vector? data)
              (map (partial add-article store) data)
              (let [new-id (uuid)]
                (<!! (-assoc-in store [:articles new-id] data))))
            (<!! (-get-in store [:articles])))
          (get-init [store]
            (<!! (-get-in store [:articles])))
          (get-article [store data]
            (<!! (-get-in store [:articles data])))]
    (assoc msg :data  
           (case type
             :init  (get-init store)
             :set-articles (set-articles store data)
             :add-article (add-article store data)
             :get-article (get-article store data)
             :unrelated))))

(defn create-socket-handler [state]
  (fn [request]
    (with-channel request channel
      (on-close channel (fn [status]))
      (on-receive channel (fn [data]
                            (let [action (read-string data)]
                              (send! channel (str (dispatch (:store state) action)))))))))

(defn create-routes
  "Create routes from server state"
  [state]
  (defroutes all-routes
    (route/resources "/")
    (GET "/" [] (io/resource "public/index.html"))
    (GET "/ws" [] (create-socket-handler state))
    (route/not-found "<h1>Page not found</h1>")))

(defn stop-server
  "stops only server"
  [{:keys [server] :as state}]
  (when-not (nil? server)
    (server :timeout 100)
    (swap! state assoc :server nil)))

(defn start-all-services
  "startAllServices TM"
  []
  (let [state (atom {:server nil
                     :store (<!! (new-mem-store)) #_(<!! (new-fs-store "data"))})]
    (create-routes @state)
    (swap! state assoc :server (run-server #'all-routes {:port 8080}))
    state))

(defn -main [& args]
  (start-all-services)
  (println "Server startet at localhost:8080"))

(comment

  (def state (start-all-services))
  
  #_(def state (atom {}))
  
  (swap! state assoc :store (<!! (new-mem-store)))
  
  (stop-server @state)

  (-> state deref :store (-assoc-in [:articles]  {#uuid "2fa45746-fed9-4598-b93c-953f8dbf8aaf"
                                                  {:title "bulldog"
                                                   :date #inst "2015-10-14T08:58:35.036-00:00"
                                                   :content "blbablalba"
                                                   :abstract "Simple blogging engine"}
                                                  #uuid "eaf5ff82-3911-4dd6-96be-c283db3283d5"
                                                  {:title "replikativ"
                                                   :date #inst "2015-10-14T08:58:54.451-00:00"
                                                   :abstract "Replication microservice based on Javascript and JVM"}
                                                  #uuid "a6d77d7f-8676-42c5-b57b-f407cc853659"
                                                  {:title "lese"
                                                   :date #inst "2015-10-14T08:59:19.233-00:00"
                                                   :abstract "Bookmarking management and sharing"}})
      <!!)
  
  (-> state deref :store (-get-in [:articles]) <!!)

  (-> state deref)
  
  )
