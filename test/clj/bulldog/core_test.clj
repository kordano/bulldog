(ns bulldog.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [konserve.memory :refer [new-mem-store]]
            [konserve.protocols :refer [-get-in -assoc-in -update-in -exists?]]
            [clojure.core.async :refer [go <!!]]
            [bulldog.core :refer :all]))



(facts "action reducer"
       (let [store (<!! (new-mem-store))]
         (fact "set-articles sets all articles consumed as single value"
               (:data (dispatch store {:type :set-articles :data {:foo :bar}}))
               => [{:foo :bar}])
         (fact "set-articles sets all articles consumed as a vector"
               (:data (dispatch store {:type :set-articles :data [{:foo :bar} {:bar :baz}]}))
               => [{:foo :bar} {:bar :baz}]))
       (let [store (<!! (new-mem-store))]
         (fact "add-article adds single value"
               (-> (dispatch store {:type :add-article :data {:foo :bar}})
                   :data
                   vals)
               => [{:foo :bar}])
         (fact "add-article adds a vector"
               (-> (dispatch store {:type :add-article :data [{:bar :baz}]})
                   :data
                   vals)
               => [{:foo :bar} {:bar :baz}])))
