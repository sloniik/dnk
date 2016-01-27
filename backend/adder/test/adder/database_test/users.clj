(ns adder.database_test.users
  (:use [clojure.test]
        [database.users])
  (:require [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.db :as k]))

;; ==== backend TESTs ====

;(def root-db-spec {:classname   "com.mysql.jdbc.Driver"
;                   :subprotocol "mysql"
;                   :ssl?        false
;                   :subname     "//127.0.0.1:3306/dnk_test"
;                   :user        "root"
;                   :password    "12345"})
;
;(def root-conn (db/pool root-db-spec))

;(defn exec-sql-file
;  [db-spec file]
;  (let [commands (str/split
;                   (slurp file) #";")]
;    (println (take 2 commands))
;    (jdbc/execute!
;      db-spec
;      commands
;      :multi? true
;      :transaction? true)))



;;Пока не работает
;(exec-sql-file root-db-spec (io/resource "dnk.sql"))

;(def test-conn db/pooled-db)

(def id-user1
      (create-user {:user_name     "test1"
                    :password_hash "test1"
                    :salt          "test1"
                    :email         "test1@test.com"
                    :dt_created    (u/now)
                    :is_active     true
                    :is_online     true
                    :is_banned     true
                    :is_admin      true}))

(def id-user2
      (create-user {:user_name     "test2"
                    :password_hash "test2"
                    :salt          "test2"
                    :email         "test2@test.com"
                    :dt_created    (u/now)
                    :is_active     false
                    :is_online     true
                    :is_banned     true
                    :is_admin      true}))

(def id-user3
      (create-user {:user_name     "test3"
                    :password_hash "test3"
                    :salt          "test3"
                    :email         "test3@test.com"
                    :dt_created    (u/now)
                    :is_active     true
                    :is_online     false
                    :is_banned     true
                    :is_admin      true}))

(def id-user4
      (create-user {:user_name     "test4"
                    :password_hash "test4"
                    :salt          "test4"
                    :email         "test4@test.com"
                    :dt_created    (u/now)
                    :is_active     true
                    :is_online     true
                    :is_banned     false
                    :is_admin      true}))

(def id-user5
      (create-user {:user_name     "test5"
                    :password_hash "test5"
                    :salt          "test5"
                    :email         "test5@test.com"
                    :dt_created    (u/now)
                    :is_active     true
                    :is_online     true
                    :is_banned     true
                    :is_admin      false}))

(def user1-list (get-user-info-by-id id-user1))
(def user2-list (get-user-info-by-id id-user2))
(def user3-list (get-user-info-by-id id-user3))
(def user4-list (get-user-info-by-id id-user4))
(def user5-list (get-user-info-by-id id-user5))

(def user1-map (first user1-list))
(def user2-map (first user2-list))
(def user3-map (first user3-list))
(def user4-map (first user4-list))
(def user5-map (first user5-list))

(deftest create-user-test
  (is (= id-user1 1))
  (is (= id-user2 2))
  (is (= id-user3 3))
  (is (= id-user4 4))
  (is (= id-user4 4))
  (is (= id-user5 5))

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
  )

(deftest get-all-users-test
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

(deftest get-user-info-by-login-test
  (is (= (first (get-user-info-by-login (user1-map :user_name)))
         user1-map))
  (is (not= (first (get-user-info-by-login (user2-map :user_name)))
            user1-map)))

(deftest get-user-info-by-id-test
  (is (= (first (get-user-info-by-id (user1-map :id_user)))
         user1-map))
  (is (not= (first (get-user-info-by-id (user2-map :id_user)))
            user1-map)))

(deftest get-user-info-by-mail-test
  (is (= (first (get-user-info-by-mail (user1-map :email)))
         user1-map))
  (is (not= (first (get-user-info-by-mail (user2-map :email)))
            user1-map)))

(deftest get-user-salt-test
  (is (= (get-user-salt id-user1)
         (:salt user1-map)))
  (is (= (get-user-salt id-user2)
         (:salt user2-map)))
  (is (= (get-user-salt id-user3)
         (:salt user3-map)))
  (is (= (get-user-salt id-user4)
         (:salt user4-map)))
  (is (= (get-user-salt id-user5)
         (:salt user5-map)))
  )
