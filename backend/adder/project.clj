(defproject dnk "0.0.1"
  :description "Game DNK"
  :url "https://github.com/sloniik/dnk/"
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
                 [mysql/mysql-connector-java "5.1.38"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]]

  :dev-dependencies
                [[lein-run "1.2.1"]]

  :main         dnk.web
  )
