(ns visualise.ui.facet-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [cljs-react-test.utils :as tu]
            [reagent.core :as reagent]
            [dommy.core :as dommy :refer-macros [sel sel1]]
            [visualise.ui.facet :refer [->cs facet-tree]]))

(def ^:dynamic c)

(use-fixtures :each (fn [test-fn]
                      (binding [c (tu/new-container!)]
                        (test-fn)
                        (tu/unmount! c))))

; this is a page object; knows DOM
(defn facet-info
  ([] (map #(facet-info %) (sel [:label])))
  ([elem] (let [cbelem (sel1 elem [:input])
                id (dommy/value cbelem)
                label (dommy/text elem)
                checked (.-checked cbelem)]
            [id label checked])))

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
            _ (reagent/render subject c)]
        (is (= (facet-info) [["GMP" "GMP (1)" false]
                             ["SCHOOLS" "SCHOOLS (3)" false]]))))

    (testing "uses state to show selected items"

      (let [subject (facet-tree (->cs {:facets [{:id    "GMP"
                                                 :name  "GMP"
                                                 :field :event-source
                                                 :count 3}
                                                {:id    "SCHOOLS"
                                                 :name  "SCHOOLS"
                                                 :field :event-source
                                                 :count 3}]}
                                      {"SCHOOLS" true}))
            _ (reagent/render subject c)]
        (is (= (facet-info) [["GMP" "GMP (3)" false]
                             ["SCHOOLS" "SCHOOLS (3)" true]]))))
    ))

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
