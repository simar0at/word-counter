A word counter for Egyptian Arabic
==================================

Count words of arz (egyptian arabic) texts like the preprocessed wikipedia,
sort the result by frequency of that word.
This is an early work in progress which might or might not be useful for anything
but the words in the egyptian wikipedia.
It works like this: it finds all words according to some Java RegExp defining seprating
characters and counts all strings that seem to be alike.
Then it generates all possible pre- and suffixes for a string and if it
finds that generated string within the rest of the strings it joins the data
for those two. These joins are done in a loop (actually some) until all possible
combinations are grouped together. There is a growing list of string combinations that
should not lead to a join of two groups. There is a list of joins that are irregular or
at least not tried by the code.
During all the joining operations all data is retained.
The last step is to generate the list of samples with the specified length.
The contents of this list is proportional to the distribution of the forms of a word.
The collection is done using java.util.Random.
For the egyptian wikipedia (ca. 3 Mio. strings) this needs about 1.7 GB of RAM to do its job.

Stemmer
-------

This program can use Jython to run the included ISRI stemmer from the NLTK
project. The results are currently so bad that it is disabled.

Output
------
The output is in XML looks sth. like this:

~~~~~ XML
<?xml version="1.0" encoding="UTF-8"?>
<tokenlist xmlns="http://www.siam.homeunix.net/tokenlist">
<comment>Processing text in testwords</comment>
<comment>Processed 22 token and their delimiters.</comment>
<t count="4" att="[HAS_ALLOGRAPH_END_YA_ALIF_MAQSURA, HAS_ALLOGRAPH_ALIF_HAMZA_BELOW]">اللى<form count="1">إللى</form>
<form count="1">إللي</form>
<form count="1">اللى</form>
<form count="1">اللي</form>
<tic><s> </s><w>إللي</w><s> </s><w>اللى</w><s> </s><w>اللي</w><s> </s><w>لي</w><s> </s><w>لى</w><s> </s><w>فى</w><s> </s><w>في</w><s> </s></tic>
<tic><u>></u><w>إللى</w><s> </s><w>إللي</w><s> </s><w>اللى</w><s> </s><w>اللي</w><s> </s><w>لي</w><s> </s><w>لى</w><s> </s><w>فى</w><s> </s></tic>
<tic><u>></u><u>></u><u>></u><w>إللى</w><s> </s><w>إللي</w><s> </s><w>اللى</w><s> </s><w>اللي</w><s> </s><w>لي</w><s> </s><w>لى</w><s> </s></tic>
<tic><u>></u><u>></u><u>></u><u>></u><u>></u><w>إللى</w><s> </s><w>إللي</w><s> </s><w>اللى</w><s> </s><w>اللي</w><s> </s><w>لي</w><s> </s></tic>
</t>
<comment>These are the 2 most frequent token with possble variants.
There were 18 more token (including those declared unknown by regexp) found in the input text</comment>
</tokenlist>
~~~~~

Support files
-------------
There is a <oXygen/> project and some xquery files which can be used with
the output of this program.

Legal stuff
-----------

This code is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License version 2 only, as
published by the Free Software Foundation and the Classpath Exception
by Oracle.