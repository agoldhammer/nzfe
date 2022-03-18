(ns nzfe.custview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.timeutils :as tu]
   [nzfe.events :as events]
   [nzfe.subs :as subs]
   #_["bulma-calendar/dist/js/bulma-calendar.min.js" :as bulmaCalendar]))

(defn set-date
  "set start or end date in app-db"
  [start-or-end datestring]
  (if (= start-or-end :start)
    (re-frame/dispatch [::events/set-date :start datestring])
    (re-frame/dispatch [::events/set-date :end datestring])))

(defn close-custview-submit
  "close the custom view and submit custom query"
  []
  (re-frame/dispatch-sync [::events/set-now-displaying :classic])
  (re-frame/dispatch [::events/submit-query]))


(defn custom-time-view
  []
  (let [[start end] @(re-frame/subscribe [::subs/get-start-end])
        start-el [:input#startcal {:type :date
                                   :defaultValue (tu/datestring start)
                                   :on-select #(set-date :start (.. % -target -value))}]
        end-el [:input#endcal {:type :date
                               :defaultValue (tu/datestring end)
                               :on-select #(set-date :end (.. % -target -value))}]]
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
         [:textarea.textarea.is-medium {:placeholder "Custom query terms"
                                        :on-change #(re-frame/dispatch
                                                     [::events/set-query-text
                                                      (.. % -target -value)])}]]]
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
       [:div.level
        [:div.level-item
         [:button.button
          {:on-click #(re-frame/dispatch [::events/set-now-displaying :classic])}
          "Close"]
         [:button.button
          {:on-click close-custview-submit}
          "Submit Query"]]]]]]))
