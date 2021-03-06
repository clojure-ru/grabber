(defproject git-grabber "0.1.1"
  :description "Calculate statistics for git repos on clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [clj-time "0.9.0"]
                 [clj-http "1.0.1"]
                 [environ "1.0.0"]
                 [cheshire "5.3.1"]
                 [postgresql "9.3-1102.jdbc41"]
                 [korma "0.4.0"]
                 [com.taoensso/carmine "2.9.0"]
                 [com.taoensso/timbre "3.3.1"]]
  :main ^:skip-aot git-grabber.core
  :profiles {:uberjar {:aot :all}})

