(ns database.users
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]
            [korma.db :as kdb]))

;TODO: дописать entity
(k/defentity users
             (k/pk :id_user)
             (k/table :users)
             (k/database core/db)
             )

;; ================ User functions ===================
(defn get-all-users
  "List of all users"
  []
  (k/select users))

;;Получаем пользователя по id пользователя
(defn get-user-info-by-id
  "Get user by user-id"
  [user-id]
  (k/select :users
            (k/where {:id_user user-id})))

;;Получем пользователя по логину
(defn get-user-info-by-login
  "Get user by login"
  [login]
  (k/select :users
          (k/where {:user_name login})))

(defn get-user-info-by-mail
  "Get user by user-id"
  [email]
  (k/select :users
          (k/where {:email email})))

(defn get-user-salt
  "Get user salt"
  [id]
  (let [user-info (get-user-info-by-id id)]
    (:salt (first user-info))))

(defn get-user-pass
  "get user password hash"
  [id]
  (let [user-info (get-user-info-by-id id)]
    (:password_hash (first user-info))))

;;;Получаем значения полей пользователя (кроме password и salt)
;(defn get-user-safe-info
;  "Get user by type key (id or login)"
;  [id]
;  (let [user-info (get-user-info-by-id id)]
;    (-> user-info
;        (dissoc :salt)
;        (dissoc :password-hash))))

(defn get-media-types
  "Get active types of media available"
  []
  (k/select :media_type
            (k/where {:is_active true})))

(defn get-all-media-types
  "Get all media types"
  []
  (k/select :media_type))

(defn get-media-type-by-id
  "Get media type with certain ID"
  [id]
  (k/select :media_type
            (k/where {:id_media_type id})))

(defn add-media-type
  "Adds new media type"
  [map]
  (:generated_key (k/insert :media_type
                            (k/values map))))

(defn update-media-type-name
  [type-id new-name]
  (k/update :media_type
            (k/set-fields {:media_type_name new-name})
            (k/where {:id_media_type type-id})))

(defn delete-media-type
  [type-id]
  (k/update :media_type
            (k/set-fields {:is_active false})
            (k/where {:id_media_type type-id})))

;;TODO: функция должна возращать файл, а не путь к файлу? Может так и назвать функцию или доописать
(defn get-user-media
  "Gets mediafiles of certaion type created by user"
  [user-id]
  (k/select :user_media
            (k/where {:id_user user-id})))

;;TODO: обсудили, что у пользователя может быть только аватар - смысл в функции?
(defn get-user-media-by-type
  "Get media by selected user and selected type"
  [user-id type-id]
  (k/select :user_media
            (k/where (and {:id_user user-id}
                          {:id_media_type type-id}))))

(defn get-all-user-sessions
  "Gets all sessions, made by user"
  [db-spec user-id]
  (k/select :user_session
            (k/where {:id_user user-id})))

;TODO: реализовать функцию
(defn get-current-user-session
  "Gets current session by certain user"
  [db-spec user-id]
  )

;;Проверяем, что данный логин еще не занят
(defn login-available?
  "Check whether login available"
  [login]
  (let [user-info (get-user-info-by-login login)]
    (if (nil? user-info)
      true
      false)))

(defn email-registered?
  "Check whether email is already registered"
  [email]
  (let [user-info (get-user-info-by-mail email)]
    (if (nil? user-info)
      true
      false)))

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database_test matches calculated hash"
  [id-user password-hash]
  (if (= password-hash (get-user-pass id-user))
    true
    false))

;;Создает нового пользователя
(defn create-user
  "Create new user"
  [user-map]
  (let [return (k/insert :users
                      (k/values user-map))]
    (:generated_key return)
    ))

(defn send-email
  "send email with code in the email body"
  [email code]
  code)

