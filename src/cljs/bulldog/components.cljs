(ns bulldog.components
  (:require [goog.dom :as gdom]
            [om.dom :as dom]
            [bulldog.replica :refer [commit-stuff]]
            [sablono.core :as html :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]))


(defn onChange
  "Creates input callback"
  [value component]
  (fn [event]
    (om/set-state!
     component
     (assoc
      (om/get-state component)
      value
      (.. event -target -value)))))



(defui EditorPage
  Object
  (render [this]
    (let [{:keys [title abstract article] :as local} (om/get-state this)]
      (html
       [:div
        [:input
         {:placeholder "What's the title?"
          :type :text
          :value title
          :onChange (onChange :title this)}]
        [:input
         {:placeholder "Give a short introduction"
          :type :text
          :value abstract
          :onChange (onChange :abstract this)}]
        [:input
         {:placeholder "Write your article"
          :type :text
          :value article
          :onChange (onChange :article this)}]
        [:div
         [:button {:onClick (fn [e] (-> js/document .-location (set! "#/")))} "Cancel" ]
         [:button "Publish"]]]))))

(defui ArticlePage
  om/IQuery
  (query [this]
    '[(:content/article nil)])
  Object
  (render [this]
    (let [{:keys [:content/article]} (om/props this)
          {:keys [title author content]} article]
      (html
       [:div
        [:h2 title]
        [:p author]
        content]))))

(defui FrontpageArticle
  Object
  (render [this]
    (let [{:keys [title abstract date-diff id]} (om/props this)]
      (html
       [:li
        [:a {:href (str "#/articles/" id)}
         [:div [:h3 title] [:span date-diff]]
         [:p abstract]]]))))

(def frontpage-article (om/factory FrontpageArticle))

(defui Frontpage
  static om/IQuery
  (query [this]
    '[(:articles/recent nil) (:content/title nil)])
  Object
  (render [this]
    (let [{:keys [:articles/recent :content/title] :as props} (om/props this)]
      (html
       [:div
        [:h2 title]
        [:button {:onClick commit-stuff} "Fetch replica"]
        [:h4 "Recent Articles"]
        [:ul (map frontpage-article recent)]]))))
