(ns nzfe.timeutils
  (:require [clojure.string :as string]
            [tick.core :as t]))

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
  [date-or-time-string]
  (if (string/includes? date-or-time-string "T")
    (str (t/date (t/instant date-or-time-string)))
    (str (t/date date-or-time-string))))

(comment
  (datestring "2022-03-01")
  (datestring "2022-03-01T11:28:00Z")
  (before-as-string 3)
  (dtstring->d+t-string (t/now)))
