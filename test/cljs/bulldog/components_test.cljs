(ns bulldog.components-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [markdown.core :refer [md->html]]
            [cljs-react-test.simulate :as sim]
            [cljs-react-test.utils :as tu]
            [bulldog.components :refer [article front-view post post-view]]
            [dommy.core :as dommy :refer-macros [sel1 sel]]))

(enable-console-print!)

(def test-articles
  {#uuid "2fa45746-fed9-4598-b93c-953f8dbf8aaf"
   {:title "alpha"
    :date #inst "2015-10-14T08:58:35.036-00:00"
    :content "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."
    :abstract "Lorem ipsum dolor sit amet"}
   #uuid "eaf5ff82-3911-4dd6-96be-c283db3283d5"
   {:title "bravo"
    :date #inst "2015-10-14T08:58:54.451-00:00"
    :content "At vero eos et accusam et justo duo dolores et ea rebum."
    :abstract "consetetur sadipscing elitr"}
   #uuid "a6d77d7f-8676-42c5-b57b-f407cc853659"
   {:title "charlie"
    :date #inst "2015-10-14T08:59:19.233-00:00"
    :content "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."
    :abstract "sed diam nonumy eirmod"}})


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

(deftest frontview-component
  (testing "renders a list of recent ten articles"
    (let [tc (.createElement js/document "div")
          _ (tu/insert-container! tc)
          data (atom {:articles test-articles})
          _ (om/root front-view data {:target tc})
          article-comps (sel tc :li)]
      (is (= 3 (count article-comps)))
      (tu/unmount! tc))))


(deftest post-component
  (testing "renders an article by showing"
    (let [tc (.createElement js/document "div")
          _ (tu/insert-container! tc)
          data (-> test-articles first val)
          _ (om/root post data {:target tc})]
      (testing "a title"
        (is (= (-> test-articles first val :title)
               (.-textContent (sel1 tc :h2)))))
      (testing "a date"
        (is (= (.toDateString (js/Date. #inst "2015-10-14T08:58:35.036-00:00"))
               (.-textContent (sel1 tc :small)))))
      (testing "a markdown content"
        (is (= (-> test-articles first val :content)
               (.-textContent (sel1 tc :p)))))
      (tu/unmount! tc))))

(deftest login-component
  (testing "sends login data to server and sets admin status"
    ))
