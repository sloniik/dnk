(ns component.room)

;; Functions implementation:
(defn -create-room
  [room-db roomID ]
  (println "here I create user " ))

;; protocol description
(defprotocol RoomManagerProtocol
  (login-user [this user-credentials]
    "login user
    get map {:user-email :user-password}
    return map  {:status :details}."))

;; protocol implementation
(defrecord UserManager [user-db]
  RoomManagerProtocol

  )