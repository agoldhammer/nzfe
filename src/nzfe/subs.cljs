(ns nzfe.subs
  (:require
   [re-frame.core :as re-frame]
   [clojure.string :as string]))

#_(re-frame/reg-sub
   ::name
   (fn [db]
     (:name db)))

(re-frame/reg-sub
 ::now-displaying
 (fn [db]
   (:now-displaying db)))

(re-frame/reg-sub
 ::alert?
 (fn [db]
   (:alert db)))

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
 ::query-time
 (fn [db]
   (let [active-time-button @(re-frame/subscribe [::time-button-active-id])]
     (if (= :tb6 active-time-button)
       @(re-frame/subscribe [::get-formatted-custom-date])
       (nth (get-in db [:time-button-bar :ids active-time-button]) 1)))))

(re-frame/reg-sub
 ::get-start-end
 (fn [db]
   [(:start db) (:end db)]))

(re-frame/reg-sub
 ::time-dd-active?
 (fn [db]
   (-> db :time-dd :active?)))

(re-frame/reg-sub
 ::get-active-tab
 (fn [db]
   (-> db (get-in [:tabs :active]))))

(re-frame/reg-sub
 ::item-count
 (fn [db]
   (-> db :count)))

#_(re-frame/reg-sub
   ::cats-loading?
   (fn [db]
     (:cats-loading? db)))

(re-frame/reg-sub
 ::categories
 (fn [db]
   (keys (get-in db [:navdata :cats]))))

#_(re-frame/reg-sub
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

#_(re-frame/reg-sub
   ::fulltopic
   (fn [db [_ category topic]]
     (let [topics (get-in db [:navdata :cats category])]
       (first (filter #(= topic (:topic %1)) topics)))))

#_(re-frame/reg-sub
   ::topic-to-query
   (fn [_ [_ category topic]]
     (:query @(re-frame/subscribe [:fulltopic category topic]))))

(re-frame/reg-sub
 ::get-active-authors
 (fn [db]
   (into #{}
         (map first (filter second (:author-display-states db))))))

#_(re-frame/reg-sub
   ::get-recent
   (fn [db]
     (:recent db)))

(defn status-author-active?
  [status active-authors]
  (some #{(string/upper-case (:author status))} active-authors))

(re-frame/reg-sub
 ::filtered-statuses
 (fn [db]
   (let [active-authors @(re-frame/subscribe [::get-active-authors])
         statuses (:recent db)]
     (filterv #(status-author-active? %1 active-authors) statuses))))

(re-frame/reg-sub
 ::display-all-authors?
 (fn [db]
   (:display-all-authors? db)))

(re-frame/reg-sub
 ::get-time-of-count
 (fn [db]
   (:time-of-count db)))

(re-frame/reg-sub
 ::recent-loading?
 (fn [db]
   (:recent-loading? db)))

(re-frame/reg-sub
 ::get-authors
 (fn [_]
   (sort (keys @(re-frame/subscribe [::get-author-display-states])))))

(re-frame/reg-sub
 ::get-author-display-states
 (fn [db]
   (:author-display-states db)))

(re-frame/reg-sub
 ::get-author-display-state
 (fn [db [_ author]]
   (get-in db [:author-display-states author])))

(comment
  @(re-frame/subscribe [::get-start-end])
  #_(re-frame/reg-sub
     ::get-display-text
     (fn [db]
       (:display db)))

;; this section is for testing
  #_(defn fake-status-list
      [n db]
      (take n (repeat (:dummy-list db))))



  #_(re-frame/reg-sub
     ::get-fake-status-list
     (fn [db [_ n]]
       (fake-status-list n db)))
;;;;;;;
  )
