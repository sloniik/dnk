(ns database.chat
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]
            [korma.db :as kdb]))

;; ================= CHAT functions ==================\

;;Отправляет сообщение в чат
(defn send-message
  "Sends message to a chat"
  [id-user id-chat text]
  (let [room-map {:id_user      id-user
                  :id_chat      id-chat
                  :message_text text
                  :dt_sent      (u/now)}]
    (k/insert :chat
              (k/values room-map))))

;TODO: понять как вытащить n сообщений
;;Получает список из n последних сообщений
(defn get-last-messages
  "Get list of n last messages in chat"
  [id-chat n]
  (select-all-by-field (u/sel-n-upd-room-map :chat :id_chat id-chat)))

;;Создает новый вопрос
(defn add-question
  "Adds question to a certain room from a certain user"
  [id-room id-user question]
  (let [room-map {:id_room    id-room
                  :id_user    id-user
                  :message    question
                  :dt_created (u/now)
                  :is_deleted false}]
    (k/insert :question
              (k/values room-map))))

;;Удаляет вопрос
(defn delete-question
  "Removes question"
  [id-question]
  (let [room-map {:is_deleted true}]
    (update-data room-map (u/sel-n-upd-room-map :question :id_question id-question))))

;;Добавляет ответ на вопрос
(defn answer-question
  "Asnwers a question"
  [id-question answer]
  (let [room-map {:answer      answer
                  :dt_answered (u/now)}]
    (update-data  room-map (u/sel-n-upd-room-map :question :id_question id-question))))

;;Удаляет ответ на вопрос
(defn delete-answer!
  "Removes answer"
  [id-question]
  (let [room-map {:is_deleted true}]
    (update-data room-map (u/sel-n-upd-room-map :answer :id_question id-question))))

;;TODO: необходимо сделать функцию по получению n сообщений (вопросов, ответов, сообщений чата - пох чего)



