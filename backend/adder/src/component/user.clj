(ns component.user
(:gen-class)
(:use user-constuser-info.user-const :as us-in)

;; Functions implementation:
(defn -create-user
  [user-db nickname email]
  (println "here I create user " nickname))

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
    (-change-profile-basic-info [user-db user-profile-data]))

  (change-profile-password [this user-password]
    (-change-profile-password [user-db user-password]))

  (add-profile-photo [this user-photo]
    (-add-profile-photo [user-db user-photo]))

  (get-user-list [this]
    (-get-user-list [user-db]))

  (deactivate-user [this user-email]()
    (-deactivate-user [user-db user-email]))

  (ban-user [this user-email]
    (-ban-user [user-db user-email]))

  (login-user [this user-credentials]
    (-login-user [user-db user-credentials])))

;(def um (->UserManager "localhost"))

;(create-user um "vasya" "abcd@vasya.com")
(map->UserManager {:user-db "localhost"})