(ns
  ^{:author "Stu Halloway"
    :doc "Compojure app that displays lab instructions."}
  labrepl
  (:use compojure.core
        hiccup.core
        hiccup.page-helpers
        ring.adapter.jetty
        [ring.middleware.reload :only [wrap-reload]]
        clojure.contrib.logging)
  (:require [compojure.route :as route]
            [labrepl.lab :as lab] :reload
            [solutions.mini-browser :as mini-browser]))

(defn with-logging [handler]
  (fn [request]
    (let [start (System/nanoTime)
          response (handler request)
          elapsed (/ (double (- (System/nanoTime) start)) 1000000.0)]
      (when response
        (log :info (str (:uri request) " [" (:request-method request) "] " elapsed " msec"
                        "\n\tParameters " (:params request)
                        "\n\tSession " (:session request)))
        response))))

(defroutes lab-routes
  (GET "/" [] (let [show-labs (fn [labs]
                                (map (fn [lab] [:li (lab/make-url lab)])
                                     labs))]
               (html
                (lab/layout
                 [:h2.logo "Clojure Labs"]
                 [:h3 "Core labs"]
                 [:ul
                  (show-labs (lab/all))]
                 [:h3 "Choose your own adventure"]
                 [:ul
                  (show-labs (lab/optional))]
                 [:h3 "Extras (mostly review)"]
                 [:ul
                  (show-labs (lab/review))]
                  ))))
  (GET "/labs/:name" [name] (html
                             (lab/layout
                              [:h2 name]
                              (lab/instructions name)))))

(defroutes static-routes
  (route/files "/")
  (route/not-found "<h1>Not Found</h1>"))

(def full-routes (-> lab-routes
                     (wrap-reload '[labrepl])
                     with-logging))

(defroutes app
  (routes full-routes static-routes))

(defn load-common-libs
  []
  (use '[clojure.java.io :only (reader writer)]
       '[clojure.contrib.io :only (read-lines)]
       '[clojure.contrib def]
       '[clojure.contrib.repl-utils :only (show)]
       'clojure.pprint)
  (require '[clojure.contrib.str-utils2 :as s]))

(defn -main [& args]
  (load-common-libs)
  (mini-browser/main)
  (run-jetty (var app) {:port 8080
                        :join? false})
  (println "Welcome to the labrepl. Browse to localhost:8080 to get started!"))
