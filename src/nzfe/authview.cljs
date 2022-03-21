(ns nzfe.authview
  (:require [nzfe.events :as events]
            [nzfe.subs :as subs]
            [re-frame.core :as re-frame]))

(defn all-or-none-box
  []
  (let [is-checked? @(re-frame/subscribe [::subs/display-all-authors?])]
    [:div.level
     [:div.control
      [:label.checkbox
       [:input {:type :checkbox :checked is-checked? :style {:margin 12}
                :on-change #(re-frame/dispatch
                             [::events/set-reset-author-display-states (not is-checked?)])}]
       "All/None"]]]
    #_[:div.level [:div.is-divider {:data-content "Authors"}]]))

(defn authbox
  "make author checkbox"
  [author]
  (let [is-checked? @(re-frame/subscribe [::subs/get-author-display-state author])]
    [:div.level
     [:div.control
      [:label.checkbox
       [:input {:type :checkbox :checked is-checked? :style {:margin 12}
                :on-change #(re-frame/dispatch
                             [::events/toggle-author-display-state author])}]
       author]]]))

(defn authboxes
  "make sequence of authboxes from author list"
  []
  (let [authors @(re-frame/subscribe [::subs/get-authors])]
    [:section.columns.is-mobile.pt-3
     (into
      [:div#authview.auth-col.column.is-6.mr-4.scrollable
       (all-or-none-box)
       [:div.level  ".............................................."]]
      (mapv authbox authors))]))

(defn set-or-reset-display-author
  []
  (let [state @(re-frame/subscribe [::subs/now-displaying])]
    (if (= state :authors)
      (re-frame/dispatch [::events/set-now-displaying :classic])
      (re-frame/dispatch [::events/set-now-displaying :authors]))))


(defn author-select-icon
  "view/select authors to display"
  []
  [:div.content.tooltip
   {:on-click set-or-reset-display-author}
   [:i.fa-solid.fa-person]
   [:span.is-small.tooltiptext "Toggle author select"]])