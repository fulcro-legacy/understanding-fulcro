(ns understanding-fulcro.part03
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.impl.data-targeting :as t]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.dom :as dom]
            [fulcro.client.data-fetch :as df]))

;; NOTE: We're using a real server in some of this, which means you have to load the cards from that server or they
;; won't work!
;; At a command prompt:
;;
;; $ lein repl
;; user=> (go)
;;
;; will start the server on port 3000

(declare Person)

(defmutation invent-an-age [{:keys [person-id]}]
  (action [{:keys [state]}]
    (swap! state assoc-in [:person/by-id person-id :person/age] (rand-int 55))))

(defmutation invent-a-friend [params]
  (remote [{:keys [state ast]}]
    (-> ast
      (m/returning state Person)
      (m/with-target (t/append-to [:friends])))))

(defsc Address [this {:keys [address/street]}]
  {:query [:db/id :address/street]
   :ident [:address/by-id :db/id]}
  (dom/div
    (dom/span "Street: " street)))

(def ui-address (prim/factory Address {:keyfn :db/id}))

(defsc Person [this {:keys [db/id person/name person/address person/age]}]
  {:query         [:db/id :person/name :person/age {:person/address (prim/get-query Address)}]
   :ident         [:person/by-id :db/id]
   :initial-state (fn [{:keys [id name]}] {:db/id id :person/name name})}
  (dom/div
    (dom/h4 name)
    (dom/ul
      (dom/li "Age: " (or age "???")
        (when-not age
          (dom/button {:onClick #(prim/transact! this `[(invent-an-age {:person-id ~id})])} "Invent Age")))
      (when address
        (dom/li "Address: "
          (ui-address address))))))

(def ui-person (prim/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [:friends] :as props}]
  {:query         [{:friends (prim/get-query Person)}]
   :initial-state {:friends []}}
  (dom/div
    (dom/h2 "Friends")
    (dom/button {:onClick #(prim/transact! this `[(invent-a-friend {})])}
      "Need More Friends!!!")
    (if friends
      (dom/ul
        (map ui-person friends))
      (dom/div "Sad. You seem to have no friends."))))

(defcard-fulcro card
  Root
  {}
  {:inspect-data true
   :fulcro       {:started-callback (fn [app]
                                      (df/load app :best-friend Person
                                        {:marker false
                                         :target (t/append-to [:friends])}))}})
