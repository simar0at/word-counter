package net.homeunix.siam.wordcounter;
/**
 * Count words of arz (egyptian arabic) texts like the preprocessed wikipedia, sort the result by frequency of that word.
 * This program will try to consider the similarity of arabic words as best as possible.
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Pattern;

import net.homeunix.siam.wordcounter.TokenAndType;
import net.homeunix.siam.wordcounter.MasryConsts.WordCounterData;
import net.homeunix.siam.wordcounter.TokenAndType.TokenType;

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
	
	private static final int CONTEXT_LENGTH = MasryConsts.CONTEXT_LENGTH;
	
	public static class TokenWithContext {
		public String word;
		public String[] context;
		
		TokenWithContext(String word, CircularBuffer<TokenAndType> context) {
			this.word = word;
			this.context = new String[CONTEXT_LENGTH];
			for (int i = 0; i < CONTEXT_LENGTH; i++)
				this.context[i] = context.get(i).token;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String s: context)
				sb.append(s);
			return sb.toString();
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
		
		private class AllographEndYa implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if ((afix != MasryConsts.ALIF_MAQSURA) || !(inputWord.endsWith(MasryConsts.YA)))
					return "";
				return inputWord.substring(0, inputWord.length() - 1) + afix;
			}
		}

		private class AllographEndHa implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if ((afix != MasryConsts.TA_MARBUTA) || !(inputWord.endsWith(MasryConsts.H)))
					return "";
				return inputWord.substring(0, inputWord.length() - 1) + afix;
			}
		}

		private class AllographAlifHamza implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if ((afix != MasryConsts.ALIF_HAMZA) || !(inputWord.startsWith(MasryConsts.ALIF)))
					return "";
				return afix + inputWord.substring(1, inputWord.length());
			}
		}
		
		private class AllographAlifHamzaBelow implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if ((afix != MasryConsts.ALIF_HAMZA_BELOW) || !(inputWord.startsWith(MasryConsts.ALIF)))
					return "";
				return afix + inputWord.substring(1, inputWord.length());
			}
		}
		
		public VaryWord allographEndYa = new AllographEndYa();
		public VaryWord allographEndHa = new AllographEndHa();
		public VaryWord allographAlifHamza = new AllographAlifHamza();
		public VaryWord allographAlifHamzaBelow = new AllographAlifHamzaBelow();
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
	
//	private static String[] getContext(CircularBuffer<TokenAndType> context) {
//		String[] result = new String[CONTEXT_LENGTH];
//		int i = 0;
//		for (TokenAndType tt: context) {
//			result[i++] = tt.token;
//		}
//		return result;
//	}
	
	private static Random rnd = new Random(22); 
	
	public static <T> List<T> randomSample(List<T> items, int m){
	    ArrayList<T> res = new ArrayList<T>(m);
	    int visited = 0;
	    Iterator<T> it = items.iterator();
	    T item = null;
	    while (m > 0){
	    	try {
	        	item = it.next(); }
	    	catch (NoSuchElementException nse) {
	    		break; }
	        if (rnd.nextDouble() < ((double)m)/(items.size() - visited)){
	            res.add(item);
	            m--;
	        }
	        visited++;
	    }
	    return res;
	}
	
	/**
	 * Old style procedural program.
	 * Open the file passed in args. Use a scanner to read it word by word and count them.
	 * @param args
	 * On argument: The file to process.
	 */
	public static void main(String[] args) {
		Path readFile = Paths.get(args[0]);
        ScannerWithDelimiterAccess s = null;

        try {
        	// Open the file using the Scanner class, use UTF-8 as charset.
            s = new ScannerWithDelimiterAccess(Files.newBufferedReader(readFile, Charset.forName("UTF-8")));
            // Set what delimiters between words look like.
            // Delimiters may start with a closing bracket or a space.
            // After that there may be one or more entities &amp; or &gt; 
            // There are one or more full stops or commas, but only if they are not preceded by a digit.
            // There are one or more dashes, quotation marks, also arabic ones, parentheses, slashes, stars, colons, semicolons or ampersands
            // and Arabic varieties of these as well as spaces and left-to-right-markers.
            s.useDelimiter(Pattern.compile("[) ]?(?:(?:&gt)|(?:&amp)|(?:[,.%](?!\\d))|[-\\u2013|()'\"\\u201c\\u201d#&/*;:?\\u061F!\\u060C\\s\\u200F])+"));
            Map<String, WordCounterData> wordCount = new LinkedHashMap<String, WordCounterData>(128000);
            
//            while (s.hasNext()) {
//                System.out.println(s.next());
//            }
//            for (int i = 0; i < 150; i++) {
//            	if (!s.hasNext()) break;
//            	s.next();
//            }
            	
            // create a circular buffer that contains a 7 word context of the current word.
            CircularBuffer<TokenAndType> context = new CircularBuffer<TokenAndType>(CONTEXT_LENGTH);
            // the current word should be in the middle of the buffer (at position position 3 for CONTEXT_LENGTH 7)
            context.add(new TokenAndType("at the beginning", TokenType.UNKNOWN));
            for (int i = 0; i < ((CONTEXT_LENGTH + 1) / 2) - 2; i++)
            	context.add(new TokenAndType(">", TokenAndType.TokenType.UNKNOWN));
            int cprep = 0;
            while(true) {
            	if (!s.hasNext())
            		throw new IllegalArgumentException("Text has to have at least " + ((CONTEXT_LENGTH + 1) / 2) + " words");
            	context.add(new TokenAndType(s.next(), TokenType.WORD));
            	cprep++;
            	context.add(new TokenAndType(s.lastDelimiter(), TokenType.DELIMITER));
            	if (cprep++ == ((CONTEXT_LENGTH + 1) / 2) - 3)
            		break;
            }
            
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            System.out.println("<tokenlist>");
            System.out.println("<comment>Processing text in " + args[0] + "</comment>");
            // the buffer is prefilled so now process the whole text (or some number of words plus delimiters)
            for (int i = 0; i < 6000000; i++)
            {
            	if (s.hasNext()) {
            		String token = s.next();
            		if (!MasryConsts.someArabicCharacters.matcher(token).find()) {
            			context.add(new TokenAndType(token, TokenType.UNKNOWN));
            			context.add(new TokenAndType(s.lastDelimiter(), TokenType.DELIMITER));
            			continue;
            		}
            		context.add(new TokenAndType(token, TokenType.WORD));
            		context.add(new TokenAndType(s.lastDelimiter(), TokenType.DELIMITER));
            	}
            	else // process the last tokens
            		context.remove();
            	// The word which shall be counted is in the middle of the context buffer.
            	TokenAndType tt = context.get(((CONTEXT_LENGTH + 1) / 2) - 1);;
                if (tt == null) {
        			System.out.println("<comment>Processed " + i + " token and their delimiters.</comment>");
            		break;
                }            	
                String word = tt.token;
            	// There shouldn't be any empty strings left as tokens!
            	if (word.equals("")) {
            		System.out.println("<comment>There is a tokenization problem. Check regexp against the following part of the input:");
            		System.out.print("\u0640(");
            		for (TokenAndType oldWord: context)
            			System.out.print(oldWord.token);
            		System.out.println(" )\u0640</comment>");
            		continue;
            	}
            	// below here we are concerned with real words only
            	if (tt.type != TokenType.WORD)
            		continue;
            	// Primitive types such as Integer are passed by value.
            	// We need a reference so use an array of Integer of length 1.
            	// Try to get the current count of this word.
            	WordCounterData data = wordCount.get(word);
            	// If there this word was already counted increase,
            	if (data != null)
            		data.inc(context);
            	// else add the word with a count of 1.
            	else 
            		wordCount.put(word, new WordCounterData(context));
            }
            
             

            CollectRemovals removals = new CollectRemovals(wordCount);
            // Order does matter! TODO: How?
            removals.skipShorterThan = 0;
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographEndYa, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographEndHa, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographAlifHamza, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographAlifHamzaBelow, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            }
//            removals.skipShorterThan = 2;
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//               	removals.collect(entry, MasryConsts.masry_feminin_postfixes, removals.postfixFemininWord, entry.getValue().postFemininMakrersFound, MasryConsts.PostFemininMarkers.class);
//            }
//            removals.skipShorterThan = 3;
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//            	removals.collect(entry, MasryConsts.masry_prefixes_indet, removals.prefixWord, entry.getValue().preNonMarkersFound, MasryConsts.PreNonMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_postfixes_indet, removals.postfixWord, entry.getValue().postNonMarkersFound, MasryConsts.PostNonMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_prefixes_nouns, removals.prefixWord, entry.getValue().preNounMarkersFound, MasryConsts.PreNounMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_postfixes_nouns, removals.postfixWord, entry.getValue().postNounMarkersFound, MasryConsts.PostNounMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_prefixes_verbs, removals.prefixWord, entry.getValue().preVerbMarkersFound, MasryConsts.PreVerbMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_postfixes_verbs, removals.postfixWord, entry.getValue().postVerbMarkersFound, MasryConsts.PostVerbMarkers.class);
//            }
            for (String s1: removals.toRemove)
            	wordCount.remove(s1);
			
            Map<String, WordCounterData> fixUps = new HashMap<>();
            // restore end alif maqsura as default for ambigous words.
            for (Iterator<Entry<String, WordCounterData>> iter = wordCount.entrySet().iterator(); iter.hasNext();) {
            	Map.Entry<String, WordCounterData> entry = iter.next();
            	if (entry.getKey().endsWith(MasryConsts.YA) && !entry.getValue().alloGraphFound.isEmpty()) {
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
            int x = 200;
            int i = x;
            int lastWordCount = 0;

            for (String word: sortedWordCount.keySet()) {
            	WordCounterData data = wordCount.get(word);
            	if (--i < 1 && data.count < lastWordCount) {
             		break;
            	}
            	System.out.print("<t count=\"" + data.count);
            	String atts = "";
            	for (@SuppressWarnings("rawtypes") EnumSet ESet: data.allEnums) {
            		String attsPart = ESet.toString();
            		if (attsPart != "[]")
            			atts = atts + attsPart;
            	}
            	if (atts != "") 
            	   System.out.print("\" att=\"" + atts);
            	System.out.println("\">" + word);
            	StringBuilder sb = new StringBuilder();
            	for (String[] foundAmidst: randomSample(data.contexts, 100)) {
            		sb.setLength(0);
            		for (String s2: foundAmidst) {
            			if (s2 == null) break;
            			sb.append("<t>");
            			sb.append(s2.replaceAll("&", "&amp;").replaceAll("<","&lt;"));
            			sb.append("</t>");
            		}            		
            		System.out.println("<tic>" + sb.toString() + "</tic>");
            	}
            	System.out.print("</t>" + lineSeparator);
            	lastWordCount = data.count;
            }
       		System.out.println("<comment>These are the " + (x - i) + " most frequent words.</comment>");
            System.out.println("</tokenlist>");
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
