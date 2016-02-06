(ns adder.database-test.rooms
  (:use [clojure.test]
        [database.rooms]
        [database.users]
        [database.core])
  (:require [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))



;; game variant
;(def g-t (:generated_key (k/insert :game_type
;          (k/values {:type_name "test game type"}))))
;
;(def g-v (:generated_key (k/insert :game_variant
;                                   (k/values {:id_game_type g-t}))))

(defn get-random-user
  "Return random game"
  []
  (let [all-users (get-all-users)
        users-count (count all-users)
        random-n (rand-int users-count)
        random-user-list (take (max 1, random-n) all-users)]
    (:id_user (last random-user-list))))


;;TODO: devArt - создать тесты комнат
(deftest users-test
  (let [game1 (create-game {:id_user_author       1
                            :id_game_variant      1
                            :title                "game 1"
                            :desc                 "very tricky game"
                            :dt_created           (u/now)
                            :game_solution        "no solution!"
                            :is_fork              false
                            ;:id_originali
                            :is_deleted           false
                            :expected_duration    7200 ;;seconds
                            :preferable_user_num  10
                            :is_private           false})]
    (testing "create-room"
      (let [room1 (create-room!  {:id_game          game1
                                  :id_user_master   1
                                  :title            "Super room"
                                  :is_private       false
                                  :has_chat         false
                                  :id_game_Variant  1
                                  :dt_start         (u/now)
                                  :dt_end           nil
                                  :is_active        true})
            room2 (create-room!  {:id_game          game1
                                  :id_user_master   1
                                  :title            "Super room"
                                  :is_private       true
                                  :has_chat         false
                                  :id_game_Variant  2
                                  :dt_start         (u/now)
                                  :dt_end           (u/now)
                                  :is_active        false})]
        (enter-room room1 (get-random-user))
        (enter-room room1 (get-random-user))
        (enter-room room1 (get-random-user))
        (enter-room room1 (get-random-user))
        (enter-room room1 (get-random-user))
        (enter-room room1 (get-random-user))
        (enter-room room2 (get-random-user))
        (enter-room room2 (get-random-user))
        (kill-room room1)

        (is (= 1 1)))))
  )
;create-game
;create-room!
;kill-room
;get-room-list
;enter-room
;leave-room
;get-users-in-room
;get-chat-id
