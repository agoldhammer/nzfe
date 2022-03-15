(ns nzfe.views
  (:require
   [clojure.string :as string]
   [goog.string :as gstring]
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

(defn content-card
  []
  [:div.card
   [:header.card-header.has-background-warning.is-small
    [:p.card-header-title.has-text-primary-light "header"]]
   [:div.card-content.has-background-light
    [:div.content "Lorem ipsum"]]])

(def test-status {:source "Times" :created_at "2022/03/11" :author "x"
                  :text "Lorem ipsum"})

(defn make-article-card
  "from status, create article card"
  [{:keys [source created_at author text]}]
  [:div.card
   [:header.card-header.has-background-info.is-small
    [:p.card-header-title.has-text-primary-light (string/join " " [author created_at source])]]
   [:div.card-content.has-background-light
    [:div.content (urlize text)]]])

(make-article-card test-status)

(defn article-column
  "make column of articles"
  []
  (let [#_#_statuses @(re-frame/subscribe [:filtered-statuses])
        test-statuses [test-status]]
    (into [:div.column.mr-2 "col2"]
          (mapv make-article-card test-statuses))))

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
      (article-column)

      #_[:div.column.mr-2 "col2"
         #_(article-view)
         (content-card)
         #_(content-card)]
      #_[:div.column.is-one-quarter "col3"]]]))

(comment
  (time-buttons))
