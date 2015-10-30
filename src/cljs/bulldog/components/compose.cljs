(ns bulldog.components.compose
  (:require [om.core :as om :include-macros true]
            [bulldog.helpers :refer [open-channel]]
            [cljs.core.async :refer [>!]]
            [bulldog.helpers :refer [open-channel handle-text-change]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn compose-view
  "Creates compose view where composition of an article or project can be selected"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div#compose-container.container
        [:h2.header "Compose"]
        [:button.compose-btn
         {:onClick #(-> js/document .-location (set! "#/compose/article"))}
         "Article"]
        [:button.compose-btn
         {:onClick #(-> js/document .-location (set! "#/compose/project"))}
         "Project"]]))))


(defn compose-project-view
  "Creates compose view for projects"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.container
        [:h2.header "Compose new project"]]))))


(defn compose-article-view 
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
         [:div#compose-container.container
          [:h2.header "Compose new article"]
          [:input#compose-title-input.input-cmp
           {:type "text"
            :placeholder "Title"
            :value (:title-text state)
            :on-change #(handle-text-change % owner :title-text)}]
          [:input#compose-abstract-input.input-cmp
           {:type "text"
            :placeholder "overview"
            :value (:abstract-text state)
            :on-change #(handle-text-change % owner :abstract-text)}]
          [:textarea#compose-markdown-input.input-cmp
           {:cols 50
            :row 4
            :placeholder "Compose your article by using Markdown"
            :value (:markdown-text state)
            :on-change #(handle-text-change % owner :markdown-text)}]
          [:div.btn-cmp
           [:button#compose-discard-btn.cancel-btn
            {:onClick (fn [e]
                        (om/set-state! owner :title-text "")
                        (om/set-state! owner :abstract-text "")
                        (om/set-state! owner :markdown-text "")
                        (-> js/document .-location (set! "#/")))}
            "Discard"]
           [:button#compose-publish-btn.ok-btn
            {:onClick
             #(go
                (>! (:socket app)
                    {:type :add-article :data {:title (om/get-state owner :title-text)
                                               :date (js/Date.)
                                               :abstract (om/get-state owner :abstract-text)
                                               :content (om/get-state owner :markdown-text)}})
                (om/set-state! owner :title-text "")
                (om/set-state! owner :abstract-text "")
                (om/set-state! owner :markdown-text "")
                (-> js/document .-location (set! "#/")))}
            "Publish"]]])
        (-> js/document .-location (set! "#/admin"))))))

