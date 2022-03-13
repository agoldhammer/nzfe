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
  (let [button-ids (re-frame/subscribe [::subs/get-time-button-ids])]
    (into [:div.navbar.has-background-grey-lighter
           {:on-click #(re-frame/dispatch [::events/set-active-time-button
                                           (keyword
                                            (-> % .-target .-id))])}]
          (mapv time-button @button-ids))))

(defn time-dropdown
  []
  (let [#_#_button-ids @(re-frame/subscribe [::subs/get-time-button-ids])
        active? (re-frame/subscribe [::subs/time-dd-active?])]

    (fn []
      (into [:div#time-dd.dropdown
             {:class (when @active? "is_active")
              :on-click #(re-frame/dispatch [::events/toggle-state :time-dd])}
             [:div.dropdown-trigger
              [:button.button
               {:aria-haspopup true
                :aria-controls "dropdown-menu"}
               [:span "Time select"]
               [:span.icon.is-small
                [:i.fas.fa-angle-down {:aria-hidden true}]]]]
             [:div#dropdown-menu.dropdown-menu {:role "menu"}
              [:div.dropdown-content
               [:a.dropdown-item "item1"]
               [:a.dropdown-item "item2"]]
              #_(mapv time-button button-ids)]]))))


(defn main-panel []
  [:div
   [:section.hero.is-primary.is-small
    [:div.hero-body
     [:p.title.is-small
      "Nooze Aggregator"]]
    [:div.navbar.has-background-grey-lighter
     [time-dropdown]]]])

(comment
  (time-buttons))
