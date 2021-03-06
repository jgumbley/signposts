(ns gov.stockport.sonar.ingest.inbound.events-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.inbound.events :as events]
            [gov.stockport.sonar.ingest.clock :as clock]
            [clj-time.core :as t]))

(def valid-event {:line-number 1 :data {:event-source "SOURCE"
                                        :event-type   "TYPE"
                                        :timestamp    "2017-01-01T12:12:12.000Z"
                                        :some-key     "SOME-VAL"}})

(facts "about event validation"

       (fact "valid event is returned unchanged if validation occurs"
             (events/validate valid-event) => valid-event)

       (fact "invalid event is returned with spec error indicated and original event data preserved"
             (let [{:keys [error data]} (events/validate {:line-number 1 :data {:event-source "SOURCE"}})]
               error => :event-spec-validation
               data => {:event-source "SOURCE"}))

       (fact "event already invalid is ignored and returned"
             (events/validate {:line-number 1 :data {} :error :some-error}) => {:line-number 1 :data {} :error :some-error}))

(facts "about normalising events"

       (fact "ensures full iso date for timestamp"
             (let [result (events/normalise {:line-number 1 :data {:timestamp "31/01/2017"}})]
               (get-in result [:data :timestamp]) => "2017-01-31T00:00:00.000Z"))

       (fact "ensures standard dob format when dob can be parsed"
             (let [result (events/normalise {:line-number 1 :data {:dob "19-nov-1991"}})]
               (get-in result [:data :dob]) => "1991-11-19"))

       (fact "leaves dob when it cannot be parsed"
             (let [result (events/normalise {:line-number 1 :data {:dob "unk"}})]
               (get-in result [:data :dob]) => "unk"))

       (fact "normalises the address"
             (let [result (events/normalise {:line-number 1 :data {:address "123 STOCKPORT ROAD, SOMEWHERE, SK1 3AB, UK"}})]
               (get-in result [:data :address]) => "123 Stockport Road, Somewhere, SK1 3AB, UK")))

(facts "about enhancing events"

       (fact "adds postcode field if none exist"
             (let [result (events/enhance {:line-number 1 :data {:address "123 Stockport Road, SK1 1AB"}})]
               (get-in result [:data :postcode]) => "SK1 1AB"))

       (fact "leaves existing postcode field in place if provided"
             (let [result (events/enhance {:line-number 1 :data {:address  "123 Stockport Road, SK1 1AB"
                                                                 :postcode "SK2 2AA"}})]
               (get-in result [:data :postcode]) => "SK2 2AA"))

       (fact "enhance if provided field is empty but address contains postcode"
             (let [result (events/enhance {:line-number 1 :data {:address  "123 Stockport Road, SK1 1AB"
                                                                 :postcode nil}})]
               (get-in result [:data :postcode]) => "SK1 1AB"))

       (fact "adds an ingestion timestamp"
             (let [some-time (t/date-time 2017)
                   _ (clock/freeze! some-time)
                   result (events/enhance {:line-number 1 :data {:address  "123 Stockport Road, SK1 1AB"
                                                                 :postcode nil}})]
               (get-in result [:data :ingestion-timestamp]) => "2017-01-01T00:00:00.000Z"
               (clock/thaw!))))

(def event {:event-source "SOME-SOURCE"
            :event-type   "SOME_TYPE"
            :timestamp    "2017-12-28T13:14:15.123Z"
            :key1         "val1"
            :key2         "val2"})


(facts "about event ids"

       (fact "they ignore the ingestion timestamp"

             (events/id {:event-source        "SOME-SOURCE"
                         :event-type          "SOME_TYPE"
                         :timestamp           "2017-12-28T13:14:15.123Z"
                         :key1                "val1"
                         :key2                "val2"
                         :ingestion-timestamp "SOMETHING"}) => (events/id {:event-source        "SOME-SOURCE"
                                                                           :event-type          "SOME_TYPE"
                                                                           :timestamp           "2017-12-28T13:14:15.123Z"
                                                                           :key1                "val1"
                                                                           :key2                "val2"
                                                                           :ingestion-timestamp "SOMETHING-ELSE"})))



