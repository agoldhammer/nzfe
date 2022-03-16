(ns nzfe.authview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

(defn authbox
  "make author checkbox"
  [author]
  [:div.level
   [:div.control
    [:label.checkbox
     [:input {:type :checkbox :checked true :style {:margin 12}
              :on-change #(println "clkd")}] author]]])

(defn authboxes
  "make sequence of authboxes from author list"
  []
  (let [authors @(re-frame/subscribe [::subs/get-authors])]
    [:section.columns.is-mobile
     (into
      [:div.auth-col.column.is-12.mr-4.scrollable]
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
   [:span.is-small.tooltiptext "Select authors to display"]])