(ns bowling.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

"In each frame the player has two opportunities to knock down 10 pins."
"ADDED: An open frame when a player doesn't knock down all 10 pins in two tries."
(s/def ::open-frame (s/and (s/coll-of (s/int-in 0 (inc 9))
                                      :kind vector?
                                      :count 2)
                           #(< (reduce + %) 10)))

(defn spare-frame-gen []
  (gen/fmap (fn [x] [x (- 10 x)])
            (s/gen (s/int-in 0 (inc 9)))))

"A spare is when the player knocks down all 10 pins in two tries."
(s/def ::spare-frame (s/with-gen (s/and (s/tuple (s/int-in 0 (inc 9))
                                                 (s/int-in 1 (inc 10)))
                                        #(= 10 (reduce + %)))
                                 spare-frame-gen))

"A strike is when the player knocks down all 10 pins on his first try."
(s/def ::strike-frame #{[10]})

(s/def ::frame (s/or :open ::open-frame
                     :spare ::spare-frame
                     :strike ::strike-frame))

(defn gen-strike-tenth []
  (gen/fmap (fn [& frames]
              (into [] (flatten frames)))
    (gen/tuple
      (s/gen ::strike-frame)
      (gen/one-of [(gen/tuple (s/gen ::strike-frame) (s/gen (s/int-in 0 (inc 10))))
                   (s/gen ::open-frame)
                   (s/gen ::spare-frame)]))))

(defn gen-spare-tenth []
  (gen/fmap (fn [& frames]
              (into [] (flatten frames)))
    (gen/tuple (s/gen ::spare-frame) (s/gen (s/int-in 0 (inc 10))))))

(defn gen-tenth-frame []
  (gen/one-of [(s/gen ::open-frame)
               (gen-strike-tenth)
               (gen-spare-tenth)]))

(defn gen-game []
  (gen/fmap
    (fn [[nine-frames tenth-frame]]
      (flatten (concat nine-frames [tenth-frame])))
    (gen/tuple (s/gen (s/coll-of ::frame :count 9))
               (gen-tenth-frame))))

(s/def ::game-rolls (s/with-gen (s/coll-of (s/int-in 0 (inc 10))
                                           :min-count 10
                                           :max-count 21)
                                gen-game))