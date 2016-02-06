;;
;; ================= ROOM functions ==================
;;
(ns database.rooms
  (:gen-class)
  (:require [database.core :as core]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))

;; =============Вспомогательные функции===============

;; выяснить приватная ли комната
(defn private-room?
  "is room private?"
  [room-id]
  (let [result (k/select :room
                         (k/fields :is_private)
                         (k/where {:id_room room-id}))]
    (:is_private result)))

(defn active-room?
  "is room private?"
  [room-id]
  (let [result (k/select :room
                         (k/fields :is_active)
                         (k/where {:id_room room-id}))]
    (:is_active result)))

(defn room-with-chat?
  "is room private?"
  [room-id]
  (let [result (k/select :room
                         (k/fields :has_chat)
                         (k/where {:id_room room-id}))]
    (:has_chat result)))


;;Получает список активных комнат конретной игры
(defn get-rooms-with-game-list
  "Get room list of certain game"
  [game-id]
  (k/select :room
            (k/where {:id_game game-id
                      :is_active true})))

;;Получает текущий список пользователей в конкретной комнате
(defn get-user-list-in-room
  "Gets list of users ID in certain room"
  [room-id]
  (k/select :room_users
            (k/fields :id_user)
            (k/where {:id_room room-id
                      :dt_left nil})))

;; Расчет количества комнат, в которых играется игра
(defn count-rooms-with-game
  "count rooms with game-id"
  [game-id]
  (let [rooms (get-rooms-with-game-list game-id)]
    (count rooms)))

;;Получает текущий список пользователей в конкретной комнате
(defn user-in-room?
  "Gets list of users ID in certain room"
  [room-id user-id]
  (let [result (k/select :room_users
                         (k/where {:id_room room-id
                                   :id_user user-id
                                   :dt_left nil}))]
    (if (empty? result) false true)))

;;Удаляет комнату (ставит is-active = false)
(defn deactivate-room
  "deactivate certain room"
  [room-id]
  (let [room-map                {:is_active true}
        deactivated-room-number (k/update :room
                                          (k/set-fields room-map)
                                          (k/where (= :id_room room-id)))]
    (if (= deactivated-room-number 1)
      deactivated-room-number
      {:error-code (:err-code err/kill-room-error)
       :error-desc (str (:err-desc err/kill-room-error) " " room-id
                        " return of operation is " deactivated-room-number)})))

;;Преобразовать список map' в вектор из use-id
(defn get-users-in-room
  "get vector of user IDs"
  [room-id]
  (let [user-list (get-user-list-in-room room-id)]
    (u/vec-map->vec-by-key user-list :id_user)))

;; Расчет количества пользователей в комнате
(defn count-users-in-room
  "count users in room-id"
  [room-id]
  (count (get-users-in-room room-id)))




;; =============Основные функции===============

;;Вернуть список room-id, в которых сейчас играется game-id
(defn get-rooms-with-game
  "get room-id vector with game game-id"
  [game-id]
  (let [rooms (get-rooms-with-game-list game-id)
        r-vec (u/vec-map->vec-by-key rooms :id_room)]
    r-vec))

;;Получает чат комнаты
(defn get-chat-id
  "Gets chats ID of a certain room"
  [room-id]
  (let [chat-list (k/select :chat
                            (k/fields :id_chat)
                            (k/where {:id_room room-id
                                      :dt_closed nil}))]
    (:id_chat (first chat-list))))

;;функция получения текущего статуса с доступом у пользователя (для приватых комнат)
(defn get-user-access
  "select from room_access"
  [room-id user-id]
  (let [id-user-list (k/select :room_access
                            (k/fields :id_user)
                            (k/where {:id_room room-id
                                      :id_user user-id}))]
    (if (empty? id-user-list)
      false
      (let [user-access-list (k/select :room_access
                                   (k/fields :is_active)
                                   (k/where {:id_room room-id
                                             :id_user user-id}))]
        (:is_active (first user-access-list))))))

;;функция выдачи прав пользователю на комнату
(defn grant-user-access
  "grand access to user in room"
  [room-id user-id]
  (let [user-has-access? (get-user-access room-id user-id)]
    (if user-has-access?
      nil
      (k/insert :room_access
                (k/values {:id_user     user-id
                           :is_active   true
                           :dt_granted  (u/now)})))))

;;Добавляет пользователя в комнату
(defn add-user->room
  "Adds user to a room"
  [room-id user-id]
  (let [room-map {:id_room room-id
                  :id_user user-id
                  :dt_joined (u/now)}
        user-already-in-room? (user-in-room? room-id user-id)]
    ;(println "ADD USER TO ROOM FUNCTION")
    ;(println "user" user-id  "in room" room-id  "? - " user-already-in-room?)
    (if user-already-in-room?
      {:error-code (:err-code err/user-already-in-room-error)
       :error-desc (str (:err-desc err/user-already-in-room-error) room-id " user-id " user-id)}
      (let [entered-user (k/insert :room_users
                            (k/values room-map))]
        ;(println "enter-user-result " entered-user)
        ;;TODO: добавить функцию добавления записи в game_users
        (:generated_key entered-user)))))

;;Убирает пользователя из комнаты (задавая dt-left)
(defn kick-user
  "Removes user from a room (set dt-left to now())"
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
;;TODO: (Future) необходимо написать функцию запроса доступа в приватную комнату




;;Создает новую комнату
(defn create-room!
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

(defn kill-room
  "correctly kill room:
  -kick all users and then
  -deactivate room"
  [room-id]
  (let [all-user-vec (get-users-in-room room-id)]
    (dorun (map #(kick-user room-id %) all-user-vec)))
  (deactivate-room room-id))

(defn enter-room
  "user enters room"
  [room-id user-id]
  (let [private? (private-room? room-id)]
    (if private?
      (grant-user-access room-id user-id)
      (let [result (add-user->room room-id user-id)]
        result))))
