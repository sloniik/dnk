;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class))

(require '[clojure.java.jdbc :as j])

(def mysql-db {:subprotocol "mysql"
               :subname "//127.0.0.1:3306/clojure_test"
               :user "clojure_test"
               :password "clojure_test"})

(j/insert! mysql-db :fruit
           {:name "Apple" :appearance "rosy" :cost 24}
           {:name "Orange" :appearance "round" :cost 49})

(def a (j/query mysql-db
          ["select * from fruit where appearance = ?" "rosy"]
          :row-fn :cost))