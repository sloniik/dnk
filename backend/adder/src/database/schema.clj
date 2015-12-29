(ns database.schema
(:gen-class)
(:require [clojure.java.jdbc :as jdbc]))

(def create-users-table
  "map for further users-table creation"
  (jdbc/create-table-ddl
  :users
  [:id :int "PRIMARY KEY AUTO_INCREMENT"]
  [:name "VARCHAR(32)"]
  [:email "VARCHAR(32)"]
  [:active "VARCHAR(2)"]
  [:ban "VARCHAR(2)"]
  [:seenLasttTime "DATE"]
  [:regdate "DATE"]
  [:password "VARCHAR(100)"]
  :table-spec "ENGINE=InnoDB"))

(def create-rooms-table
  "map for further users-table creation"
  (jdbc/create-table-ddl
    :rooms
    [:id :int "PRIMARY KEY AUTO_INCREMENT"]
    [:name "VARCHAR(100)"]
    :table-spec "ENGINE=InnoDB"))