(ns mars-exploration.core
  (:gen-class))



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

(def map-char-to-dir
  {\N :N
   \E :E
   \W :W
   \S :S})

(def map-dir-to-char
  (reduce
   (fn [acc [k v]] (assoc acc v k))
   {}
   map-char-to-dir))

(defn char->direction
  "Converts a single character to a compass direction."
  [c]
  (map-char-to-dir c))

(defn direction->char
  "Converts a compass direction to a character."
  [d]
  (map-dir-to-char d))

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

(defn *is-on-plateau?
  "Helper function to `is-on-plateau?`"
  [plateau [x y]]
  (let [[[lx ly] [ux uy]] (get-bounds plateau)]
    (if (and (<= lx x ux) (<= ly y uy))
      true
      false)))

(defn is-on-plateau?
  "Checks whether a probe is on the plateau or if it has fallen."
  [plateau probe]
  (let [{x :x y :y} probe]
    (*is-on-plateau? plateau [x y])))

(def is-fallen? (complement is-on-plateau?))

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
        fallen? (is-fallen? plateau probe)
        fallen?' (is-fallen? plateau probe')]
    (cond
      fallen? probe
      :else (set-fallen probe' fallen?'))))

(defn move-over-plateau
  "Moves a probe over a plateau. If the probes falls off the plateau,
   it won't move anymore."
  [plateau probe seq-of-moves]
  (reduce (partial move-and-check plateau) probe seq-of-moves))

(defn get-line
  "Gets a trimmed line of input from stdin."
  []
  (let [input (read-line)]
    (if (nil? input)
      ""
      (clojure.string/upper-case (clojure.string/trim input)))))

;; TODO Add further validation?!
(defn parse-plateau-size
  "Gets the plateau size from stdin and produces a new plateau."
  []
  (let [input (get-line)
        parse-int #(Integer/parseInt %)
        [ux uy] (map parse-int (clojure.string/split input #" +"))
        plateau (new-plateau [ux uy])]
    plateau))

;; TODO Add further validation?!
(defn parse-initial-probe-position
  []
  (let [input (get-line)]
    (if (empty? input)
      :end
      (let [[cx cy cd] (clojure.string/split input  #" +")
            parse-int #(Integer/parseInt %)
            [x y] (map parse-int [cx cy])
            direction (char->direction (first cd))]
        (new-probe x y direction)))))

(defn parse-movements
  []
  (map char->move (get-line)))

(defn process-sequence-of-probes
  "To be used in the main loop of the program.
   Given an existing plateau and a vector of previous probes,
   tries to get information about the new probe.
   If it gets an empty line, all probes are printed
   and the program exits."
  [plateau v-probes]
  (let [probe (parse-initial-probe-position)]
    (if (= probe :end)
      v-probes
      (let [probe' (set-fallen probe (is-fallen? plateau probe))
            moves (parse-movements)
            probe'' (move-over-plateau plateau probe' moves)]
        (recur plateau (conj v-probes probe''))))))

(defn print-probe
  "Returns the string representation of a single probe
   in the desired output format."
  [{x :x y :y direction :direction}]
  (apply str (interpose " " [x y (map-dir-to-char direction)])))

(def map-dir-to-probe-str
  {:N \^
   :W \<
   :E \>
   :S \v})

(defn generate-pairs
  "Generates pairs of elements from two collections. The first element
  varies faster."
  [v1 v2]
  (let [inner (fn [y]
                (map #(vec [% y]) v1))]
    (map inner v2)))

(defn print-blank-plateau
  [plateau]
  (let [[[lx ly] [ux uy]] (get-bounds plateau)
        [lx' ly'] [(dec lx) (dec ly)]
        [ux' uy'] [(+ 2 ux) (+ 2 uy)]
        print-xy (fn [[x y]]
                   (if (*is-on-plateau? plateau [x y])
                     "."
                     " "))
        print-row (fn [row]
                    (apply str (map print-xy row)))]
    (map print-row (generate-pairs (range lx' ux') (range ly' uy')))))

(defn print-plateau
  "Returns the string representation of a plateau
   and the probes over it."
  [plateau v-probes]
  (let [[[lx ly] [ux uy]] (get-bounds plateau)
        [lx' ly'] [(dec lx) (dec ly)]
        [ux' uy'] [(+ 2 ux) (+ 2 uy)]
        blank (vec (map vec (print-blank-plateau plateau)))
        print-probe (fn [acc {x :x y :y dir :direction}]
                      (assoc-in acc [(+ (- uy' y) ly') (- x lx')] (map-dir-to-probe-str dir)))
        raw-plateau (reduce print-probe blank v-probes)]
    (map #(apply str %) raw-plateau)))

(defn -main
  [& args]
  (do
    (def plot?
      (if (= "--plot" (first args))
        true
        false))
    (def plateau (parse-plateau-size))
    (def v-probes (process-sequence-of-probes plateau []))
    (dorun (map (comp println print-probe) v-probes))
    (if plot?
      (doseq [line (print-plateau plateau v-probes)]
        (println line))
      nil)))
