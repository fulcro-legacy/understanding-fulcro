(ns understanding-fulcro.server
  (:require
    [fulcro.easy-server :refer [make-fulcro-server]]
    understanding-fulcro.part02
    understanding-fulcro.part03
    ))

(defn build-server
  [{:keys [config] :or {config "config/dev.edn"}}]
  (make-fulcro-server
    :parser-injections #{:config}
    :config-path config))



