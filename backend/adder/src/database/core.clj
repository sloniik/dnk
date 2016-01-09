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
  (select-col-from-table db-spec
                         table-name
                         "*"))

(defn select-col-from-table-by-field
  "return specific column from table where field-name = field-val"
  [db-spec table-name col-name field-name field-val]
  (jdbc/query db-spec
              [(str "select " col-name " from " table-name " where " field-name " = ?") field-val]))

(defn select-all-values-from-table-by-field
  "return all values from specified table with col-id-name and id-value"
  [db-spec table-name field-name field-val]
  (select-col-from-table-by-field db-spec
                                  table-name
                                  "*"
                                  field-name
                                  field-val))

;; ================ User functions ===================
;;Список пользователей. Получаются значения полей, кроме password и salt
(def user-table "Users")
(def user-email-col "Email")
(def salt-key :salt)
(def email-key :email)
(def user-pass-col "PasswordHash")
(def pass-key :passwordhash)
(def user-id-col "idUser")
(def user-login-col "UserName")

(defn get-all-users
  "List of all users"
  [db-spec]
  (select-all-values-from-table db-spec user-table))

;;Получаем пользователя по id пользователя
(defn get-user-info-by-id
  "Get user by user-id"
  [db-spec user-id]
  (select-all-values-from-table-by-field db-spec
                                         user-table
                                         user-id-col
                                         user-id))

;;Получем пользователя по логину
(defn get-user-info-by-login
  "Get user by login"
  [db-spec login]
  (select-all-values-from-table-by-field db-spec
                                         user-table
                                         user-login-col
                                         login))

;;Общая функция информации по пользователю  пользователя
(defn get-user-info
  "Get user salt"
  [db-spec id type]
  (if (= type :login)
    (get-user-info-by-login db-spec id)
    (get-user-info-by-id db-spec id)))

(defn get-user-salt
  "Get user salt"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec
                                 id
                                 id-type)]
    (salt-key user-info)))

(defn get-user-pass
  "get user password hash"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec
                                 id
                                 id-type)]
    (pass-key user-info)))

;;Получаем значения полей пользователя (кроме password и salt)
(defn get-user-safe-info
  "Get user by type key (id or login)"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec
                                 id
                                 id-type)]
    (dissoc
      (dissoc user-info salt-key)
      pass-key)))

(def get-media-types
  "Get all types of media available"
  [db-spec])

(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [db-spec user-id id-type media-type])

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [db-spec user-id]
  (select-all-values-from-table-by-field db-spec
                                         user-table
                                         user-login-col
                                         user-id))

(defn get-current-user-session
  "Gets current session by certain user"
  [db-spec user-id])

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [db-spec login]
  (let [user-info (get-user-safe-info db-spec
                                      login
                                      :login)]
    (if (nil? user-info)
      true
      false)))

(defn email-registered?
  "Check whether email is already registered"
  [db-spec email]
  (let [user-info (select-col-from-table-by-field db-spec
                                                  user-table
                                                  user-email-col
                                                  user-email-col
                                                  email)]
    (if (nil? user-info)
      true
      false)))

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database matches calculated hash"
  [db-spec id id-type password-hash]
  (if (= password-hash (get-user-pass db-spec
                                      id
                                      id-type))
    true
    false))

;;======================== Game Get Functions =====================================

(def game-table "Game")
(def deleted?-field "isDeleted")
(def private?-field "isPrivate")
(def game-type-table "GameType")
(def game-variant-table "GameVariant")
(def game-media-table "GameMedia")
(def id-game-field "idGame")
(def id-game-type-filed "idGameType")
(def id-game-variant-field "idGameVariant")
(def id-author-field "idAuthor")
(def id-original-field "idOriginal")

;;Получаем список всех игр
(defn get-all-games
  "Get collection of all games ever created"
  [db-spec]
  (select-all-values-from-table db-spec
                                game-table))

;;TODO: понять как передать false
;;Получаем список игр с параметром isDeleted = false
(def get-all-active-games
  "Get collection of all games that are currently active"
  [db-spec]
  (select-all-values-from-table-by-field db_spec
                                         game-table
                                         deleted?-field
                                         false))

;;TODO: понять как передать false
;;Получем список игр с параметром isPrivate = false
(defn get-all-public-games
  "Get collection of all non-private games"
  [db-spec]
  (select-all-values-from-table-by-field db_spec
                                         game-table
                                         private?-field
                                         false))

;;Получаем список всех типов игр
(defn get-game-types
  "Get all available game types"
  [db-spec]
  (select-all-values-from-table db-spec
                                game-type-table))

;;Получаем список всех вариантов определенного типа игры
(defn get-game-variants
  [db-spec id-game-type]
      (select-all-values-from-table-by-field db_spec
                                             game-variant-table
                                             id-game-type-filed
                                             id-game-type))

;;Получаем список игр определенного типа
(defn get-games-by-variant
  "Get collection of games by variant"
  [db-spec id-game-variant]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-game-variant-field
                                             id-game-variant))

;TODO: пока не знаю как писать
;;Получаем несколько новых игр
(defn get-new-games
  "Get collection of n newest games"
  [db-spec number]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-game-variant-field
                                             id-game-variant))


;;Получаем список игр конкретного автора
(defn get-games-by-autor
  "Get collection of games by author"
  [db-spec id-author]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-author-field
                                             id-author))

;TODO: понять, как передается Null как значение поля в БД
;;Получаем список игр с idOriginal = null
(defn get-all-original-games
  "Get all games that are not forks"
  [db-spec]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-original-field
                                             nil))

;;Получаем список форков игры
(defn get-game-forks
  "Get all forks of a certain game"
  [db-spec id-game]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-original-field
                                             id-game))

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(defn get-game-media
  "Get all media for a certain game"
  [db-spec game-id]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-original-field
                                             id-game))

;;TODO: надо определиться с формой
;;Получаем набор пользователей по данной игре
(defn get-game-users
  "Get all users for a certain game"
  [db-spec id-game]
      (select-all-values-from-table-by-field db_spec
                                             game-media-table
                                             id-game-field
                                             id-game))

(defn get-game-by-id
  "Get game data by it's id"
  [db-spec id-game]
      (select-all-values-from-table-by-field db_spec
                                             game-table
                                             id-game-field
                                             id-game))


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
