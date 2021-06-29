(ns game.core)

(start-game (object-named "scene"))

(clear-scene! (object-named "scene"))

;; load platforms
(child+ (object-named "scene") (hc/clone! :platforms))

(def spawner (agent nil))
(restart-agent spawner nil)
(agent-error spawner)

(let [pl (object-named "platforms")]
  (send-off spawner spawn-platform pl))

;; (tc/timeline* :loop
;;   (tc/wait 0.5)
;;   (tc/tween {:local {
;;                      :position (l/v3 0 2 0)
;;                      :scale (l/v3 (rand))}} (object-named "Cube") 0.9))



(tc/abort!)

(destroy (object-named "Cube"))

;(platform-update (object-named "pl0"))
(destroy (object-named "pl0"))

;(hook+ (object-named "pl0") :update :move #'game.core/platform-update)
(hook- (object-named "platforms") :update :tick-tock)

(add-platform (object-named "platforms") (str "pl" (rand)))

(tc/timeline$ :loop
              (tc/wait 1)
              (add-platform (object-named "platforms") (str "pl" (rand))))
