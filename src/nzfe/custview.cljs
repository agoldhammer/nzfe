(ns nzfe.custview
  (:require [nzfe.events :as events]
            [nzfe.subs :as subs]
            [nzfe.timeutils :as tu]
            [re-frame.core :as re-frame]))

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
        candidate-query @(re-frame/subscribe [::subs/get-query-text])
        query (if (= candidate-query "") "Custom query terms" candidate-query)
        start-el [:input#startcal {:type :date
                                   :defaultValue (tu/datestring start)
                                   :on-change #(set-date :start (.. % -target -value))}]
        end-el [:input#endcal {:type :date
                               :defaultValue (tu/datestring end)
                               :on-change #(set-date :end (.. % -target -value))}]]
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
         [:textarea.textarea.is-medium {:placeholder query
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
