(ns bulldog.components
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! >! timeout]]
            [bulldog.helpers :refer [handle-text-change open-channel]]
            [sablono.core :as html :refer-macros [html]])
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

(defn article [data]
  (html [:li.article-entry
         [:a {:href (str "#/articles/" (:id data))}
          [:div.article-header
           [:h1.article-title (:title data)]
           [:small.article-date (:date-diff data)]]
          [:p.article-abstract (:abstract data)]]]))

(defn post [data]
  (om/component
   (html
    [:div.post-view
     [:h1.header (:title data)]
     [:small.post-date (.toDateString (:date data))]
     [:p.post-content (:content data)]])))

(defn front-view [app owner]
  (reify
      om/IDidMount
      (did-mount [_]
        (when-not (:socket app)
          (open-channel app))
        (go-loop []
          (om/transact!
           app :articles
           (fn [old]
             (map #(assoc % :date-diff (time-diff (:date %))) old)))
          (<! (timeout 60000))
          (recur)))
      om/IRender
      (render [_]
        (html
         [:div.container
          (when (:admin? app) [:div.circle-btn [:a {:href "#/compose"} "+"]])
          [:ul#article-list
           (let [sorted-articles (->> (:articles app)
                                      vec
                                      (sort-by :date >)
                                      (take 7)
                                      (map #(assoc % :date-diff (time-diff (:date %)))))]
             (map article sorted-articles))]]))))

(defn post-view [app owner]
  (reify
      om/IRender
    (render [state]
      (om/build post (:current-article app)))))

(defn admin-view
  "Creates admin view"
  [app owner]
  (reify
      om/IInitState
      (init-state [_]
        {:login-text ""})
      om/IRenderState
      (render-state [_ state]
        (letfn [(send-login [app owner]
                  (go
                    (>! (:socket app)
                        {:type :login :data (om/get-state owner :login-text)})
                    (om/set-state! owner :login-text "")))]
          (if (:admin? app)
            (-> js/document .-location (set! "#/"))
            (html
             [:div#login-container.container
              [:h2.header "Login"]
              [:input.input-cmp#login-input
               {:type "password"
                :placeholder "Type password"
                :value (:login-text state)
                :on-key-down (fn [e] (when (= (.-keyCode e) 13)
                                       (send-login app owner)))
                :on-change #(handle-text-change % owner :login-text)}]
              [:div.btn-cmp
               [:button.cancel-btn {:onClick #(-> js/document .-location (set! "#/"))} "Cancel"]
               [:button.ok-btn {:onClick #(send-login app owner)} "Login"]]]))))))


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
          (om/build admin-view app)))))

(defn navbar
  "Renders the navbar"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:nav#navbar
        [:a.nav-item {:href "#/"} "Home"]
        [:a.nav-item {:href "#/portfolio"} "Portfolio"]
        [:a.nav-item {:href "#/about"} "About"]]))))


(defn project-card
  "Renders a project card"
  [{:keys [title sourcecode showcase description technologies]}]
  (html
   [:li.project
    [:div.project-header
     [:h2 title]
     [:div.project-links
      [:a.project-source {:href sourcecode :target "_blank"} "Sourcecode"]
      [:a.project-showcase {:href showcase :target "_blank"} "Showcase"]]]
    [:p.project-description description]
    [:ul.project-technologies
     (map
      #(vector :li.project-tech %)
      technologies)]]))



(defn portfolio-view
  "Renders a list of project cards"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.portfolio-container.container
        [:h1.header "Portfolio"]
        [:ul.projects-list
         (map project-card
              [{:title "Bulldog"
                :sourcecode "https://github.com/kordano/bulldog"
                :showcase "http://young-lake-3660.herokuapp.com/#/"
                :description "Building better webpresences"
                :technologies ["ClojureScript" "Clojure" "Om" "Datomic" "ELK"]}
               {:title "CodingKitchen.io"
                :sourcecode "https://github.com/codingkitchen/page"
                :showcase "http://www.codingkitchen.io"
                :description "Website for code kitchen, a teaching house for webtechnologies"
                :technologies ["JavaScript" "React" "Node.js" "Flux" "Tape"]}])]]))))
