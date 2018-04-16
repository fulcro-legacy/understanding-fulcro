(ns understanding-fulcro.part03
  (:require
    [fulcro.server :refer [defquery-root]]))

(defquery-root :best-friend
  (value [env params]
    (Thread/sleep 1000)
    {:db/id 42
     :person/name "Sally"
     :person/address {:db/id 5 :address/street "115 Main"}}))
