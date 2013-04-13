(defproject regexp-unfolder "0.1.0"
  :description "regexp dictionary generator/unfolder"
  :url "https://launchpad.net/regexp-unfolder"
  :license {:name "Simplified BSD License"
            :url "http://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [instaparse "1.0.0"]
                 [org.clojure/core.logic "0.8.3"]]
  :aot :all
  :main regexp-unfolder.core)
