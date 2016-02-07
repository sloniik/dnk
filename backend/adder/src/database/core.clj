;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:gen-class)
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [clojure.java.jdbc :as jdbc]
            [jdbc.pool.c3p0 :as pool]
            [utilities.core :as u]
            [database.errors :as err]
            [korma.core :as k]
            [korma.db :as kdb]))


(def db (kdb/mysql
                {:classname   "com.mysql.jdbc.Driver"
                 :subprotocol "mysql"
                 :ssl?        false
                 :subname     "//127.0.0.1:3306/dnk_test"
                 :user        "dnk_test"
                 :password    "dnk_test"
                 :make-pool?  true}))
(kdb/defdb korma-db db)

;;; === MAIN_DB_FUNCTIONs ===
;(defn select-col
;  "return specific column from table"
;  [db-spec select-map]
;    (jdbc/query db-spec [(str "select " (name (:col-name select-map))
;                              " from "  (name (:table-name select-map)))]))
;
;;;korma-mod
;(defn k-select-col
;  [select-map]
;  (let [t-n (:table-name select-map)
;        c-n (:col-name   select-map)]
;  (k/select t-n
;            (k/fields c-n))))
;
;(defn select-all
;  "return all values from table "
;  [db-spec table-name]
;  (select-col db-spec (u/sel-n-upd-map (name table-name) "*")))
;
;;;korma-mod
;(defn k-select-all
;  [table-name]
;  (k/select table-name))
;
;(defn select-col-by-field
;  "return specific column from table where field-name = field-val"
;  [db-spec select-map]
;  (jdbc/query db-spec
;              [(str "select " (name (:col-name   select-map))
;                    " from "  (name (:table-name select-map))
;                    " where " (name (:field-name select-map)) " = ?")
;               (:field-val select-map)]))
;;;korma-mod
;(defn k-select-col-by-field
;  [select-map]
;  (let [t-n (:table-name select-map)
;        c-n (:col-name select-map)
;        f-n (:field-name select-map)
;        f-v (:field-val select-map)]
;    (k/select t-n
;              (k/fields c-n)
;              (k/where (= f-n f-v)))))
;;(k-select-col-by-field  (u/sel-n-upd-map :fruit :name :cost 2000))
;
;(defn select-all-by-field
;  "return all values from specified table with col-id-name and id-value"
;  [db-spec select-map]
;  (select-col-by-field db-spec (u/sel-n-upd-map (:table-name select-map)
;                                                "*"
;                                                (:field-name select-map)
;                                                (:field-val select-map))))
;;;korma-mod
;(defn k-select-all-by-field
;  [select-map]
;  (let [t-n (:table-name select-map)
;   f-n (:field-name select-map)
;   f-v (:field-val select-map)]
;  (k/select t-n
;            (k/where (= f-n f-v)))))
;;(k-select-all-by-field (u/sel-n-upd-map :users :id_user 2))
;
;;;==DEPRICATED
;(defn select-cols-multi-cond
;  "on input:
;    vector of columns to select
;    vector of maps in where: {:col-name :operation :col-val}
;  return select"
;  [db-spec table-name col-name-vec cond-map-array]
;  (let [select-col-names  (u/vec->str-with-delimiter col-name-vec ", ")
;        where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
;        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
;        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
;        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
;        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
;    (jdbc/query db-spec
;                [(str "select " select-col-names
;                      " from " table-name
;                      " where " w-cond)])))
;
;(defn insert-data
; "insert data (new-record-map) to the table (table-name-key)"
; [db-spec table-name new-record-map]
; (jdbc/insert! db-spec
;               (name table-name)
;               new-record-map))
;
;;;korma-mod
;(defn k-insert-data
;  [table-name new-record-map]
;  (k/sql-only
;  (k/insert table-name
;            (k/values new-record-map))))
;;(k-insert-data :fruit {:name "Забор" :appearance "Острый" :cost 100 :flag true})
;
;(defn update-data
;  "update string (update-record-map) in the table (table-name-key) where update-cond-map (:table-name :field-name field-val)"
;  [db-spec update-record-map update-cond-map]
;  (jdbc/update! db-spec
;                (name (:table-name update-cond-map))
;                update-record-map
;                [(str (name (:field-name update-cond-map)) " = ? ") (:field-val update-cond-map)]))
;
;;;korma-mod
;(defn k-update-data
;  [update-record-map update-cond-map]
;  (let [t-n (:table-name update-cond-map)
;        f-n (:field-name update-cond-map)
;        f-v (:field-val update-cond-map)]
;      (k/update t-n
;                (k/set-fields update-record-map)
;                (k/where (= f-n f-v)))))
;;(k-update-data {:cost 2000} (u/sel-n-upd-map :fruit :id_name 14))
;
;(defn update-data-multi-cond
;  "update string (update-record-map) in the table (table-name-key) where conditions are in map cond-map-array"
;  [db-spec table-name update-record-map cond-map-array]
;  (let [where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
;        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
;        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
;        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
;        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
;    (jdbc/update! db-spec (name table-name) update-record-map [w-cond])))
;
;(defn delete-data
;  "delete data from table with some condition"
;  [db-spec update-map]
;  (jdbc/delete! db-spec
;                (name (:table-name update-map))
;                [(str (name (:field-name update-map)) " = ? ") (:field-val update-map)]))
;
;;;korma-mod
;(defn k-delete-data
;  [update-map]
;  (let [t-n (:table-name update-map)
;        f-n (:field-name update-map)
;        f-v (:field-val update-map)]
;    (k/delete t-n
;              (k/where (= f-n f-v)))))
;;(k-delete-data (u/sel-n-upd-map :users :user_name "devAer"))
;
;(defn delete-data-multi-cond
;  "delete in the table (table-name-key) where conditions are in map cond-map-array"
;  [db-spec table-name cond-map-array]
;  (let [where-col-names   (u/vec-map->vec-by-key cond-map-array :field-name)
;        where-operation   (u/vec-map->vec-by-key cond-map-array :operation)
;        where-col-val     (u/vec-map->vec-by-key cond-map-array :field-val)
;        where-cond        (u/concat-vec->str-vec where-operation where-col-val)
;        w-cond            (u/concat-vec->str where-col-names where-cond " and ")]
;    (jdbc/delete! db-spec
;                  (name table-name)
;                  [w-cond])))
;
;
;(defn selc
;  [table-name col-name text]
;  (k/select table-name
;             (k/where {col-name [like (str text "%")]})
;             ))
;
;(selc :fruit :name "Ca")

;;TODO: необходимы функции для chat. Например, в create-room на вход мапа, где есть ссылка на чат, если он есть. А кто созда
;;TODO: нет функций для работы с таблицей game_users ++ я плохо понимаю назначение это таблицы (написал в письме)