(ns bulldog.components-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-react-test.simulate :as sim]
            [cljs-react-test.utils :as tu]
            [bulldog.components :refer [article article-list]]
            [dommy.core :as dommy :refer-macros [sel1 sel]]))

(enable-console-print!)

(deftest article-component
  (testing "renders title, date and abstract of an article"
    (let [date (js/Date.)
          tc (.createElement js/document "div")
          _ (tu/insert-container! tc)
          data (atom {:title "foo"
                      :date date
                      :abstract "bar baz"})
          _ (om/root article data {:target tc})
          title-node (sel1 tc [:h1])
          date-node (sel1 tc [:small])
          abstract-node (sel1 tc [:p])]
      (is (re-find #"foo" (.-textContent title-node)))
      (is (re-find #"bar baz" (.-textContent abstract-node)))
      (is (re-find (re-pattern (.toDateString date)) (.-textContent date-node)))
      (tu/unmount! tc))))

(deftest article-list-component
  (testing "renders a list of recent ten articles"
    (let [date (js/Date.)
          tc (.createElement js/document "div")
          _ (tu/insert-container! tc)
          data (atom {:articles (vec (repeat 20 {:title "foo"
                                                 :date date
                                                 :abstract "bar baz"}))})
          _ (om/root article-list data {:target tc})
          article-comps (sel tc :li)]
      (is (= 10 (count article-comps)))
      (tu/unmount! tc))))
