(ns database.for-test
  (:require [database.core :as db]
            [clojure.java.jdbc :as jdbc]))

;; ==== backend TESTs ====

(def p-db  db/pooled-db)

(db/create-user p-db {:user_name   "devPop"
                        :password_hash  "12345"
                        :salt        "54321"
                        :email       "devPop@test.com"
                        :dt_created  "2016-01-01"
                        :is_active   true
                        :is_online   false
                        :is_banned   false
                        :is_admin     false})

(db/create-user p-db {:user_name   "devAer"
                        :password_hash  "abcde"
                        :salt        "edcba"
                        :email       "devArt@test.com"
                        :dt_created  "2016-01-02"
                        :is_online   false
                        :is_active   true
                        :is_banned   false
                        :is_admin     false})

(db/delete-data p-db user-table "user_name" "devPop")
(db/delete-data p-db user-table "user_name" "devArt")














;; ==== other TESTs ====
(db/select-all-values-from-table p-db "fruit")

(jdbc/query p-db
            ["select name, cost from fruit where appearance = ?" "rosy"])

(db/select-col-from-table p-db "fruit" "cost")
(jdbc/query p-db [(str "select name from fruit where cost = ?" ) 24])
(db/select-col-from-table-by-field p-db "fruit" "name" "cost" 24)


(db/select-all-values-from-table p-db "fruit")
(db/insert-data p-db :fruit
             {:name "Cactus" :appearance "Spiky" :cost 2000 :flag true})


;; ==== TESTs ====
(db/select-all-values-from-table p-db "fruit")

(db/select-col-from-table-new p-db "fruit" "name")

(jdbc/query p-db [(str "select " "*" " from " "fruit" " where " "cost" " = ?") "24"])

(db/select-col-from-table p-db "fruit" "cost")
(jdbc/query p-db [(str "select name from fruit where cost = ?" ) 24])
(db/select-col-from-table-by-field p-db "fruit" "name" "cost" 24)