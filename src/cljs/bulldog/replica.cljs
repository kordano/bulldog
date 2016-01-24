(ns bulldog.replica
  (:require [konserve.memory :refer [new-mem-store]]
            [replikativ.peer :refer [client-peer]]
            [replikativ.crdt.cdvcs.realize :refer [head-value]]
            [replikativ.stage :refer [create-stage! connect! subscribe-crdts!]]
            [replikativ.crdt.cdvcs.stage :as s]
            [cljs.core.async :refer [>! chan timeout]]
            [full.cljs.async :refer [throw-if-throwable]])
  (:require-macros [full.cljs.async :refer [go-try <? go-loop-try]]
                   [cljs.core.async.macros :refer [go-loop]]))


(def uri "ws://127.0.0.1:31744")

(def cdvcs-id #uuid "8e9074a1-e3b0-4c79-8765-b6537c7d0c44")
  
(def eval-fns
  {'(fn [_ new] (if (set? new) new #{new}))
   (fn [_ new] (if (set? new) new #{new}))
   'conj conj})


(enable-console-print!)

(defn start-local []
  (go-try
   (let [local-store (<? (new-mem-store))
         err-ch (chan)
         local-peer (client-peer "BULLDOG CLIENT" local-store err-ch)
         stage (<? (create-stage! "eve@replikativ.io" local-peer err-ch))
         _ (<? (s/create-cdvcs! stage :description "Blog articles" :id cdvcs-id))
         _ (go-loop [e (<? err-ch)]
             (when e
               (.log js/console "ERROR:" e)
               (recur (<? err-ch))))]
     {:store local-store
      :stage stage
      :error-chan err-ch
      :peer local-peer})))

(defn commit-stuff []
  (go-try
   (def client-state (<? (start-local)))
   (<? (connect! (:stage client-state) uri))
   
   #_(<? (s/transact (:stage client-state)
                   ["eve@replikativ.io" cdvcs-id]
                   'conj
                   {:author "baz"
                    :title "qux"
                    :id #uuid "ad707027-38a7-4c2a-99c6-86fbcf5b6082"
                    :abstract "bar baz qux"
                    :content "foo bar bar foo foo"}))
   #_(<? (s/commit! (:stage client-state) {"eve@replikativ.io" #{cdvcs-id}}))))


