(ns gov.stockport.sonar.visualise.ui.search.search-control
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.ui.search.query-control-state :as qcs]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.query.client :refer [search]]))

(defn nugget [{:keys [query-type search-term]}]
  [:div.input-group.nugget.mr-2
   [:span.input-group-addon.name (name query-type)]
   [:span.input-group-addon.val search-term
    [:i.fa.fa-times.ml-2
     {:on-click #(scs/remove-search-criteria! query-type)}]]])


(defn search-criteria-control [query-callback]
  (scs/init! query-callback)
  (fn []
    [:div.container-fluid.py-1
     {:style {:background-color "#1d2932"}}
     [:div.form-inline
      [:select.custom-select.form-control.mr-2
       {:value     (scs/selected-control)
        :autoFocus "autofocus"
        :on-change #(scs/set-selected-field! (keyword (-> % .-target .-value)))}
       (map
         (fn [{:keys [target description]}]
           ^{:key target}
           [:option {:value target} description])
         (sort-by :display-order qcs/options))]
      [:div.input-group
       [:input.form-control {:value       (scs/search-term)
                             :placeholder (get-in qcs/query-types [(scs/selected-control) :placeholder])
                             :on-change   #(scs/set-search-term! (-> % .-target .-value))
                             :on-key-up   #(when (= 13 (-> % .-keyCode)) (scs/add-search-criteria!))}]
       [:span.input-group-btn
        [:button.btn.btn-success.mr-2
         {:on-click #(scs/add-search-criteria!)}
         [:i.fa.fa-search]]]]
      `[:span.py-1
        {:style {:display   :inline-flex
                 :flex-wrap :wrap}}
        ~@(map nugget (scs/search-criteria))]]]))

(defn query-wrapper [handler]
  (fn [terms]
    (if (not-empty terms)
      (search (qcs/extract-query-defs terms) handler)
      (handler {}))))

(defn new-search-control [handler]
  (let [query-callback (query-wrapper handler)]
    [search-criteria-control query-callback]))