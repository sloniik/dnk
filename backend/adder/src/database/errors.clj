(ns database.errors)

(def create-game-error      {:err-code '0001'
                             :err-desc "Can't create the following game "})
(def change-game-info-error {:err-code '0002'
                             :err-desc "Can't commint the following changes to game-id "})
