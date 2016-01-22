(ns utilities.core)

(defn elem-in-col?
  "test if elem in col"
  "return true if so and nil otherwise"
  [elem col]
  (some #(= elem %) col))

(defn get-uuid
  "return new uuid"
  [] (str (java.util.UUID/randomUUID)))

(defn vec-map->vec-by-key
  "takes vector of map and by key returns vector"
  [vec key]
  (loop [v vec
         result-vec []]
      (if (nil? (key (first v)))
        result-vec
        (recur (drop 1 v)
               (conj result-vec (key (first v)))))))
;; test
(vec-map->vec-by-key [{:a 1 :b 2} {:a 2} {:a 3} {:b 2}] :a)

(defn vec->str-with-delimiter
  "takes vector and convert it to string with delimeter"
  [vec str-delimiter]
  (loop [v vec
         vstr ""]
    (if (nil? (first v))
      vstr
      (recur (drop 1 v)
             (str vstr (first v) str-delimiter)))))
;;test
(vec->str-with-delimiter [1 2 3] " ")

