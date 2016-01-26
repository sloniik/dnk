(ns database.errors)

;; ===== USER ERRORS =====
(def incorrect-user-login   {:err-code '1001'
                             :err-desc "This login is already taken"})

(def incorrect-user-email   {:err-code '1002'
                             :err-desc "This email is used by another user"})

(def incorrect-user-passw   {:err-code '1003'
                             :err-desc "Password doesn't match"})

;; ===== GAME ERRORS =====
(def create-game-error      {:err-code '2001'
                             :err-desc "Can't create the following game "})
(def change-game-info-error {:err-code '2002'
                             :err-desc "Can't commint the following changes to game-id "})


;; ===== ROOM ERRORS =====