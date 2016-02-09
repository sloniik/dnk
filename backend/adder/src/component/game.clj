(ns component.game
  (:gen-class)
  (:require [database.core :as core]
            [database.errors :as err]
            [database.games :as games-db]))

;;TODO сделать функцию проверки игры на похожесть
(defn create
  "Create new record in GAME table.
  Search for similar game first
  Return gameID and operation-status
  game info - map"
  [game-info]
  (games-db/create-game game-info))

(defn random-game
  "Return random game"
  []
  (let [public-games (games-db/get-all-public-games)
        game-rand-num (rand-int (count public-games))
        n-games (take game-rand-num public-games)]
    (last n-games)))
