(ns bulldog.core
  (:require [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [run-server]]))

(defonce server (atom nil))

(defroutes all-routes
  (GET "/" [] (io/resource "public/index.html"))
  (route/not-found "<h1>Page not found</h1>"))

(defn stop-server
  "startAllServices"
  [server]
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn run [server]
  (reset! server (run-server #'all-routes {:port 8080})))

(defn -main [&args]
  (run server))

(comment

  (run server)

  
  )
