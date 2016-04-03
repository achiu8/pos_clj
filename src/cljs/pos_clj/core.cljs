(ns pos-clj.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state (atom {:orders [{:table-number 1 :status "OPEN"} {:table-number 2 :status "OPEN"}]}))

(defn close  [_ order]
  (om/update! order :status "close"))

(defn order [{:keys [table-number status] :as order}]
  (html [:div
         [:p (str "table number: " table-number)]
         [:p (str "status: " status)]
         [:button {:on-click #(close % order)} "Close"]]))

(defn root-component [app owner]
  (reify
    om/IRender
    (render [_]
      (html
       [:div
        [:h1 "Orders"]
        [:div (map order (:orders app))]]))))

(om/root
 root-component
 app-state
 {:target (js/document.getElementById "app")})
