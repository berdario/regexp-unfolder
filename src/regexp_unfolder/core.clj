(ns regexp-unfolder.core
  (:require [instaparse.core :as insta]))

(def rchar #"[^\\\-\[\]]|\\." )

(def regexp (insta/parser 
             (str "
             re = union | simple-re
             union = re '|' simple-re
             simple-re = concat | base-re
             concat = simple-re base-re
             base-re = elementary-re | star | plus
             star = elementary-re '*'
             plus = elementary-re '+'
             elementary-re = group | '.' | '$' | char | set
             group = '(' re ')'
             set = positive-set | negative-set
             positive-set = '['  set-items ']'
             negative-set = '[^' set-items ']'
             set-items = set-item*
             set-item = range | char
             range = char '-' char
             char = #'" rchar "'" )))
