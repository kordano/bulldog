(ns bulldog.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [konserve.memory :refer [new-mem-store]]
            [konserve.protocols :refer [-get-in -assoc-in -update-in -exists?]]
            [clojure.core.async :refer [go <!!]]
            [bulldog.core :refer :all]))



(facts "about action reducer"
       (fact "set-articles sets all articles consumed as vector or single value"
             (let [store (<!! (new-mem-store))]
               (dispatch store {:type :set-articles :data {:foo :bar}})
               => [{:foo :bar}]
               (dispatch store {:type :set-articles :data [{:foo :bar} {:bar :baz}]})
               => [{:foo :bar} {:bar :baz}]))
       (fact "add-article joins a vector of articles or single value"
             (let [store (<!! (new-mem-store))]
               (dispatch store {:type :add-article :data {:foo :bar}})
               => [{:foo :bar}]
               (dispatch store {:type :add-article :data [{:bar :baz}]})
               => [{:foo :bar} {:bar :baz}]
               )))
