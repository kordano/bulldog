(ns bulldog.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! >!]]
            [bulldog.helpers :refer [handle-text-change open-channel]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

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
      (when-not (:socket app)
        (open-channel app)))
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

(defn login-view
  "Creates login view"
  [app owner]
  (reify
      om/IInitState
    (init-state [_]
      {:login-text ""})
      om/IRenderState
    (render-state [_ state]
      (html
       [:div#login-container
        [:h2 "Login"]
        [:input {:type "password"
                 :placeholder "password required"
                 :value (:login-text state)
                 :on-change #(handle-text-change % owner :login-text)}]
        [:button {:onClick 
                  #(go
                    (>! (:socket app)
                        {:type :login :data (om/get-state owner :login-text)})
                    (om/set-state! owner :login-text ""))}
         "Login"]]))))

(defn compose-view 
  "Creates compose view, request admin password and shows textarea for new article"
  [app owner]
  (reify
      om/IInitState
      (init-state [_]
        {:title-text ""
         :abstract-text ""
         :markdown-text ""})
      om/IDidMount
      (did-mount [_]
        (when-not (:socket app)
          (open-channel app)))
      om/IRenderState
      (render-state [_ state]
        (if (:admin? app)
          (html
           [:div#compose-container
            [:h2 "Compose new article"]
            [:input {:type "text"
                     :placeholder "Title"
                     :value (:title-text state)
                     :on-change #(handle-text-change % owner :title-text)}]
            [:input {:type "text"
                     :placeholder "Abstract"
                     :value (:abstract-text state)
                     :on-change #(handle-text-change % owner :abstract-text)}]
            [:textarea {:cols 50
                        :row 4
                        :placeholder "Write your article in Markdown"
                        :value (:markdown-text state)
                        :on-change #(handle-text-change % owner :markdown-text)}]
            [:button {:onClick (fn [e]
                                 (om/set-state! owner :title-text "")
                                 (om/set-state! owner :abstract-text "")
                                 (om/set-state! owner :markdown-text "")) }
             "Discard"]
            [:button {:onClick
                      #(go
                         (>! (:socket app)
                             {:type :add-article :data {:title (om/get-state owner :title-text)
                                                        :date (js/Date.)
                                                        :abstract (om/get-state owner :abstract-text)
                                                        :content (om/get-state owner :markdown-text)}})
                         (om/set-state! owner :title-text "")
                         (om/set-state! owner :abstract-text "")
                         (om/set-state! owner :markdown-text ""))}
             "Publish"]])
          (om/build login-view app)))))

