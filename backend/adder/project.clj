(defproject adder "0.1.0-SNAPSHOT"
  :description "Add two numbers"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
                [[org.clojure/clojure "1.8.0-RC3"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring-json-params "0.1.0"]
                 [compojure "1.4.0"]
                 [clj-json "0.2.0"]
                 [org.clojure/data.generators "0.1.2"]
                 ]

  :dev-dependencies
                [[lein-run "1.2.1"]]

  :main         cabinet.web
  )
