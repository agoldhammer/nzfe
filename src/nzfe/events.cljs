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

