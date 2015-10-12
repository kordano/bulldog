(ns bulldog.core
  (:require [konserve.filestore :refer [new-fs-store]]
            [konserve.memory :refer [new-mem-store]]
            [konserve.protocols :refer [-get-in -assoc-in -update-in -exists?]]
            [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [send! with-channel on-close on-receive run-server]]
            [clojure.core.async :refer [go <!!]]))

(defn now [] (new java.util.Date))

(defn dispatch 
  "Reduces incoming actions"
  [store {:keys [type meta data]}]
  (letfn [(set-articles [store data]
            (<!! (-assoc-in store [:articles]
                            (if (vector? data)
                              data
                              (vector data))))
            (<!! (-get-in store [:articles])))
          (add-article [store data]
            (<!! (-update-in store [:articles]
                             #(if (vector? data)
                                (concat % data)
                                (conj % data))))
            (<!! (-get-in store [:articles])))]
    (case type
      :set-articles (set-articles store data)
      :add-article (add-article store data)
      :unrelated)))

(defn create-socket-handler [state]
  (fn [request]
    (with-channel request channel
      (on-close channel (fn [status]))
      (on-receive channel (fn [data]
                            (let [action (read-string data)]
                              (dispatch (:store state) action)))))))

(defn create-routes
  "Create routes from server state"
  [state]
  (defroutes all-routes
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
  (let [state (atom {:server nil :store []})]
    (create-routes state)
    (reset!
     state
     {:server (run-server #'all-routes {:port 8080})
      :store (<!! (new-mem-store)) #_(<!! (new-fs-store "data"))})
    state))

(defn -main [&args]
  (start-all-services))

(comment

  (def state (start-all-services))
  
  (swap! state assoc :store (<!! (new-mem-store)))
  
  (stop-server state)

  (-> state deref :store (-get-in [:articles]) <!!)

)
