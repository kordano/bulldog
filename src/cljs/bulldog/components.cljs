(ns bulldog.components
  (:require [goog.dom :as gdom]
            [om.dom :as dom]
            [sablono.core :as html :refer-macros [html]]
            [om.next :as om :refer-macros [defui]]))


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
        [:h4 "Recent Articles"]
        [:ul (map frontpage-article recent)]]))))
