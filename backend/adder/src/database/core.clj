;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]
            [utilities.core :as u]
            [database.errors :as err]))


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
(defn select-col
  "return specific column from table"
  [db-spec select-map]
    (jdbc/query db-spec [(str "select " (:col-name select-map)
                              " from "  (:table-name select-map))]))

(defn select-all
  "return all values from table "
  [db-spec table-name]
  (select-col db-spec (u/sel-n-upd-map (name table-name) "*")))

(defn select-col-by-field
  "return specific column from table where field-name = field-val"
  [db-spec select-map]
  (jdbc/query db-spec
              [(str "select " (name (:col-name   select-map))
                    " from "  (name (:table-name select-map))
                    " where " (name (:field-name select-map)) " = ?")
               (:field-val select-map)]))

(defn select-all-by-field
  "return all values from specified table with col-id-name and id-value"
  [db-spec select-map]
  (select-col-by-field db-spec (u/sel-n-upd-map (:table-name select-map)
                                                "*"
                                                (:field-name select-map)
                                                (:field-val select-map))))

(defn select-cols-multi-cond
  "on input:
    vector of columns to select
    vector of maps in where: {:col-name :operation :col-val}
  return select"
  [db-spec table-name col-name-vec cond-map-array]
  (let [select-col-names  (u/vec->str-with-delimiter col-name-vec ", ")
        where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
    (jdbc/query db-spec
                [(str "select " select-col-names
                      " from " table-name
                      " where " w-cond)])))

(defn insert-data
 "insert data (new-record-map) to the table (table-name-key)"
 [db-spec table-name new-record-map]
 (jdbc/insert! db-spec
               (name table-name)
               new-record-map))

(defn update-data
  "update string (update-record-map) in the table (table-name-key) where update-cond-map (:table-name :field-name field-val)"
  [db-spec update-record-map update-cond-map]
  (jdbc/update! db-spec
                (name (:table-name update-cond-map))
                update-record-map
                [(str (name (:field-name update-cond-map)) " = ? ") (:field-val update-cond-map)]))

(defn update-data-multi-cond
  "update string (update-record-map) in the table (table-name-key) where conditions are in map cond-map-array"
  [db-spec table-name update-record-map cond-map-array]
  (let [where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
    (jdbc/update! db-spec (name table-name) update-record-map [w-cond])))

(defn delete-data
  "delete data from table with some condition"
  [db-spec update-map]
  (jdbc/delete! db-spec
                (name (:table-name update-map))
                [(str (name (:field-name update-map)) " = ? ") (:field-val update-map)]))

(defn delete-data-multi-cond
  "delete in the table (table-name-key) where conditions are in map cond-map-array"
  [db-spec table-name cond-map-array]
  (let [where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
    (jdbc/delete! db-spec
                  (name table-name)
                  [w-cond])))

;; ================ User functions ===================
(defn get-all-users
  "List of all users"
  [db-spec]
  (select-all db-spec :users))

;;Получаем пользователя по id пользователя
(defn get-user-info-by-id
  "Get user by user-id"
  [db-spec user-id]
  (first (select-all-by-field db-spec (u/sel-n-upd-map :users :id_user user-id))))

;;Получем пользователя по логину
(defn get-user-info-by-login
  "Get user by login"
  [db-spec login]
  (first (select-all-by-field db-spec (u/sel-n-upd-map :users :user_name login))))

(defn get-user-info-by-mail
  "Get user by user-id"
  [db-spec user-mail]
  (first (select-all-by-field db-spec (u/sel-n-upd-map :users :user_name user-mail))))

;;Общая функция информации по пользователю  пользователя
(defn get-user-info
  "Get user info"
  [db-spec id type]
  (cond
        (= type :login)
        (get-user-info-by-login db-spec id)
        (= type :email)
        (get-user-info-by-mail db-spec id)
        :default
        (get-user-info-by-id db-spec id)))

(defn get-user-salt
  "Get user salt"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec id id-type)]
    (:salt user-info)))

(defn get-user-pass
  "get user password hash"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec id id-type)]
    (:password_hash user-info)))

;;Получаем значения полей пользователя (кроме password и salt)
(defn get-user-safe-info
  "Get user by type key (id or login)"
  [db-spec id id-type]
  (let [user-info (get-user-info db-spec id id-type)]
    (-> user-info
        (dissoc :salt)
        (dissoc :password-hash))))

