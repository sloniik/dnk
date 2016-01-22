;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource
           (java.text SimpleDateFormat)
           (java.util Date))
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]))

;; UTILITY

(defn now [] (.format (SimpleDateFormat. "dd.MM.yyyy HH:mm:ss") (Date.)))

;; ERRORS
(def err-create-game      {:err-code '0001'
                           :err-desc "Can't create the following game "})
(def err-change-game-info {:err-code '0002'
                           :err-desc "Can't commint the following changes to game-id "})

(def db-spec {:classname   "com.mysql.jdbc.Driver"
              :subprotocol "mysql"
              :ssl?        false
              :subname     "//127.0.0.1:3306/dnk_test"
              :user        "dnk_test"
              :password    "dnk_test"})

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
  [db-spec
   table-name
   col-name]

    (jdbc/query db-spec [(str "select " col-name " from " table-name)]))

(defn select-all-values-from-table
  "return all values from table "
  [db-spec
   table-name]

  (select-col-from-table db-spec
                         table-name
                         "*"))

(defn select-col-from-table-by-field
  "return specific column from table where field-name = field-val"
  [db-spec
   table-name
   col-name
   field-name
   field-val]

  (jdbc/query db-spec
              [(str "select " col-name " from " table-name " where " field-name " = ?") field-val]))

(defn select-all-values-from-table-by-field
  "return all values from specified table with col-id-name and id-value"
  [db-spec
   table-name
   field-name
   field-val]

  (select-col-from-table-by-field db-spec table-name "*" field-name field-val))

(defn select-col-from-table-cond-array
  "on input - vector of maps: {:col-name :operation :val}"
  [db-spec table-name col-name cond-array]
  (jdbc/query db-spec
              [(str "select " col-name " from" table-name "where " )])
  )


(defn insert-data
 "insert data (new-record-map) to the table (table-name-key)"
 [db-spec table-name new-record-map]
 (jdbc/insert! db-spec table-name new-record-map))

(defn update-data
  "update string (update-record-map) in the table (table-name-key) where col-name col-val"
  [db-spec table-name update-record-map cond-col cond-val]
  (jdbc/update! db-spec table-name
                update-record-map
                [(str cond-col " = ? ") cond-val]))

(defn delete-data
  [db-spec table-name-key cond-col cond-val]
  (jdbc/delete! db-spec table-name-key
                [(str cond-col " = ? ") cond-val]))

;; ================ User functions ===================
;;Список пользователей. Получаются значения полей, кроме password и salt
(def user-table "users")
(def user-session-table "user_session")
(def user-email-col "email")
(def user-pass-col "password_hash")
(def user-id-col "id_user")
(def user-login-col "user_name")
(def user-id-key :id_user)
(def salt-key :salt)
(def email-key :email)
(def pass-key :password-hash)
(def token-key :user_token)
(def user_active?-key :is_active)
(def user-banned?-key :is_banned)

(defn get-all-users
  "List of all users"
  [db-spec]
  (select-all-values-from-table db-spec user-table))

;;Получаем пользователя по id пользователя
(defn get-user-info-by-id
  "Get user by user-id"
  [db-spec user-id]
  (select-all-values-from-table-by-field db-spec user-table user-id-col user-id))

;;Получем пользователя по логину
(defn get-user-info-by-login
  "Get user by login"
  [db-spec login]
  (select-all-values-from-table-by-field db-spec user-table user-login-col login))

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
  (let [user-info (get-user-info db-spec id id-type)]
    (salt-key user-info)))

(defn get-user-pass
  "get user password hash"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec id id-type)]
    (pass-key user-info)))

;;Получаем значения полей пользователя (кроме password и salt)
(defn get-user-safe-info
  "Get user by type key (id or login)"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec id id-type)]
    (dissoc
      (dissoc user-info salt-key)
      pass-key)))

(def get-media-types
  "Get all types of media available"
  [db-spec])

;TODO: реализовать функцию get-user-media
(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [db-spec
   user-id
   id-type
   media-type])

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [db-spec user-id]
  (select-all-values-from-table-by-field db-spec
                                         user-table
                                         user-login-col
                                         user-id))

;TODO: реализовать функцию
(defn get-current-user-session
  "Gets current session by certain user"
  [db-spec
   user-id]
  )

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [db-spec
   login]
  (let [user-info (get-user-safe-info db-spec
                                      login
                                      :login)]
    (if (nil? user-info)
      true
      false)))

(defn email-registered?
  "Check whether email is already registered"
  [db-spec
   email]
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
  [db-spec
   id-user
   id-type
   password-hash]
  (if (= password-hash (get-user-pass db-spec
                                      id-user
                                      id-type))
    true
    false))

