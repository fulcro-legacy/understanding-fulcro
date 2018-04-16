(ns understanding-fulcro.part03
  (:require
    [fulcro.server :refer [defquery-root defmutation]]))

(defquery-root :best-friend
  (value [env params]
    (Thread/sleep 1000)
    {:db/id          42
     :person/name    "Sally"
     :person/address {:db/id 5 :address/street "115 Main"}}))

(def people
  [{:db/id 1 :person/name "Bobby Sue" :person/address {:db/id (rand-int 1000) :address/street "Next Door"}}
   {:db/id 2 :person/name "Vi" :person/address {:db/id (rand-int 1000) :address/street "Bash"}}
   {:db/id 3 :person/name "That Weird Kid" :person/address {:db/id (rand-int 1000) :address/street "Basement"}}
   {:db/id 4 :person/name "Dreamboat Gilbert" :person/address {:db/id (rand-int 1000) :address/street "Basement Pit"}}
   {:db/id 5 :person/name "Korn" :person/address {:db/id (rand-int 1000) :address/street "On Stage"}}
   {:db/id 6 :person/name "My Dog" :person/address {:db/id (rand-int 1000) :address/street "Fireplace"}}
   {:db/id 7 :person/name "This Chair" :person/address {:db/id (rand-int 1000) :address/street "Under My Butt"}}
   {:db/id 8 :person/name "Pizza" :person/address {:db/id (rand-int 1000) :address/street "Porch"}}])

(defmutation invent-a-friend [params]
  (action [env]
    (println "Server inventing a friend.")
    (rand-nth people)))
