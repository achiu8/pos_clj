(ns pos-clj.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state (atom {:orders [{:table-number 1 :status "OPEN"} {:table-number 2 :status "CLOSE"}]}))

(defn root-component [app owner]
  (reify
    om/IRender
    (render [_]
      (html
       [:div
        [:h1 "Orders"]
        [:div (map (fn [order] [:div (str "order: " (:table-number order))]) (:orders app))]]))))

(om/root
 root-component
 app-state
 {:target (js/document.getElementById "app")})
