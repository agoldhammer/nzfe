(ns nzfe.custview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   #_["bulma-calendar/dist/js/bulma-calendar.min.js" :as bulmaCalendar]))

(defn custom-time-view
  []
  (let [start-el [:input#startcal {:type :date :defaultValue "2022-03-15"
                                   :on-select #(println (.. % -target -value))}]
        end-el [:input#endcal {:type :date
                               :defaultValue "2022-03-17"
                               :on-select #(println (.. % -target -value))}]]
    [:div.modal.is-active
     [:div.modal-background]
     [:div.modal-card
      [:header.modal-card-head.has-background-primary
       [:p.modal-card-title "Custom query panel"]
       [:button.delete
        {:on-click #(re-frame/dispatch [::events/set-now-displaying :classic])}]]
      [:div.modal-card-body.has-background-light
       [:div.level
        [:div.level-item
         #_[:label.mr-4 "Custom query"]
         [:textarea.textarea.is-medium {:placeholder "Custom query text"}]]]
       [:div.level
        [:div.level-item
         [:div.control
          [:label.mr-4 "Start Date"]
          start-el]]
        [:div.level-item
         [:div.control
          [:label.mr-4 "End Date"]
          end-el]]]]
      [:footer.modal-card-foot.has-background-primary
       [:button.button
        {:on-click #(re-frame/dispatch [::events/set-now-displaying :classic])}
        "Close"]]]]))