(ns datomic-onprem-tutorial.peer
  "Experiments with datomic peer library."
  (:require
   ;; peer API - has to be used to create a database?
   [datomic.api :as d]))


;;; Dev Setup: https://docs.datomic.com/on-prem/dev-setup.html
(def db-uri "datomic:dev://localhost:4334/hello")

(comment
  
  (d/create-database db-uri)

  (def conn (d/connect db-uri))

  @(d/transact conn [{:db/doc "Hello world"}])
;; => {:db-before datomic.db.Db@7cd35b9c,
;;     :db-after datomic.db.Db@cd529379,
;;     :tx-data
;;     [#datom[13194139534314 50 #inst "2019-10-29T12:28:11.626-00:00" 13194139534314 true] #datom[17592186045419 62 "Hello world" 13194139534314 true]],
;;     :tempids {-9223301668109598142 17592186045419}}

  ;;
  )


;; Peer server can be started separately
;;   bin/run -m datomic.peer-server -h localhost -p 8998 -a myaccesskey,mysecret -d hello,datomic:dev://localhost:4334/hello
;; and now I can use client api!
;; => see `datomic-onprem-tutorial.client`


;;; ... Now continuing with the client api tutorial: https://docs.datomic.com/on-prem/getting-started/transact-schema.html
;;; -> see Peer Getting Started instead: https://docs.datomic.com/on-prem/peer-getting-started.html
;;;

;; schema is just data and you describe your custom attributes using Datomic's built-in attributes
(def movie-schema [{:db/ident :movie/title
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The title of the movie"}

                   {:db/ident :movie/genre
                    :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc "The genre of the movie"}

                   {:db/ident :movie/release-year
                    :db/valueType :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc "The year the movie was released in theaters"}])

(comment

  (def transact-schema-result @(d/transact conn movie-schema))
;; => {:db-before datomic.db.Db@e3efa4c7,
;;     :db-after datomic.db.Db@ff58449a,
;;     :tx-data
  ;;     [#datom[13194139534316 50 #inst "2019-10-29T12:42:27.875-00:00" 13194139534316 true]
  ;;      #datom[72 10 :movie/title 13194139534316 true] #datom[72 40 23 13194139534316 true]
  ;;      #datom[72 41 35 13194139534316 true] #datom[72 62 "The title of the movie" 13194139534316 true]
  ;;      #datom[73 10 :movie/genre 13194139534316 true] #datom[73 40 23 13194139534316 true]
  ;;      #datom[73 41 35 13194139534316 true] #datom[73 62 "The genre of the movie" 13194139534316 true]
  ;;      #datom[74 10 :movie/release-year 13194139534316 true] #datom[74 40 22 13194139534316 true]
  ;;      #datom[74 41 35 13194139534316 true] #datom[74 62 "The year the movie was released in theaters" 13194139534316 true]
 ;;       #datom[0 13 72 13194139534316 true] #datom[0 13 73 13194139534316 true] #datom[0 13 74 13194139534316 true]],
;;     :tempids {-9223301668109598141 72, -9223301668109598140 73, -9223301668109598139 74}}  ;;
  )


;; Transacting Data: https://docs.datomic.com/on-prem/peer-getting-started.html#transacting
(def first-movies [{:movie/title "The Goonies"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Commando"
                    :movie/genre "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title "Repo Man"
                    :movie/genre "punk dystopia"
                    :movie/release-year 1984}])

(comment
  
  (def transact-data-result @(d/transact conn first-movies))
  transact-data-result
;; => {:db-before datomic.db.Db@565c4eec,
;;     :db-after datomic.db.Db@97e3cf46,
;;     :tx-data
  ;;     [#datom[13194139534322 50 #inst "2019-10-29T12:48:36.203-00:00" 13194139534322 true]
  ;;      #datom[17592186045427 72 "The Goonies" 13194139534322 true]
  ;;      #datom[17592186045427 73 "action/adventure" 13194139534322 true]
  ;;      #datom[17592186045427 74 1985 13194139534322 true]
  ;;      #datom[17592186045428 72 "Commando" 13194139534322 true]
  ;;      #datom[17592186045428 73 "action/adventure" 13194139534322 true]
  ;;      #datom[17592186045428 74 1985 13194139534322 true]
  ;;      #datom[17592186045429 72 "Repo Man" 13194139534322 true]
  ;;      #datom[17592186045429 73 "punk dystopia" 13194139534322 true]
  ;;      #datom[17592186045429 74 1984 13194139534322 true]],
;;     :tempids
;;     {-9223301668109598132 17592186045427,
;;      -9223301668109598131 17592186045428,
;;      -9223301668109598130 17592186045429}}
  ;;
  )


;; Query data: https://docs.datomic.com/on-prem/peer-getting-started.html#querying-data
(comment
  
  ;; you have to retrieve current db value first
  (def db (d/db conn))

  ;; how do we ask for all the movies in our database?
  ;; => "find me the ids of all entities which have an attributed called :movie/title"
  (def all-movies-q '[:find ?e :where [?e :movie/title]])
  (def all-movies (d/q all-movies-q db))
  all-movies
;; => #{[17592186045427] [17592186045428] [17592186045429] [17592186045422] [17592186045423] [17592186045424]}

  ;; you could use to get full entity data from IDs via Entitiy API but perhaps you're only interested
  ;; in getting movie titles
  (def all-titles-q '[:find ?movie-title :where [_ :movie/title ?movie-title]])
  (def all-titles (d/q all-titles-q db))
  all-titles
;; => #{["Commando"] ["The Goonies"] ["Repo Man"]}

  ;; what if you want the titles of movies released in 1985?

  (def titles-from-1985-q '[:find ?movie-title
                            :where
                            [?e :movie/title ?movie-title]
                            ;; if you make a typo here you'll get "Unable to resolve entity :movie/year"!
                            [?e :movie/release-year 1985]])
  (def titles-from-1985 (d/q titles-from-1985-q db))
  titles-from-1985
;; => #{["Commando"] ["The Goonies"]}


  ;; finally, to return all the attributes for each movie released in 1985?
  
(def all-data-from-1985-q '[:find ?e ?title ?year ?genre
                            :where
                            [?e :movie/title ?title]
                            [?e :movie/release-year ?year]
                            [?e :movie/genre ?genre]
                            [?e :movie/release-year 1985]
                            ])
  (def all-data-from-1985 (d/q all-data-from-1985-q db))
  all-data-from-1985
  ;; => #{[17592186045427 "The Goonies" 1985 "action/adventure"]
  ;;      [17592186045428 "Commando" 1985 "action/adventure"]}
  )


;; HIstory of values in the database: https://docs.datomic.com/on-prem/peer-getting-started.html#history
(comment

  ;; first update commando movie/genre
  ;; => you need to find it's entity ID:
  (def commando-id (ffirst (d/q '[:find ?e :where [?e :movie/title "Commando"]]
                                db)))

  ;; then issue a transaction:
  (def commando-update-result @(d/transact conn [{:db/id commando-id :movie/genre "future governor"}]))
  commando-update-result
;; => {:db-before datomic.db.Db@97e3cf46,
;;     :db-after datomic.db.Db@e0e9b05a,
;;     :tx-data
  ;;     [#datom[13194139534326 50 #inst "2019-10-29T13:15:53.938-00:00" 13194139534326 true]
  ;;      #datom[17592186045428 73 "future governor" 13194139534326 true]
                                                                     ;; notice 'retraction' here
  ;;      #datom[17592186045428 73 "action/adventure" 13194139534326 false]],
;;     :tempids {}}


  ;; now verify that command has been updated:
  (d/q all-data-from-1985-q db)
  ;; => #{[17592186045427 "The Goonies" 1985 "action/adventure"]
  ;;      [17592186045428 "Commando" 1985 "action/adventure"]
  ;; WHOOPS!
  ;; We issued our transaction against a connection, but we are issuing queries against a database value, which is a snapshot as of a point in time
  ;; we used old db value => need to get a new one!
  (def db (d/db conn))
  (d/q all-data-from-1985-q db)
  ;; => #{[17592186045427 "The Goonies" 1985 "action/adventure"]
  ;;      [17592186045428 "Commando" 1985 "future governor"] 

  ;; we can use `d/as-of` to query against historical version
  ;; here we're using transaction time as returned in the :tx-data when we first transacted the movies
  (def old-db (d/as-of db 13194139534322))
  (d/q all-data-from-1985-q old-db)
  ;; => #{[17592186045427 "The Goonies" 1985 "action/adventure"]
  ;;      [17592186045428 "Commando" 1985 "action/adventure"]}


  ;; to see all the history
  (def hdb (d/history db))
  (d/q '[:find ?genre 
         :where [?e :movie/title "Commando"]
         [?e :movie/genre ?genre]] hdb)
  ;; => #{["action/adventure"] ["future governor"]}

  ;;
  )
