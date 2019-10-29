(ns datomic-onprem-tutorial.client
  "Experiments with datomic client api.
  See `datomic-onprem-tutorial.peer` for a complete version using peer library."
  (:require
   ;; client API
   [datomic.client.api :as d]))


;;; Connect to a database: https://docs.datomic.com/on-prem/getting-started/connect-to-a-database.html
;;; 

;; server config 
(def cfg {:server-type :peer-server
          :access-key "myaccesskey"
          :secret "mysecret"
          :endpoint "localhost:8998"
          ;; this is needed to make it work on localhost: https://forum.datomic.com/t/ssl-handshake-error-when-connecting-to-peer-server-locally/1067
          ;; and https://docs.datomic.com/on-prem/peer-server.html#connecting
          :validate-hostnames false})

;; connect to the database
(comment

  (def client (d/client cfg))

  (def conn (d/connect client {:db-name "hello" :timeout 1000}))

  ;;
  )


;;; query the data: https://docs.datomic.com/on-prem/getting-started/query-the-data.html
(comment
  
  (def db (d/db conn))
  db
;; => {:t 1014, :next-t 1015, :db-name "hello", :database-id "datomic:dev://localhost:4334/hello", :type :datomic.client/db}
  (def all-movies-q '[:find ?e
                      :where [?e :movie/title]])
  (d/q all-movies-q db)


  ;;
  )
