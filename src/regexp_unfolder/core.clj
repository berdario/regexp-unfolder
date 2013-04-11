(ns regexp-unfolder.core
  (:require [net.cgrand.parsley :as p]))

(def metachars "\\\\|*+.\\-\\[\\]$()^" )
(def rchar (re-pattern (str \[ \^ metachars \] \| \\ \\ \[ metachars \])))

(comment (def regexp (p/parser {:main :re
                        :root-tag :re}
                       :char rchar
                       :range [:char "-" :char]
                       :set-item #{:range :char}
                       :set-items :set-item*
                       :positive-set ["["  :set-items "]"]
                       :negative-set ["[^" :set-items "]"]
                       :set #{:positive-set :negative-set}
                       :group ["(" :re ")"]
                       :elementary-re #{:group "." "$" :char :set}
                       :star [:elementary-re "*"]
                       :plus [:elementary-re "+"]
                       :base-re #{:elementary-re :star :plus}
                       :concat [:simple-re :base-re]
                       :simple-re #{:concat :base-re}
                       :union [:re "|" :simple-re]
                       :re #{:union :simple-re} )))
