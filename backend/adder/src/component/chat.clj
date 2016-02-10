(ns component.chat
  (:gen-class)
  (:require [database.core :as core]
            [database.chat :as chat-db]
            [component.user :as user-db]
            [database.errors :as err]
            [utilities.core :as u]))

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
  (chat-db/delete-answer question-id))

(defn last-mess
  "return n last messages or messages after some date"
  [chat-id n last-date]
  (if (= n 0)
    (chat-db/get-messages-after-date chat-id last-date)
    (chat-db/get-n-messages chat-id n)))
