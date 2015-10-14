(ns bulldog.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! >! put! close!]]
            [chord.client :refer [ws-ch]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn article [data]
  (om/component
   (html [:li.article-entry
          [:a {:href (str "#/articles/" (:id data))}
           [:h1.article-title (:title data)]
           [:small.article-date (.toDateString (:date data))]
           [:p.article-abstract (:abstract data)]]])))


(defn post [data]
  (om/component
   (html
    [:div.post-view
     [:h2.post-title (:title data)]
     [:small.post-date (.toDateString (:date data))]
     [:p.post-content (:content data)]])))


(defn front-view [app owner]
  (reify
      om/IDidMount
      (did-mount [_]
        (go
          (let [{:keys [ws-channel error]} (<! (ws-ch "ws://localhost:8080/ws"))]
            (if-not error
              (do
                (om/transact! app :socket (fn [_] ws-channel) )
                (>! ws-channel {:type :init :data nil})
                (go-loop [{{:keys [type meta data] :as message} :message err :error} (<! ws-channel)]
                  (if-not err
                    (when message
                      (case type
                        :init (om/transact! app :articles (fn [_] data))
                        :get-article (om/transact! app :current-article (fn [_] data))
                        :unrelated)
                      (recur (<! ws-channel)))
                    (println "Channel error on response"))))
              (println "Channel error on open" error)))))
      om/IRender
      (render [_]
        (let [recent-articles (->> (:articles app)
                                   (map (fn [[k v]] (assoc v :id (str k))))
                                   (sort-by :date <)
                                   (take 10))]
          (html
           [:ul#article-list
            (map #(om/build article %) recent-articles)])))))

(defn post-view [app owner]
  (reify
      om/IRender
      (render [state]
        (om/build post (:current-article app)))))
