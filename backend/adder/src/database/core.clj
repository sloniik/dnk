;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]))


(def db-spec {:classname   "com.mysql.jdbc.Driver"
              :subprotocol "mysql"
              :subname     "//127.0.0.1:3306/clojure_test"
              :user        "clojure_test"
              :password    "clojure_test"})

;http://clojure-doc.org/articles/ecosystem/java_jdbc/connection_pooling.html
(defn pool
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

(def pooled-db  (pool db-spec))

;; === MAIN_DB_FUNCTIONs ===
(defn select-col-from-table
  "return specific column from table"
  [db-spec table-name col-name]
    (jdbc/query db-spec [(str "select " col-name " from " table-name)]))

(defn select-all-values-from-table
  "return all values from table "
  [db-spec table-name]
  (select-col-from-table db-spec table-name "*"))

(defn select-col-from-table-by-field
  "return specific column from table where field-name = field-val"
  [db-spec table-name col-name field-name field-val]
  (jdbc/query db-spec
              [(str "select " col-name " from " table-name " where " field-name " = ?") field-val]))

(defn select-all-values-from-table-by-field
  "return all values from specified table with col-id-name and id-value"
  [db-spec table-name field-name field-val]
  (select-col-from-table-by-field db-spec table-name "*" field-name field-val))

;; ================ User functions ===================
;;Список пользователей. Получаются значения полей, кроме password и salt
(defn get-all-users
  "List of all users"
  [])

;;Получаем соль по id пользователя
(defn get-user-salt
  "Get salt by user-id"
  [user-id])

;;Получем соль по логину
(defn get-user-salt
  "Get salt by login"
  [login])

;;Получаем значения полей пользователя (кроме password и salt) по user-id
(defn get-user
  "Get user by user-id"
  [user-id])

(defn get-user
  "Get user by login"
  [login])

(def get-media-types
  "Get all types of media available"
  [])

(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [user-id media-type])

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [user-id])

(defn get-current-user-session
  "Gets current session by certain user"
  [user-id])

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [login])

(defn email-registered?
  "Check whether email is already registered"
  [email])

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database matches calculated hash"
  [password-hash])

;;======================== Game Get Functions =====================================

;;Получаем список всех игр
(defn get-all-games
  "Get collection of all games ever created"
  [])

;;Получаем список игр с параметром isDeleted = false
(def get-all-active-games
  "Get collection of all games that are currently active"
  [])
;;Получем список игр с параметром isPrivate = false
(defn get-all-public-games
  "Get collection of all non-private games"
  [])

;;Получаем список всех типов игр
(defn get-game-types
  "Get all available game types"
  [])

;;Получаем список всех вариантов определенного типа игры
(defn get-game-variants
  [])

;;Получаем список игр определенного типа
(defn get-games-by-variant
  "Get collection of games by variant"
  [id-game-variant])

;;Получаем несколько новых игр
(defn get-new-games
  "Get collection of n newest games"
  [number])

(defn get-games-by-autor
  "Get collection of games by author"
  [author-id])

;;Получаем список игр с isFork = false
(defn get-all-original-games
  "Get all games that are not forks"
  [])

;;Получаем список форков игры
(defn get-game-forks
  "Get all forks of a certain game"
  [game-id])

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(defn get-game-media
  "Get all media for a certain game"
  [game-id])

;;Получаем набор пользователей (TODO: надо определиться с формой) по данной игре
(defn get-game-users
  "Get all users for a certain game"
  [game-id])

(defn get-game-by-id
  "Get game data by it's id"
  [game-id])



;; ==== TESTs ====
(select-all-values-from-table pooled-db "fruit")

(jdbc/query pooled-db
            ["select name, cost from fruit where appearance = ?" "rosy"])

(defn select-col-from-table-new
  "return specific column from table"
  [db-spec table-name col-name]
  (jdbc/with-db-connection [t-con db-spec]
                           (println (jdbc/db-connection t-con))
                           (jdbc/query t-con [(str "select " col-name " from " table-name)])))



(select-col-from-table-new pooled-db "fruit" "name")
(def a (select-col-from-table pooled-db "fruit" "name"))
(def b (select-col-from-table pooled-db "fruit" "cost"))

(jdbc/query pooled-db [(str "select " "*" " from " "fruit" " where " "cost" " = ?") "24"])
(select-all-values-from-table-by-id db-spec "fruit" "cost" 24)

(select-col-from-table pooled-db "fruit" "cost")
(jdbc/query pooled-db [(str "select name from fruit where cost = ?" ) 24])
(select-col-from-table-by-id pooled-db "fruit" "name" "cost" 24)



(defn update-or-insert!
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
                         (let [result (jdbc/update! t-con table row where-clause)]
                           (println t-con)
                           (println db)
                           (if (zero? (first result))
                             (jdbc/insert! t-con table row)
                             result))))

(update-or-insert! db-spec :fruit
                   {:name "Cactus" :appearance "Spiky" :cost 2000}
                   ["name = ?" "Cactus"])
;; inserts Cactus (assuming none exists)
;(update-or-insert! mysql-db :fruit
;                   {:name "Cactus" :appearance "Spiky" :cost 2500}
;                   ["name = ?" "Cactus"])
;;; updates the Cactus we just inserted