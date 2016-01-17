(ns utilities.core)

(defn elem-in-col?
  "test if elem in col"
  "return true if so and nil otherwise"
  [elem col]
  (some #(= elem %) col))

(defn get-uuid
  "return new uuid"
  [] (str (java.util.UUID/randomUUID)))

;
;(let [db (myapp.db/connection)]
;  (jdbc/with-connection db
;                        (jdbc/with-query-results rs ["select * from foo"]
;                                                 (doseq [row rs]
;                                                   (println row))))))
;(sql/insert! db :fruit
;           {:name "Apple" :appearance "rosy" :cost 24}
;           {:name "Orange" :appearance "round" :cost 49})

;(def tstr (j/create-table-ddl
;  :users
;  [:id :int "PRIMARY KEY AUTO_INCREMENT"]
;  [:name "VARCHAR(32)"]
;  [:email "VARCHAR(32)"]
;  [:active? "TINYINT(1)"]
;  [:ban? "TINYINT(1)"]
;  [:passw-hash "VARCHAR(100)"]
;  :table-spec "ENGINE=InnoDB"))

;; insert into database
;(j/insert! mysql-db :fruit
;           {:name "Apple" :appearance "rosy" :cost 24}
;           {:name "Orange" :appearance "round" :cost 49})

;; создание таблицы
;(j/db-do-commands mysql-db
;                  (j/create-table-ddl
;                    :users
;                    [:id :int "PRIMARY KEY AUTO_INCREMENT"]
;                    [:name "VARCHAR(32)"]
;                    [:email "VARCHAR(32)"]
;                    [:active "VARCHAR(2)"]
;                    [:ban "VARCHAR(2)"]
;                    [:seenLasttTime "DATE"]
;                    [:regdate "DATE"]
;                    [:password "VARCHAR(100)"]
;                    :table-spec "ENGINE=InnoDB"))