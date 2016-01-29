(ns database.rooms
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]
            [korma.db :as kdb]))

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
  (k/select :game
            (k/where {:id_game id-game
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
  (k/select :game
            (k/where {:id_root id-room
                      :dt_left nil})))

;;Получает чат комнаты
(defn get-chat
  "Gets chat of a certain room"
  [db-spec id-room]
  (k/select :game
            (k/where {:id_room id-room
                      :dt_closed nil})))

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



