(ns bulldog.components.nav
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

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


