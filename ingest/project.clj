(defproject ingest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojurewerkz/elastisch "2.2.1"]
                 [clj-http "2.3.0"]
                 [base64-clj "0.1.1"]
                 [semantic-csv "0.1.0"]
                 [levand/immuconf "0.1.0"]]

  :test-paths ["test/unit" "test/utils"]

  :resource-paths ["config"]

  :profiles {:dev     {:plugins      [[lein-midje "3.2.1"]]
                       :dependencies [[midje "1.8.3"]
                                      [faker "0.2.2"]]}})
