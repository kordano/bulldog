(ns bulldog.core-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures are]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-react-test.simulate :as sim]
            [cljs-react-test.utils :as tu]
            [bulldog.core :refer [now article-comp]]
            [dommy.core :as dommy :refer-macros [sel1 sel]]))

(enable-console-print!)

(def ^:dynamic c)

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

(deftest article-components
  (testing "renders title, date and abstract of an article"
    (let [date (now)
          data {:title "foo"
                :date date
                :abstract "bar baz"}
          _ (om/root article-comp data {:target c})
          title-node (sel1 c [:h1])
          abstract-node (sel1 c [:p])]
      (is (re-find #"foo" (.-textContent title-node)))
      (is (re-find #"bar baz" (.-textContent abstract-node))))))
