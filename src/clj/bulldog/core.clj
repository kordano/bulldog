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

(def state (atom {:server nil :store nil}))

(def server (atom nil))

(defn dispatch 
  "Reduces incoming actions"
  [{:keys [store] :as state} {:keys [type meta data]}]
  (case type
    :set-articles (<!! (-assoc-in store [:articles] data))
    :unrelated))

(defn socket-handler [request]
  (with-channel request channel
    (on-close channel (fn [status]))
    (on-receive channel (fn [data]
                          (let [action (read-string data)]
                            (dispatch @state action))))))

(defroutes all-routes
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/ws" [] socket-handler)
  (route/not-found "<h1>Page not found</h1>"))

(defn stop-server
  "startAllServices"
  [{:keys [server] :as state}]
  (when-not (nil? server)
    (server :timeout 100)
    (swap! state assoc :server nil)))

(defn run [server]
  (reset!
   server
   {:server (run-server #'all-routes {:port 8080})
    :store (<!! (new-mem-store)) #_(<!! (new-fs-store "data"))}))

(defn -main [&args] (run server))

(comment

 (run server)

 (stop-server server)

 (-> state deref :store (-get-in [:articles]) <!!)
 
 (dispatch @state {:type :set-articles :data {:foo :bar}})
 
 )
