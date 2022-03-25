(ns nzfe.views
  (:require [nzfe.alertview :as alertview]
            [nzfe.artview :as artview]
            [nzfe.authview :as authview]
            [nzfe.catview :as cv]
            [nzfe.custview :as custv]
            [nzfe.events :as events]
            [nzfe.subs :as subs]
            [nzfe.timedd :as timedd]
            [re-frame.core :as re-frame]))

(defn navx
  "make navbar"
  []
  (let [query @(re-frame/subscribe [::subs/get-query-text])
        qry-display (if (= query "") "Latest!" query)
        duration @(re-frame/subscribe [::subs/get-duration])
        authors-unfiltered? @(re-frame/subscribe [::subs/display-all-authors?])
        filter-flag-color (if authors-unfiltered? "aliceblue" "red")]
    [:nav.navbar
     [:div.navbar-brand
      [:figure.image.navbar-item.pr-4
       {:style {:background-color filter-flag-color}}
       [:img {:src "/static/images/signature.jpg"}]]
      [:button#navbutton.navbar-burger
       {:on-click #(re-frame/dispatch [::events/toggle-navmenu])}
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]
       [:span {:aria-hidden true}]]]
     [:div#navmenu.navbar-menu
      [:div.navbar-start
       [:div.navbar-item.pr-4 (timedd/time-dropdown)]
       [:div.navbar-item.pr-4 (authview/author-select-icon)]
       [:div.stats.box.mt-2
        [:p.stats.mb-0.mt-0 (str "query: " qry-display)]
        [:p.stats (str "duration: " duration " hrs")]]]

      [:div.navbar-end]]]))

(defn classic-cols-display
  []
  [:section.columns.is-mobile.pt-3
   (cv/category-column)
   (artview/article-column)])

(defn top-display
  []
  [:div.main-view
   (navx)])

(defn main-panel []
  (let [error-msg @(re-frame/subscribe [::subs/alert?])
        now-displaying @(re-frame/subscribe [::subs/now-displaying])]
    (condp = now-displaying
      :authors (conj (top-display) (authview/authboxes))
      :custom-time (conj (top-display) (custv/custom-time-view))
      :classic (if error-msg
                 (conj (top-display) (alertview/alert-view error-msg))
                 (conj (top-display) (classic-cols-display)))
      (println "main-panel: shouldn't happen"))))

;; outtakes
#_(defn tabber
    "set up tabbed view"
    []
    (let [active-tab-id @(re-frame/subscribe [::subs/get-active-tab])]
      [:div.tabs
       [:ul
        [:li {:id :tab0 :class (if (= :tab0 active-tab-id) "is-active" "")
              :on-click #(re-frame/dispatch [::events/set-active-tab :tab0])} [:a "Classic"]]
        [:li {:id :tab1 :class (if (= :tab1 active-tab-id) "is-active" "")
              :on-click #(re-frame/dispatch [::events/set-active-tab :tab1])} [:a "New"]]]]))

#_(defn art-count-display
    []
    (let [count @(re-frame/subscribe [::subs/item-count])
          time-of-count @(re-frame/subscribe [::subs/get-time-of-count])
          [d-of-c t-of-c] (tu/dtstring->d+t-string time-of-count)]
      [:div.level-item
       {:on-click #(re-frame/dispatch [::events/get-count])}
       [:span.has-text-primary-light.tooltip.time-count (str count " items on " d-of-c
                                                             " at " t-of-c)
        [:span.tooltiptext "click to update count"]]]))

#_(defn hero-display
    []
    [:section.hero.has-background-info.is-small
     [:div.hero-body
      [:div.level-left
       [:div.level-item
        [:i.fa-solid.fa-bars.pr-4]]
       [:div.level-item
        [:figure.image.pr-4
         [:img {:src "/images/signature.jpg"}]]]
       #_[:div.level-item [:div.subtitle.is-small.has-text-primary-light.pr-4 "Nooze Aggregator"]]
       [:div.level-item.pr-4 (timedd/time-dropdown)]
       [:div.level-item.pr-4 (authview/author-select-icon)]]]])


