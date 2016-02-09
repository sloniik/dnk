;; ==== backend USERS TESTs ====

(ns adder.database_test.users
  (:use [clojure.test]
        [component.user]
        [database.users])
  (:require [utilities.core :as u]))

(deftest users-test
  (let [
        id-user1 (create-user {:user_name     "test1"
                               :password_hash "test1"
                               :salt          "test1"
                               :email         "test1@test.com"
                               :dt_created    (u/now)
                               :is_active     true
                               :is_online     true
                               :is_banned     true
                               :is_admin      true})
        id-user2 (create-user {:user_name     "test2"
                               :password_hash "test2"
                               :salt          "test2"
                               :email         "test2@test.com"
                               :dt_created    (u/now)
                               :is_active     false
                               :is_online     true
                               :is_banned     true
                               :is_admin      true})
        id-user3 (create-user {:user_name     "test3"
                               :password_hash "test3"
                               :salt          "test3"
                               :email         "test3@test.com"
                               :dt_created    (u/now)
                               :is_active     true
                               :is_online     false
                               :is_banned     true
                               :is_admin      true})
        id-user4 (create-user {:user_name     "test4"
                               :password_hash "test4"
                               :salt          "test4"
                               :email         "test4@test.com"
                               :dt_created    (u/now)
                               :is_active     true
                               :is_online     true
                               :is_banned     false
                               :is_admin      true})
        id-user5 (create-user {:user_name     "test5"
                               :password_hash "test5"
                               :salt          "test5"
                               :email         "test5@test.com"
                               :dt_created    (u/now)
                               :is_active     true
                               :is_online     true
                               :is_banned     true
                               :is_admin      false})

        user1-list (get-user-info-by-id id-user1)
        user2-list (get-user-info-by-id id-user2)
        user3-list (get-user-info-by-id id-user3)
        user4-list (get-user-info-by-id id-user4)
        user5-list (get-user-info-by-id id-user5)

        user1-map (first user1-list)
        user2-map (first user2-list)
        user3-map (first user3-list)
        user4-map (first user4-list)
        user5-map (first user5-list)
        ]

    (testing "get-all-users"
      (is (empty? (rest user1-list)))
      (is (empty? (rest user2-list)))
      (is (empty? (rest user3-list)))
      (is (empty? (rest user4-list)))
      (is (empty? (rest user5-list)))

      (is (= (:user_name (first user1-list)) "test1"))
      (is (= (:user_name (first user2-list)) "test2"))
      (is (= (:user_name (first user3-list)) "test3"))
      (is (= (:user_name (first user4-list)) "test4"))
      (is (= (:user_name (first user5-list)) "test5"))

      (is (= (first (get-all-users))
             user1-map))
      (is (= (first (rest (get-all-users)))
             user2-map))
      (is (= (first (rest (rest (get-all-users))))
             user3-map))
      (is (= (first (rest (rest (rest (get-all-users)))))
             user4-map))
      (is (= (first (rest (rest (rest (rest (get-all-users))))))
             user5-map))
      (is (empty? (rest (rest (rest (rest (rest (get-all-users))))))))
      )

    (testing "get-user-info-by-login"
      (is (= (first (get-user-info-by-login (user1-map :user_name)))
             user1-map))
      (is (not= (first (get-user-info-by-login (user2-map :user_name)))
                user1-map)))

    (testing "get-user-info-by-mail"
      (is (= (first (get-user-info-by-mail (user1-map :email)))
             user1-map))
      (is (not= (first (get-user-info-by-mail (user2-map :email)))
                user1-map))
      )


    (testing "get-user-salt"
      (is (= (get-salt id-user1)
             (:salt user1-map)))
      (is (= (get-salt id-user2)
             (:salt user2-map)))
      (is (= (get-salt id-user3)
             (:salt user3-map)))
      (is (= (get-salt id-user4)
             (:salt user4-map)))
      (is (= (get-salt id-user5)
             (:salt user5-map)))
      )

    (testing "get-pass"
      (is (= (get-pass id-user1)
             (:password_hash user1-map)))
      (is (= (get-pass id-user2)
             (:password_hash user2-map)))
      (is (= (get-pass id-user3)
             (:password_hash user3-map)))
      (is (= (get-pass id-user4)
             (:password_hash user4-map)))
      (is (= (get-pass id-user5)
             (:password_hash user5-map)))
      )
    )
  )

(deftest test-media
  (testing "add-media-types"
    (let [test-media-id1 (add-media-type {:media_type_name "user avatar"
                                          :is_active       true})
          test-media1 (first (get-media-type-by-id test-media-id1))
          test-media-id2 (add-media-type {:media_type_name "game avatar"
                                          :is_active       true})
          test-media2 (first (get-media-type-by-id test-media-id2))
          test-media-id3 (add-media-type {:media_type_name "room avatar"
                                          :is_active       true})
          test-media3 (first (get-media-type-by-id test-media-id3))
          test-media-id4 (add-media-type {:media_type_name "trash picture"
                                          :is_active       false})
          test-media4 (first (get-media-type-by-id test-media-id4))
          all-active-media-types (conj [] test-media1 test-media2 test-media3)
          all-media-types (conj all-active-media-types test-media4)
          media-types_small (into [] (take-last 3 (get-media-types)))
          media-types_full (into [] (take-last 4 (get-all-media-types)))
          avatar (first (get-media-type-by-id test-media-id3))
          ]
      ;(println avatar)
      (is (= (:media_type_name avatar) "room avatar"))
      (is (= all-active-media-types media-types_small))
      (is (= all-media-types media-types_full))
      (is (= avatar test-media3))
      ))
  )