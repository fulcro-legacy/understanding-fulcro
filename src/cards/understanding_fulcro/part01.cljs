(ns understanding-fulcro.part01
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.data-fetch :as df]
            [fulcro.client.dom :as dom]))

(defmutation bump [params]
  (action [{:keys [state]}]
    (swap! state update :n inc)))

(defsc Root [this {:keys [:n]}]
  {:query         [:n]
   :initial-state (fn [_] {:n 1})}
  (dom/div (str "The number is " n)
    (dom/button {:onClick #(prim/transact! this `[(bump {})])} "Bump")))

(defcard-fulcro card
  Root
  {}
  {:inspect-data true})

