(ns user-info.user-const)
(def user-list [{:id 1
                 :name "Sergey"
                 :email "serge@me.com"
                 :passw-hash 123
                 }
                {:id 2
                 :name "Ivan"
                 :email "ivan@me.com"
                 :passw-hash 123
                 }
                {:id 3
                 :name "Vasilisa"
                 :email "vas@me.com"
                 :passw-hash 123
                 }])

(def eligible-user {:id 1
                    :name "Sergey"
                    :email "serge@me.com"
                    :passw-hash 123})
