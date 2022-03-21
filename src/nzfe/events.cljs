(ns nzfe.events
  (:require [ajax.core :as ajax]
            [clojure.string :as string]
            [day8.re-frame.http-fx]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [nzfe.db :as db]
            [nzfe.subs :as subs]
            [nzfe.timeutils :as tu]
            [re-frame.core :as re-frame]
            [tick.core :as t]
            [tick.locale-en-us]))

(re-frame/reg-event-db
 ::initialize-db
 #_:clj-kondo/ignore
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-time-button
 (fn [db [_ activate-id]]
   (when (= activate-id :tb6) (re-frame/dispatch [::set-now-displaying :custom-time]))
   (assoc-in db [:time-button-bar :active] activate-id)))

(re-frame/reg-event-db
 ::toggle-state
 (fn [db [_ control-id]]
   (update-in db [control-id :active?] not)))

(re-frame/reg-event-db
 ::toggle-navmenu
 (fn [db [_]]
   (.toggle (.-classList (.getElementById js/document "navbutton")) "is-active")
   (.toggle (.-classList (.getElementById js/document "navmenu")) "is-active")
   db))

#_(re-frame/reg-event-db
   ::set-active-tab
   (fn [db [_ tab-id]]
     (assoc-in db [:tabs :active] tab-id)))

(re-frame/reg-event-db
 ::ajax-error
 (fn [db [_ details]]
   (re-frame/dispatch [::alert (:status-text details)])
   (.log js/console "ajax-error" details)
   db))

(re-frame/reg-event-db
 ::got-cats
 (fn [db [_ result]]
   (->
    db
    (assoc :cats-loading? false)
    (assoc :count (:count result))
    (assoc :navdata (dissoc result :count)))))

(re-frame/reg-event-fx
 ::get-cats
 (fn [{:keys [db]} _]
   {:db (assoc db :cats-loading? true)
    :http-xhrio {:method :get
                 :uri "/json/cats"
                 :timeout 10000
                 :response-format
                 (ajax/json-response-format {:keywords? true})
                 :on-success [::got-cats]
                 :on-failure [::ajax-error]}}))

(re-frame/reg-event-db
 ::alert
 (fn [db [_ msg]]
   (when (nil? msg)
     (re-frame/dispatch [::get-recent]))
   (assoc db :alert msg)))

;; auxiliary function to set up author-display-state section of db
(defn set-author-display-states
  "Given a seq of statuses, return map {auth: true} with key for each author"
  [statuses]
  (let [authors (distinct (map
                           (comp string/upper-case :author) statuses))]
    (zipmap authors (repeat true))))

(re-frame/reg-event-fx
 ::get-recent
 (fn [{:keys [db]} _]
   {:db (-> db
            (assoc :recent-loading? true)
            (assoc :display "Latest!"))
    :http-xhrio {:method :get
                 :uri "/json/recent"
                 :timeout 10000
                 :format (ajax/url-request-format :java)
                 :response-format
                 (ajax/json-response-format {:keywords? true})
                 :on-success [::got-recent]
                 :on-failure [::ajax-error]}}))

(re-frame/reg-event-db
 ::got-recent
 (fn [db [_ result]]
   (when (empty? result) (re-frame/dispatch [::alert "Server returned nothing"]))
   #_(re-frame/dispatch [::reset-content-scroll-pos])
   (re-frame/dispatch [::set-display-all-authors-flag true])
   (->
    db
    (assoc :author-display-states (set-author-display-states result))
    (assoc :recent-loading? false)
    (assoc :recent result))))

(re-frame/reg-event-db
 ::got-statuses
 (fn [db [_ result]]
   (re-frame/dispatch [::get-count])

   (let [{:keys [error statuses]} result]
     (when (empty? statuses) (re-frame/dispatch [::alert "Sorry, no data for this time frame"]))
     (if (not= error 0)
       (do
         (js/console.error "Server error" error)
         (re-frame/dispatch [::alert "Server error: " error])
         db)
       (do
         (re-frame/dispatch [::set-display-all-authors-flag true])
         (->
          db
          (assoc :author-display-states (set-author-display-states statuses))
          (assoc :recent-loading? false)
          (assoc :recent statuses)))))))

(re-frame/reg-event-db
 ::got-count
 (fn [db [_ result]]
   (->
    db
    (assoc :time-of-count (t/now))
    (assoc :cats-loading? false)
    (assoc :count (:count result)))))

(re-frame/reg-event-db
 ::set-now-displaying
 (fn [db [_ state]]
   (assoc db :now-displaying state)))

(re-frame/reg-event-db
 ::toggle-author-display-state
 (fn [db [_ author]]
   (update-in db [:author-display-states author] not)))


(re-frame/reg-event-db
 ::set-display-all-authors-flag
 (fn [db [_ true-or-false]]
   (assoc db :display-all-authors? true-or-false)))


(re-frame/reg-event-db
 ::set-reset-author-display-states
 (fn [db [_ true-or-false]]
   (let [authors (keys (:author-display-states db))]
     (->
      db
      (assoc :display-all-authors? true-or-false)
      (assoc :author-display-states
             (zipmap authors (repeat true-or-false)))))))


(re-frame/reg-event-fx
 ::get-count
 (fn [{:keys [_]} _]
   {:http-xhrio {:method :get
                 :uri "/json/count"
                 :timeout 10000
                 :response-format
                 (ajax/json-response-format {:keywords? true})
                 :on-success [::got-count]
                 :on-failure [::ajax-error]}}))


