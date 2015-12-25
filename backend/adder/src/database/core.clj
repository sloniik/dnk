;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc])
  (:require [database.schema :as schema])
  (:require [jdbc.pool.c3p0 :as pool]))

(def mysql-db
  "db-spec to create conn"
  {:classname "com.mysql.jdbc.Driver"
               :subprotocol "mysql"
               :subname "//127.0.0.1:3306/clojure_test"
               :user "clojure_test"
               :password "clojure_test"})

(defn pool
  "function creates pool of conn to database (which is specified in spec)"
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pooled-db (delay (pool mysql-db)))


(defn db-connection
  "create pooled connection to database"
  [] @pooled-db)

;; conn to database (within a pool)
(def conn (db-connection))


;; create tables
; create users table
;(jdbc/db-do-commands conn schema/create-users-table)

;;ORM
(defn select-all-from-table
  "perform simple _select * from <table>_
  conn - connection
  table - string table name"
  [conn table]
  (jdbc/query conn [ (str "select * from " table)]))

;;=======================
;; user-table-functions
;;=======================

;CREATE TABLE User
;(`idUser` BIGINT NOT NULL AUTO_INCREMENT,PRIMARY KEY (idUser)
;`UserName` VARCHAR(250) NOT NULL
;`PasswordHash` VARBINARY(65535) NOT NULL
;`Salt` VARCHAR(250) NOT NULL
;`Email` VARCHAR(250)  NULL
;`UserStory` VARCHAR(2048)  NULL
;`PhoneNumber` VARCHAR(250)  NULL
;`UserToken` VARBINARY(65535)  NULL
;`idUserSession` BIGINT  NULL
;`dtCreated` DATETIME NOT NULL
;`isOnline` BIT NOT NULL
;`isActive` BIT NOT NULL
;`isAdmin` BIT NOT NULL)

(defn select-all-users
  "select all info in users table"
  [conn]
  (select-all-from-table conn "Users"))

(defn select-user-by-col-val
  "select all user-info by any col and value"
  [conn col val]
  (jdbc/query conn [(str "select * from Users where " col "=?") val]))

(defn select-user-by-id
  "select all user-info by id
  id - guid|int"
  [conn id]
  (select-user-by-col-val conn "idUser" id))

(defn select-user-by-name
  "select all user-info by name
  name - string"
  [conn name]
  (select-user-by-col-val conn "UserName" name))

(defn select-user-by-email
  "select all user-info by name
  name - string"
  [conn email]
  (select-user-by-col-val conn "Email" email))

;(j/insert! mysql-db :fruit
;           {:name "Apple" :appearance "rosy" :cost 24}
;           {:name "Orange" :appearance "round" :cost 49})





;;ORM-function
;(defn query
;  "get map: [{:table table-name}
;             {:where-col where-col-name}
;             {:where-col-val where-col-value}
;             {:col1 col1-name
;             :col2 col2-name}"
;  [table-name w-col-name w-col-val &cols]
;  (let [t-name :table table-name
;        where-col-name :where-col w-col-name
;        where-col-value :where-col-val w-col-val
;        all-cols (vals cols)
;        str-col-list (if (nil? all-cols ) "*"
;                                          (map (fn [ch] (str ch " ,")) all-cols))
;        str-where (if (nil? where-col-name) ""
;                                            (str "where " where-col-name "= ?" " " where-col-value)))
;        ]
;    (jdbc/query conn [(str "select " str-col-list " from " t-name " " str-where]))))

