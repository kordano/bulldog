(ns bulldog.parser
  (:require [om.next :as om]))

(defn- time-diff [date]
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

(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))


(defmethod read :articles/recent
  [{:keys [state] :as env} key {:keys [type]}]
  {:value (->> (:articles @state)
               vec
               (sort-by :date >)
               (map #(assoc % :date-diff (time-diff (:date %)))))})
