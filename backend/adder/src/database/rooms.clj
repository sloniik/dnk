(ns database.rooms
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))

;; ================= ROOM functions ==================\

;;Создает новую комнату
(defn create-room
  "Creates room for a certain game
  room-map structure: {:id_user_master
                      :title
                       :is_private
                       :has_chat
                       :id_game_Variant
                       :dt_start
                       :dt_end
                       :is_active}
  return new-room-id"
  [room-map]
  ;;TODO: создать чат
  ;(let [room-map (assoc room-map :id_chat (create-chat))]
  (let [new-room (k/insert :room
                           (k/values room-map))]
    (:generated_key new-room)))

;;Удаляет комнату (ставит is-active = false)
(defn deactivate-room
  "deactivate certain room"
  [room-id]
  (let [room-map {:is_active true}
        killed-room-number (k/update :room
                                   (k/set-fields room-map)
                                   (k/where (= :id_room room-id)))]
    (if (= killed-room-number 1)
      killed-room-number
      {:error-code (:err-code err/kill-room-error)
       :error-desc (str (:err-desc err/kill-room-error) " " room-id
                        " return of operation is " killed-room-number)})))

;;Получает список активных комнат конретной игры
(defn get-room-list
  "Get room list of certain game"
  [game-id]
  (k/select :room
            (k/where {:id_game game-id
                      :is_active true})))

;;Получает текущий список пользователей в конкретной комнате
(defn get-users-in-room
  "Gets list of users ID in certain room"
  [room-id]
  (k/select :room_users
            (k/fields :id_user)
            (k/where {:id_room room-id
                      :dt_left nil})))

;;Получает чат комнаты
(defn get-chat-id
  "Gets chats ID of a certain room"
  [room-id]
  (let [chat-list (k/select :room
                            (k/fields :id_chat)
                            (k/where {:id_room room-id
                                      :dt_closed nil}))]
    (:id_chat (first chat-list))))

;;функция получения текущего статуса с доступом у пользователя
(defn get-user-access
  "select from room_access"
  [room-id user-id]
  (let [id-user-list (k/select :room_access
                            (k/fields :id_user)
                            (k/where {:id_room room-id
                                      :id_user user-id}))
        id-user (:id_user (first id-user-list))]
    (if (nil? id-user)
      false
      (let [user-access-list (k/select :room_access
                                   (k/fields :is_active)
                                   (k/where {:id_room room-id
                                             :id_user user-id}))
            user-access (:is_active (first user-access-list))]
        (if (nil? user-access)
          false
          user-access)))
    ))

;;TODO: понять, как давать доступ к комнате и дописать функцию grant-user-access
;;функция выдачи прав пользователю на комнату
(defn grant-user-access
  "grand access to user in room"
  [room-id user-id]
  (let [user-access (get-user-access room-id user-id)]))

;;Добавляет пользователя в комнату
(defn add-user->room
  "Adds user to a room"
  [room-id user-id]
  (let [room-map {:id_room room-id
                  :id_user user-id
                  :dt_joined (u/now)}
        entered-user (k/insert :room_users
                               (k/values room-map))]
    ;;TODO: добавить функцию добавления записи в game_users
    (:generated_key entered-user)))

;;Убирает пользователя из комнаты (задавая dt-left)
(defn kick-user
  "Removes user from a room (settind dt-left)"
  [room-id user-id]
  (let [room-map {:dt_left (u/now)}]
    (k/update :room_users
              (k/set-fields room-map)
              (k/where (= :id_room room-id))
              (k/where (= :id_user user-id))
              (k/where (= :dt_left nil)))))

(defn change-room-game
  "change game in room-id for game-id"
  [room-id game-id]
  (let [result (k/update :room
            (k/set-fields {:id_game game-id})
            (k/where (= :id_room room-id)))]
    (if (= result 1)
      result
      {:error-code (:err-code err/update-game-in-room-error)
       :error-desc (str (:err-desc err/update-game-in-room-error) " " game-id
                        " in room-id " room-id)})))

(defn change-game-master
  "change game master in room"
  [room-id user-id]
  (let [result (k/update :room
                         (k/set-fields {:id_user_master user-id})
                         (k/where (= :id_room room-id)))]
    (if (= result 1)
      result
      {:error-code (:err-code err/update-game-master-in-room-error)
       :error-desc (str (:err-desc err/update-game-master-in-room-error) " " user-id
                        " in room-id " room-id)})))

(defn kill-room
  "correctly kill room:
  -kick all users and then
  -deactivate room"
  [room-id]
  (let [all-user-list (get-users-in-room room-id)
        all-user-vec (u/vec-map->vec-by-key all-user-list :id_user)]
    (map #(kick-user room-id %) all-user-vec)
    (deactivate-room room-id)))

(defn enter-room
  "user enters room"
  [room-id user-id]
  (grant-user-access room-id user-id)
  (add-user->room room-id user-id))