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

(defsc Address [this {:keys [address/street]}]
  {:query [:db/id :address/street]
   :ident [:address/by-id :db/id]}
  (dom/div
    (dom/span "Street: " street)))

(def ui-address (prim/factory Address {:keyfn :db/id}))

(defsc Person [this {:keys [person/name person/address]}]
  {:query         [:db/id :person/name {:person/address (prim/get-query Address)}]
   :ident         [:person/by-id :db/id]
   :initial-state (fn [{:keys [id name]}] {:db/id id :person/name name})}
  (dom/div
    (dom/h3 "Person")
    (dom/ul
      (dom/li (str "Name: " name))
      (when address
        (dom/li "Address: "
          (ui-address address))))))

(def ui-person (prim/factory Person {:keyfn :db/id}))

(defsc Root [this {:keys [:friends] :as props}]
  {:query         [{:friends (prim/get-query Person)}]
   :initial-state {:friends []}}
  (dom/div
    (dom/h2 "Friends")
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
