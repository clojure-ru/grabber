(ns git-grabber.storage.repositories
  (:require [korma.core :refer :all]
            [git-grabber.storage.config :refer :all]
            [clojure.set :refer [rename-keys]]
            [clj-time.coerce :refer [from-string to-sql-date]]
            [clj-time.core :as t]
            [taoensso.timbre :as timbre]
            [git-grabber.storage.owners :refer [get-owner-id-with-name]])
  (:refer-clojure :exclude [update]))

(defentity repositories)

(def repository-fields [:id :name :description :size :fork
                        :created_at :updated_at :pushed_at])

(defn get-all-repositories-paths []
  (map :full_name (select repositories (fields :full_name))))

(defn get-repository-id-by-path [path]
  (-> (select repositories
                   (fields :id)
                   (where {:full_name path})
                   (limit 1))
      first
      :id))

(defn prepare-repository-owner [repository fields]
  (if-let [owner-id (get-owner-id-with-name (-> repository :owner :login))]
    (merge fields {:owner_id owner-id})
    fields))

(defn prepare-fields-keys [repository]
  (rename-keys (select-keys repository repository-fields) {:id :github_id,
                                                           :fork :is_fork}))

(defn convert-date [repository date-key]
  {date-key (to-sql-date (from-string (date-key repository)))})

(defn prepare-repository-dates [fields]
  (merge fields
         (convert-date fields :created_at)
         (convert-date fields :updated_at)
         (convert-date fields :pushed_at)))

(defn update-repository-info [repository-map repository-path]
  (if-not repository-map
          (timbre/info (str "[Empty Repository Info for update] "
                     "|| storage/repositories.cljs:48 || "
                     repository-path))
          (let [prepare-fields (-> repository-map
                                   prepare-fields-keys
                                   prepare-repository-dates)]
            (update repositories
                    (set-fields (prepare-repository-owner repository-map prepare-fields))
                    (where (select-keys repository-map [:full_name]))))))

(defn set-repository-clojure-flag [repository-path clojure-flag]
  (update repositories 
          (set-fields {:is_clojure clojure-flag})
          (where {:full_name repository-path})))

