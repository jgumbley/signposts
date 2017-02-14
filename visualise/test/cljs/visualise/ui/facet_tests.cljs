(ns visualise.ui.facet-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [hipo.core :as hipo]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.facet :refer [->cs facet-tree]]))

(def nested-facet-data
  {:control-state {}
   :data          {:facets [{:id     "GMP"
                             :name   "GMP"
                             :field  :event-source
                             :count  1
                             :facets [{:id    "GMP.ASBO"
                                       :name  "ASBO"
                                       :field :event-type
                                       :count 1}]}
                            {:id     "HOMES"
                             :name   "HOMES"
                             :count  3
                             :facets [{:id    "HOMES.ARREARS"
                                       :name  "ARREARS"
                                       :field :event-type
                                       :count 1}
                                      {:id    "HOMES.EVICTION"
                                       :name  "EVICTION"
                                       :field :event-type
                                       :count 2}]}
                            {:id     "SCHOOLS"
                             :name   "SCHOOLS"
                             :count  5
                             :field  :event-type
                             :facets [{:id    "SCHOOLS.AWOL"
                                       :name  "AWOL"
                                       :field :event-type
                                       :count 2}
                                      {:id    "SCHOOLS.EXCLUSION"
                                       :name  "EXCLUSION"
                                       :field :event-type
                                       :count 3}]}]}})
(deftest facet-test

  (testing "single level facts"

    (testing "display"
      (let [subject (facet-tree (->cs {:facets [{:id    "GMP"
                                                 :name  "GMP"
                                                 :field :event-source
                                                 :count 1}
                                                {:id    "SCHOOLS"
                                                 :name  "SCHOOLS"
                                                 :field :event-source
                                                 :count 3}]}))
            dom (hipo/create subject)]
        (is (= (map dommy/text (sel dom [:label])) ["GMP (1)" "SCHOOLS (3)"]))
        (is (= (map dommy/value (sel dom [:input])) ["GMP" "SCHOOLS"]))
        (is (= (map #(dommy/attr % :checked) (sel dom [:input])) [nil nil]))))

    (testing "checked when id present in state"
      (let [!cs (->cs {:facets [{:id    "GMP"
                                 :name  "GMP"
                                 :field :event-source
                                 :count 3}
                                {:id    "SCHOOLS"
                                 :name  "SCHOOLS"
                                 :field :event-source
                                 :count 3}]}
                      {"SCHOOLS" true})
            subject (facet-tree !cs)
            dom (hipo/create subject)]
        (is (= (map #(.-checked %) (sel dom [:input])) [false true]))))
    ))