;TODO: реализовать функцию
(def get-media-types
  "Get all types of media available"
  [db-spec])

;TODO: реализовать функцию get-user-media
(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [db-spec user-id id-type media-type])

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [db-spec user-id]
  (select-all-by-field db-spec (u/sel-n-upd-map :users :user_name user-id)))

;TODO: реализовать функцию
(defn get-current-user-session
  "Gets current session by certain user"
  [db-spec user-id])

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [db-spec login]
  (let [user-info (get-user-safe-info db-spec login :login)]
    (if (nil? user-info)
      true
      false)))

(defn email-registered?
  "Check whether email is already registered"
  [db-spec email]
  (let [user-info (select-col-by-field db-spec (u/sel-n-upd-map :users :email :email email))]
    (if (nil? user-info)
      true
      false)))

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database matches calculated hash"
  [db-spec id-user id-type password-hash]
  (if (= password-hash (get-user-pass
                         db-spec id-user id-type))
    true
    false))

;;Создает нового пользователя
(defn create-user
  "Create new user"
  [db-spec
   user-map]
  (:generated_key (first (insert-data db-spec :users user-map))))

(defn send-email
  "send email with code in the email body"
  [email code]
  code)

(defn activate-user
  "activate user after confirming email with code"
  [email code]
  (let [user-info (get-user-safe-info db-spec email :email)
        user-id (:id_user user-info)]
    (update-data db-spec
                 {:is_active  true}
                 (u/sel-n-upd-map :users :id_user user-id))))

;;Меняет профиль пользователя
(defn update-user-profile
  "Updates user profile"
  [db-spec id-user profile-map]
  (update-data db-spec
               profile-map
               (u/sel-n-upd-map :users :id_user id-user)))

;;Обновляем токен пользователя
(defn update-user-token
  "Updates token in table Users"
  [db-spec id-user token]
  (update-data db-spec
               {:user_token token}
               (u/sel-n-upd-map :users :id_user id-user)))

(defn create-user-session
  "Creates new user session"
  [db-spec
   id-user]
  (let [user-map {:id_user id-user}]
    (insert-data db-spec :user_session user-map)))

;;Деактивирует пользователя. ставит в таблице Users is_active=false
(defn deactivate-user
  "Deactivates user (updating record in table Users"
  [db-spec
   id-user]
  (update-data db-spec
               {:is_active false}
               (u/sel-n-upd-map :users :id_user id-user)))

;;Банит пользователя, запрещая ему активность на сайте
(defn ban-user
  "Bans user blocking his activity"
  [db-spec
   id-user]
  (update-data db-spec
               {:is_banned true}
               (u/sel-n-upd-map :users :id_user id-user)))

;;======================== Game  Functions =====================================

;;Получаем список всех игр
(defn get-all-games
  "Get collection of all games ever created"
  [db-spec]
  (select-all db-spec :game))

;;Получаем список игр с параметром isDeleted = false
(defn get-all-active-games
  "Get collection of all games that are currently active"
  [db-spec]
  (select-all-by-field db-spec (u/sel-n-upd-map  :game :is_deleted false)))

;;Получем список игр с параметром isPrivate = false
(defn get-all-public-games
  "Get collection of all non-private games"
  [db-spec]
  (select-cols-multi-cond db-spec
                          (u/sel-n-upd-map :game)
                          ["*"]
                          [{:field-name :is_private
                            :operation "="
                            :field-val false}
                           {:field-name :is_active
                            :operation "="
                            :field-val true}]))

;;Получаем список всех типов игр
(defn get-game-types
  "Get all available game types"
  [db-spec]
  (select-all db-spec :game_type))

;;Получаем список всех вариантов определенного типа игры
(defn get-game-variants
  [db-spec id-game-type]
  (select-all-by-field db-spec (u/sel-n-upd-map :game_variant :id_game_type id-game-type)))

;;Получаем список игр определенного типа
(defn get-games-by-variant
  "Get collection of games by variant"
  [db-spec id-game-variant]
  (select-all-by-field db-spec (u/sel-n-upd-map :game :id_game_variant id-game-variant)))

;TODO: пока не знаю как писать
;;Получаем несколько новых игр
(defn get-new-games
  "Get collection of n newest games"
  [db-spec number])

