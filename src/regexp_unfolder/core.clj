(ns regexp-unfolder.core
  (:require [instaparse.core :as insta])
  (:require [clojure.core.logic :as l]))

(def regexp (insta/parser 
             "re = union | simple-re
             union = re '|' simple-re
             simple-re = concat | base-re
             concat = simple-re base-re
             base-re = elementary-re | star | plus
             star = elementary-re '*'
             plus = elementary-re '+'
             elementary-re = group | any | '$' | char | set
             any = '.'
             group = '(' re ')'
             set = positive-set | negative-set
             positive-set = '['  set-items ']'
             negative-set = '[^' set-items ']'
             set-items = set-item*
             set-item = range | char
             range = char '-' char
             char = #'[^\\\\\\-\\[\\]]|\\.'" ))

(declare fns)

(defn handle-tree [[ type & nodes]]
  ((fns type) nodes))

(defn star [node &] handle-tree node)

(defn plus [node &] handle-tree node)

(defn any-char [node &] handle-tree node )

(defn handle-set [node &] handle-tree node)

(defn handle-negset [node &] handle-tree node)

(defn items [nodes] (map handle-tree nodes))

(defn handle-range [node &] handle-tree node)

(defn handle-char [node &] (print node) )

(def handle-first (comp handle-tree first))

(def fns {:re handle-first, :union handle-first, :simple-re handle-first, :concat handle-first, :base-re handle-first, :star star, :plus plus, :elementary-re handle-first, :any any-char, :group handle-first, :set handle-first, :positive-set handle-set, :negative-set handle-negset, :set-items items, :set-item handle-first, :range handle-range, :char handle-char})

 
;; Encoding a regexp finite state machine in core.logic, adapted from a snippet made by Peteris Erins
;; the state space is #{'ok 'fail}, 'ok is the accepting state, the alphabet is made of all the printable characters
;; most of the logic thus deals with the starting state and the transitions

(declare transitions)
 
(defn transitiono [state trans new transitions]
  (l/conde
   [(l/fresh [f] 
             (l/firsto transitions f)
             (l/== [state trans new] f))]
   [(l/fresh [r]
             (l/resto transitions r)
             (transitiono state trans new r))])
  )
 
(defn recognizeo
  ([input]
     (l/fresh [q0]
            (recognize q0 input)))
  ([q input]
     (l/matche [input] ; start pattern matching on the input
        (['("")]
           (l/== q 'ok)) ; accept the empty string if we are in an accepting state
        ([[i . nput]]
           (l/fresh [qto]
                  (transitiono q i qto transitions) ; assert it must be what we transition to qto from q with input symbol i
                  (recognize qto nput)))))) ; recognize the remainder
 

(defn unfold [regex] 
  (def transitions
    [['ok \a 'ok]
     ['ok \b 'fail]
     ['fail \a 'fail]
     ['fail \b 'fail]] )
  (map (partial apply str) (l/run* [q] (recognizeo q))))
