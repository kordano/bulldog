(ns bulldog.components.admin
  (:require [om.core :as om :include-macros true]
            [bulldog.helpers :refer [open-channel handle-text-change]]
            [cljs.core.async :refer [>!]]
            [sablono.core :as html :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn send-login
  "Send login data to server"
  [app owner]
  (go
    (>! (:socket app)
        {:type :login :data (om/get-state owner :login-text)})
    (om/set-state! owner :login-text "")))


(defn admin-view
  "Creates admin view"
  [app owner]
  (reify
      om/IInitState
      (init-state [_]
        {:login-text ""})
      om/IRenderState
      (render-state [_ state]
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
             [:button.ok-btn {:onClick #(send-login app owner)} "Login"]]])))))
