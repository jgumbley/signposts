(ns gov.stockport.sonar.visualise.util.fmt-help-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [cljs-time.core :as t]))

(deftest fmt-help-tests
  (testing "address summary"

    (is (= (fh/address-summary {}) nil))

    (is (= (fh/address-summary {:address "" :postcode ""}) nil))

    (is (= (fh/address-summary {:address "" :postcode "SK2 1AA"}) "SK2 1AA"))

    (is (= (fh/address-summary {:address  "22 Acacia Ave, Somewhere, SK2 4HI"
                                :postcode "SK2 4HI"})
           (str "22 Acacia Ave" fh/ellipsis " SK2 4HI")))


    (is (= (fh/address-summary {:address  "22 Acacia Ave Somewhere SK2 4HI"
                                :postcode "SK2 4HI"})
           (str "22 Acacia Ave So" fh/ellipsis " SK2 4HI"))))

  (testing "date-of-birth"

    (is (= (fh/date-of-birth {}) nil))

    (is (= (fh/date-of-birth {:dob "1971-04-03"}) "3 Apr 1971"))))




