(ns bowling.examples
  (:require [clojure.repl :refer [doc]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]))

(s/def ::my-string string?)

(comment
  (s/describe ::my-string)
  (s/valid? ::my-string "afasf")
  (s/valid? ::my-string 1)
  (s/exercise ::my-string)
  :end)

(s/def ::non-empty-string (s/and string? not-empty))

(comment
  (s/exercise ::non-empty-string)
  :end)

(s/def ::string-or-pos-int (s/or :string string?
                                 :number pos-int?))

(comment
  (s/exercise ::string-or-pos-int)
  (s/conform ::string-or-pos-int "adfas")
  (s/conform ::string-or-pos-int 0)
  (s/conform ::string-or-pos-int 1)
  :end)

(defn double-or-nothing [str-or-n]
  (* 2 (if (string? str-or-n)
         0
         str-or-n)))

(s/fdef double-or-nothing
  :args (s/cat :str-or-n ::string-or-pos-int)
  :ret number?
  :fn (fn [{:keys [args ret]}]
        (if (= :number (-> args :str-or-n first))
          (pos-int? ret)
          (zero? ret))))

(s/def :git/sha string?)
(s/def ::sha int?)

(s/def ::a string?)
(s/def ::b int?)
(s/def ::c int?)
(s/def ::d string?)
(s/def ::my-map (s/keys :req [::a ::b ::c ::sha]
                        :opt [::d :git/sha]))

(comment
  (s/exercise ::my-map)
  :end)
(comment
  (doc double-or-nothing)
  (s/exercise-fn `double-or-nothing)
  (stest/check `double-or-nothing)
  :end)

(comment
  (s/fdef double-or-nothing
    :args (s/cat :str-or-n (s/with-gen ::string-or-pos-int
                                       #(s/gen (s/int-in 0 (int (/ Integer/MAX_VALUE 2))))))
    :ret number?
    :fn (fn [{:keys [args ret]}]
          (if (= :number (-> args :str-or-n first))
            (pos-int? ret)
            (zero? ret)))))