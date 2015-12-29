;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:require [clojure.java.jdbc :as j]))


(def mysql-db {:classname "com.mysql.jdbc.Driver"
               :subprotocol "mysql"
               :subname "//127.0.0.1:3306/clojure_test"
               :user "clojure_test"
               :password "clojure_test"})

(j/insert! mysql-db :fruit
           {:name "Apple" :appearance "rosy" :cost 24}
           {:name "Orange" :appearance "round" :cost 49})

(def a (j/query mysql-db
          ["select * from fruit where appearance = ?" "rosy"]
          :row-fn :cost))

(def tstr (j/create-table-ddl
  :users
  [:id :int "PRIMARY KEY AUTO_INCREMENT"]
  [:name "VARCHAR(32)"]
  [:email "VARCHAR(32)"]
  [:active? "TINYINT(1)"]
  [:ban? "TINYINT(1)"]
  [:passw-hash "VARCHAR(100)"]
  :table-spec "ENGINE=InnoDB"))


;; insert into database
(j/insert! mysql-db :fruit
           {:name "Apple" :appearance "rosy" :cost 24}
           {:name "Orange" :appearance "round" :cost 49})

;; пока не понял разнцы со следующим выражением, но это подключение к базе
(def conn
  (j/get-connection mysql-db))

;; подключение к базе
(def db (j/db-find-connection mysql-db))
(def db2 (j/connection))

;; создание таблицы
(j/db-do-commands mysql-db
                  (j/create-table-ddl
                    :users
                    [:id :int "PRIMARY KEY AUTO_INCREMENT"]
                    [:name "VARCHAR(32)"]
                    [:email "VARCHAR(32)"]
                    [:active "VARCHAR(2)"]
                    [:ban "VARCHAR(2)"]
                    [:seenLasttTime "DATE"]
                    [:regdate "DATE"]
                    [:password "VARCHAR(100)"]
                    :table-spec "ENGINE=InnoDB"))

;;test function by Suobig
(def suobig-test
  "Тестовая функция")

(def pooled-db-spec
  {:datasource (j/make-pool mysql-db)})

;; ================ User functions ===================


;;Список пользователей. Получаются значения полей, кроме password и salt
(def get-all-users
  "List of all users"
  )

;;Получаем соль по id пользователя
(def get-user-salt
  "Get salt by user-id"
  [user-id])

;;Получем соль по логину
(def get-user-salt
  "Get salt by login"
  [login])

;;Получаем значения полей пользователя (кроме password и salt) по user-id
(def get-user
  "Get user by user-id"
  [user-id])

(def get-user
  "Get user by login"
  [login])

(def get-media-types
  "Get all types of media available"
  )

(def get-user-media
  "Gets mediafiles of certaion type created by user"
  [user-id media-type])

(def get-all-user-sessions
  "Gets all sessions, made by user"
  [user-id])

(def get-current-user-session
  "Gets current session by certain user"
  [user-id])

;;Проверяем, что данный логин еще не занят
(def is-login-available
  "Check whether login available"
  [login])

(def is-email-registered
  "Check whether email is already registered"
  [email])

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(def is-password-match
  "Check if hashed password in database matches calculated hash"
  [password-hash])


