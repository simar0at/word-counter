package net.homeunix.siam.wordcounter;
/**
 * Count words of arz (egyptian arabic) texts like the preprocessed wikipedia, sort the result by frequency of that word.
 * This program will try to consider the similarity of arabic words as best as possible.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import net.homeunix.siam.wordcounter.MasryConsts.WordCounterData;

/**
 * Hull class
 * 
 * @author Omar Siam
 *
 */
public class Run {
	
	public static final String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * To get a word/frequency map sorted by the frequency of a word this seems to be the most simple solution.
	 * Obviously needs an unsorted key/value map as an input. Sorts by frequency and then alphabetically.
	 * 
	 * @author Omar Siam
	 *
	 */
	public static class IntArray0ValueComp implements Comparator<String>
	{

    	Map<String, WordCounterData> base;
    	
    	public IntArray0ValueComp(Map<String, WordCounterData> baseMap) {
    		base = baseMap;
    	}
    	
		@Override
		public int compare(String o1, String o2) {
			// Sort the most frequently used first.
			int ret = -Integer.compare(base.get(o1).count, base.get(o2).count);
			// If frequency is the same sort alphabetically ascending
			if (0 == ret) {
				ret = o1.compareTo(o2);
			}
			return ret;
		}
    	
    }
	
	public static class CollectRemovals {
		public int skipShorterThan = 3;
		public Queue<String> toRemove = new ArrayDeque<String>();
		private Map<String, WordCounterData> wordCount;
		
		public CollectRemovals(Map<String, WordCounterData> wordCount) {
			this.wordCount = wordCount;
		}
		
		public interface VaryWord {
			public String varyWord(String inputWord, String afix);
		}
		
