(ns gov.stockport.sonar.auth.login-handler
  (:require [buddy.sign.jwt :as jwt]
            [ring.util.response :refer [response]]
            [buddy.core.keys :as keys]
            [gov.stockport.sonar.esproxy.proxy :refer [is-valid-elastic-search-user?]]
            [buddy.auth :refer [throw-unauthorized]]
            [gov.stockport.sonar.auth.session-manager :as sm]))

(def pubkey (keys/public-key "config/pubkey.pem"))

(defn handle-login [{creds :body}]
  (when (not (is-valid-elastic-search-user? creds)) (throw-unauthorized))
  (let [session (sm/create-session creds)
        token (jwt/encrypt {:user session} pubkey
                           {:alg :rsa-oaep :enc :a128cbc-hs256})]
    (assoc-in (response "") [:cookies "token"] token)))

(defn handle-logout [{session :identity}]
  (sm/logout session)
  (response ""))