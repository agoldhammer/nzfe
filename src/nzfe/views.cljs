(ns nzfe.views
  (:require
   [re-frame.core :as re-frame]
   #_[nzfe.events :as events]
   [nzfe.subs :as subs]))



(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1.title.has-text-danger
      "Hello there from " @name]
     #_[display-re-pressed-example]]))
