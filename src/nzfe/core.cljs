(ns nzfe.core
  (:require [nzfe.config :as config]
            [nzfe.events :as events]
            [nzfe.views :as views]
            [re-frame.core :as re-frame]
            [reagent.dom :as rdom]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

#_:clj-kondo/ignore
(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::events/initialize-content])
  (dev-setup)
  (mount-root))
