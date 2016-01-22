(ns database.for-test
  (:use database.core as db))


;; ==== backend TESTs ====

(def pooled-db  db/pooled-db)

(create-user pooled-db {:user_name   "devPop"
                        :password_hash  "12345"
                        :salt        "54321"
                        :email       "devPop@test.com"
                        :dt_created  "2016-01-01"
                        :is_active   true
                        :is_online   false
                        :is_banned   false
                        :is_admin     false})

(create-user pooled-db {:user_name   "devAer"
                        :password_hash  "abcde"
                        :salt        "edcba"
                        :email       "devArt@test.com"
                        :dt_created  "2016-01-02"
                        :is_online   false
                        :is_active   true
                        :is_banned   false
                        :is_admin     false})

(delete-data pooled-db user-table "user_name" "devPop")
(delete-data pooled-db user-table "user_name" "devArt")














;; ==== other TESTs ====
(select-all-values-from-table pooled-db "fruit")

(jdbc/query pooled-db
            ["select name, cost from fruit where appearance = ?" "rosy"])

(defn select-col-from-table-new
  "return specific column from table"
  [db-spec table-name col-name]
  (jdbc/with-db-connection [t-con db-spec]
                           (println (jdbc/db-connection t-con))
                           (jdbc/query t-con [(str "select " col-name " from " table-name)])))



(select-col-from-table-new db-spec "fruit" "name")
(def a (select-col-from-table pooled-db "fruit" "name"))
(def b (select-col-from-table pooled-db "fruit" "cost"))

(jdbc/query pooled-db [(str "select " "*" " from " "fruit" " where " "cost" " = ?") "24"])
(select-all-values-from-table-by-field db-spec "fruit" "cost" 24)

(select-col-from-table pooled-db "fruit" "cost")
(jdbc/query pooled-db [(str "select name from fruit where cost = ?" ) 24])
(select-col-from-table-by-field pooled-db "fruit" "name" "cost" 24)



(defn update-or-insert!
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
                            (let [result (jdbc/update! t-con table row where-clause)]
                              (println t-con)
                              (println db)
                              (if (zero? (first result))
                                (jdbc/insert! t-con table row)
                                result))))

(update-or-insert! db-spec :fruit
                   {:name "Cactus" :appearance "Spiky" :cost 2000}
                   ["name = ?" "Cactus"])



(select-all-values-from-table pooled-db "fruit")
(insert-data pooled-db :fruit
             {:name "Cactus" :appearance "Spiky" :cost 2000 :flag true})


(def aaa (update-data pooled-db :fruit
                      {:name "Кактус":appearance "Very Spikey"}
                      "id_name" 2))

;; ==== TESTs ====
(select-all-values-from-table pooled-db "fruit")

(jdbc/query pooled-db
            ["select name, cost from fruit where appearance = ?" "rosy"])

(defn select-col-from-table-new
  "return specific column from table"
  [db-spec table-name col-name]
  (jdbc/with-db-connection [t-con db-spec]
                           (println (jdbc/db-connection t-con))
                           (jdbc/query t-con [(str "select " col-name " from " table-name)])))



(select-col-from-table-new pooled-db "fruit" "name")
(def a (select-col-from-table pooled-db "fruit" "name"))
(def b (select-col-from-table pooled-db "fruit" "cost"))

(jdbc/query pooled-db [(str "select " "*" " from " "fruit" " where " "cost" " = ?") "24"])
(select-all-values-from-table-by-field db-spec "fruit" "cost" 24)

(select-col-from-table pooled-db "fruit" "cost")
(jdbc/query pooled-db [(str "select name from fruit where cost = ?" ) 24])
(select-col-from-table-by-field pooled-db "fruit" "name" "cost" 24)

(defn update-or-insert!
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
                            (let [result (jdbc/update! t-con table row where-clause)]
                              (println t-con)
                              (println db)
                              (if (zero? (first result))
                                (jdbc/insert! t-con table row)
                                result))))

(update-or-insert! db-spec :fruit
                   {:name "Cactus" :appearance "Spiky" :cost 2000}
                   ["name = ?" "Cactus"])
;; inserts Cactus (assuming none exists)
;(update-or-insert! mysql-db :fruit
;                   {:name "Cactus" :appearance "Spiky" :cost 2500}
;                   ["name = ?" "Cactus"])
;;; updates the Cactus we just inserted

