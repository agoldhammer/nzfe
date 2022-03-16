(ns nzfe.authview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

(defn author-dd
  "view/select authors to display"
  []
  (let [authors @(re-frame/subscribe [::subs/get-authors])]
    #_(println authors)
    [:div#authsel.dropdown.is-large
     [:div.dropdown-trigger.tooltip
      [:span.is-small.tooltiptext "Select authors to display"]
      [:i.fa-solid.fa-person]
      [:div.dropdown-menu
       [:div.dropdown-content
        [:a.dropdown-item.is-active "item"]
        [:a.dropdown-item "item2"]
        #_[:a.dropdown-item
           [:div.control
            [:label.checkbox
             [:input {:type :checkbox :checked true}]]]]
        #_[:label.checkbox
           [:input {:type "checkbox"} "chk2"]]]]]]))