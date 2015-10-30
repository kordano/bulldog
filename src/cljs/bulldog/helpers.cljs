(ns bulldog.helpers
  (:require [om.core :as om :include-macros true]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

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

(defn open-channel
  "Opens channel to server and "
  [app]
  (go
    (let [uri (goog.Uri. js/location.href)
          ssl? (= (.getScheme uri) "https")
          socket-uri (str (if ssl?  "wss://" "ws://")
                          (.getDomain uri)
                          (str ":" (if (= (.getDomain uri) "localhost") 8080 (.getPort uri)))
                          "/ws")
          {:keys [ws-channel error]} (<! (ws-ch socket-uri))]
      (if-not error
        (do
          (om/transact! app :socket (fn [_] ws-channel))
          (>! ws-channel {:type :init :data nil})
          (go-loop [{{:keys [type meta data] :as message} :message err :error} (<! ws-channel)]
            (if-not err
              (when message
                (case type
                  :init (om/transact! app :articles (fn [_] data))
                  :get-article (om/transact! app :current-article (fn [_] data))
                  :add-article (om/transact! app :articles (fn [_] data))
                  :login (om/transact! app :admin? (fn [_] data))
                  :unrelated)
                (recur (<! ws-channel)))
              (println "Channel error on response"))))
        (println "Channel error on open" error)))))

(defn handle-text-change
  "Store and update input text in view component"
  [e owner text]
  (om/set-state! owner text (.. e -target -value)))
