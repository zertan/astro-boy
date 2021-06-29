(ns game.core
  (:require [arcadia.core :refer :all]
            [arcadia.linear :as l]
            [hard.core :as hc]
            [tween.core :as tc])
  (:import [UnityEngine Color]))

(defn clear-scene! [o]
  (doseq [c (children o)]
    (destroy c)))

(defn start-game [o]
  (clear-scene! o)
  (map
   #(child+ o (hc/clone! %))
   [:camera :sun]))

;;

;; (defn platform-update [o k]
;;   (let [old-pos (.. o transform position)
;;         velocity (l/v3 0 0 -0.1)]
;;     (set! (.. o transform position)
;;           (l/v3+ old-pos velocity))))

(def pl0
  (let [platform (create-primitive :cube)]
    (hc/local-scale! platform (l/v3 1 0.1 4))
    (hc/material-color! platform (hc/color [0 1 0]))
    platform))

(defn add-platform [platforms n]
  (let [platform (create-primitive :cube)
        t (tc/timeline* :loop
                        (tc/tween {:local {:position (l/v3 0 0 -12)}} platform 2)
                        (destroy platform))]
    (hc/name! platform n)
    (hc/local-scale! platform (l/v3 1 0.1 4))
    (hc/material-color! platform (hc/color [0 1 0]))
    (child+ platforms platform)))

(def tick (atom 100))

(defn tick-tock [platforms k]
  (when (= @tick 0)
   (add-platform platforms (str "pl" (rand))))
  (swap! tick #(if-not (= % 0) (dec %) 100)))

(hook+ (object-named "platforms") :update :tick-tock #'game.core/tick-tock)
