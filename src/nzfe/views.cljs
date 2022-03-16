(ns nzfe.views
  (:require [clojure.string :as string]
            [goog.string :as gstring]
            [nzfe.authview :as authview]
            [nzfe.catview :as cv]
            [nzfe.events :as events]
            [nzfe.subs :as subs]
            [re-frame.core :as re-frame]))

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
                     "dropdown tooltip")
            :on-click #(re-frame/dispatch [::events/toggle-state :time-dd])}
           [:div.dropdown-trigger
            [:i.fa-solid.fa-clock]
            [:span.tooltiptext "Time frame selector"]
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

;; functions below are used in building articles
;; need to turn urls into links and eliminate from text

(defn link-url
  [url]
  [:a {:href url :target "_blank"} " ...more \u21aa"])

(def re-url #"https?://\S+")

(defn extract-urls
  [text]
  (re-seq re-url text))

(defn suppress-urls
  [text]
  (string/replace text re-url ""))

(defn urlize
  [text]
  (let [urls (extract-urls text)
        modtext (gstring/unescapeEntities (suppress-urls text))]
    (into [:p.art-content modtext] (mapv link-url urls))))

;; --- end of urlize-related funcs ----------

(defn make-article-card
  "from status, create article card"
  [{:keys [source created_at author text]}]
  [:div.card
   [:header.card-header.has-background-info.is-small
    [:p.card-header-title.has-text-primary-light (string/join " " [author created_at source])]]
   [:div.card-content.has-background-light
    [:div.content (urlize text)]]])

(defn article-column
  "make column of articles"
  []
  (let [statuses @(re-frame/subscribe [::subs/filtered-statuses])
        ;; TODO change name of recent-loading?
        loading? @(re-frame/subscribe [::subs/recent-loading?])
        #_#_test-statuses [test-status]]
    (into [:div#art-col.column.is-7.mr-4.scrollable]
          (if loading?
            [[:img {:src "static/hourglass.gif" :alt "hourglass"}]]
            (mapv make-article-card statuses)))))

(defn art-count-display
  []
  (let [count @(re-frame/subscribe [::subs/item-count])
        time-of-count @(re-frame/subscribe [::subs/get-time-of-count])]
    [:div.level-item
     {:on-click #(re-frame/dispatch [::events/get-count])}
     [:span.has-text-primary-light.tooltip.time-count (str count " items at " time-of-count)
      [:span.tooltiptext "click to update count"]]]))

(defn hero-display
  []
  [:section.hero.has-background-info.is-small
   [:div.hero-body
    [:div.level
     [:div.level-item [:p.is-small.has-text-primary-light "Nooze Aggregator"]]
     [:div.level-item (time-dropdown)]
     [:div.level-item #_[:span.is-small.has-text-primary-light.tooltip "sources"
                         [:span.tooltiptext "filter sources"]]
      (authview/author-select-icon)]
     (art-count-display)]]])

(defn classic-cols-display
  []
  [:section.columns.is-mobile
   (cv/category-column)
   (article-column)])

(defn blank-section
  []
  (authview/authboxes)
  #_[:section.columns.is-mobile
     [:div#art-col.column.is-1.mr-4.scrollable
      [:div.level
       [:div.level-item
        [:div.control
         [:label.checkbox
          [:input {:type :checkbox :checked true :style {:margin 8}
                   :on-change #(println "clkd")}] "Yo!"]]]]]])

(defn top-display
  []
  [:div.main-view
   (hero-display)
   (tabber)])

(defn classic-panel
  []
  (conj (top-display) (classic-cols-display)))

(defn alert-view
  [msg]
  ;; close alert after 5 secs
  (js/setTimeout #(re-frame/dispatch [::events/alert nil]) 5000)
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Error!"]
     [:button.delete]]
    [:div.modal-card-body
     [:p (str msg)]]
    [:footer.modal-card-foot
     [:button.button
      {:on-click #(re-frame/dispatch [::events/alert nil])}
      "Close"]]]])

;; TODO alerting mechanism, custom query
(defn main-panel []
  (let [error-msg @(re-frame/subscribe [::subs/alert?])
        now-displaying @(re-frame/subscribe [::subs/now-displaying])]
    (condp = now-displaying
      :authors (conj (top-display) (blank-section))
      :classic (if error-msg
                 (conj (top-display) (alert-view error-msg))
                 (classic-panel)))))

(comment
  (time-buttons))
