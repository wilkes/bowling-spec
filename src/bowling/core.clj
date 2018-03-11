(ns bowling.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.repl :refer [doc]]))

"
The game consists of 10 frames.
In each frame the player has two opportunities to knock down 10 pins.
The score for the frame is the total number of pins knocked down, plus bonuses
for strikes and spares.

A spare is when the player knocks down all 10 pins in two tries.
The bonus for that frame is the number of pins knocked down by the next roll.

A strike is when the player knocks down all 10 pins on his first try.
The bonus for that frame is the value of the next two balls rolled.

In the tenth frame a player who rolls a spare or strike is allowed to roll the
extra balls to complete the frame.  However no more than three balls can be
rolled in tenth frame.
"

"The game consists of 10 frames."
(s/def ::game (s/coll-of ::frame :kind vector? :count 10))

"In each frame the player has two opportunities to knock down 10 pins."
(s/def ::base-frame (s/and (s/coll-of (s/int-in 0 (inc 10))
                                      :kind vector?
                                      :min-count 1
                                      :max-count 2)
                           #(<= (reduce + %) 10)))

"ADDED: An open frame when a player doesn't knock down all 10 pins in two tries."
(s/def ::open-frame (s/and (s/coll-of (s/int-in 0 (inc 9))
                                      :kind vector?
                                      :count 2)
                           #(< (reduce + %) 10)))

(defn spare-frame-gen []
  (gen/fmap (fn [x] [x (- 10 x)])
            (s/gen (s/int-in 0 (inc 9)))))

"A spare is when the player knocks down all 10 pins in two tries."
(s/def ::spare-frame (s/with-gen (s/and ::base-frame
                                        #(= 10 (reduce + %))
                                        #(= 2 (count %)))
                                 spare-frame-gen))

"A strike is when the player knocks down all 10 pins on his first try."
(s/def ::strike-frame #{[10]})

(s/def ::frame (s/or :open ::open-frame
                     :spare ::spare-frame
                     :strike ::strike-frame))

"In the tenth frame a player who rolls a spare or strike is allowed to roll the
extra balls to complete the frame. However no more than three balls can be
rolled in tenth frame."
(s/def ::tenth-frame (s/and (s/coll-of (s/int-in 0 (inc 10)))
                            :kink vector?
                            :min-count 2
                            :max-count 3))

"The score for the frame is the total number of pins knocked down, plus bonuses
for strikes and spares."

"The bonus for a spare is the number of pins knocked down by the next roll."

"The bonus for that frame is the value of the next two balls rolled."

"ADDED: The score for the game is the sum of the scores for all 10 frames."