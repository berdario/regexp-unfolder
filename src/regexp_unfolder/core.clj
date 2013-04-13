(ns regexp-unfolder.core
  (:require [instaparse.core :as insta])
  (:require [clojure.core.logic :as l]))

(def parse-regexp (insta/parser 
             "re = union | simple-re?
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

(defn handle-tree [q qto [ type & nodes]]
  (if (nil? nodes)
    [[q "" qto]]
    ((fns type) q qto nodes)))

(defn star [q qto node &] handle-tree q qto node)

(defn plus [q qto node &] handle-tree q qto node)

(defn any-char [q qto node &] handle-tree q qto node )

(defn handle-set [q qto node &] handle-tree q qto node)

(defn handle-negset [q qto node &] handle-tree q qto node)

(defn items [q qto nodes] (map (partial handle-tree q qto) nodes))

(defn handle-range [q qto node &] handle-tree q qto node)

(defn handle-char [q qto node &] (print node) )

(defn handle-concat [q qto nodes] 
  (let [syms (for [x  (rest nodes)] (gensym q))]
    (mapcat handle-tree  (cons q syms) (conj syms qto ) nodes)
  ))

(defn handle-first [q qto node &] (handle-tree q qto node))

(def fns {:re handle-first, :union handle-first, :simple-re handle-first, :concat handle-concat, :base-re handle-first, :star star, :plus plus, :elementary-re handle-first, :any any-char, :group handle-first, :set handle-first, :positive-set handle-set, :negative-set handle-negset, :set-items items, :set-item handle-first, :range handle-range, :char handle-char})

(l/defne transition-membero
  [state trans newstate otransition]
  ([_ _ _ [state trans-set newstate]]
     (l/membero trans trans-set)))
 
(defn transitiono [state trans newstate transitions]
  (l/conde
   [(l/fresh [f] 
             (l/firsto transitions f)
             (transition-membero state trans newstate f))]
   [(l/fresh [r]
             (l/resto transitions r)
             (transitiono state trans newstate r))])
  )

(declare transitions)
 
;; Recognize a regexp finite state machine encoded in core.logic, adapted from a snippet made by Peteris Erins

(defn recognizeo
  ([input]
     (recognizeo 'q0 input))
  ([q input]
     (l/matche [input] ; start pattern matching on the input
        (['("")]
           (l/== q 'ok)) ; accept the empty string if we are in an accepting state
        ([[i . nput]]
           (l/fresh [qto]
                  (transitiono q i qto transitions) ; assert it must be what we transition to qto from q with input symbol i
                  (recognizeo qto nput)))))) ; recognize the remainder
 

(defn unfold [regex] 
  (def transitions
    [['q0 [""] 'ok]
     ['q0 [\a \b] 'q0]] )
  (map (partial apply str) (l/run* [q] (recognizeo q))))
