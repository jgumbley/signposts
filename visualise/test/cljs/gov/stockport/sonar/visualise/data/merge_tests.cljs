(ns gov.stockport.sonar.visualise.data.merge-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.data.merge :as merge]))

(deftest merge-tests

  (testing "merge two empty lists of events"
    (is (= (merge/merge-events [] []) [])))

  (testing "merge new events into empty list"
    (is (= (merge/merge-events [] [{:id 1 :data 1}]) [{:id 1 :data 1}])))

  (testing "merge new events onto locked events"
    (is (= (merge/merge-events [{:id 1 :data 1}] [{:id 1 :data 1}]) [{:id 1 :data 1}])))

  (testing "in the strange case that the new event has updated data we keep the locked one"
    (is (= (merge/merge-events [{:id 1 :data 1}
                                {:id 2 :data 2}]

                               [{:id 1 :data 10}
                                {:id 3 :data 3}])

           [{:id 1 :data 1}
            {:id 2 :data 2}
            {:id 3 :data 3}]))))
