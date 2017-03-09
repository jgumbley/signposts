(ns gov.stockport.sonar.visualise.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.json :refer [wrap-json-response]]
            [hiccup.page :refer [include-js include-css html5]]
            [gov.stockport.sonar.visualise.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [gov.stockport.sonar.esproxy.proxy :as proxy]))

(def mount-target
  [:div#app
   [:center [:i.fa.fa-spin.fa-refresh.fa-5x]]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css")
                "/css/font-awesome.min.css")])

(defn loading-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app.js")]))

(defn cards-page []
  (html5
    (head)
    [:body
     mount-target
     (include-js "/js/app_devcards.js")]))

(defroutes
  routes
  (GET "/" [] (loading-page))
  (GET "/wip" [] (loading-page))
  (GET "/cards" [] (cards-page))
  (POST "/query" [] proxy/query-handler)
  (resources "/")
  (not-found "Not Found"))

(defn wrap-common-middleware [handler]
  (-> handler
      (wrap-json-response)))

(def app (-> #'routes
             (wrap-middleware)
             (wrap-common-middleware)))