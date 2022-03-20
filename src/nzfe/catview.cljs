(ns nzfe.catview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.timeutils :as tu]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

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
     [:div.card-content
      [:span#cnt.pl-6 (str "[" count " items on " d-of-c " at " t-of-c "]")]]]))

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