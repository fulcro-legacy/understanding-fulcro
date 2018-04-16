(ns understanding-fulcro.part02
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.data-fetch :as df]
            [fulcro.client.dom :as dom]))

(defsc Address [this {:keys [address/street]}]
  {:query [:db/id :address/street]
   :ident [:address/by-id :db/id]})

(defsc Person [this {:keys [person/name person/address]}]
  {:query         [:db/id :person/name {:person/address (prim/get-query Address)}]
   :ident         [:person/by-id :db/id]
   :initial-state (fn [{:keys [id name]}] {:db/id id :person/name name})})

(defsc Root [this {:keys [:friend] :as props}]
  {:query         [{:friend (prim/get-query Person)}]
   :initial-state (fn [_] {:friend (prim/get-initial-state Person {:id 1 :name "Sam"})})}
  (dom/div "TODO"))

(defcard-fulcro card
  Root
  {}
  {:inspect-data true})

(defn get-reconciler
  "Get the Fulcro Reconciler from the dev card"
  [] (some-> card-fulcro-app deref :reconciler))

(comment
  (prim/get-initial-state Person {:id 97 :name "Bo"})
  (prim/get-query Person)

  (prim/get-initial-state Root {})
  (prim/query Root {})
  (->
    (prim/query Root {})
    first
    :friend
    meta
    )

  (prim/tree->db (prim/get-query Root) (prim/get-initial-state Root {}) true)

  (let [db (prim/tree->db (prim/get-query Root) (prim/get-initial-state Root {}) true)]
    (prim/db->tree (prim/get-query Root) db db))

  (prim/merge-component! (get-reconciler)
    Person
    {:db/id 2 :person/name "Tom"}
    :replace [:friend])

  (df/load (get-reconciler) :friend Person {:marker false}))
