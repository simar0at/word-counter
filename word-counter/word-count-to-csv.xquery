xquery version "1.0";

declare default element namespace "http://www.siam.homeunix.net/tokenlist";
declare option saxon:output "method=text";

declare function local:how_many_more($t as node(),
                                     $shown as xs:integer) as xs:string {
    let $count := count($t/form) - $shown                                      return if ($count > 1) then
        concat(" und ", $count, " weitere Formen.")
    else
       ""
};

for $t in //t[form]
return
   concat($t/text()[1], ";", $t/@count, ";", string-join($t/form[position() < 4], ", "), local:how_many_more($t, 4), "&#10;")