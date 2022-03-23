(ns nzfe.alertview
  (:require [nzfe.events :as events]
            [re-frame.core :as re-frame]))

(defn close-alert
  []
  (re-frame/dispatch [::events/alert nil]))

(defn alert-view
  [msg]
  ;; close alert after 5 secs
  (js/setTimeout close-alert 5000)
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Error!"]
     [:button.delete
      {:on-click close-alert}]]
    [:div.modal-card-body
     [:p (str msg)]]
    [:footer.modal-card-foot
     [:button.button
      {:on-click close-alert}
      "Close"]]]])