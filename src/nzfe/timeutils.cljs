(ns nzfe.timeutils
  (:require
   [tick.core :as t]))

(defn yesterday-as-stringdate
  []
  (-> (t/now)
      (t/<< (t/new-duration 24 :hours))
      (t/date)
      str))

(defn today-as-stringdate
  []
  (-> (t/now)
      t/date
      str))

(defn before-as-string
  "give string value of datetime specified interval before now"
  [duration-in-hours]
  (-> (t/now)
      (t/<< (t/new-duration duration-in-hours :hours))
      str))

(comment
  (before-as-string 3))
