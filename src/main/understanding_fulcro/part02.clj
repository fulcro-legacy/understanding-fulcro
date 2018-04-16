(ns understanding-fulcro.part02
  (:require
    [fulcro.server :refer [defquery-root]]))

(defquery-root :friend
  (value [env params]
    {:db/id 42
     :person/name "Person From Server"
     :person/address {:db/id 5 :address/street "111 Main"}}))
