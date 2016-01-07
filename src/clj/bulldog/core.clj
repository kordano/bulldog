(ns bulldog.core
  (:gen-class :main true)
  (:require [bulldog.database :refer [add-article init-db get-article get-init login]]
            [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [endophile.core :refer [mp]]
            [endophile.hiccup :refer [to-hiccup]]
            [konserve.memory :refer [new-mem-store]]
            [hasch.core :refer [uuid]]
            [org.httpkit.server :refer [send! with-channel on-close on-receive run-server]]
            [clojure.core.async :refer [go <!!]]))

(defn dispatch 
  [store {:keys [type meta data] :as msg}]
  "Reduces incoming actions"
  (assoc msg :data  
         (case type
           :init  (get-init store)
           :add-article (add-article store data)
           :get-article (get-article store data)
           :login (login store data)
           :unrelated)))

(defn create-socket-handler [state]
  "Socket dispatcher"
  (fn [request]
    (with-channel request channel
      (on-close channel
                (fn [status]
                  (println (str "Channel closed:" status))))
      (on-receive channel
                  (fn [data]
                    (let [action (read-string data)
                          response (str (dispatch (:store state) action))]
                      (send! channel response)))))))

(defn create-routes
  "Create routes from server state"
  [state]
  (defroutes all-routes
    (route/resources "/")
    (GET "/" [] (io/resource "public/index.html"))
    (GET "/ws" [] (create-socket-handler state))
    (route/not-found "<h1>Page not found</h1>")))

(defn stop-server
  "Stops server"
  [{:keys [server] :as state}]
  (when-not (nil? server)
    (server :timeout 100)
    (swap! state assoc :server nil)))

(defn start-server
  "start the server"
  [port pw]
  (let [state (atom {:server nil
                     :store (<!! (new-mem-store)) #_(<!! (new-fs-store "data"))})]
    (create-routes @state)
    (init-db state pw)
    (swap! state assoc :server (run-server #'all-routes {:port port}))
    state))

(defn -main [& args]
  (let [port (or (System/getenv "PORT") (second args))]
    (start-server (if port (Integer/parseInt port) 8080) "bamboozle")
    (println "Server startet at localhost:8080")))

(comment
  
  (def state (start-server 8080 "test"))
  
  #_(def state (atom {}))
  
  (swap! state assoc :store (<!! (new-mem-store)))

  (-> state deref :store)

  (stop-server @state)
 
 
  )
