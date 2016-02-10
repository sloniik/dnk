(ns component.user
  (:gen-class)
  (:require [database.users :as user-db]
            [database.errors :as err]
            [utilities.core :as u]))

(defn get-salt
  "Get user salt"
  [id]
  (let [user-info (user-db/get-user-info-by-id id)]
    (:salt (first user-info))))

(defn get-pass
  "get user password hash"
  [id]
  (let [user-info (user-db/get-user-info-by-id id)]
    (:password_hash (first user-info))))

;;Проверяем, что данный логин еще не занят
(defn login-valid?
  "Check whether login available"
  [login]
  (let [user-info (user-db/get-user-info-by-login login)]
    (if (nil? user-info)
      true
      false)))

(defn email-valid?
  "Check whether email is already registered"
  [email]
  (let [user-info (user-db/get-user-info-by-mail email)]
    (if (nil? user-info)
      true
      false)))

;;Проверяем, что пользовтель не забанен. Use-case - вход в комнату
(defn banned?
  "check whether user is banned"
  [user-id]
  (let [user (user-db/get-user-info-by-id user-id)]
    (:is_banned user)))

;;Передаем вычисленных хеш пароля + соли и проверяем, совпадает ли он с хешем в базе
(defn password-match?
  "Check if hashed password in database_test matches calculated hash"
  [user-id password-hash]
  (if (= password-hash (get-pass user-id))
    true
    false))

;; ==== HighLevel-Functions ====
;; сценарий №1: регистрация пользователя
;; проверить, что логин и email дейсвительные. если все ок, зарегистрировать пользователя
(defn register
  "register user with login and/or email
  (= pasword password-repeat) was checked on the client side
  (salt) was generated on the client side
  (password-hash) was generated on the client side"
  [user-info]
  (let [login          (:login user-info)
        email          (:email user-info)
        passwd         (:password-hash user-info)
        salt           (:salt user-info)
        login-correct? (login-valid? login)
        email-correct? (email-valid? email)
        code           (u/get-uuid)]
    (cond
      (and login-correct? email-correct?)
        (do
          (user-db/create-user {:user_name     login
                                :email         email
                                :password_hash passwd
                                :salt          salt
                                :dt_created    (u/now)
                                :is_active     false   ;при создании человек не активен, так как надо подтвердить email
                                :is_banned     false
                                :is_admin      false
                                :email_code    code})  ; код верификации email
          (user-db/register-email email code))
      (not login-correct?)
        {:error-code (:err-code err/incorrect-user-login)
         :error-desc (str (:err-desc err/incorrect-user-login) " " login)}
      (not email-correct?)
        {:error-code (:err-code err/incorrect-user-email)
         :error-desc (str (:err-desc err/incorrect-user-email) " " email)})))

;; сценарий №2: вход пользователя по логину и паролю
;; проверить, что логин и пароль дейсвительные. если все ок, отдать token пользователю
(defn login
  "login user with login-info - {:login :password-hash}"
  [login-info]
  (let [login             (:login login-info)
        login-correct?    (login-valid? login)
        passwd            (:password-hash login-info)
        user-info         (user-db/get-user-info-by-login login)
        user-id           (:id_user user-info)
        password-correct? (password-match? user-id passwd)
        user-active?      (:is_active user-info)]
    (cond
      (and login-correct? password-correct? user-active?)
        (user-db/create-user-session user-id); вернуть пользователю сессию
      (not login-correct?)
        {:error-code (:err-code err/incorrect-user-login)
         :error-desc (str (:err-desc err/incorrect-user-login) " " login)}
      (not password-correct?)
        {:error-code (:err-code err/incorrect-user-passw)
         :error-desc (:err-desc err/incorrect-user-passw)}
      (not user-active?)
        {:error-code (:err-code err/inactive-user-error)
         :error-desc (:err-desc err/inactive-user-error)})))

(defn update
  "update user profile"
  [user-id profile-map]
  (user-db/update-user-profile user-id profile-map))

(defn ban
  "ban user"
  [user-id]
  (user-db/ban-user user-id))

(defn deactivate
  "deactivate user"
  [user-id]
  (user-db/deactivate-user user-id))

(defn list
  "get all not banned users"
  []
  (user-db/get-all-users))