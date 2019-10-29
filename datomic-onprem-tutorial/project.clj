(defproject datomic-onprem-tutorial "0.1.0-SNAPSHOT"
  :description "Examples from the datomic on-prem docs: https://docs.datomic.com/on-prem/getting-started/connect-to-a-database.html"
  :url "https://github.com/jumarko/datomic-experiments/datomic-onprem-tutorial"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/client-pro "0.9.37"]
                 [com.datomic/datomic-pro "0.9.5981"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}

  )
