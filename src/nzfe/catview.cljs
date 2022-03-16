(ns nzfe.catview
  (:require
   [re-frame.core :as re-frame]
   [nzfe.events :as events]
   [nzfe.subs :as subs]))

(defn recent-card
  []
  [:div.card
   {:id "recent"
    :on-click #(re-frame/dispatch [::events/get-recent])}
   [:header.card-header.has-background-danger.is-small
    [:p#latest.card-header-title.has-text-primary-light "Latest!"]]])

(defn topic-button
  [[topic desc]]
  [:div.content {:id topic
                 :on-click #(re-frame/dispatch [::events/topic-req topic])}
   desc])

(defn category-card
  [category]
  (let [topic-descs @(re-frame/subscribe [::subs/topic-descs-by-category category])]
    [:div.card
     [:header.card-header.has-background-info.is-small.cat-content
      [:p.card-header-title.has-text-primary-light
       {:on-click #(re-frame/dispatch [::events/category-req  category])}
       (name category)]]
     (into [:div.card-content.has-background-light.topic-content]
           (mapv #(topic-button %1) topic-descs))]))

(defn category-cards []
  (let [categories @(re-frame/subscribe [::subs/categories])]
    (mapv category-card categories)))

(defn category-column
  "make category column"
  []
  (into [:div.column.is-4.ml-1.scrollable]
        (into [(recent-card)] (category-cards))))