(ns bowling.rolls
  (:require [bowling.spec :as bowling]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.repl :refer [doc]]
            [expound.alpha :as expound]))

(set! s/*explain-out* expound/printer)

(defn safe-sum [& xs]
  (transduce (keep identity) + xs))

(s/fdef safe-sum
  :args (s/cat :xs (s/* (s/or :int (s/int-in 0 (inc 10))
                              :nil nil?)))
  :ret int?)
  
(defn score-frame [[r1 r2] [r3 r4 & _]]
  (cond
    (= 10 r1) (safe-sum r1 r3 r4)
    (= 10 (safe-sum r1 r2)) (safe-sum r1 r2 r3)
    :else (safe-sum r1 r2)))

(s/fdef score-frame
  :args (s/cat :frame ::bowling/frame
               :rolls (s/coll-of (s/int-in 0 (inc 10))))
  :ret (s/int-in 0 (inc 30))
  :fn (fn [{:keys [args ret]}]
        (case (-> args :frame first)
          :open (< ret 10)
          :spare (<= 10 ret 20)
          :strike (<= 10 ret 30))))

(defn ->frames [rolls]
  (loop [frames [] [r1 & rs] rolls]
    (cond
      (nil? r1) frames
      (= 10 r1) (recur (conj frames [r1]) rs)
      :else (recur (conj frames [r1 (first rs)]) (rest rs)))))

(s/fdef ->frames
  :args (s/cat :rolls (s/coll-of (s/int-in 0 (inc 10))))
  :ret (s/coll-of ::bowling/frame))

(defn score-frames [frames]
  (loop [scored [] [frame & remaining] frames]
    (if (nil? frame)
      scored
      (recur (conj scored (score-frame frame (flatten remaining)))
             remaining))))

(s/fdef score-frames
  :args (s/cat :frames (s/coll-of ::bowling/frame))
  :ret (s/coll-of (s/int-in 0 (inc 30))))

(defn score-game [rolls]
  (reduce + (take 10 (score-frames (->frames rolls)))))

(s/fdef score-game
  :args (s/cat :rolls ::bowling/game-rolls)
  :ret (s/int-in 0 (inc 300)))

(comment
  (do
    (assert (= 0 (score-game (repeat 20 0))))
    (assert (= 20 (score-game (repeat 20 1))))
    (assert (= 16 (score-game (into [5 5 3] (repeat 17 0)))))
    (assert (= 24 (score-game (into [10 3 4] (repeat 16 0)))))
    (assert (= 300 (score-game (repeat 12 10)))))

  (-> 'bowling.rolls
      stest/enumerate-namespace
      stest/check
      expound/explain-results)

  (expound/explain-results (stest/check 'bowling.rolls/->frames))

  :end)
