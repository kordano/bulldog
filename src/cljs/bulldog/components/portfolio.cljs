(ns bulldog.components.portfolio
  (:require [om.core :as om :include-macros true]
            [bulldog.svg :as svg]
            [sablono.core :as html :refer-macros [html]]))


(defn project-card
  "Renders a project card"
  [{:keys [title sourcecode showcase description technologies]}]
  (html
   [:li.project
    [:div.project-header
     [:h2 title]
     [:div.project-links
      [:a.project-source
       {:href sourcecode
        :target "_blank"
        :title "Show Sourcecode"}
       [:svg.circle-icon
        {:width "100%"
         :height "100%"
         :viewBox "0 0 438.549 438.549"}
        svg/github-icon]]
      [:a.project-showcase
       {:href showcase
        :target "_blank"
        :title "Show Application"}
       [:svg.circle-icon
        {:width "100%"
         :height "100%"
         :viewBox "0 0 350 350"}
        svg/showcase-icon]]]]
    [:p.project-description description]
    [:ul.project-technologies [:small "Technologies: "] 
     (map #(vector :li.project-tech %) technologies)]]))


(defn portfolio-view
  "Renders a list of project cards"
  [app owner]
  (reify
    om/IRender
    (render [this]
      (html
       [:div.container
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
