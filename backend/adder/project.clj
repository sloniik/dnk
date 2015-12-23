(defproject adder "0.1.0-SNAPSHOT"
  :description "Add two numbers"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies
                [[org.clojure/clojure "1.8.0-RC4"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring-json-params "0.1.3"]
                 [compojure "1.4.0"]
                 [clj-json "0.5.3"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [clojure.jdbc/clojure.jdbc-hikari "0.3.3"]
                 [hikari-cp "1.5.0"]
                 [mysql/mysql-connector-java "5.1.38"]]

  :dev-dependencies
                [[lein-run "1.2.1"]]

  :main         cabinet.web
  )
