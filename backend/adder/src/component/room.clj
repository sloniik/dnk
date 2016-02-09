(ns component.room
  (:gen-class)
  (:require [database.core :as core]
            [database.rooms :as room-db]
            [component.user :as user-db]
            [database.errors :as err]
            [utilities.core :as u]))

;;Преобразовать список map' в вектор из use-id
(defn get-users
  "get vector of user IDs"
  [room-id]
  (let [user-list (room-db/get-user-list-in-room room-id)]
    (u/vec-map->vec-by-key user-list :id_user)))

;;Вернуть список room-id, в которых сейчас играется game-id
(defn get-rooms
  "get room-id vector with game game-id"
  [game-id]
  (let [rooms (room-db/get-rooms-with-game-list game-id)
        r-vec (u/vec-map->vec-by-key rooms :id_room)]
    r-vec))

(defn create
  "user (user-id) leaves the room (room-id)"
  [room-info]
  (room-db/create-room room-info))

(defn kill
  "correctly kill room:
  -kick all users and then
  -deactivate room"
  [room-id]
  (let [all-user-vec (get-users room-id)]
    (dorun (map #(room-db/kick-user room-id %) all-user-vec))
    (room-db/deactivate-room room-id)))

(defn enter
  "user enters room"
  [room-id user-id]
  (let [private? (room-db/private-room? room-id)
        user-banned? (user-db/banned? user-id)]
    (if user-banned?
      {:error-code (:err-code err/banned-user-error)
       :error-desc (str (:err-desc err/banned-user-error) " " user-id)}
      (if private?
        (room-db/grant-user-access room-id user-id)
        (let [result (room-db/add-user->room room-id user-id)]
          result)))))

(defn quit
  "user (user-id) leaves the room (room-id)"
  [room-id user-id]
  (room-db/kick-user room-id user-id))

;; Расчет количества комнат, в которых играется игра
(defn count-rooms
  "count rooms with game-id"
  [game-id]
  (let [rooms (room-db/get-rooms-with-game-list game-id)]
    (count rooms)))

(defn count-users
   "count rooms with game-id"
   [room-id]
   (let [users (room-db/get-user-list-in-room room-id)]
     (count users)))