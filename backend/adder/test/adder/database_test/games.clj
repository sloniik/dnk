(ns adder.database-test.games
  (:use [clojure.test]
        [database.games]
        [database.core]))

;;TODO: devPop сделать тесты на database.games

(deftest game-test
  (testing "select-games"
    (is (= (count (get-all-games)) 5))
    (is (= (count (get-all-active-games)) 4))
    (is (= (count (get-all-public-games)) 2))
    (is (= (count (get-n-new-public-games 1)) 1))
    (is (= (count (get-n-new-private-games 1)) 1))
    (is (= (count (get-games-by-author 1)) 3))
    (is (= (count (get-games-by-author 2)) 1))
    (is (= (count (get-all-games-by-author 1)) 4))
    (is (= (count (get-game-media 1)) 1))
    (is (= (count (get-game-users 1)) 2))
    (is (= (count (get-game-users 2)) 0))
    (is (= (:id_game (first (get-game-by-id 1))) 1))
))