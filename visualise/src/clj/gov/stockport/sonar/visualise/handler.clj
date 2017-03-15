(ns gov.stockport.sonar.visualise.handler
  (:require [bidi.ring :refer [make-handler ->ResourcesMaybe ->Resources]]
            [ring.util.response :as rur :refer [response redirect content-type]]
            [ring.middleware.json :refer [wrap-json-response]]
            [hiccup.page :refer [include-js include-css html5]]
            [gov.stockport.sonar.visualise.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [gov.stockport.sonar.esproxy.proxy :as proxy]
            [gov.stockport.sonar.auth.login-handler :as login]
            [buddy.auth :refer [authenticated?]]
            [gov.stockport.sonar.auth.auth-middleware :refer [wrap-buddy-auth]]))

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

(defn html [content]
  (fn [_] (-> (response content) (content-type "text/html"))))

(defn loading-page []
  (html
    (html5
      (head)
      [:body
       mount-target
       (include-js "/js/app.js")])))

(defn cards-page []
  (html
    (html5
      (head)
      [:body
       mount-target
       (include-js "/js/app_devcards.js")])))

(def not-found-404
  (fn [_]
    (-> (rur/not-found "Oops! Not Found!") (content-type "text/html"))))

(def routes ["" [["/" :app]
                 ["/login" {:get  :login
                            :post :do-login}]
                 ["/query" {:post :es-query}]
                 ["" (->ResourcesMaybe {:prefix "public/"})]
                 [true :404]]])

(defn redirect-if-not-auth [handler]
  (fn [req]
    (if (not (authenticated? req))
      (redirect "/login")
      (handler req))))

(def handlers {:login    (loading-page)
               :do-login login/handle-login
               :404      not-found-404
               :app      (loading-page)
               ;:app      (redirect-if-not-auth (loading-page))
               :es-query proxy/handle-query})

(def app-handler (make-handler routes (fn [handler-key-or-handler] (get handlers handler-key-or-handler handler-key-or-handler))))

(defn wrap-common-middleware [handler]
  (-> handler
      (wrap-json-response)
      (wrap-buddy-auth)))

(def app (->
           app-handler
           (wrap-common-middleware)
           (wrap-middleware)))