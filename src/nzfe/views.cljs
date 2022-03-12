(ns nzfe.views
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

;; time buttons

(defn time-button [button-id]
  (let [active? (= button-id @(re-frame/subscribe [::subs/time-button-active-id]))
        cls (if active? "button is-small is-warning" "button is-small")]
    [:div.navbar-item
     [:button {:id button-id
               :class cls} @(re-frame/subscribe [::subs/button-id-to-text
                                                 button-id])]]))


(defn time-buttons []
  (let [button-ids @(re-frame/subscribe [::subs/get-time-button-ids])]
    (into [:div.navbar.has-background-grey-lighter
           {:on-click #(re-frame/dispatch [::events/set-active-time-button
                                           (keyword
                                            (-> % .-target .-id))])}]
          (mapv time-button button-ids))))

(defn time-dropdown
  []
  (let [button-ids @(re-frame/subscribe [::subs/get-time-button-ids])]
    (into [:div.nabar.has-background-grey-lighter
           [:div.dropdown.is-hoverable
            [:div.dropdown-trigger
             [:button.button
              [:span "Time select"]
              [:span.icon.is-small
               [:i.fas.fa-angle-down]]]]
            [:div#dropdown-menu.dropdown-menu
             [:div.dropdown-content
              [:a.dropdown-item.is-active "item1"]
              [:a.dropdown-item "item2"]]
             #_(mapv time-button button-ids)]]])))


(defn main-panel []
  [:div
   [:section.hero.is-primary.is-small
    [:div.hero-body
     [:p.title.is-small
      "Nooze Aggregator"]]]
   #_(time-buttons)
   (time-dropdown)])

(comment
  (time-buttons))
