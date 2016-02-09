(ns adder.database-test.games
  (:use [clojure.test]
        [database.rooms]
        [database.users]
        [database.games]
        [database.core])
  (:require [clojure.string :as str]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]))

;;TODO: devPop сделать тесты на database.games
