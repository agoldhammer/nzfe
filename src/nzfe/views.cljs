(ns nzfe.views
  (:require
   [nzfe.catview :as cv]
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
    (into [:div.dropdown-content
           {:on-click #(re-frame/dispatch [::events/set-active-time-button
                                           (keyword
                                            (-> % .-target .-id))])}]
          (mapv time-button @button-ids))))

(defn time-dropdown
  []
  (let [active? (re-frame/subscribe [::subs/time-dd-active?])]
    (into [:div#time-dd
           {:class (if @active?
                     "dropdown is-active"
                     "dropdown")
            :on-click #(re-frame/dispatch [::events/toggle-state :time-dd])}
           [:div.dropdown-trigger
            [:i.fa-solid.fa-clock]
            #_[:button.button.has-background-info-light
               {:aria-haspopup true
                :aria-controls "dropdown-menu"}
               [:span.has-background-link-light "Time select"]
               [:span.icon.is-small
                [:i.fas.fa-angle-down {:aria-hidden true}]]]]
           [:div#dropdown-menu.dropdown-menu {:role "menu"}
            (time-buttons)]])))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(defn tabber
  "set up tabbed view"
  []
  (let [active-tab-id @(re-frame/subscribe [::subs/get-active-tab])]
    [:div.tabs
     [:ul
      [:li {:id :tab0 :class (if (= :tab0 active-tab-id) "is-active" "")
            :on-click #(re-frame/dispatch [::events/set-active-tab :tab0])} [:a "Classic"]]
      [:li {:id :tab1 :class (if (= :tab1 active-tab-id) "is-active" "")
            :on-click #(re-frame/dispatch [::events/set-active-tab :tab1])} [:a "New"]]]]))

(defn content-card
  []
  [:div.card
   [:header.card-header.has-background-warning.is-small
    [:p.card-header-title.has-text-primary-light "header"]]
   [:div.card-content.has-background-light
    [:div.content "Lorem ipsum"]]])



(defn main-panel []
  (let [count @(re-frame/subscribe [::subs/item-count])]
    [:div
     [:section.hero.has-background-info.is-small
      [:div.hero-body
       [:div.level
        [:div.level-item [:p.is-small.has-text-primary-light "Nooze Aggregator"]]
        [:div.level-item (time-dropdown)]
        [:div.level-item [:span.is-small.has-text-primary-light "sources"]]
        [:div.level-item [:span.is-small.has-text-primary-light "time"]]
        [:div.level-item [:p.is-small.has-text-primary-light (str count)]]]]]
     (tabber)
     [:section.columns.is-mobile
      (cv/category-column)

      [:div.column.mr-2 "col2"
       (content-card)
       (content-card)]
      #_[:div.column.is-one-quarter "col3"]]]))

(comment
  (time-buttons))
