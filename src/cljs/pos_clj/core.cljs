(ns pos-clj.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.core.async :refer [put! chan <!]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(enable-console-print!)

(defonce app-state (atom {:orders [{:table-number 1 :status "OPEN"} {:table-number 2 :status "OPEN"}]}))

(def page-chan (chan))

(def history-container (.getElementById js/document "history-container"))

(secretary/set-config! :prefix "#")

(let [history (History. false nil history-container)
      navigation EventType/NAVIGATE]
  (goog.events/listen history
                     navigation
                     #(-> % .-token secretary/dispatch!))
  (doto history (.setEnabled true)))

(defn close  [_ order]
  (om/update! order :status "close"))

(defn order [{:keys [table-number status] :as order}]
  (html [:div
         [:p (str "table number: " table-number)]
         [:p (str "status: " status)]
         [:button {:on-click #(close % order)} "Close"]]))

(defn new-order [orders owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (html
       [:div
        [:form
         [:label "table number:"]
         [:input {:type "text"}]
         [:input {:type "button" :value "Submit"}]]]))))

(defn root-component [app owner]
  (reify
    om/IRender
    (render [_]
      (html
       [:div
        [:h1 "Orders"]
        [:a  {:href "#/orders/new"} "New Order"]
        [:div (map order (:orders app))]]))))

(defroute
  new-order-path
  "/orders/new" []
  (put! page-chan new-order))

(defroute
  root-path
  "/" []
  (put! page-chan root-component))

(go
  (while true
    (let [page (<! page-chan)]
      (om/root
       page
       app-state
       {:target (js/document.getElementById "app")}))))

(put! page-chan root-component)