;;Создает нового пользователя
(defn create-user
  "Create new user"
  [db-spec
   user-map]
  (insert-data db-spec user-table user-map)
  )

;;Меняет профиль пользователя
(defn update-user-profile
  "Updates user profile"
  [db-spec
   id-user
   profile-map]
  (update-data db-spec user-table profile-map user-id-col id-user)
  )

;;Обновляем токен пользователя
(defn update-user-token
  "Updates token in table Users"
  [db-spec
   id-user
   token]
  (update-data db-spec user-table (hash-map [token-key token]) user-id-col id-user))

(defn create-user-session
  "Creates new user session"
  [db-spec
   id-user]
  (let [user-map (hash-map user-id-key id-user)]
    (insert-data db-spec user-session-table user-map)))

;;Деактивирует пользователя. ставит в таблице Users is_active=false
(defn deactivate-user
  "Deactivates user (updating record in table Users"
  [db-spec
   id-user]
  (update-data db-spec user-table (hash-map [user_active?-key false]) user-id-col id-user))

;;Банит пользователя, запрещая ему активность на сайте
(defn ban-user
  "Bans user blocking his activity"
  [db-spec
   id-user]
  (update-data db-spec user-table (hash-map [user-banned?-key false]) user-id-col id-user))


;;======================== Game  Functions =====================================

(def game-table-key :game)
(def game-table "game")
(def game-deleted?-field "is_deleted")
(def game-private?-field "is_private")
(def game-type-table "game_type")
(def game-variant-table "game_variant")
(def game-media-table "game_media")
(def id-game-field "id_game")
(def id-game-type-filed "id_game_type")
(def id-game-variant-field "id_game_variant")
(def id-author-field "id_author")
(def id-original-field "id_original")

;;Получаем список всех игр
(defn get-all-games
  "Get collection of all games ever created"
  [db-spec]
  (select-all-values-from-table db-spec
                                game-table-key))


;;Получаем список игр с параметром isDeleted = false
(defn get-all-active-games
  "Get collection of all games that are currently active"
  [db-spec]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         game-deleted?-field
                                         false))

;;TODO: переписать на get-all-active-games иначе не понятно, зачем возвращать удаленные игры?
;;TODO: понять, как получать данные по несколькоим условиям
;;Получем список игр с параметром isPrivate = false
(defn get-all-public-games
  "Get collection of all non-private games"
  [db-spec]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         game-private?-field
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
  (select-all-values-from-table-by-field db-spec
                                         game-variant-table
                                         id-game-type-filed
                                         id-game-type))

;;Получаем список игр определенного типа
(defn get-games-by-variant
  "Get collection of games by variant"
  [db-spec id-game-variant]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         id-game-variant-field
                                         id-game-variant))

;TODO: пока не знаю как писать
;;Получаем несколько новых игр
(defn get-new-games
  "Get collection of n newest games"
  [db-spec number])


;;Получаем список игр конкретного автора
(defn get-games-by-author
  "Get collection of games by author"
  [db-spec id-author]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         id-author-field
                                         id-author))

;;Получаем список игр с idOriginal = null
(defn get-all-original-games
  "Get all games that are not forks"
  [db-spec]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         id-original-field
                                         nil))

;;Получаем список форков игры
(defn get-game-forks
  "Get all forks of a certain game"
  [db-spec id-game]
      (select-all-values-from-table-by-field db-spec
                                             game-table-key
                                             id-original-field
                                             id-game))

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(defn get-game-media
  "Get all media for a certain game"
  [db-spec id-game]
      (select-all-values-from-table-by-field db-spec
                                             game-table-key
                                             id-original-field
                                             id-game))

;;Получаем набор пользователей по данной игре
(defn get-game-users
  "Get all users for a certain game"
  [db-spec id-game]
  (select-all-values-from-table-by-field db-spec
                                         game-media-table
                                         id-game-field
                                         id-game))

(defn get-game-by-id
  "Get game data by it's id"
  [db-spec id-game]
  (select-all-values-from-table-by-field db-spec
                                         game-table-key
                                         id-game-field
                                         id-game))

(defn get-random-game
  "Return random game"
  [db-spec]
  (let [public-games (get-all-public-games db-spec)
        game-rand-num (rand-int (count public-games))
        n-games (take game-rand-num public-games)]
    (last n-games)))

;;TODO сделать функцию проверки игры на похожесть
(defn create-game
  "Create new record in GAME table.
  Search for similar game first
  Return gameID and operation-status
  game info - map"
  [db-spec game-info]
  (let [result (insert-data db-spec game-table-key game-info)]
    (if (nil? (:generated_key result))
    {:error-code (:err-code err-create-game)
     :error-desc (str (:err-desc err-create-game) game-info)}
    (:generated_key result))))

