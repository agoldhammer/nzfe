(ns nzfe.alertview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]))

(defn alert-view
  [msg]
  ;; close alert after 5 secs
  (js/setTimeout #(re-frame/dispatch [::events/alert nil]) 5000)
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
      {:on-click #(re-frame/dispatch [::events/alert nil])}
      "Close"]]]])