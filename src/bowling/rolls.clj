(ns bowling.rolls)

(defn safe-sum [& xs]
  (transduce (keep identity) + xs))
  
(defn score-frame [[r1 r2] [r3 r4 & _]]
  (cond
    (= 10 r1) (safe-sum r1 r3 r4)
    (= 10 (safe-sum r1 r2)) (safe-sum r1 r2 r3)
    :else (safe-sum r1 r2)))

(defn ->frames [rolls]
  (loop [frames [] [r1 & rs] rolls]
    (cond
      (nil? r1) frames
      (= 10 r1) (recur (conj frames [r1]) rs)
      :else (recur (conj frames [r1 (first rs)]) (rest rs)))))

(defn score-frames [frames]
  (loop [scored [] [frame & remaining] frames]
    (if (nil? frame)
      scored
      (recur (conj scored (score-frame frame (flatten remaining)))
             remaining))))

(defn score-game [rolls]
  (reduce + (take 10 (score-frames (->frames rolls)))))

(comment
  (do
    (assert (= 0 (score-game (repeat 20 0))))
    (assert (= 20 (score-game (repeat 20 1))))
    (assert (= 16 (score-game (into [5 5 3] (repeat 17 0)))))
    (assert (= 24 (score-game (into [10 3 4] (repeat 16 0)))))
    (assert (= 30 (score-game (repeat 12 10)))))
  :end)
