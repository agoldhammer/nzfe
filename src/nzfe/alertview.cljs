(ns nzfe.alertview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]))

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
     [:button.delete]]
    [:div.modal-card-body
     [:p (str msg)]]
    [:footer.modal-card-foot
     [:button.button
      {:on-click close-alert}
      "Close"]]]])