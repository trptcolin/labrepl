(ns labrepl.lab
  (:use hiccup.core
        hiccup.page-helpers
        labrepl.web))

(defn all
  []
  [:names-and-places :looping :rock-paper-scissors
   :project-euler :unified-update-model])

(defn review
  []
  [:intro :its-all-data])

(defn optional
  []
  [:mini-browser :zero-sum :cellular-automata :defstrict])

(defn lab-url
  [lab-name]
  (str "/labs/" (name lab-name)))

(defn make-url
  [lab]
  [:a {:href (lab-url lab)} (name lab)])

(defn instructions
  [lab]
  (let [lab-ns (symbol (str "labs." (name lab)))]
    (require lab-ns)
    ((ns-resolve lab-ns 'instructions))))

(defn layout [title & body]
  {:pre [(string? (last title))]}
  (html
    [:head
     [:title (last title)]
     (apply include-css default-stylesheets)
     (apply include-js default-javascripts)]
    [:body
     [:div {:id "header"}
      title]
     [:div {:id "content"}
      body]
     [:div {:id "footer"}
      "Clojure labrepl. Copyright 2010 Relevance Inc. All Rights Reserved."]]))

