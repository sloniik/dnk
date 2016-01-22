(ns utilities.core
  (:import java.text.SimpleDateFormat
           java.util.Date))

(defn elem-in-col?
  "test if elem in col
  return true if so and nil otherwise"
  [elem col]
  (some #(= elem %) col))

(defn now [] (.format (SimpleDateFormat. "dd.MM.yyyy HH:mm:ss") (Date.)))

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
             (if (nil? (first (drop 1 v)))
               (str vstr (first v))
               (str vstr (first v) str-delimiter))))))
;;test
(vec->str-with-delimiter [1 2 3] ", ")

(defn vec-map->str
  "vector of maps convert to vector by key and further convert it to string by delimeter"
  [vec key delimeter]
  (let [v (vec-map->vec-by-key vec key)]
    (vec->str-with-delimiter v delimeter)))

(defn concat-vec->str-vec
  "concat two vectors to vector of strings
  second vector item checked for being strings and wrap them in apostrophe"
  [vec1 vec2]
  (loop [v1 vec1
         v2 vec2
         vstr []]
    (let [a (first v1)
          bb (first v2)
          b (if (string? bb)
              (str "'" bb "'")
              bb)]
      (cond
        (and (nil? a) (nil? b))
        vstr
        (or (and (nil? a) (not (nil? b)))
            (and (not (nil? a)) (nil? b)))
        "Vectors should be of the same size"
        :else
        (recur
          (drop 1 v1)
          (drop 1 v2)
          (conj vstr (str a " " b)))))))
;;test
(concat-vec->str-vec ["=" ">" "like"] [1 "devArt" true])




(defn concat-vec->str
  "concat two vectors with delimiter"
  [vec1 vec2 delimiter]
  (loop [v1 vec1
         v2 vec2
         vstr ""]
    (let [a (first v1)
          b (first v2)
          ]
      (cond
        (and (nil? a) (nil? b))
        vstr
        (or (and (nil? a) (not (nil? b)))
            (and (not (nil? a)) (nil? b)))
        "Vectors should be of the same size"
        :else
        (recur
          (drop 1 v1)
          (drop 1 v2)
          (if (nil? (first (drop 1 v1)))
            (str vstr a " " b)
            (str vstr a " " b " " delimiter " ")))))))
;;test
(concat-vec->str [1 2 3] ["=?" "> ?" "like ?"] "and")
(concat-vec->str ["user_name" "id_user"] ["=" "<"] "and")

