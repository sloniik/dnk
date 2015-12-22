(ns utilities.core)

(defn elem-in-col?
  "test if elem in col"
  "return true if so and nil otherwise"
  [elem col]
  (some #(= elem %) col))

(defn uuid
  "returns UUID"
  [] (.toString (java.util.UUID/randomUUID)))