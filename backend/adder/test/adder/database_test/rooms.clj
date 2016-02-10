(ns adder.database-test.rooms
  (:use [clojure.test]
        [database.rooms]
        [database.users]
        [database.games]
        [database.core]
        [component.room])
  (:require [utilities.core :as u]))

;; game variant
;(def g-t (:generated_key (k/insert :game_type
;          (k/values {:type_name "test game type"}))))
;
;(def g-v (:generated_key (k/insert :game_variant
;                                   (k/values {:id_game_type g-t}))))

;;TODO: devArt - создать тесты комнат
(deftest users-test
  (let [user1 1
        user2 5
        game1 (create-game {:id_user_author       user1
                            :id_game_variant      1
                            :title                "game 1"
                            :desc                 "very tricky game 1"
                            :dt_created           (u/now)
                            :game_solution        "no solution! #1"
                            :is_fork              false
                            ;:id_originali
                            :is_deleted           false
                            :expected_duration    7200 ;;seconds
                            :preferable_user_num  10
                            :is_private           false})
        game2 (create-game {:id_user_author       user1
                            :id_game_variant      1
                            :title                "game 2"
                            :desc                 "very very tricky game 2"
                            :dt_created           (u/now)
                            :game_solution        "no solution! #2"
                            :is_fork              true
                            :id_original          game1
                            :is_deleted           false
                            :expected_duration    7200 ;;seconds
                            :preferable_user_num  10
                            :is_private           true})
        room1 (create-room  {:id_game          game1
                             :id_user_master   1
                             :title            "Super room"
                             :is_private       false
                             :has_chat         false
                             :id_game_Variant  1
                             :dt_start         (u/now)
                             :dt_end           nil
                             :is_active        true})
        room2 (create-room  {:id_game          game1
                             :id_user_master   1
                             :title            "Super room"
                             :is_private       true
                             :has_chat         false
                             :id_game_Variant  2
                             :dt_start         (u/now)
                             :dt_end           (u/now)
                             :is_active        false})
        user-ent-1  (enter room1 user1)
        user-ent-1  (enter room1 user2)
        user-ent-2  (enter room2 user2)]

    (testing "create-room"
      (println "room1: " room1 " room2: " room2)
      (is (= 1 1)))

    (testing "enter-room"
      (let [users-in-room (get-users room1)]
        (println "users-in-room" users-in-room)
        (println "users-in-room" [user1 user2])
        (is (= (count-users room1) 2))
        (is (= (count-users room2) 1))
        (is (= true (user-in-room? room1 user1)))
        (is (= true (user-in-room? room1 user2)))
        (is (= true (user-in-room? room2 user2)))
        (is (= users-in-room [user1 user2]))))

    (testing "get-room-list"
      (let [n-room-game1 (count-rooms game1)
            n-room-game2 (count-rooms game2)]
        (is (= n-room-game1 1))
        (is (= n-room-game2 1))))

    (testing "leave-room"
      (let [left-user (kick-user room2 user2)]
        (println "left-user: " left-user)))

    (testing "kill-room"
      (let [killed-room (kill room1)]
        (println killed-room)
        (is (= killed-room 1)))))
  )
