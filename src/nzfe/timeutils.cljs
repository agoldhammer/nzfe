(ns nzfe.timeutils
  (:require
   [clojure.string :as string]
   [tick.core :as t]))

#_(defn yesterday-as-stringdate
    []
    (-> (t/now)
        (t/<< (t/new-duration 24 :hours))
        (t/date)
        str))

#_(defn today-as-stringdate
    []
    (-> (t/now)
        t/date
        str))

(defn now-as-string
  []
  (str (t/now)))

(defn before-as-string
  "give string value of datetime specified interval before now"
  [duration-in-hours]
  (-> (t/now)
      (t/<< (t/new-duration duration-in-hours :hours))
      str))

(defn dtstring->d+t-string
  "split datetime string into date and time"
  [dtstring]
  (string/split dtstring #"T"))

(defn datestring
  "will return date only whether datetime or date"
  [datetime-or-string]
  (str (t/date datetime-or-string)))

(comment
  (datestring "2022-03-01")
  (before-as-string 3)
  (dtstring->d+t-string (t/now)))
