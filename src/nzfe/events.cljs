(ns nzfe.events
  (:require
   [re-frame.core :as re-frame]
   [nzfe.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-event-db
 ::initialize-db
 #_:clj-kondo/ignore
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-db
 ::set-active-time-button
 (fn [db [_ activate-id]]
   (when (= activate-id :tb6) (re-frame/dispatch [:toggle-show-custom-time-panel]))
   (assoc-in db [:time-button-bar :active] activate-id)))

(re-frame/reg-event-db
 ::toggle-state
 (fn [db [_ control-id]]
   (update-in db [control-id :active?] not)))

(re-frame/reg-event-db
 ::set-active-tab
 (fn [db [_ tab-id]]
   (assoc-in db [:tabs :active] tab-id)))

