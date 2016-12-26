(ns mars-exploration.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn char->move
  "Converts a single character to a corresponding move.
  If the char does not represent a valid move, a \"null\" move
  is returned."
  [c]
  (case c
    \L :left
    \R :right
    \M :move
    :noOp))

(defn new-plateau
  "Creates a new plateau to be explored. Takes the upper-right
   coordinate as a parameter."
  [[x y]]
  {:lower-bounds {:x 0 :y 0} 
   :upper-bounds {:x x :y y}
   :probes []})

(defn new-probe
  "Creates a new probe."
  [x y direction]
  {:x x :y y :direction direction})

;; TODO check for out of bounds?!
(defn add-probe
  "Adds a new probe to an existing plateau. Takes an existing plateau
  and the probe's ''x'' and ''y'' position on the plateau and its ''direction''."
  [plateau x y direction]
  (let [probe (new-probe x y direction)
        probes (get plateau :probes)
        probes' (conj probes probe)]
    (assoc plateau :probes probes')))

(defn move-probe-forward
  "Moves a probe in its current direction."
  [{x :x
    y :y
    dir :direction
    :as probe}]
  (case dir
    :N (assoc probe :y (inc y))
    :S (assoc probe :y (dec y))
    :E (assoc probe :x (inc x))
    :W (assoc probe :x (dec x))))

(def clockwise-directions
  [:S :W :N :E])

(defn turn-probe
  "Turns a probe left or right."
  [{x :x
    y :y
    dir :direction
    :as probe} lOrR]
  (let [index (.indexOf clockwise-directions dir)
        sense (if (= lOrR :right) inc dec)
        index' (mod (sense index) (count clockwise-directions))
        dir' (nth clockwise-directions index')]
    (assoc probe :direction dir')))

(defn apply-movement
  "Applies a movement key to a probe."
  [probe movement]
  (cond
   (or (= movement :right) (= movement :left)) (turn-probe probe movement)
   (= movement :move) (move-probe-forward probe)))

(defn apply-movements
  "Applies a sequence of movement keys to a probe."
  [probe seq-of-moves]
  (reduce apply-movement probe seq-of-moves))
