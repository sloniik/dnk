(ns adder.database-test.chat
  (:use [clojure.test]
        [database.chat]
        [database.rooms]
        [database.games]
        [database.core])
  (:require [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))

(deftest chat-test
  (let [user1 1
        user2 5
        room1 103
        room2 101
        chat1 (create-chat room1)
        chat2 (create-chat room2)
        chat-msg1 "Привет, как дела?"
        chat-msg2 "Все норм, как сам?"
        question1 "Это имеет отношение к заводу часов?"
        question2 "Про что речь, товарищи?"
        answer1   "Да"
        answer2   "Не имеет значения"

        msg1 (send-message user1 chat1 chat-msg1)
        msg2 (send-message user2 chat1 chat-msg2)
        msg3 (send-message user2 chat1 chat-msg1)
        ]

    (testing "create-chat"
      (println "chat1: " chat1 " chat2: " chat2)
      (is (= 1 1)))

    (testing "get-n-messages"
      (let [msgs (get-n-messages chat1 3)]
        (println msgs)))

    (testing "questions"
      (let [
            q1 (add-question room1 user1 question1)
            q2 (add-question room1 user2 question2)
            a1 (answer-question q1 answer1)
            a2 (answer-question q2 answer2)
            d1 (delete-question q1)
            d2 (delete-answer q2)
            ]
      (is (= q1 1 ))
      (is (= q2 1 ))
      (is (= a1 1 ))
      (is (= a2 1 ))
      (is (= d1 1 ))
      (is (= d2 1 ))))
    )
  )