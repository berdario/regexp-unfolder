(ns regexp-unfolder.core-test
  (:use midje.sweet
        regexp-unfolder.core))

(defn strchar? [c]
  (or
   (char? c) 
   (and ((juxt string? #(= 1 (count %))) c))) )

(facts "parse-to-fsm is correct" 
 (fact 
  (parse-to-fsm "a") => [['q0 ["a"] 'ok]]
  (parse-to-fsm "") => [['q0 [""] 'ok]])

 (fact
  (parse-to-fsm "ab") => (just (just 'q0 ["a"] anything)
                               (just anything ["b"] 'ok)))
 (fact
  (parse-to-fsm "a+") => [['q0 ["a"] 'ok]
                          ['ok ["a"] 'ok]])

 (fact
  (parse-to-fsm "a*") => [['q0 [""] 'ok]
                          ['q0 ["a"] 'q0]])

 (fact
  (parse-to-fsm ".") => (just (just 'q0 (n-of strchar? (count printables)) 'ok )))

 (fact
  (parse-to-fsm "[ab]") => [['q0 ["a" "b"] 'ok]])

 (fact
  (parse-to-fsm "[a-c]") => [['q0 [\a \b \c] 'ok]])

 (fact
  (parse-to-fsm "[^0-9]") => 
  (just (just 'q0 
              (n-of 
               #(and (strchar? %) 
                     (not (re-matches #"\d" (str %))))            
               (- (count printables) 10)) 
              'ok)))
)


(facts "the unfolder actually generates matching strings"
       (doseq [re [""  "a" "ab" "a+" "a*" "." "[ab]" "[a-c]" "[^0-9]"
                   "ba[rz]" "[a-z3-7]" "[a-z3-7][01]" "[a-c]*" "a+b+"]]
         (fact (and (map 
                     #(re-matches (re-pattern re) %)
                     (take 40 (-unfold re))))
               => boolean)))


