(ns component.user
  (:gen-class)
  (:use user-constuser-info.user-const :as us-in)
  (:use user-info.user-const :as uc)
  (:use database.core :as db))

;; Util functions
(defn create-new-user [nickname email]
  let [user-init-profile {:name nickname
                          :email  email
                          :active? false
                          :ban? false
                          :seen-last-time 0
                          :registration-date java.util.Date}]
  user-init-profile)

(defn user-exist?
  "checks if user already exist in database"
  [login]
  (let [u-l (get-list-of-user-logins (db/get-user-list))]
    (util/elem-in-col? login u-l)))

(defn get-list-of-user-logins
  "forms list of users' logins"
  [u-l]
  (map #(get % login-key-word ) u-l))


;; Functions implementation:
(defn -create-user
  [user-db nickname email]
  (let [user-init-profile (create-new-user nickname email)]
    (db/update user-db user-init-profile)
    (println "here I create user " nickname)))

(defn -change-profile-basic-info
  [user-db user-profile-data]
  (println "here I change profile basic info" user-profile-data))

(defn -change-profile-password
  [user-db user-password]
  (println "here I change passeo" user-password))

(defn -add-profile-photo
  [user-db user-photo])

(defn -get-user-list
  [user-db])

(defn -deactivate-user
  [user-db user-email])

(defn -ban-user
  [user-db user-email])

(defn -login-user
  [user-db user-credentials])

;; protocol description
(defprotocol UserManagerProtocol
  (create-user [this nickname email]
    "create user in database.
    return map  {:status :details}.")

  (change-profile-basic-info [this user-profile-data]
    "change user profile data.
    get map     {:nick-name :user-email}
    return map  {:status :details}.")

  (change-profile-password [this user-password]
    "change user profile data.
    get map     {:old-password :new-password :new-password-repeat}
    return map  {:status :details}.")

  (add-profile-photo [this user-photo]
    "change user profile photo.
    get file with user-photochange user profile data.
    get map     {:nick-name :user-email}
    return map  {:status :details}.")

  (get-user-list [this]
  "get user list from database
  get conn
  return coll of map {:status
                      :details {:nick-name :active? :ban? :seen-last-time :registration-date}}")

  (deactivate-user [this user-email]
    "deactivate user in user-list
    get user-email
    return map  {:status :details}.
    --if user is deactivated they can't login")

  (ban-user [this user-email]
    "ban user
    get user-email
    return map  {:status :details}.
    --if user is banned they can't write questions")

  (login-user [this user-credentials]
    "login user
    get map {:user-email :user-password}
    return map  {:status :details}."))

;; protocol implementation
(defrecord UserManager [user-db]
  UserManagerProtocol
  (create-user [this nickname email]
    (-create-user user-db nickname email))

  (change-profile-basic-info [this user-profile-data]
    (-change-profile-basic-info user-db user-profile-data))

  (change-profile-password [this user-password]
    (-change-profile-password user-db user-password))

  (add-profile-photo [this user-photo]
    (-add-profile-photo user-db user-photo))

  (get-user-list [this]
    (-get-user-list user-db))

  (deactivate-user [this user-email]()
    (-deactivate-user user-db user-email))

  (ban-user [this user-email]
    (-ban-user user-db user-email))

  (login-user [this user-credentials]
    (-login-user user-db user-credentials)))

(def um (->UserManager user-db))

;(create-user um "vasya" "abcd@vasya.com")
