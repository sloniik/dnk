;; There are function which are for working with database
;; low-level functions

(ns database.core
  (:use user-info.user-const :as uc))

(defn get-user-list
  "TBD: returns list of all users"
  []
  uc/user-list)