#_(re-frame/reg-event-fx
   ::get-query
   (fn [{:keys [db]} [_ query]]
     {:db (assoc db :recent-loading? true)
      :http-xhrio {:method :get
                   :uri "/json/qry"
                   :timeout 30000
                   :format (ajax/url-request-format :java)
                   :params {:data query}
                   :response-format
                   (ajax/json-response-format {:keywords? true})
                   :on-success [::got-recent]
                   :on-failure [::ajax-error]}}))

(re-frame/reg-event-fx
 ::json-query
 (fn [{:keys [db]} [_ payload]]
   {:db (assoc db :recent-loading? true)
    :http-xhrio {:method :post
                 :uri "/json/xqry"
                 :timeout 30000
                 :format (ajax/json-request-format)
                 :params payload
                 :response-format
                 (ajax/json-response-format {:keywords? true})
                 :on-success [::got-statuses]
                 :on-failure [::ajax-error]}}))


#_(re-frame/reg-event-db
   ::initialize-db
   (fn  [_ _]
     db/default-db))


(re-frame/reg-event-db
 ::initialize-content
 (fn [db _]
   (re-frame/dispatch [::get-count])
   (re-frame/dispatch [::get-cats])
   (re-frame/dispatch [::get-recent])
   db))


#_(re-frame/reg-event-db
   ::set-display
   (fn [db [_ display]]
     (assoc db :display display)))


#_(re-frame/reg-event-db
   ::topic-req
   (fn [db [_ topic]]
     (let [time-part @(re-frame/subscribe [::subs/query-time])
           query (str time-part " *" topic)]
       (re-frame/dispatch [::set-display query])
       (re-frame/dispatch [::get-query query]))
     db))

(re-frame/reg-event-db
 ::set-start-end
 (fn [db]
   (let [selected-duration @(re-frame/subscribe [::subs/get-duration])]
     (if (= selected-duration :custom) ;; start/end selected by custom menu
       db
       (let [end (tu/now-as-string)
             start (tu/before-as-string selected-duration)]
         (-> db
             (assoc :start start)
             (assoc :end end)))))))

(re-frame/reg-event-db
 ::topic-req-new
 (fn [db [_ topic]]
   (let [query (str "*" topic)]
     (re-frame/dispatch [::set-now-displaying :classic])
     (re-frame/dispatch [::set-start-end])
     (re-frame/dispatch [::set-query-text query])
     (re-frame/dispatch [::submit-query]))
   db))

(comment
  @(re-frame/subscribe [::subs/topics-by-category :Culture])
  (re-frame/dispatch [::topic-req-new "Business"]))

(re-frame/reg-event-db
 ::category-req-new
 (fn [db [_ category]]
   (let [topics @(re-frame/subscribe [::subs/topics-by-category (keyword category)])
         query (string/join (map #(str " *" %1) topics))]
     (re-frame/dispatch [::set-now-displaying :classic])
     (re-frame/dispatch [::set-start-end])
     (re-frame/dispatch [::set-query-text query])
     (re-frame/dispatch [::submit-query]))
   db))


#_(re-frame/reg-event-db
   ::category-req
   (fn [db [_ category]]
     (let [time-part @(re-frame/subscribe [::subs/query-time])
           topics    @(re-frame/subscribe [::subs/topics-by-category category])
           text-part (string/join (map #(str " *" %1) topics))]
       (re-frame/dispatch [::set-display (string/join " " [time-part text-part])])
       (re-frame/dispatch [::get-query (string/join " " [time-part text-part])]))
     db))


;; ?OLD VERSION
#_(re-frame/reg-event-db
   ::custom-query-req
   (fn [db [_ text]]
     (let [time-part @(re-frame/subscribe [::subs/query-time])]
       (re-frame/dispatch [::get-query (string/join " " [time-part text])]))
     db))

;; ?NEW VERSION
(re-frame/reg-event-db
 ::submit-query
 (fn [db [_]]
   (let [[start end] @(re-frame/subscribe [::subs/get-start-end])
         text @(re-frame/subscribe [::subs/get-query-text])]
     (re-frame/dispatch [::json-query {:start start :end end
                                       :words (string/split text #" ")}]))
   db))

(re-frame/reg-event-db
 ::set-query-text
 (fn [db [_ text]]
   (assoc db :query-text text)))


#_(re-frame/reg-event-db
   ::set-active-time-button
   (fn [db [_ activate-id]]
     (when (= activate-id :tb6) (re-frame/dispatch [:toggle-show-custom-time-panel]))
     (assoc-in db [:time-button-bar :active] activate-id)))


#_(re-frame/reg-event-db
   ::set-custom-query-status
   (fn [db [_ status]]
     (assoc-in db [:custom-query :status] status)))


#_(re-frame/reg-event-db
   ::toggle-show-custom-time-panel
   (fn [db]
     (update db :show-custom-time-panel? not)))


(re-frame/reg-event-db
 ::set-date
 (fn [db [_ start-or-end date]]
   (assoc db start-or-end date)))


#_(re-frame/reg-event-db
   ::reset-content-scroll-pos
   (fn [db]
     (let [content (.getElementById js/document "art-col")]
       (aset content "scrollTop" 0))
     db))



(comment
  (string/split "harris jones" #" ")
  (re-frame/dispatch [::ajax-error {:status-text "screwup"}])
  (re-frame/dispatch [::alert "yo"])
  (re-frame/dispatch [::get-count])
  (re-frame/dispatch [::get-cats])
  (re-frame/dispatch [::get-recent])
  (re-frame/dispatch [::set-date :start "2022-03-01"])
  (str (t/instant))
  (t/now)
  (t/date (t/now))
  (t/format :iso-zoned-date-time (t/zoned-date-time))
  ;; 3 hours before
  (str (t/<< (t/now) (t/new-duration 3 :hours))))


