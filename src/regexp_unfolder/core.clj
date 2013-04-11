(ns regexp-unfolder.core
  (:require [instaparse.core :as insta]))

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


