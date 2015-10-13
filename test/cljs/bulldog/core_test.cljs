(ns bulldog.core-test
  (:require [cljs.test :refer-macros [deftest is testing use-fixtures are]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs-react-test.simulate :as sim]
            [cljs-react-test.utils :as tu]
            [dommy.core :as dommy :refer-macros [sel1 sel]]))

(enable-console-print!)

(testing "addition"
  (is (= 42 (+ 9 10 11 12))))
