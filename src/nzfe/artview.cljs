(ns nzfe.artview
  (:require [clojure.string :as string]
            [goog.string :as gstring]
            [nzfe.subs :as subs]
            [re-frame.core :as re-frame]))

;; functions below are used in building articles
;; need to turn urls into links and eliminate from text

(defn link-url
  [url]
  [:a {:href url :target "_blank"} " ...more \u21aa"])

(def re-url #"https?://\S+")

(defn extract-urls
  [text]
  (re-seq re-url text))

(defn suppress-urls
  [text]
  (string/replace text re-url ""))

(defn urlize
  [text]
  (let [urls (extract-urls text)
        modtext (gstring/unescapeEntities (suppress-urls text))]
    (into [:p.art-content modtext] (mapv link-url urls))))

;; --- end of urlize-related funcs ----------

(defn make-article-card
  "from status, create article card"
  [{:keys [source created_at author text]}]
  [:div.card
   [:header.card-header.has-background-info.is-small.art-card-header
    [:p.card-header-title.has-text-primary-light (string/join " " [author created_at source])]]
   [:div.card-content.art-content
    [:div.content (urlize text)]]])

(defn article-column
  "make column of articles"
  []
  (let [statuses @(re-frame/subscribe [::subs/filtered-statuses])
        ;; TODO change name of recent-loading?
        loading? @(re-frame/subscribe [::subs/recent-loading?])
        #_#_test-statuses [test-status]]
    (into [:div#art-col.column.is-7.mr-1.scrollable]
          (if loading?
            [[:img {:src "static/hourglass.gif" :alt "hourglass"}]]
            (mapv make-article-card statuses)))))