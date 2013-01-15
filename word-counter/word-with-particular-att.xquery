xquery version "1.0";
declare option saxon:output "method=xml";
declare option saxon:output "indent=yes";
for $wordItem in /wordlist/word
let $att := $wordItem/@att 
where contains($att, "HAS_ALLOGRAPH_ALIF_HAMZA")
return (
<word count="{$wordItem/@count}"> {$wordItem/text()[1]} {
for $context in $wordItem/foundAmidst 
   (:for $context in subsequence($wordItem/foundAmidst, 0, 10):)
   let $contextText := string-join($context/t, "")
   order by $context/t[10]
   (: pos 8/15 is the token itsself :)
   return 
   <foundAmidst>{$contextText}</foundAmidst>
}
</word>
)
(:    concat($word/text()[1], ": ", $att, "&#10;"):)