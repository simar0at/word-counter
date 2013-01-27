xquery version "1.0";

declare default element namespace "http://www.siam.homeunix.net/tokenlist";
declare option saxon:output "method=text";

(:declare function local:how_many_more($t as node(),
                                     $shown as xs:integer) as xs:string {
    let $count := count($t/form) - $shown
    return if ($count > 1) then
        concat(";und;", $count, ";weitere Formen.")
    else
       ";;;"
};:)

declare function local:how_many_more($t as node(),
                                     $shown as xs:integer) as xs:string {
    let $count := count($t/form) - $shown
    return if ($count > 1) then
        concat(";und ", $count, " weitere Formen.")
    else
       ";"
};

declare function local:examples_with_precent($forms as node()+ 
                                            ) as xs:string {
    string-join(
    ( for $word in $forms
    return
       concat($word, '(', round($word/@count div $word/../@count * 100), '%)')
    ), ", ")
};

string-join(
for $t in //t[form]
return
   concat($t/text()[1], ";", $t/@count, ";", local:examples_with_precent($t/form[position() <= 5]), local:how_many_more($t, 5), "&#10;")
, "") 