xquery version "1.0";
declare option saxon:output "method=xml";
declare option saxon:output "indent=yes";
(:string-join( :) 
for $wordItem in subsequence(wordlist/word, 2, 10)
let $word := normalize-space($wordItem/text()[1]) (:there might be more text nodes due to new lines...:)
(:where $text = "لغايه":)
return
   string-join(( 
   for $context in $wordItem/foundAmidst 
   (:for $context in subsequence($wordItem/foundAmidst, 0, 10):)
   order by $context/t[10]
   return
   (: pos 8/15 is the token itsself :)
      string-join($context/t, "")
   ), concat($word, ': ', $wordItem/@count,
   "&#10;"))
(:), "&#10;"):)