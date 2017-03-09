(defproject esproxy "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.5.1"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-json "0.4.0"]
                 [ring-cors "0.1.9"]
                 [buddy "1.3.0"]
                 [bidi "2.0.16"]
                 [clj-http "2.3.0"]
                 [base64-clj "0.1.1"]
                 [prone "1.1.2"]
                 [levand/immuconf "0.1.0"]]

  :resource-paths ["config"]

  :main gov.stockport.sonar.esproxy.app

  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins      [[lein-midje "3.2.1"]]}})