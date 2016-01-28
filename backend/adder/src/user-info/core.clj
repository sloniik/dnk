(ns user-info.core
  (:use database.core :as db)
  (:use utilities.core :as util))



(defn login-user
  "function gets login, password and check if user is eligible"
  [login password]
  (db/check-user login (passw-hash password)))

;; #TBR - key to search as login
(def login-key-word :name)

(defn get-list-of-user-logins
  "forms list of users' logins"
  [u-l]
  (map #(get % login-key-word ) u-l))

(defn user-exist?
  "checks if user already exist in database_test"
  [login]
  (let [u-l (get-list-of-user-logins (db/get-user-list))]
    (util/elem-in-col? login u-l)))

(defn create-user!
  "creates user in database_test"
  [params]
  (if (user-exist? {:login params})
    {:status 101
     :header ""
     :body "User is already in the list"}
    ))
  ;;(create new id))
