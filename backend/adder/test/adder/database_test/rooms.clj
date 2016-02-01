(ns adder.database-test.rooms
  (:use [clojure.test]
        [database.users]
        [database.core])
  (:require [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.db :as k]))



(create-game {:id_user_author       1
              :id_game_variant      1
              :title                "game 1"
              :desc                 "very tricky game"
              :dt_created           (u/now)
              :game_solution        "no solution!"
              :is_fork              false
              ;:id_original
              :is_deleted           false
              :expected_duration    7200 ;;seconds
              :preferable_user_num  10
              :is_private           false})
;;TODO: devArt - создать тесты комнат
(deftest users-test
  (let [game1 (create-game {:id_user_author       1
                            :id_game_variant      1
                            :title                "game 1"
                            :description          "very tricky game"
                            :dt_created           (u/now)
                            :game_solution        "no solution!"
                            :is_fork              false
                            ;:id_original
                            :is_deleted           false
                            :expected_duration    7200 ;;seconds
                            :preferable_user_num  10
                            :is_private           false})]
    (testing "create-room"
      (is (= 1 1))))
    )
;create-game
;create-room
;kill-room
;get-room-list
;enter-room
;leave-room
;get-users-in-room
;get-chat-id
