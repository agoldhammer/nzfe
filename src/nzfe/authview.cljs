(ns nzfe.authview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

(defn author-panel
  "view/select authors to display"
  []
  (let [authors @(re-frame/subscribe [::subs/get-authors])]
    (println authors)
    [:aside.menu
     [:p.menu-label
      "Select Authors"]]))