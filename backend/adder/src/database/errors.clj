(ns database.errors)

;; ===== COMMON ERRORS ====
(def illegal-argument {:err-code '0001'
                       :err-desc "This argument cannot be proceded by function"})

;; ===== USER ERRORS =====
(def incorrect-user-login   {:err-code '1001'
                             :err-desc "This login is already taken"})

(def incorrect-user-email   {:err-code '1002'
                             :err-desc "This email is used by another user"})

(def incorrect-user-passw   {:err-code '1003'
                             :err-desc "Password doesn't match"})

(def inactive-user-error    {:err-code '1004'
                             :err-desc "User email is not activated"})
(def banned-user-error      {:err-code '1005'
                             :err-desc "User is banned. User-id:"})
(def email-sending-error    {:err-code '1006'
                             :err-desc "We can't send e-mail to the following email-address:"})

;; ===== GAME ERRORS =====
(def create-game-error      {:err-code '2001'
                             :err-desc "Can't create the following game "})
(def change-game-info-error {:err-code '2002'
                             :err-desc "Can't commint the following changes to game-id "})


;; ===== ROOM ERRORS =====
(def kill-room-error        {:err-code '3001'
                             :err-desc "Can't deactivate room"})
(def update-game-in-room-error
                            {:err-code '3002'
                             :err-desc "Can't change game in "})
(def update-game-master-in-room-error
                            {:err-code '3003'
                             :err-desc "Can't update game master id"})
(def user-already-in-room-error
                            {:err-code '3004'
                             :err-desc "User has already entered the room"})

;; ===== CHAT ERRORS =====
(def room-has-no-chat        {:err-code '4001'
                             :err-desc "Room doesn't have any chat, room-id: "})
