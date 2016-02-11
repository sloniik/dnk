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

(defn list
  "return list of game with a type:
  :all - all games
  :active - all active games
  :public - all public"
  [type n]
  (if n
    (cond
      (= type :public)
        (games-db/get-n-new-public-games n)
      (= type :private)
        (games-db/get-n-new-private-games n)
      :else
        (games-db/get-n-new-public-games n))
    (cond
      (= type :all)
        (games-db/get-all-games)
      (= type :active)
        (games-db/get-all-active-games)
      (= type :public)
        (games-db/get-all-public-games)
      :else
        (games-db/get-all-active-games))
    ))

(defn change
  "change game profile"
  [game-id game-info]
  (games-db/change-game-info-by-id game-id game-info))

(defn authors-list
  "get all authors games"
  [author-id type]
  (cond
    (= type :all)
      (games-db/get-all-games-by-author author-id)
    (= type :active)
      (games-db/get-games-by-author author-id)))