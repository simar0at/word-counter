xquery version "1.0";

declare default element namespace "http://www.siam.homeunix.net/tokenlist";
declare option saxon:output "method=text";

for $t in //t[orth]
return
   concat($t/text()[1], ";", $t/@count, "&#10;")