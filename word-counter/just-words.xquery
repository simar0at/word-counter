xquery version "1.0";
declare option saxon:output "method=text";
(:string-join( :)
for $word in wordlist/word
let $text := $word/text()[1] (:there might be more text nodes due to new lines...:)
return
   $text
(:, "&#10;"):)