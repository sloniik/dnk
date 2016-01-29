(ns database.rooms
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]
            [korma.db :as kdb]))

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
(defn kill-room
  "deactivate certain room"
  [id-room]
  (let [room-map {:is_active true}
        killed-room-number (k/update :room
                                   (k/set-fields room-map)
                                   (k/where (= :id_room id-room)))]
    (if (= killed-room-number 1)
      killed-room-number
      {:error-code (:err-code err/kill-room-error)
       :error-desc (str (:err-desc err/kill-room-error) " " id-room
                        " return of operation is " killed-room-number)})))

;;Получает список активных комнат конретной игры
(defn get-room-list
  "Get room list of certain game"
  [id-game]
  (k/select :room
            (k/where {:id_game id-game
                      :is_active true})))

;;Получает текущий список пользователей в конкретной комнате
(defn get-users-in-room
  "Gets list of users ID in certain room"
  [id-room]
  (k/select :room_users
            (k/fields :id_user)
            (k/where {:id_room id-room
                      :dt_left nil})))

;;Получает чат комнаты
(defn get-chat
  "Gets chats ID of a certain room"
  [id-room]
  (let [chat-list (k/select :room
                            (k/fields :id_chat)
                            (k/where {:id_room id-room
                                      :dt_closed nil}))]
    (first chat-list)))

;;Добавляет пользователя в комнату
(defn enter-room
  "Adds user to a room"
  [id-user id-room]
  (let [room-map {:id_room id-room
                  :id_user id-user
                  :dt_joined (u/now)}
        entered-user (k/insert :room_users
                               (k/values room-map))]
    (:generated_key entered-user)))

;;Убирает пользователя из комнаты (задавая dt-left)
(defn leave-room
  "Removes user from a room (settind dt-left)"
  [id-user id-room]
  (let [room-map {:dt_left (u/now)}]
    (k/update :room_users
              (k/set-fields room-map)
              (k/where (= :id_room id-room))
              (k/where (= :id_user id-user))
              (k/where (= :dt_left nil)))))

