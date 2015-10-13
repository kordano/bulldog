(ns bulldog.test-runner
  (:require [cljs.test :as test]
            [doo.runner :refer-macros [doo-tests]]
            [bulldog.core-test]
            [bulldog.components-test]))

(doo-tests 'bulldog.core-test
           'bulldog.components-test)
