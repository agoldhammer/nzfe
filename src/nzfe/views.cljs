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

#_(defn category-card
    [category]
    [:div.card
     [:header.card-header.has-background-info.is-small
      [:p.card-header-title.has-text-primary-light category]]
     [:div.card-content.has-background-light
      [:div.content "Topic1"]
      [:div.content "Topic2"]]])

(defn content-card
  []
  [:div.card
   [:header.card-header.has-background-warning.is-small
    [:p.card-header-title.has-text-primary-light "header"]]
   [:div.card-content.has-background-light
    [:div.content "Lorem ipsum"]]])

(defn recent-card
  []
  [:div.card
   {:id "recent"
    :on-click #(re-frame/dispatch [::events/get-recent])}
   [:header.card-header.has-background-danger.is-small
    [:p.card-header-title.has-text-primary-light "Latest!"]]])

(defn topic-button
  [[topic desc]]
  [:div.content {:id topic
                 :on-click #(re-frame/dispatch [::events/topic-req topic])}
   desc])

(defn category-card
  [category]
  (let [topic-descs @(re-frame/subscribe [::subs/topic-descs-by-category category])]
    [:div.card
     [:header.card-header.has-background-info.is-small
      [:p.card-header-title.has-text-primary-light
       {:on-click #(re-frame/dispatch [::events/category-req  category])}
       (name category)]]
     (into [:div.card-content.has-background-light]
           (mapv #(topic-button %1) topic-descs))]))

#_(defn category-cards []
    (let [categories @(re-frame/subscribe [::subs/categories])]
      (into [[:div (recent-button)]]
            (mapv category-card categories))))

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
        [:div.level-item [:p.is-small.has-text-primary-light (str count)]]]]
      #_[:div.navbar.has-background-grey-lighter
         (time-dropdown)]]
     (tabber)
     [:section.columns.is-mobile
      [:div.column.is-one-quarter.ml-2 "col1"
       #_(category-cards)
       (recent-card)
       (category-card :Culture)
       (content-card)
       #_(category-card)]
      [:div.column.mr-2 "col2"
       (content-card)
       (content-card)]
      #_[:div.column.is-one-quarter "col3"]]]))

(comment
  (time-buttons))
