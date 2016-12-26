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
   :upper-bounds {:x x :y y}})

(defn get-bounds
  "Extracts the lower-left and upper-right coordinates from a plateau."
  [plateau]
  (let [{lx :x ly :y} (get plateau :lower-bounds)
        {ux :x uy :y} (get plateau :upper-bounds)]
    [[lx ly] [ux uy]]))

(defn is-on-plateau?
  "Checks whether a probe is on the plateau or if it has fallen."
  [plateau probe]
  (let [[[lx ly] [ux uy]] (get-bounds plateau)
        {x :x y :y} probe]
    (if (and (<= lx x ux) (<= ly y uy))
      true
      false)))

(defn new-probe
  "Creates a new probe."
  [x y direction]
  {:x x
   :y y
   :direction direction
   :fallen? false})

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

(defn set-fallen
  "Marks a probe as fallen. If a status is provided, set that to the probe."
  ([probe]
   (assoc probe :fallen? true))
  ([probe status]
   (assoc probe :fallen? status)))

(defn move-and-check
  "Moves a probe over a plateau (if it has not fallen yet)
   and marks it as \"fallen\" if it walks off the plateau."
  [plateau probe move]
  (let [probe' (apply-movement probe move)
        fallen? (not (is-on-plateau? plateau probe))
        fallen?' (not (is-on-plateau? plateau probe'))]
    (cond
      fallen? probe
      :else (set-fallen probe' fallen?'))))

(defn move-over-plateau
  "Moves a probe over a plateau. If the probes falls off the plateau,
   it won't move anymore."
  [plateau probe seq-of-moves]
  (reduce (partial move-and-check plateau) probe seq-of-moves))
