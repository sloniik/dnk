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

;;======================== Game Get Functions =====================================

;;Получаем список всех игр
(def get-all-games
  "Get collection of all games ever created")

;;Получаем список игр с параметром isDeleted = false
(def get-all-active-games
  "Get collection of all games that are currently active")

;;Получем список игр с параметром isPrivate = false
(def get-all-public-games
  "Get collection of all non-private games")

;;Получаем список всех типов игр
(def get-game-types
  "Get all available game types")

;;Получаем список всех вариантов определенного типа игры
(def get-game-variants)

;;Получаем список игр определенного типа
(def get-games-by-variant
  "Get collection of games by variant"
  [id-game-variant])

;;Получаем несколько новых игр
(def get-new-games
  "Get collection of n newest games"
  [number])

(def get-games-by-autor
  "Get collection of games by author"
  [id-author])

;;Получаем список игр с isFork = false
(def get-all-original-games
  "Get all games that are not forks")

;;Получаем список форков игры
(def get-game-forks
  "Get all forks of a certain game"
  [game-id])

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(def get-game-media
  "Get all media for a certain game"
  [game-id])

;;Получаем набор пользователей (TODO: надо определиться с формой) по данной игре
(def get-game-users
  "Get all users for a certain game"
  [game-id])

(def get-game-by-id
  "Get game data by it's id"
  [game-id])