		private class PrefixWord implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				return afix + inputWord;
			}
		}

	    private class PostfixWord implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				return inputWord + afix;
			}
		}
		
		private class PostfixFemininWord implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if (!(inputWord.endsWith(MasryConsts.H) || inputWord.endsWith(MasryConsts.TA_MARBUTA)))
					return "";
				return inputWord.substring(0, inputWord.length() - 1) + afix;
			}
		}
		
		private class HomographEndYa implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if (!(inputWord.endsWith(MasryConsts.YA)))
					return "";
				return inputWord.substring(0, inputWord.length() - 1) + afix;
			}
		}
		
		public VaryWord homographEndYa = new HomographEndYa();
		public VaryWord prefixWord = new PrefixWord();
		public VaryWord postfixWord = new PostfixWord();
		public VaryWord postfixFemininWord = new PostfixFemininWord();
		
		public <E extends Enum<E> > void collect(Map.Entry<String, WordCounterData> entry, String[] afixes, VaryWord vary, EnumSet<E> markers, Class<E> elementType) {
			if (entry.getKey().length() < skipShorterThan)
				return;
        	for (int i = 0; i < afixes.length; i++) {
        		String word2 = vary.varyWord(entry.getKey(), afixes[i]);
        		if (word2 != "") {
        			WordCounterData data2 = wordCount.get(word2);
        			if (data2 != null) {
        				entry.getValue().add(data2);
        				markers.add(elementType.getEnumConstants()[i]);
        				toRemove.add(word2);
        			}
        		}
        	}
		}
	}

	private static final int CONTEXT_LENGTH = 7;
	
	/**
	 * Old style procedural program.
	 * Open the file passed in args. Use a scanner to read it word by word and count them.
	 * @param args
	 * On argument: The file to process.
	 */
	public static void main(String[] args) {
		Path readFile = Paths.get(args[0]);
        Scanner s = null;

        try {
        	// Open the file using the Scanner class, use UTF-8 as charset.
            s = new Scanner(Files.newBufferedReader(readFile, Charset.forName("UTF-8")));
            // Set what delimiters between words look like.
            // Delimiters may start with a closing bracket or a space.
            // After that there may be one or more entities &amp; or &gt; 
            // There are one or more full stops or commas, but only if they are not preceded by a digit.
            // There are one or more dashes, quotation marks, parentheses, slashes, stars, colons, semicolons or ampersands
            // and Arabic varieties of these as well as spaces and left-to-right-markers.
            s.useDelimiter("[) ]?(?:(?:&gt)|(?:&amp)|(?:[,.%](?!\\d))|[-\\u2013|()'\"\\u201c\\u201d#&/*;:\\u060C\\s\\u200F])+");
            Map<String, WordCounterData> wordCount = new LinkedHashMap<String, WordCounterData>(128000);
            
//            while (s.hasNext()) {
//                System.out.println(s.next());
//            }
//            for (int i = 0; i < 150; i++) {
//            	if (!s.hasNext()) break;
//            	s.next();
//            }
            	
            // create a circular buffer that contains a 7 word context of the current word.
            CircularBuffer<String> context = new CircularBuffer<String>(CONTEXT_LENGTH);
            // the current word should be at position position 3
            // TODO: need more for more CONTEXT_LENGTH
            context.add("at");
            context.add("the");
            context.add("beginning");
            for (int i = 0; i < ((CONTEXT_LENGTH + 1) / 2) - 1; i++) {
            	if (!s.hasNext())
            		throw new IllegalArgumentException("Text has to have at least " + ((CONTEXT_LENGTH + 1) / 2) + " words");
            	context.add(s.next());
            }
            
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            System.out.println("<wordlist>");
            System.out.println("<comment>Processing text in " + args[0] + "</comment>");
            // the buffer is prefilled so now process the whole text (or some number of words)
            for (int i = 0; i < 3000000; i++)
            {
            	if (!s.hasNext()) {
            		System.out.println("<comment>Processed " + i + " token.</comment>");
            		break;
            	}
            	context.add(s.next());
            	// The word which shall be counted is in the middle of the context buffer.
            	String word = context.get(((CONTEXT_LENGTH + 1) / 2) - 1);
            	// There shouldn't be any empty strings left as tokens!
            	if (word.equals("")) {
            		System.out.println("<comment>There is a tokenization problem. Check regexp against the following part of the input:");
            		System.out.print("\u0640(");
            		for (String oldWord: context)
            			System.out.print(" " + oldWord);
            		System.out.println(" )\u0640</comment>");
            		continue;
            	}
            	// Primitive types such as Integer are passed by value.
            	// We need a reference so use an array of Integer of length 1.
            	// Try to get the current count of this word.
            	WordCounterData data = wordCount.get(word);
            	// If there this word was already counted increase,
            	if (data != null)
            		data.count++;
            	// else add the word with a count of 1.
            	else 
            		wordCount.put(word, new WordCounterData());
            }
            
             

            CollectRemovals removals = new CollectRemovals(wordCount);
            // Order does matter! TODO: How?
            removals.skipShorterThan = 0;
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_homograph_end_ya, removals.homographEndYa, entry.getValue().homoGraphEndYaFound, MasryConsts.HomoGraphEndYa.class);
            }
            removals.skipShorterThan = 2;
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
               	removals.collect(entry, MasryConsts.masry_feminin_postfixes, removals.postfixFemininWord, entry.getValue().postFemininMakrersFound, MasryConsts.PostFemininMarkers.class);
            }
            removals.skipShorterThan = 3;
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_prefixes_indet, removals.prefixWord, entry.getValue().preNonMarkersFound, MasryConsts.PreNonMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_indet, removals.postfixWord, entry.getValue().postNonMarkersFound, MasryConsts.PostNonMarkers.class);
            	removals.collect(entry, MasryConsts.masry_prefixes_nouns, removals.prefixWord, entry.getValue().preNounMarkersFound, MasryConsts.PreNounMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_nouns, removals.postfixWord, entry.getValue().postNounMarkersFound, MasryConsts.PostNounMarkers.class);
            	removals.collect(entry, MasryConsts.masry_prefixes_verbs, removals.prefixWord, entry.getValue().preVerbMarkersFound, MasryConsts.PreVerbMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_verbs, removals.postfixWord, entry.getValue().postVerbMarkersFound, MasryConsts.PostVerbMarkers.class);
            }
            for (String s1: removals.toRemove)
            	wordCount.remove(s1);
			
            Map<String, WordCounterData> fixUps = new HashMap<>();
            // restore end alif maqsura as default for ambigous words.
            for (Iterator<Entry<String, WordCounterData>> iter = wordCount.entrySet().iterator(); iter.hasNext();) {
            	Map.Entry<String, WordCounterData> entry = iter.next();
            	if (entry.getKey().endsWith(MasryConsts.YA) && !entry.getValue().homoGraphEndYaFound.isEmpty()) {
            		String oldEntry = entry.getKey(); 
            		fixUps.put(oldEntry.substring(0, oldEntry.length() - 1) + MasryConsts.ALIF_MAQSURA, entry.getValue());
            		iter.remove();
            	}
            }
            wordCount.putAll(fixUps);
            
            // The words are counted, but unsorted. Sort them by copying them to a TreeMap with the
            // special sorting algorithm from above in place.
            Map<String, WordCounterData> sortedWordCount = new TreeMap<String, WordCounterData>(new IntArray0ValueComp(wordCount));
            sortedWordCount.putAll(wordCount);
            
            // print the x most frequent words
            int x = 1000000;
            int i = x;

            for (String word: sortedWordCount.keySet()) {
            	WordCounterData data = wordCount.get(word);
            	System.out.print("<word count=\"" + data.count);
            	String atts = "";
            	for (@SuppressWarnings("rawtypes") EnumSet ESet: data.allEnums) {
            		String attsPart = ESet.toString();
            		if (attsPart != "[]")
            			atts = atts + attsPart;
            	}
            	if (atts != "") 
            	   System.out.print("\" att=\"" + atts);
            	System.out.print("\">" + word + "</word>" + lineSeparator);
            	if (--i == 0) {
             		break;
            	}
            }
       		System.out.println("<comment>These are the " + (x - i) + " most frequent words.</comment>");
            System.out.println("</wordlist>");
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (s != null) {
                s.close();
            }
        }

	}

}
