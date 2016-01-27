;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :refer [select fields where insert values update set-fields delete]]
            [korma.db :as kdb]))


(def db (kdb/mysql
                {:classname   "com.mysql.jdbc.Driver"
                 :subprotocol "mysql"
                 :ssl?        false
                 :subname     "//127.0.0.1:3306/dnk_test"
                 :user        "dnk_test"
                 :password    "dnk_test"
                 :make-pool?  true}))
(kdb/defdb korma-db db)

;; === MAIN_DB_FUNCTIONs ===
(defn select-col
  "return specific column from table"
  [db-spec select-map]
    (jdbc/query db-spec [(str "select " (name (:col-name select-map))
                              " from "  (name (:table-name select-map)))]))

;;korma-mod
(defn k-select-col
  [select-map]
  (let [t-n (:table-name select-map)
        c-n (:col-name   select-map)]
  (select t-n
            (fields c-n))))
;(k-select-col (u/sel-n-upd-map  :fruit :name))

(defn select-all
  "return all values from table "
  [db-spec table-name]
  (select-col db-spec (u/sel-n-upd-map (name table-name) "*")))

;;korma-mod
(defn k-select-all
  [table-name]
  (select table-name))
;(k-select-all :fruit)

(defn select-col-by-field
  "return specific column from table where field-name = field-val"
  [db-spec select-map]
  (jdbc/query db-spec
              [(str "select " (name (:col-name   select-map))
                    " from "  (name (:table-name select-map))
                    " where " (name (:field-name select-map)) " = ?")
               (:field-val select-map)]))
;;korma-mod
(defn k-select-col-by-field
  [select-map]
  (let [t-n (:table-name select-map)
        c-n (:col-name select-map)
        f-n (:field-name select-map)
        f-v (:field-val select-map)]
    (select t-n
              (fields c-n)
              (where (= f-n f-v)))))
(k-select-col-by-field  (u/sel-n-upd-map :fruit :name :cost 2000))

(defn select-all-by-field
  "return all values from specified table with col-id-name and id-value"
  [db-spec select-map]
  (select-col-by-field db-spec (u/sel-n-upd-map (:table-name select-map)
                                                "*"
                                                (:field-name select-map)
                                                (:field-val select-map))))
;;korma-mod
(defn k-select-all-by-field
  [select-map]
  (let [t-n (:table-name select-map)
   f-n (:field-name select-map)
   f-v (:field-val select-map)]
  (select t-n
            (where (= f-n f-v)))))
;(k-select-all-by-field (u/sel-n-upd-map :users :id_user 2))

;;==DEPRICATED
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

;;korma-mod
(defn k-insert-data
  [table-name new-record-map]
  (insert table-name
            (values new-record-map)))
;(k-insert-data :fruit {:name "Забор" :appearance "Острый" :cost 100 :flag true})

(defn update-data
  "update string (update-record-map) in the table (table-name-key) where update-cond-map (:table-name :field-name field-val)"
  [db-spec update-record-map update-cond-map]
  (jdbc/update! db-spec
                (name (:table-name update-cond-map))
                update-record-map
                [(str (name (:field-name update-cond-map)) " = ? ") (:field-val update-cond-map)]))

;;korma-mod
(defn k-update-data
  [update-record-map update-cond-map]
  (let [t-n (:table-name update-cond-map)
        f-n (:field-name update-cond-map)
        f-v (:field-val update-cond-map)]
      (update t-n
                (set-fields update-record-map)
                (where (= f-n f-v)))))
;(k-update-data {:cost 2000} (u/sel-n-upd-map :fruit :id_name 14))

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

;;korma-mod
(defn k-delete-data
  [update-map]
  (let [t-n (:table-name update-map)
        f-n (:field-name update-map)
        f-v (:field-val update-map)]
    (delete t-n
              (where (= f-n f-v)))))
;(k-delete-data (u/sel-n-upd-map :users :user_name "devAer"))

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
  (select :game
            (where {:is_private false
                       :is_active true})))
  ;(select-cols-multi-cond db-spec
  ;                        (u/sel-n-upd-map :game)
  ;                        ["*"]
  ;                        [{:field-name :is_private
  ;                          :operation "="
  ;                          :field-val false}
  ;                         {:field-name :is_active
  ;                          :operation "="
  ;                          :field-val true}]))

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
  (select :game
            (where {:id_game id-game
                      :is_active true})))
  ;(select-cols-multi-cond db-spec
  ;                        (u/sel-n-upd-map :room)
  ;                        ["*"]
  ;                        [{:field-name :id_game
  ;                          :operation "="
  ;                          :field-val id-game}
  ;                         {:field-name :is_active
  ;                          :operation "="
  ;                          :field-val true}]))

;TODO: точно ли не is EMPTY?
;;Получает текущий список пользователей в конкретной комнате
(defn get-users-in-room
  "Gets list of users in certain room"
  [db-spec id-room]
  (select :game
            (where {:id_root id-room
                      :dt_left nil})))
  ;(select-cols-multi-cond db-spec
  ;                        (u/sel-n-upd-map :room_users)
  ;                        ["*"]
  ;                        [{:field-name :id_room
  ;                          :operation "="
  ;                          :field-val id-room}
  ;                         {:field-name :dt_left
  ;                          :operation "="
  ;                          :field-val nil}]))

;;Получает чат комнаты
(defn get-chat
  "Gets chat of a certain room"
  [db-spec id-room]
  (select :game
            (where {:id_room id-room
                      :dt_closed nil})))
  ;(select-cols-multi-cond db-spec
  ;                        (u/sel-n-upd-map :chat)
  ;                        ["*"]
  ;                        [{:field-name :id_room
  ;                          :operation "="
  ;                          :field-val id-room}
  ;                         {:field-name :dt_closed
  ;                          :operation "="
  ;                          :field-val nil}]))

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
    (update-data db-spec map (u/sel-n-upd-map :room_users :id_room id-room))))

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


(defn selc
  [table-name col-name text]
  (select table-name
             (where {col-name [like (str text "%")]})
             ))

(selc :fruit :name "Ca")