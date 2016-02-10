(ns component.chat
  (:gen-class)
  (:require [database.chat :as chat-db]))

(defn create
  "Create chat in a room"
  [room-id]
  (chat-db/create-chat room-id))

(defn send-mess
  "send message to chat"
  [user-id chat-id text]
  (chat-db/send-message user-id chat-id text))

(defn ask
  "ask question in a room"
  [room-id user-id question]
  (chat-db/add-question room-id user-id question))

(defn answer
  "answer question"
  [question-id answer]
  (chat-db/answer-question question-id answer))

(defn delete
  "delete question or answer"
  [question-id]
  (chat-db/delete-question question-id))

(defn last-mess
  "return n last messages or messages after some date
  :m - chat message
  :q - questions
  :a - answers
  if n == nil then should use last-date, else - use n"
  [chat-id n last-date message-type]
  (cond
    (= message-type :m)
    (let [messages (if (nil? n)
                     (chat-db/get-last-messages chat-id last-date)
                     (chat-db/get-n-messages chat-id n))]
      messages)
    (= message-type :q)
    (let [messages
          (if (nil n)
            (chat-db/get-last-questions chat-id last-date)
            (chat-db/get-n-questions chat-id n))
          questions (map #(dissoc % :answer) messages)]
      questions)
    (= message-type :a)
    (let [messages
          (if (nil? n)
            (chat-db/get-last-questions chat-id last-date)
            (chat-db/get-n-questions chat-id n))
          answers (map #(dissoc % :question) messages)]
      answers)))

