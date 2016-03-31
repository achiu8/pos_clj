(ns pos-clj.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [pos-clj.core-test]))

(enable-console-print!)

(doo-tests 'pos-clj.core-test)
