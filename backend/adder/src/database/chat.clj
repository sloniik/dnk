(ns database.chat
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))

;; ================= CHAT functions ==================\

;; Создание чата, в случае если у room has-chat = true
(defn create-chat
  [room-id]
  (let [chat-map {:id_room room-id
                  :dt_created (u/now)
                  }
        new-chat (k/insert :chat
                         (k/values chat-map))]
    (:generated_key new-chat)))

;;Отправляет сообщение в чат
(defn send-message
  "Sends message to a chat"
  [user-id chat-id text]
  (let [room-map {:id_user      user-id
                  :id_chat      chat-id
                  :message_text text
                  :dt_sent      (u/now)}]
    (k/insert :chat_Message
              (k/values room-map))))

;;Получает список из n последних сообщений
(defn get-n-messages
  "Get list of n last messages in chat"
  [chat-id n]
  (k/select :chat_Message
            (k/fields :id_user :message_text :dt_sent)
            (k/where
              (and
                (= :id_chat chat-id)
                (= :is_deleted nil)))
            (k/order :dt_sent :desc)
            (k/limit n)))

(defn get-messages-after-date
  "Get list of n last messages in chat"
  [chat-id last-update]
  (k/select :chat_Message
            (k/fields :id_user :message_text :dt_sent)
            (k/where
              (and
                (= :id_chat chat-id)
                (= :is_deleted nil)
                (> :dt_sent last-update)))
            (k/order :dt_sent :desc)))

;;Создает новый вопрос
(defn add-question
  "Adds question to a certain room from a certain user"
  [room-id user-id question]
  (let [room-map {:id_room      room-id
                  :id_user      user-id
                  :message_text question
                  :dt_created   (u/now)
                  :is_deleted   false}]
    (k/insert :question
              (k/values room-map))))

;;Удаляет вопрос
(defn delete-question
  "Removes question"
  [question-id]
  (let [room-map {:is_deleted true}]
    (k/update :question
              (k/set-fields room-map)
              (k/where (= :id_question question-id)))))

;;Добавляет ответ на вопрос
(defn answer-question
  "Asnwers a question"
  [question-id answer]
  (let [room-map {:answer      answer
                  :dt_answered (u/now)}]
    (k/update :question
              (k/set-fields room-map)
              (k/where (= :id_question question-id)))))

;;Удаляет ответ на вопрос
(defn delete-answer
  "Removes answer"
  [question-id]
  (let [room-map {:is_deleted true}]
    (k/update :answer
              (k/set-fields room-map)
              (k/where (= :id_question question-id)))))

