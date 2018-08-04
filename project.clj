(defproject bowling "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/spec.alpha "0.2.168"]
                 [expound "0.7.1"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})