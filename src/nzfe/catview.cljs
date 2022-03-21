(ns nzfe.catview
  (:require [goog.i18n.NumberFormat.Format]
            [nzfe.events :as events]
            [nzfe.subs :as subs]
            [nzfe.timeutils :as tu]
            [re-frame.core :as re-frame])
  (:import [goog.i18n NumberFormat]
           [goog.i18n.NumberFormat Format]))

;;https://gist.github.com/zentrope/181d591b52dcf3f5d336bc15131a1116
(def nff
  (NumberFormat. Format/DECIMAL))

(defn- nf
  [num]
  (.format nff (str num)))

(defn recent-card
  []
  (let [count @(re-frame/subscribe [::subs/item-count])
        time-of-count @(re-frame/subscribe [::subs/get-time-of-count])
        [d-of-c t-of-c] (tu/dtstring->d+t-string time-of-count)]
    [:div.card
     {:id "recent"
      :on-click #(re-frame/dispatch [::events/get-recent])}
     [:header.card-header.has-background-danger.is-small
      [:p#latest.card-header-title.has-text-primary-light "Latest!"]]
     [:div#recent-content.card-content
      [:span#cnt (str "[" (nf count) " items on " d-of-c " at " t-of-c "]")]]]))

(defn topic-button
  [[topic desc]]
  [:div.content.topic-content {:id topic
                               :on-click #(re-frame/dispatch [::events/topic-req-new topic])}
   desc])

(defn category-card
  [category]
  (let [topic-descs @(re-frame/subscribe [::subs/topic-descs-by-category category])]
    [:div.card
     [:header.card-header.has-background-info.is-small
      [:p.card-header-title.has-text-primary-light.cat-content
       {:on-click #(re-frame/dispatch [::events/category-req-new  category])}
       (name category)]]
     (into [:div.card-content.topic-ctr]
           (mapv #(topic-button %1) topic-descs))]))

(defn category-cards []
  (let [categories @(re-frame/subscribe [::subs/categories])]
    (mapv category-card categories)))

(defn category-column
  "make category column"
  []
  (into [:div.column.is-5.ml-1.scrollable]
        (into [(recent-card)] (category-cards))))