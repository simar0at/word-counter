xquery version "1.0";
declare option saxon:output "method=text";
string-join( 
for $word in wordlist/word
let $text := normalize-space($word/text()[1]) (:there might be more text nodes due to new lines...:)
where $text = "ما"
return
   string-join($word/foundAmidst, concat($text, ': ', $word/@count, "&#10;"))
, "&#10;")