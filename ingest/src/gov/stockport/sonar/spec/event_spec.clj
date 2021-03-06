(ns gov.stockport.sonar.spec.event-spec
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.ingest.helper.dates :refer [date-string?]]))

(s/def ::event (s/keys :req [::event-source ::event-type ::timestamp]
                       :opt [::name ::dob]))

(s/def ::event-source string?)                              ; consider minimum characters for these ?
(s/def ::event-type string?)                                ; consider minimum characters for these ?
(s/def ::timestamp date-string?)
(s/def ::dob date-string?)

(s/def ::name string?)

(def explainer (partial s/explain-str ::event))

(defn promote-to-namespaced-keywords [event]
  (reduce merge {} (map (fn [[k v]] {(keyword (str 'gov.stockport.sonar.spec.event-spec) (name k)) v}) event)))