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
                                      :is_active     false
                                      :is_online     true
                                      :is_banned     true
                                      :is_admin      true}))

(def user3 (db/create-user test-conn {:user_name     "test2"
                                      :password_hash "test2"
                                      :salt          "test2"
                                      :email         "test2@test.com"
                                      :dt_created    (u/now)
                                      :is_active     false
                                      :is_online     true
                                      :is_banned     true
                                      :is_admin      true}))

(def user4 (db/create-user test-conn {:user_name     "test2"
                                      :password_hash "test2"
                                      :salt          "test2"
                                      :email         "test2@test.com"
                                      :dt_created    (u/now)
                                      :is_active     false
                                      :is_online     true
                                      :is_banned     true
                                      :is_admin      true}))

(deftest user-creation
  (is (= (:generated_key user1) 1))
  (is (= (:generated_key user2) 2))
  (is (= (:generated_key user3) 3))
  (is (= (:generated_key user4) 4)))

(deftest all-users
  (is (db/get-all-users test-conn)))

(db/select-cols-multi-cond test-conn
                           "users"
                           ["id_user" "user_name" "salt"]
                           [{:field-name "user_name" :operation "=" :field-val "test"}
                            {:field-name "email" :operation "like" :field-val "abs%"}])

;; ==== other TESTs ====

(jdbc/query test-conn ["select name, cost from fruit where appearance = ?" "rosy"])

(db/select-col test-conn (u/sel-n-upd-map  "fruit" "cost"))
(jdbc/query test-conn [(str "select name from fruit where cost = ?") 24])
(db/select-col-by-field test-conn (u/sel-n-upd-map "fruit" "name" "cost" 24))


(db/select-all test-conn "fruit")
(db/insert-data test-conn :fruit {:name "Cactus" :appearance "Spiky" :cost 2000 :flag true})


;; ==== TESTs ====
;;(db/select-all test-conn "fruit")
