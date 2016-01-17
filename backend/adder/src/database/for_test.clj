(ns database.for-test
  (:use database.core as :db))

(def db-spec db/pooled-db)
(db/create-user db-spec {:user_name   "devPop"
                         :passw-hash  "12345"
                         :salt        "54321"
                         :email       "devPop@test.com"
                         :dt_created  "2016-01-01"
                         :is_active   true
                         :is_banned   false
                         :is_admin     false})

(db/create-user db-spec {:user_name   "devAer"
                          :passw-hash  "abcde"
                          :salt        "edcba"
                          :email       "devArt@test.com"
                          :dt_created  "2016-01-02"
                          :is_active   true
                          :is_banned   false
                          :is_admin     false})

(db/delete-data db-spec :user_name "devPop")
(db/delete-data db-spec :user_name "devArt")
