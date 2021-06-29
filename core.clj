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

(defn platform-init-x [o]
  (let [old-pos (.. o transform position)
        velocity (l/v3 (+ -1.5 (int (* 4 (rand)))) 0 10)]
    (set! (.. o transform position)
          (l/v3+ old-pos velocity))
    (l/v3+ old-pos velocity)))

(defn add-platform [platforms n]
  (let [platform (create-primitive :cube)
        pos (platform-init-x platform)
        t (tc/timeline* :loop
                        (tc/wait 2)
                        (tc/tween {:local {:position (l/v3+ pos (l/v3 0 0 -22))}} platform 4)
                        (destroy platform))]
    (hc/name! platform n)
    (hc/local-scale! platform (l/v3 1 0.1 (* 4 (rand))))
    (hc/material-color! platform (hc/color [(rand) (rand) (rand)]))
    (child+ platforms platform)))

(def tick (atom (int (* 15 (rand)))))

(defn tick-tock [platforms k]
  (when (= @tick 0)
   (add-platform platforms (str "pl" (rand))))
  (swap! tick #(if-not (= % 0) (dec %) (int (* 15 (rand))))))

(hook+ (object-named "platforms") :update :tick-tock #'game.core/tick-tock)