;;NOT IN TO-DO LIST
(defn approve-game
  "Approve game by game-id"
  [db-spec game-id])

(defn change-game-info-by-id
   "Create new record in GAME table.
   Search for similar game first
   Return gameID and operation-status"
  [db-spec game-id game-info]
  (let [result (update-data db-spec game-table-key game-info id-game-field game-id)]
    (if (nil? (first result))
    {:error-code (:err-code err-change-game-info)
     :error-desc (str (:err-desc err-change-game-info) game-id " game-info " game-info)}
    (first result))))

(defn get-random-game
  "Return random game"
  [db-spec]
  (let [games (get-all-public-games db-spec)
        game-number (count games)
        n-games (take (rand-int game-number) games)]
    (last n-games)))

;; ================= ROOM ==================

(def room-table "room")
(def room-users-table "room_users")
(def chat-table "chat")
(def chat-message-table "chat_message")
(def room-active?-key :is_active)
(def room-id-key :id_room)
(def chat-id-key :id_chat)
(def room-joined-key :dt_joined)
(def room-left-key :dt_left)
(def room-id-col "id_room")




;;Создает новую комнату
(defn create-room
  "Creates room for a certain game"
  [db-spec
   room-map]
  (insert-data db-spec room-table room-map)
  )

;;Удаляет комнату (ставит is-active = false)
(defn kill-room
  "Kills certain rooom"
  [db-spec id-room]
  (let [map (hash-map room-active?-key true)]
    (update-data db-spec room-table map room-id-col id-room)))

;TODO: добавить второе условие (is_active = true)
;;Получает список активных комнат конретной игры
(defn get-room-list
  "Get room list of certain game"
  [db-spec id-game]
  (select-all-values-from-table-by-field db-spec room-table id-game-field id-game)
  )

;TODO: добавить второе условие (dt_left = nil)
;;Получает текущий список пользователей в конкретной комнате
(defn get-users-in-room
  "Gets list of users in certain room"
  [db-spec id-room]
  (select-all-values-from-table-by-field db-spec room-users-table room-id-col id-room)
  )

;TODO: добавить второе услвоие (dt_closed = nil)
;;Получает чат комнаты
(defn get-chat
  "Gets chat of a certain room"
  [db-spec id-room]
  (select-all-values-from-table-by-field db-spec chat-table room-id-col id-room )
  )

;;Добавляет пользователя в комнату
(defn enter-room
  "Adds user to a room"
  [db-spec id-user id-room]
  (let [map (hash-map room-id-key id-room
                      user-id-key id-user
                      room-joined-key (now))]
    (insert-data db-spec room-users-table map)))

;TODO: добавить второе усовие для update-data (id_user = id-user)
;;Убирает пользователя из комнаты (задавая dt-left)
(defn leave-room
  "Removes user from a room (settind dt-left)"
  [db-spec id-user id-room]
  (let [map (hash-map room-left-key (now))]
    (update-data db-spec room-users-table map room-id-col id-room)))

;;Отправляет сообщение в чат
(defn send-message
  "Sends message to a chat"
  [db-spec id-user id-chat text]
  (let [map (hash-map :id_user      id-user
                      :id_chat      id-chat
                      :message_text text
                      :dt_sent      (now))]
           (insert-data db-spec chat-message-table map)))

;TODO: понять как вытащить n сообщений
;;Получает список из n последних сообщений
(defn get-last-messages
  "Get list of n last messages in chat"
  [db-spec id-chat n]
  (select-all-values-from-table-by-field db-spec chat-message-table :id_chat id-chat)
  )

;;Создает новый вопрос
(defn add-question
  "Adds question to a certain room from a certain user"
  [db-spec id-room id-user question]
  (let [map (hash-map :id_room id-room
                      :id_user id-user
                      :message question
                      :dt_created (now)
                      :is_deleted false)]
    (insert-data db-spec :question map)))

;TODO: реализовать функцию delete-question
;;Удаляет вопрос
(defn delete-question
  "Removes question"
  [db-spec id-question]
  (let [map (hash-map :is_deleted false)]
    (update-data db-spec :question map :id_question id-question)))

;;Добавляет ответ на вопрос
(defn answer-question
  "Asnwers a question"
  [db-spec id-question  answer]
  (let [map (hash-map :answer answer
                     :dt_answered (now))]
    (update-data db-spec :question map :id_question id-question))
  )

;TODO: реализовать функцию delete-answer
;;Удаляет ответ на вопрос
(defn delete-answer
  "Removes answer"
  [db-spec id-question])


(def a ["2000" "1"])
(jdbc/query db-spec [(str "select " "*" " from " "fruit" " where " "cost" " = ? and id_name = ?") (1 vec-par ) (2 vec-par)])