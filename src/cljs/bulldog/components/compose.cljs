(ns bulldog.components.compose
  (:require [om.core :as om :include-macros true]
            [bulldog.helpers :refer [open-channel]]
            [cljs.core.async :refer [>!]]
            [bulldog.helpers :refer [open-channel handle-text-change]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn create-btn-row
  "Creates interaction button row"
  [cancel-fn ok-fn]
  [:div.btn-row
   [:button#compose-discard-btn.cancel-btn.btn-li
    {:onClick cancel-fn}
    "Discard"]
   [:button#compose-publish-btn.ok-btn.btn-li
    {:onClick ok-fn}
    "Publish"]])

(defn create-input-cmp
  "Create input components based on given value, text and placeholder"
  [owner value text-key placeholder]
  [:input.input-cmp
   {:type "text"
    :value value
    :on-change #(handle-text-change % owner text-key)
    :placeholder placeholder}])

(defn compose-view
  "Creates compose view where composition of an article or project can be selected"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.container
        [:div
         [:h1.header "Compose"]
         [:div.btn-list
          [:button.btn-li.ok-btn
           {:onClick #(-> js/document .-location (set! "#/compose/article"))}
           "Article"]
          [:button.btn-li.ok-btn
           {:onClick #(-> js/document .-location (set! "#/compose/project"))}
           "Project"]
          [:button.cancel-btn.btn-li
           {:onClick #(-> js/document .-location (set! "#/"))}
           "Discard"]]]]))))


(defn compose-project-view
  "Creates compose view for projects"
  [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:title ""
       :source-url ""
       :demo-url ""
       :description ""
       :new-technology ""
       :technologies ""})
    om/IDidMount
    (did-mount [_]
      (when-not (:socket app)
        (open-channel app)))
    om/IRenderState
    (render-state [_ {:keys [title source-url demo-url description technologies]
                      :as state}]
      (if (:admin? app)
        (html
         [:div.container
          [:div
           [:h1.header "Compose new project"]
           (map
            (partial apply create-input-cmp owner)
            [[title :title "Title"]
             [description :description "Description"]
             [source-url :source-url "Source-Url"]
             [demo-url :demo-url "Demo-Url"]
             [technologies :technologies "Technologies"]])]
          (create-btn-row
           (fn [e] (-> js/document .-location (set! "#/compose")))
           (fn [e] (do
                     (println state)
                     #_(-> js/document .-location (set! "#/")))))])
        (fn [e] (-> js/document .-location (set! "#/")))))))


(defn cancel-article-view
  "Return to compose view on cancel"
  [owner e]
  (om/set-state! owner :title "")
  (om/set-state! owner :abstract "")
  (om/set-state! owner :markdown "")
  (-> js/document .-location (set! "#/compose")))

(defn send-article-view
  "Format and send article to server"
  [owner app event]
  (go
    (>! (:socket app)
        {:type :add-article
         :data {:title (om/get-state owner :title)
                :date (js/Date.)
                :abstract (om/get-state owner :abstract)
                :content (om/get-state owner :markdown)}})
    (om/set-state! owner :title "")
    (om/set-state! owner :abstract "")
    (om/set-state! owner :markdown "")
    (-> js/document .-location (set! "#/"))))

(defn compose-article-view 
  "Creates compose view, request admin password and shows textarea for new article"
  [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:title ""
       :abstract ""
       :markdown ""})
    om/IDidMount
    (did-mount [_]
      (when-not (:socket app)
        (open-channel app)))
    om/IRenderState
    (render-state [_ {:keys [title abstract markdown]
                      :as state}]
      (if (:admin? app)
        (html
         [:div.container
          [:div#compose-container
           [:h1.header "Compose new article"]
           (map
            (partial apply create-input-cmp owner)
            [[title :title "Title"]
             [abstract :abstract "Abstract"]])
           [:textarea#compose-markdown-input.input-cmp
            {:cols 50
             :row 4
             :placeholder "Compose your article by using Markdown"
             :value markdown
             :on-change #(handle-text-change % owner :markdown)}]]
          (create-btn-row
           (partial cancel-article-view owner)
           (partial (partial send-article-view owner) app))])
        (-> js/document .-location (set! "#/admin"))))))
