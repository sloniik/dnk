(ns database.users
  (:gen-class)
  (:require [database.errors :as err]
            [korma.core :as k]))

;;TODO: дописать entity
;(k/defentity users
;             (k/pk :id_user)
;             (k/table :users)
;             (k/database core/db)
;             )

;; ================ User functions ===================
(defn get-all-users
  "List of all users"
  []
  (k/select :users
            (k/where {:is_banned false})))

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
  ;;Media type map: {:media-type-name (varchar)
  ;;                 :is_active       (bit)}
  [media-type-map]
  (:generated_key (k/insert :media_type
            (k/values media-type-map))))

(defn add-user-media
  "Adds user media"
  ;media map: {:id_user
  ;            :id_media_type
  ;            :file_path
  ;            :name}
  [media-map]
  (:generated_key (k/insert :user_media
                            (k/values media-map))))

(defn change-user-media-path [])

(defn update-user-media [] "Changes media path name")

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
  [user-id]
  (k/select :user_session
            (k/where {:id_user user-id})))

;TODO: реализовать функцию
(defn get-current-user-session
  "Gets current session by certain user"
  [user-id])

;;Создает нового пользователя
(defn create-user
  "Create new user"
  [user-map]
  (let [return (k/insert :users
                         (k/values user-map))]
    (:generated_key return)))

;TODO: реализовать функцию отправки email с кодом верификации
(defn send-email
  "Send email with code in the email body"
  [email code]
  true)

(defn add-email-code
  "Add email-code to users table"
  [email code]
  (let [user (get-user-info-by-mail email)
        user-id (:id_user user)]
    (k/update :users
              (k/set-fields {:email-code code})
              (k/where (= :id_user user-id)))))

(defn register-email
  "Register email:
  -- add code to users table
  -- send email with the code"
  [email code]
  (let [email-status? (send-email email code)]
    (if (not email-status?)
      {:error-code (:err-code err/email-sending-error)
       :error-desc (str (:err-desc err/email-sending-error) " " email)}
      (add-email-code email code))))

(defn activate-user
  "activate user after confirming email with code"
  [email code]
  (let [user-info (get-user-info-by-mail email)
        user-id (:id_user user-info)]
    (k/update :users
              (k/set-fields {:is_active  true})
              (k/where (= :id_user user-id)))))

;;Меняет профиль пользователя
(defn update-user-profile
  "Updates user profile"
  [user-id profile-map]
  (k/update :users
            (k/set-fields profile-map)
            (k/where (= :id_user user-id))))

;;Обновляем токен пользователя
(defn update-user-token
  "Updates token in table Users"
  [user-id token]
  (k/update :users
            (k/set-fields {:user_token token})
            (k/where (= :id_user user-id))))

(defn create-user-session
  "Creates new user session"
  [user-id]
  (let [user-map {:id_user user-id}]
    (k/insert :user_session
              (k/values user-map))))

;;Деактивирует пользователя. ставит в таблице Users is_active=false
(defn deactivate-user
  "Deactivates user (updating record in table Users"
  [user-id]
  (k/update :users
            (k/set-fields {:is_active false})
            (k/where (= :id_user user-id))))

;;Банит пользователя, запрещая ему активность на сайте
(defn ban-user
  "Bans user blocking his activity"
  [user-id]
  (k/update :users
            (k/set-fields {:is_banned false})
            (k/where (= :id_user user-id))))