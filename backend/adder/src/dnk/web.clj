(ns dnk.web
  (:use compojure.core)
  (:use ring.middleware.json-params)
  (:use ring.middleware.params
        ring.util.response
        ring.adapter.jetty)

(:require [clj-json.core :as json]))

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defroutes handler
           (GET "/" []
                (json-response {"hello" "world"}))

           (POST "/login" [login, password]
                (json-response {"resultCode" 0, "resultDesc" password})))

(defn -main [& args]
  (println "Working!"))

(def app
  (-> handler
      wrap-json-params))

;(run-jetty app {:port 8080})
