(ns game.core
  (:require [arcadia.core :refer :all]
            [arcadia.linear :as l]
            [hard.core :as hc]
            [hard.physics :as hp]
            [hard.input :as hi]
            [tween.core :as tc]))

(defn clear-scene! [o]
  (doseq [c (children o)]
    (destroy c)))

(defn start-game [o]
  (map
   #(child+ o (hc/clone! %))
   [:camera :sun]))

(defn platform-init-x [o]
  (let [old-pos (.. o transform position)
        velocity (l/v3 (+ -1.5 (int (* 4 (rand)))) 0 10)]
    (set! (.. o transform position)
          (l/v3+ old-pos velocity))
    (l/v3+ old-pos velocity)))

(defn player-move [o velocity]
  (let [old-pos (.. o transform position)
        ;velocity (l/v3 (+ -1.5 (int (* 4 (rand)))) 0 10)
        ]
    (set! (.. o transform position)
          (l/v3+ old-pos velocity))
    (l/v3+ old-pos velocity)))

(defn player-jump [o]
  (hp/global-force! (hp/->rigidbody o) 0 30 0))

(defn add-platform [platforms n]
  (let [platform (create-primitive :cube)
        pos (platform-init-x platform)
        t (tc/timeline* :loop
                        (tc/wait 2)
                        (tc/tween {:local {:position (l/v3+ pos (l/v3 0 0 -22))}} platform 7)
                        (destroy platform))]
    (hc/name! platform n)
    (hc/local-scale! platform (l/v3 1 0.1 (+ 1 (* 2 (rand)))))
    (hc/material-color! platform (hc/color [(rand) (rand) (rand)]))
    (child+ platforms platform)))

(def spawn-time 25)
(def tick (atom (int (+ spawn-time (* spawn-time (rand))))))
(def player (create-primitive :sphere))
(def stable (create-primitive :cube))
(def on-ground? (atom false))

(defn create-player [player]
  (hc/local-scale! player (l/v3 0.2 0.2 0.2))
  (set! (.. player transform position) (l/v3 -3 1.4 -7.8 
                                        ;-0.2 0 -10
                                        )))

(defn handle-input [o k]
  (if (hi/key? "left") (player-move o (l/v3 -0.05 0 0)))
  (if (hi/key? "right") (player-move o (l/v3 0.05 0 0)))
  (if (and (hi/key? "space")
           @on-ground?) (player-jump o))
  (if (< (hc/Y player) -10)
    (set! (.. player transform position) (l/v3 -3 1.4 -7.8 
                                        ;-0.2 0 -10
                                        ))))

(defn tick-tock [platforms k]
  (when (= @tick 0)
   (add-platform platforms (str "pl" (rand))))
  (swap! tick #(if-not (= % 0) (dec %) (int (+ spawn-time (* spawn-time (rand)))))))

(defn handle-ground [player c k]
  (swap! on-ground? not))

(defn init-game [scene k]
  (let [platforms (hc/clone! :platforms)]
    (doseq [r [:camera :sun]]
      (child+ scene (hc/clone! r)))
    (child+ scene platforms)
    (hook+ platforms :update :tick-tock #'game.core/tick-tock)
    (hook+ player :update :move #'game.core/handle-input)
    (hook+ player :on-collision-enter :collision-enter #'game.core/handle-ground)
    (hook+ player :on-collision-exit :collision-exit #'game.core/handle-ground)))


