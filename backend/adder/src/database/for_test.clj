(ns database.for-test
  (use clojure.test)
  (:require [database.core :as db]
            [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]))

;; ==== backend TESTs ====

(def root-db-spec {:classname   "com.mysql.jdbc.Driver"
                   :subprotocol "mysql"
                   :ssl?        false
                   :subname     "//127.0.0.1:3306/dnk_test"
                   :user        "root"
                   :password    "12345"})

(def root-conn (db/pool root-db-spec))

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

(def test-conn db/pooled-db)

(def user1 (db/create-user test-conn {:user_name     "test1"
                                      :password_hash "test1"
                                      :salt          "test1"
                                      :email         "test1@test.com"
                                      :dt_created    (u/now)
                                      :is_active     true
                                      :is_online     true
                                      :is_banned     true
                                      :is_admin      true}))

(def user2 (db/create-user test-conn {:user_name     "test2"
                                      :password_hash "test2"
                                      :salt          "test2"
                                      :email         "test2@test.com"
                                      :dt_created    (u/now)
                                      :is_active     true
                                      :is_online     false
                                      :is_banned     true
                                      :is_admin      true}))

(def user3 (db/create-user test-conn {:user_name     "test3"
                                      :password_hash "test3"
                                      :salt          "test3"
                                      :email         "test3@test.com"
                                      :dt_created    (u/now)
                                      :is_active     true
                                      :is_online     true
                                      :is_banned     false
                                      :is_admin      true}))

(def user4 (db/create-user test-conn {:user_name     "test4"
                                      :password_hash "test4"
                                      :salt          "test4"
                                      :email         "test4@test.com"
                                      :dt_created    (u/now)
                                      :is_active     true
                                      :is_online     true
                                      :is_banned     true
                                      :is_admin      false}))

(def user1-map (db/get-user-info-by-id test-conn user1 ))
(def user2-map (db/get-user-info-by-id test-conn user2 ))
(def user3-map (db/get-user-info-by-id test-conn user3 ))
(def user4-map (db/get-user-info-by-id test-conn user4 ))

(deftest create-user-test
  (is (= user1 1))
  (is (= user2 2))
  (is (= user3 3))
  (is (= user4 4)))

(deftest get-user-info-by-id-test
  (is (= (:user_name user1-map) "test1"))
  (is (= (:user_name user2-map) "test2"))
  (is (= (:user_name user3-map) "test3"))
  (is (= (:user_name user4-map) "test4")))

(deftest get-all-users-test
  (is (= (first (db/get-all-users test-conn))
         user1-map))
  (is (= (first (rest (db/get-all-users test-conn)))
         user2-map))
  (is (= (first (rest (rest (db/get-all-users test-conn))))
         user3-map))
  (is (= (first (rest (rest (rest (db/get-all-users test-conn)))))
         user4-map))
  (is (empty? (rest (rest (rest (rest (db/get-all-users test-conn)))))))
  )

(deftest get-user-info-by-login-test
  (is (= (db/get-user-info-by-login test-conn (user1-map :user_name))
         user1-map))
  (is (not= (db/get-user-info-by-login test-conn (user2-map :user_name))
            user1-map)))

(deftest get-user-info-by-id-test
  (is (= (db/get-user-info-by-id test-conn (user1-map :id_user))
         user1-map))
  (is (not= (db/get-user-info-by-id test-conn (user2-map :id_user))
            user1-map)))

(deftest get-user-info-test
  (is (= (db/get-user-info test-conn (user1-map :user_name) :login)
         user1-map))
  (is (not= (db/get-user-info test-conn (user2-map :user_name) :login)
            user1-map))
  (is (= (db/get-user-info test-conn (user1-map :email) :email)
         user1-map))
  (is (not= (db/get-user-info test-conn (user2-map :email) :email)
            user1-map))
  )
;(deftest all-users
;  (is (db/get-all-users test-conn)))

;(db/select-cols-multi-cond test-conn
;                           "users"
;                           ["id_user" "user_name" "salt"]
;                           [{:field-name "user_name" :operation "=" :field-val "test"}
;                            {:field-name "email" :operation "like" :field-val "abs%"}])

;; ==== other TESTs ====

;(jdbc/query test-conn ["select name, cost from fruit where appearance = ?" "rosy"]);

;(db/select-col test-conn (u/sel-n-upd-map  "fruit" "cost"))
;(jdbc/query test-conn [(str "select name from fruit where cost = ?") 24])
;(db/select-col-by-field test-conn (u/sel-n-upd-map "fruit" "name" "cost" 24))


;(db/select-all test-conn "fruit")
;(db/insert-data test-conn :fruit {:name "Cactus" :appearance "Spiky" :cost 2000 :flag true})


;; ==== TESTs ====
;;(db/select-all test-conn "fruit")