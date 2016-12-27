(defproject mars-exploration "0.1.0-SNAPSHOT"
  :description "Controls probes to explore Mars surface."
  :url "http://github.com/thalesmg/mars-exploration"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot mars-exploration.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.8.3"]
                                  [org.clojure/test.check "0.9.0"]]
                   :plugins [[lein-midje "3.2.1"]]}})
