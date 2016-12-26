(ns mars-exploration.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [mars-exploration.core :refer :all]))
(require '[clojure.test.check :as tc])
(require '[clojure.test.check.generators :as gen])
(require '[clojure.test.check.properties :as prop])
(require '[clojure.test.check.clojure-test :as ct])

(def plateau5x5 (new-plateau [5 5]))

(fact "A full turn should leave the probe inaltered."
  (let [probe (new-probe 3 2 :E)]
    (reduce turn-probe probe (repeat 4 :R)) => probe
    (reduce turn-probe probe (repeat 4 :L)) => probe))
    
(facts "About moving a probe forward"
  (fact "The probe's coordinate should change correctly."
    (move-probe-forward (new-probe 2 2 :E)) => (new-probe 3 2 :E)
    (move-probe-forward (new-probe 2 2 :N)) => (new-probe 2 3 :N)
    (move-probe-forward (new-probe 2 2 :W)) => (new-probe 1 2 :W)
    (move-probe-forward (new-probe 2 2 :S)) => (new-probe 2 1 :S)))

(facts "About the given examples"
  (let [moves1 (map char->move "LMLMLMLMM")
        moves2 (map char->move "MMRMMRMRRM")
        probe1 (new-probe 1 2 :N)
        probe2 (new-probe 3 3 :E)
        probe1' (new-probe 1 3 :N)
        probe2' (new-probe 5 1 :E)]
    (fact "The examples should be reproduced"
      (apply-movements probe1 moves1) => probe1'
      (apply-movements probe2 moves2) => probe2')
    (fact "The examples are all on the plateau"
      (is-on-plateau? plateau5x5 probe1) => truthy
      (is-on-plateau? plateau5x5 probe2) => truthy
      (is-on-plateau?
       plateau5x5
       (move-over-plateau plateau5x5 probe1 moves1)) => truthy
      (is-on-plateau?
       plateau5x5
       (move-over-plateau plateau5x5 probe2 moves2)) => truthy)))

(facts "About falling probes"
  (fact "A probe can be on the border of the plateau without falling"
    (let [probe (new-probe 5 5 :S)]
      (is-on-plateau? plateau5x5 probe) => truthy))
  (tabular
   (fact "A fallen probe can neither move nor turn"
     (let [p (assoc (new-probe 11 11 :N) :fallen? true)]
       (move-and-check plateau5x5 p ?move) => p))
   ?move
   :move
   :left
   :right)
  (fact "Once a probe falls, it stays down."
    (let [before (new-probe 5 5 :N)
          moves (map char->move "MMMM")
          after (set-fallen (new-probe 5 6 :N))]
      (move-over-plateau plateau5x5 before moves) => after)))