;TODO: переделать функции Update
(defn activate-user
  "activate user after confirming email with code"
  [email code]
  ;(let [user-info (get-user-safe-info email)
  ;      user-id (:id_user user-info)]
    ;(update-data db-spec
    ;             {:is_active  true}
    ;             (u/sel-n-upd-map :users :id_user user-id)))
  )

;;Меняет профиль пользователя
(defn update-user-profile
  "Updates user profile"
  [db-spec id-user profile-map]
  ;(update-data db-spec
  ;             profile-map
  ;             (u/sel-n-upd-map :users :id_user id-user))
  )

;;Обновляем токен пользователя
(defn update-user-token
  "Updates token in table Users"
  [db-spec id-user token]
  ;(update-data db-spec
  ;             {:user_token token}
  ;             (u/sel-n-upd-map :users :id_user id-user))
  )

(defn create-user-session
  "Creates new user session"
  [id-user]
  (let [user-map {:id_user id-user}]
    (k/insert :user_session
              (k/values user-map)))
  )

;;Деактивирует пользователя. ставит в таблице Users is_active=false
(defn deactivate-user
  "Deactivates user (updating record in table Users"
  [db-spec
   id-user]
  ;(update-data db-spec
  ;             {:is_active false}
  ;             (u/sel-n-upd-map :users :id_user id-user))
  )

;;Банит пользователя, запрещая ему активность на сайте
(defn ban-user
  "Bans user blocking his activity"
  [db-spec
   id-user]
  ;(update-data db-spec
  ;             {:is_banned true}
  ;             (u/sel-n-upd-map :users :id_user id-user))
  )

;; ==== HighLevel-Functions ====
;; сценарий №1: регистрация пользователя
;; проверить, что логин и email дейсвительные. если все ок, зарегистрировать пользователя
(defn register-user!
  "register user with login and/or email
  (= pasword password-repeat) was checked on the client side
  (salt) was generated on the client side
  (password-hash) was generated on the client side"
  [user-info]
  (let [login-correct? (login-available? (:login user-info))
        email-correct? (email-registered? (:email user-info))
        code u/get-uuid]
    (cond
      (and login-correct? email-correct?)
      (do
        (create-user {:user_name     (:login user-info)
                      :email         (:email user-info)
                      :password_hash (:password-hash user-info)
                      :salt          (:salt user-info)
                      :dt_created    (u/now)
                      :is_active     false   ;при создании человек не активен, так как надо подтвердить email
                      :is_banned     false
                      :is_admin      false
                      :email_code    code})  ; код верификации email
        (send-email (:email user-info) code))
      (not login-correct?)
      {:error-code (:err-code err/incorrect-user-login)
       :error-desc (str (:err-desc err/incorrect-user-login) " " (:login user-info) )}
      (not email-correct?)
      {:error-code (:err-code err/incorrect-user-email)
       :error-desc (str (:err-desc err/incorrect-user-email) " " (:email user-info) )})))

;; сценарий №2: вход пользователя по логину и паролю
;; проверить, что логин и пароль дейсвительные. если все ок, отдать token пользователю
(defn login-user
  "login user with login-info - {:login :password-hash}"
  [db-spec login-info]
  (let [login-correct? (login-available? (:login login-info))
        user-info (get-user-info-by-login (:login login-info))
        user-id (:id_user user-info)
        password-correct? (password-match? user-id (:password-hash login-info))
        user-active? (:is_active user-info)]
    (cond
      (and login-correct? password-correct? user-active?)
      (create-user-session user-id); вернуть пользователю сессию
      (not login-correct?)
      {:error-code (:err-code err/incorrect-user-login)
       :error-desc (str (:err-desc err/incorrect-user-login) " "
                        (:login login-info) )}
      (not password-correct?)
      {:error-code (:err-code err/incorrect-user-passw)
       :error-desc (:err-desc err/incorrect-user-passw)}
      (not user-active?)
      {:error-code (:err-code err/inactive-user-error)
       :error-desc (:err-desc err/inactive-user-error)})))