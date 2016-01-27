(ns database.users
  (:gen-class)
  (:require [utilities.core :as u]
            [database.errors :as err]
            [korma.core :refer [select fields where insert values update set-fields delete]]
            [korma.db :as kdb]))

;; ================ User functions ===================
(defn get-all-users
  "List of all users"
  []
  (select :users))

;;Получаем пользователя по id пользователя
(defn get-user-info-by-id
  "Get user by user-id"
  [user-id]
  (select :users
          (where {:id_user user-id})))

;;Получем пользователя по логину
(defn get-user-info-by-login
  "Get user by login"
  [login]
  (select :users
          (where {:user_name login})))

(defn get-user-info-by-mail
  "Get user by user-id"
  [email]
  (select :users
          (where {email email})))

(defn get-user-salt
  "Get user salt"
  [id]
  (let [user-info (get-user-info-by-id id)]
    (:salt user-info)))

(defn get-user-pass
  "get user password hash"
  [db-spec id id-type]
  (let [user-info (get-user-info-by-id id)]
    (:password_hash user-info)))

;;Получаем значения полей пользователя (кроме password и salt)
(defn get-user-safe-info
  "Get user by type key (id or login)"
  [db-spec id id-type]
  (let [user-info (get-user-info-by-id id)]
    (-> user-info
        (dissoc :salt)
        (dissoc :password-hash))))

(def get-media-types
  "Get all types of media available"
  []
  (select :media_type))

;TODO: реализовать функцию get-user-media
(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [user-id]
  (select :user_media
          (where {:id_user user-id})))

(defn get-user-media-by-type
  '
  [])

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [db-spec user-id]
  (select-all-by-field db-spec (u/sel-n-upd-map :users :user_name user-id)))

;TODO: реализовать функцию
(defn get-current-user-session
  "Gets current session by certain user"
  [db-spec user-id])

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [db-spec login]
  (let [user-info (get-user-safe-info db-spec login :login)]
    (if (nil? user-info)
      true
      false)))

(defn email-registered?
  "Check whether email is already registered"
  [db-spec email]
  (let [user-info (select-col-by-field db-spec (u/sel-n-upd-map :users :email :email email))]
    (if (nil? user-info)
      true
      false)))

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database matches calculated hash"
  [db-spec id-user id-type password-hash]
  (if (= password-hash (get-user-pass
                         db-spec id-user id-type))
    true
    false))

;;Создает нового пользователя
(defn create-user
  "Create new user"
  [db-spec
   user-map]
  (:generated_key (first (insert-data db-spec :users user-map))))

(defn send-email
  "send email with code in the email body"
  [email code]
  code)

(defn activate-user
  "activate user after confirming email with code"
  [email code]
  (let [user-info (get-user-safe-info db-spec email :email)
        user-id (:id_user user-info)]
    (update-data db-spec
                 {:is_active  true}
                 (u/sel-n-upd-map :users :id_user user-id))))

;;Меняет профиль пользователя
(defn update-user-profile
  "Updates user profile"
  [db-spec id-user profile-map]
  (update-data db-spec
               profile-map
               (u/sel-n-upd-map :users :id_user id-user)))

;;Обновляем токен пользователя
(defn update-user-token
  "Updates token in table Users"
  [db-spec id-user token]
  (update-data db-spec
               {:user_token token}
               (u/sel-n-upd-map :users :id_user id-user)))

(defn create-user-session
  "Creates new user session"
  [db-spec
   id-user]
  (let [user-map {:id_user id-user}]
    (insert-data db-spec :user_session user-map)))

;;Деактивирует пользователя. ставит в таблице Users is_active=false
(defn deactivate-user
  "Deactivates user (updating record in table Users"
  [db-spec
   id-user]
  (update-data db-spec
               {:is_active false}
               (u/sel-n-upd-map :users :id_user id-user)))

;;Банит пользователя, запрещая ему активность на сайте
(defn ban-user
  "Bans user blocking his activity"
  [db-spec
   id-user]
  (update-data db-spec
               {:is_banned true}
               (u/sel-n-upd-map :users :id_user id-user)))