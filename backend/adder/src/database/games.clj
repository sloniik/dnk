;;
;;======================== Game  Functions =====================================
;;
(ns database.games
  (:gen-class)
  (:require [database.core :as core]
            [database.users :as user]
            [database.errors :as err]
            [utilities.core :as u]
            [korma.core :as k]))

;;Получаем список всех игр
(defn get-all-games
  "Get collection of all games ever created"
  []
  (k/select :game))

;;Получаем список игр с параметром isDeleted = false
(defn get-all-active-games
  "Get collection of all games that are currently active"
  []
  (k/select :game
            (k/where (= :is_deleted false))))

;;Получем список игр с параметром isPrivate = false
(defn get-all-public-games
  "Get collection of all non-private games"
  []
  (k/select :game
            (k/where {:is_private false
                      :is_active true})))
;;Получаем список всех типов игр
(defn get-game-types
  "Get all available game types"
  []
  (k/select :game_type))

;;Получаем список всех вариантов определенного типа игры
(defn get-game-variants
  [game-type-id]
  (k/select :game_variant
            (k/where (= :id_game_type game-type-id))))

;;Получаем список игр определенного типа
(defn get-games-by-variant
  "Get collection of games by variant"
  [game-variant-id]
  (k/select :game
            (k/where (= :id_game_variant game-variant-id))))

;;Получаем несколько новых игр
(defn get-n-new-public-games
  "Get collection of n newest games"
  [n]
  (k/select :game
            (k/where
              (and
                (= :is_deleted false)
                (= :is_private false)))
            (k/order :dt_created :desc)
            (k/limit n)))

(defn get-n-new-private-games
  "Get collection of n newest games"
  [n]
  (k/select :game
            (k/where
              (and
                (= :is_deleted false)
                (= :is_private true)))
            (k/order :dt_created :desc)
            (k/limit n)))

(defn get-n-new-original-games
  "Get collection of n newest games"
  [n]
  (k/select :game
            (k/where
              (and
                (= :is_deleted false)
                (= :is_fork    false)))
            (k/order :dt_created :desc)
            (k/limit n)))

;;Получаем список игр конкретного автора
(defn get-games-by-author
  "Get collection of games by author"
  [author-id]
  (k/select :game
            (k/where
              (and
                (= :id_author author-id)
                (= :is_deleted false)))))

(defn get-all-games-by-author
  "Get collection of games by author"
  [author-id]
  (k/select :game
            (k/where
              (= :id_author author-id))))

;;Получаем список игр с idOriginal = null
(defn get-original-games
  "Get all games that are not forks"
  []
  (k/select :game
            (k/where
              (and
                (= :is_deleted false)
                (= :is_fork    false)))))

(defn get-all-original-games
  "Get all games that are not forks"
  []
  (k/select :game
            (k/where
              (= :is_deleted false))))

;;Получаем список форков игры
(defn get-game-forks
  "Get all forks of a certain game"
  [game-id]
  (k/select :game
            (k/where
              (= :is_original game-id))))

;;Получаем набор данных [GameMediaType/TypeName GameMedia/filePath] по данной игре
(defn get-game-media
  "Get all media for a certain game"
  [game-id]
  (k/select :game_media
            (k/where
              (= :is_game game-id))))

;;Получаем набор пользователей по данной игре
(defn get-game-users
  "Get all users for a certain game"
  [game-id]
  (k/select :game_users
            (k/where
              (= :is_game game-id))))

(defn get-game-by-id
  "Get game data by it's id"
  [game-id]
  (k/select :game
            (k/where
              (= :is_game game-id))))

;;NOT IN TO-DO LIST
(defn approve-game
  "Approve game by game-id"
  [game-id])


;;TODO сделать функцию проверки игры на похожесть
(defn create-game
  "Create new record in GAME table.
  Search for similar game first
  Return gameID and operation-status
  game info - map"
  [game-info]
  (let [result (k/insert :game
                         (k/values game-info))]
    ;(println result)
    (if (nil? (:generated_key result))
      {:error-code    (:err-code err/create-game-error)
       :error-desc    (str (:err-desc err/create-game-error) game-info)}
      (:generated_key result))))


(defn change-game-info-by-id
  "Create new record in GAME table.
  Search for similar game first
  Return gameID and operation-status"
  [game-id game-info]
  (let [result (k/update :game
                         (k/set-fields game-info)
                         (k/where (= :id_game game-id)))]
    (if (nil? (first result))
      {:error-code (:err-code err/change-game-info-error)
       :error-desc (str (:err-desc err/change-game-info-error) game-id " game-info " game-info)}
      (first result))))