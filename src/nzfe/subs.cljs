(ns nzfe.subs
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as string]))

#_(re-frame/reg-sub
   ::name
   (fn [db]
     (:name db)))

(re-frame/reg-sub
 ::get-time-button-ids
 (fn [db]
   (keys (get-in db [:time-button-bar :ids]))))

(re-frame/reg-sub
 ::time-button-active-id
 (fn [db]
   (get-in db [:time-button-bar :active])))

(re-frame/reg-sub
 ::button-id-to-text
 (fn [db [_ button-id]]
   (nth (get-in db [:time-button-bar :ids button-id]) 0)))

(re-frame/reg-sub
 ::item-count
 (fn [db]
   (:count db)))

(re-frame/reg-sub
 ::get-time-of-count
 (fn [db]
   (:time-of-count db)))

(re-frame/reg-sub
 ::cats-loading?
 (fn [db]
   (:cats-loading? db)))

(re-frame/reg-sub
 ::recent-loading?
 (fn [db]
   (:recent-loading? db)))

(re-frame/reg-sub
 ::categories
 (fn [db]
   (keys (get-in db [:navdata :cats]))))

(re-frame/reg-sub
 ::category
 (fn [db [_ category]]
   (get-in db [:navdata :cats category])))

(re-frame/reg-sub
 ::topics-by-category
 (fn [db [_ category]]
   (mapv #(:topic %1) (get-in db [:navdata :cats category]))))

(re-frame/reg-sub
 ::topic-descs-by-category
 (fn [db [_ category]]
   (mapv #((juxt :topic :desc) %1) (get-in db [:navdata :cats category]))))

(re-frame/reg-sub
 ::fulltopic
 (fn [db [_ category topic]]
   (let [topics (get-in db [:navdata :cats category])]
     (first (filter #(= topic (:topic %1)) topics)))))

(re-frame/reg-sub
 ::topic-to-query
 (fn [db [_ category topic]]
   (:query @(re-frame/subscribe [:fulltopic category topic]))))

(re-frame/reg-sub
 ::get-display-text
 (fn [db]
   (:display db)))

;; this section is for testing
(defn fake-status-list
  [n db]
  (take n (repeat (:dummy-list db))))

(re-frame/reg-sub
 ::get-authors
 (fn [db]
   (sort (keys @(re-frame/subscribe [:get-author-display-states])))))

(re-frame/reg-sub
 ::get-author-display-states
 (fn [db]
   (:author-display-states db)))

(re-frame/reg-sub
 ::get-author-display-state
 (fn [db [_ author]]
   (get-in db [:author-display-states author])))

(re-frame/reg-sub
 ::get-fake-status-list
 (fn [db [_ n]]
   (fake-status-list n db)))
;;;;;;;

(re-frame/reg-sub
 ::get-active-authors
 (fn [db]
   (into #{}
         (map first (filter second (:author-display-states db))))))

(re-frame/reg-sub
 ::get-recent
 (fn [db]
   (:recent db)))

(defn status-author-active?
  [status active-authors]
  (some #{(string/upper-case (:author status))} active-authors))

(re-frame/reg-sub
 ::filtered-statuses
 (fn [db]
   (let [active-authors @(re-frame/subscribe [:get-active-authors])
         statuses (:recent db)]
     (filterv #(status-author-active? %1 active-authors) statuses))))

(re-frame/reg-sub
 ::display-all-authors?
 (fn [db]
   (:display-all-authors? db)))

(re-frame/reg-sub
 ::time-dd-active?
 (fn [db]
   (-> db :time-dd :active?)))