;;Получаем список игр конкретного автора
(defn get-games-by-author
  "Get collection of games by author"
  [db-spec id-author]
  (select-all-by-field db-spec (u/sel-n-upd-map :game :id_author id-author)))

;;Получаем список игр с idOriginal = null
(defn get-all-original-games
  "Get all games that are not forks"
  [db-spec]
  (select-all-by-field db-spec (u/sel-n-upd-map :game :id_original nil)))

;;Получаем список форков игры
(defn get-game-forks
  "Get all forks of a certain game"
  [db-spec id-game]
      (select-all-by-field db-spec (u/sel-n-upd-map :game :id_original id-game)))

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(defn get-game-media
  "Get all media for a certain game"
  [db-spec id-game]
      (select-all-by-field db-spec (u/sel-n-upd-map :game_media :id_game id-game)))

;;Получаем набор пользователей по данной игре
(defn get-game-users
  "Get all users for a certain game"
  [db-spec id-game]
  (select-all-by-field db-spec (u/sel-n-upd-map :game_users :id_game id-game)))

(defn get-game-by-id
  "Get game data by it's id"
  [db-spec id-game]
  (select-all-by-field db-spec (u/sel-n-upd-map :game :id_game id-game)))

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
  (let [result (insert-data db-spec :game game-info)]
    (if (nil? (:generated_key result))
      {:error-code    (:err-code err/create-game-error)
       :error-desc    (str (:err-desc err/create-game-error) game-info)}
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
  (let [result (update-data db-spec  game-info (u/sel-n-upd-map :game :id_game game-id))]
    (if (nil? (first result))
      {:error-code (:err-code err/change-game-info-error)
       :error-desc (str (:err-desc err/change-game-info-error) game-id " game-info " game-info)}
      (first result))))

(defn get-random-game
  "Return random game"
  [db-spec]
  (let [games (get-all-public-games db-spec)
        game-number (count games)
        n-games (take (rand-int game-number) games)]
    (last n-games)))

;; ================= ROOM ==================\

;;Создает новую комнату
(defn create-room
  "Creates room for a certain game"
  [db-spec room-map]
  (insert-data db-spec :room room-map))

;;Удаляет комнату (ставит is-active = false)
(defn kill-room
  "Kills certain rooom"
  [db-spec id-room]
  (let [map {:is_active true}]
    (update-data db-spec map (u/sel-n-upd-map :room :id_room id-room))))

;;Получает список активных комнат конретной игры
(defn get-room-list
  "Get room list of certain game"
  [db-spec id-game]
  (select-cols-multi-cond db-spec
                          (u/sel-n-upd-map :room)
                          ["*"]
                          [{:field-name :id_game
                            :operation "="
                            :field-val id-game}
                           {:field-name :is_active
                            :operation "="
                            :field-val true}]))

;TODO: точно ли не is EMPTY?
;;Получает текущий список пользователей в конкретной комнате
(defn get-users-in-room
  "Gets list of users in certain room"
  [db-spec id-room]
  (select-cols-multi-cond db-spec
                          (u/sel-n-upd-map :room_users)
                          ["*"]
                          [{:field-name :id_room
                            :operation "="
                            :field-val id-room}
                           {:field-name :dt_left
                            :operation "="
                            :field-val nil}]))

;;Получает чат комнаты
(defn get-chat
  "Gets chat of a certain room"
  [db-spec id-room]
  (select-cols-multi-cond db-spec
                          (u/sel-n-upd-map :chat)
                          ["*"]
                          [{:field-name :id_room
                            :operation "="
                            :field-val id-room}
                           {:field-name :dt_closed
                            :operation "="
                            :field-val nil}]))

;;Добавляет пользователя в комнату
(defn enter-room
  "Adds user to a room"
  [db-spec id-user id-room]
  (let [map {:id_room id-room
             :id_user id-user
             :dt_joined (u/now)}]
    (insert-data db-spec :room_users map)))

;;Убирает пользователя из комнаты (задавая dt-left)
(defn leave-room
  "Removes user from a room (settind dt-left)"
  [db-spec id-user id-room]
  (let [map {:dt_left (u/now)}]
    (update-data db-spec map (u/sel-n-upd-map :room_users :id_room id-room)))
  (update-data-multi-cond db-spec :game map
                          [{:field-name :is_private
                            :operation "="
                            :field-val false}
                           {:field-name :is_active
                            :operation "="
                            :field-val true}]))

;;Отправляет сообщение в чат
(defn send-message
  "Sends message to a chat"
  [db-spec id-user id-chat text]
  (let [map {:id_user      id-user
             :id_chat      id-chat
             :message_text text
             :dt_sent      (u/now)}]
    (insert-data db-spec :chat map)))

;TODO: понять как вытащить n сообщений
;;Получает список из n последних сообщений
(defn get-last-messages
  "Get list of n last messages in chat"
  [db-spec id-chat n]
  (select-all-by-field db-spec (u/sel-n-upd-map :chat :id_chat id-chat)))

;;Создает новый вопрос
(defn add-question
  "Adds question to a certain room from a certain user"
  [db-spec id-room id-user question]
  (let [map {:id_room    id-room
             :id_user    id-user
             :message    question
             :dt_created (u/now)
             :is_deleted false}]
    (insert-data db-spec :question map)))

;;Удаляет вопрос
(defn delete-question
  "Removes question"
  [db-spec id-question]
  (let [map {:is_deleted true}]
    (update-data db-spec map (u/sel-n-upd-map :question :id_question id-question))))

;;Добавляет ответ на вопрос
(defn answer-question
  "Asnwers a question"
  [db-spec id-question answer]
  (let [map {:answer      answer
             :dt_answered (u/now)}]
    (update-data db-spec  map (u/sel-n-upd-map :question :id_question id-question))))

;;Удаляет ответ на вопрос
(defn delete-answer!
  "Removes answer"
  [db-spec id-question]
  (let [map {:is_deleted true}]
    (update-data db-spec map (u/sel-n-upd-map :answer :id_question id-question))))

;;TODO: необходимо сделать функцию по получению n сообщений (вопросов, ответов, сообщений чата - пох чего)

;; ==== HighLevel-Functions ====
;; сценарий №1: регистрация пользователя
;; проверить, что логин и email дейсвительные. если все ок, зарегистрировать пользователя
(defn register-user!
  "register user with login and/or email
  (= pasword password-repeat) was checked on the client side
  (salt) was generated on the client side
  (password-hash) was generated on the client side"
  [db-spec user-info]
  (let [login-correct? (login-available? db-spec (:login user-info))
        email-correct? (email-registered? db-spec (:email user-info))
        code u/get-uuid]
    (cond
      (and login-correct? email-correct?)
      (do
        (create-user db-spec {:user_name     (:login user-info)
                              :email         (:email user-info)
                              :password_hash (:password-hash user-info)
                              :salt          (:salt user-info)
                              :dt_created    (u/now)
                              :is_active     false   ;при создании человек не активен, так как надо подтвердить email
                              :is_banned     false
                              :is_admin      false
                              :email_code    code})  ; код верификации email
        (send-email (:email user-info) code))
      (not login-correct?)
      {:error-code (:err-code err/incorrect-user-login)
       :error-desc (str (:err-desc err/incorrect-user-login) " " (:login user-info) )}
      (not email-correct?)
      {:error-code (:err-code err/incorrect-user-email)
       :error-desc (str (:err-desc err/incorrect-user-email) " " (:email user-info) )})))

;; сценарий №2: вход пользователя по логину и паролю
;; проверить, что логин и пароль дейсвительные. если все ок, отдать token пользователю
(defn login-user
  "login user with login-info - {:login :password-hash}"
  [db-spec login-info]
  (let [login-correct? (login-available? db-spec (:login login-info))
        user-info (get-user-info db-spec (:login login-info) :login)
        user-id (:id_user user-info)
        password-correct? (password-match? db-spec user-id :login (:password-hash login-info))
        user-active? (:is_active user-info)]
    (cond
      (and login-correct? password-correct? user-active?)
      (create-user-session db-spec user-id); вернуть пользователю сессию
      (not login-correct?)
      {:error-code (:err-code err/incorrect-user-login)
       :error-desc (str (:err-desc err/incorrect-user-login) " "
                        (:login login-info) )}
      (not password-correct?)
      {:error-code (:err-code err/incorrect-user-passw)
      :error-desc (:err-desc err/incorrect-user-passw)}
      (not user-active?)
      {:error-code (:err-code err/inactive-user-error)
       :error-desc (:err-desc err/inactive-user-error)})))