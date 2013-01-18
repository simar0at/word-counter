/*
 * Copyright (c) 2012, Omar Siam. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  I designates this
 * particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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

import org.plyjy.factory.PySystemObjectFactory;

import net.homeunix.siam.stemmer.StemmerI;
import net.homeunix.siam.wordcounter.MasryConsts.IrregularJoin;
import net.homeunix.siam.wordcounter.TokenAndType;
import net.homeunix.siam.wordcounter.Run.CollectRemovals.VaryWord;
import net.homeunix.siam.wordcounter.TokenAndType.TokenType;

/**
 * Hull class
 * 
 * @author Omar Siam
 *
 */
public class Run {
	
	public static final String lineSeparator = System.getProperty("line.separator");
	
	public static int originalWordCount = 0;
	
//	public static PySystemObjectFactory<StemmerI> isriFactory = new PySystemObjectFactory<StemmerI>(
//            StemmerI.class, "nltk", "ISRIStemmer");
	
	
	static final int CONTEXT_LENGTH = MasryConsts.CONTEXT_LENGTH;
	
	public static class CollectRemovals {
		public int skipShorterThan = 3;
		public Map<String, String> toRemove = new HashMap<String, String>();
		private Map<String, WordCounterData> wordCount;
		
		public CollectRemovals(Map<String, WordCounterData> wordCount) {
			this.wordCount = wordCount;
			Map<String, String[]> prefixStopWords = new HashMap<String, String[]>();
			Map<String, String[]> postfixStopWords = new HashMap<String, String[]>();
			for (int i = 0; i < MasryConsts.masry_prefixes_nouns_stopwords.length; i++)
				prefixStopWords.put(MasryConsts.masry_prefixes_nouns[i], MasryConsts.masry_prefixes_nouns_stopwords[i]);
			for (int i = 0; i < MasryConsts.masry_postfixes_nouns_stopwords.length; i++)
				postfixStopWords.put(MasryConsts.masry_postfixes_nouns[i], MasryConsts.masry_postfixes_nouns_stopwords[i]);
			this.prefixWordNoun = new PrefixWord(prefixStopWords);
			this.postfixWordNoun = new PostfixWord(postfixStopWords);
			prefixStopWords = new HashMap<String, String[]>();
			postfixStopWords = new HashMap<String, String[]>();			
			for (int i = 0; i < MasryConsts.masry_prefixes_verbs_stopwords.length; i++)
				prefixStopWords.put(MasryConsts.masry_prefixes_verbs[i], MasryConsts.masry_prefixes_verbs_stopwords[i]);
			for (int i = 0; i < MasryConsts.masry_postfixes_verbs_stopwords.length; i++)
				postfixStopWords.put(MasryConsts.masry_postfixes_verbs[i], MasryConsts.masry_postfixes_verbs_stopwords[i]);
			this.prefixWordVerb = new PrefixWord(prefixStopWords);
			this.postfixWordVerb = new PostfixWord(postfixStopWords);
			prefixStopWords = new HashMap<String, String[]>();
			postfixStopWords = new HashMap<String, String[]>();
			for (int i = 0; i < MasryConsts.masry_prefixes_indet_stopwords.length; i++)
				prefixStopWords.put(MasryConsts.masry_prefixes_indet[i], MasryConsts.masry_prefixes_indet_stopwords[i]);
			for (int i = 0; i < MasryConsts.masry_postfixes_indet_stopwords.length; i++)
				postfixStopWords.put(MasryConsts.masry_postfixes_indet[i], MasryConsts.masry_postfixes_indet_stopwords[i]);
			this.prefixWordIndet = new PrefixWord(prefixStopWords);
			this.postfixWordIndet = new PostfixWord(postfixStopWords);

			this.joinMap = new HashMap<String, String>();
			for (int i = 0; i < MasryConsts.joinThese.length; i++)
				this.joinMap.put(MasryConsts.joinThese[i], MasryConsts.joinWithThese[i]);
		}
		
		public interface VaryWord {
			public String[] getStopWords(String afix);
			public String varyWord(String inputWord, String afix);
		}
		
		public class PrefixWord implements VaryWord {
	    	private Map<String, String[]> stopWords;
	    	
	    	PrefixWord(Map<String, String[]> stopWords) {
	    		this.stopWords = stopWords;
	    	}
	    	public String varyWord(String inputWord, String afix) {
				return afix + inputWord;
			}

			@Override
			public String[] getStopWords(String afix) {
				String[] result = null;
				if (stopWords != null)
					result = stopWords.get(afix);
				if (result != null) Arrays.sort(result);
				return result;
			}
		}

	    public class PostfixWord implements VaryWord {
	    	private Map<String, String[]> stopWords;
	    	
	    	PostfixWord(Map<String, String[]> stopWords) {
	    		this.stopWords = stopWords;
	    	}
	    	
			public String varyWord(String inputWord, String afix) {
				return inputWord.replace(MasryConsts.ALIF_MAQSURA, MasryConsts.YA) + afix;
			}

			@Override
			public String[] getStopWords(String afix) {
				String[] result = null;
				if (stopWords != null)
					result = stopWords.get(afix);
				if (result != null) Arrays.sort(result);
				return result;
			}
		}
		
		

		private class AllographAlif implements VaryWord {
			public String varyWord(String inputWord, String replaceAlifHamza) {
				if (!(replaceAlifHamza == MasryConsts.ALIF_HAMZA || replaceAlifHamza == MasryConsts.ALIF_HAMZA_BELOW) || !(inputWord.contains(MasryConsts.ALIF_HAMZA) || inputWord.contains(MasryConsts.ALIF_HAMZA_BELOW)))
					return "";
				String result = inputWord.replace(replaceAlifHamza, MasryConsts.ALIF);
				if (result.equals(inputWord))
					return "";
				return result;
			}

			@Override
			public String[] getStopWords(String afix) {
				String[] result = new String[] {"إل",
												"أل",
												"أسم", // common error
												"كأن", // ka'anna vs. kana
												"كإن", // ka'inna vs. kana
												"دإ", // typo
				}; 
				Arrays.sort(result);
				return result;
			}
		}
		
		private class AllographEndTatweel implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if ((afix != MasryConsts.TATWEEL))
					return "";
				return inputWord + afix;
			}

			@Override
			public String[] getStopWords(String afix) {
				return null;
			}
		}
		
//		private class AllographAlifHamzaBelow implements VaryWord {
//			public String varyWord(String inputWord, String afix) {
//				if ((afix != MasryConsts.ALIF_HAMZA_BELOW) || !(inputWord.startsWith(MasryConsts.ALIF)))
//					return "";
//				return afix + inputWord.substring(1, inputWord.length());
//			}
//
//			@Override
//			public String[] getStopWords(String afix) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		}

		private class FemininRegularPlurals implements VaryWord {
			public String varyWord(String inputWord, String afix) {
				if (!(inputWord.endsWith(MasryConsts.TA_MARBUTA)))
					return "";
				return inputWord.substring(0, inputWord.length() - 1) + afix;
			}

			@Override
			public String[] getStopWords(String afix) {
				String[] result = new String[] {"دة",
												"وصلة", // wasalaat are links
												};  
				Arrays.sort(result);
				return result;
			}
		}
		
		public VaryWord allographEndTatweel = new AllographEndTatweel();
		public VaryWord allographAlifHamza = new AllographAlif();
		public VaryWord allographAlifHamzaBelow = new AllographAlif();
		public VaryWord femininRegularPlurals = new FemininRegularPlurals();
		public VaryWord prefixWordIndet;
		public VaryWord postfixWordIndet;
		public VaryWord prefixWordNoun;
		public VaryWord postfixWordNoun;
		public VaryWord prefixWordVerb;
		public VaryWord postfixWordVerb;
		
		public Map<String, String> joinMap;
		
		@SuppressWarnings("unchecked")
		public <E extends Enum<E> > void collect(Map.Entry<String, WordCounterData> entry, String[] afixes, VaryWord vary, EnumSet<E> markers, Class<E> elementType) {
			String word = entry.getKey();
			WordCounterData data = entry.getValue();
			int equivalentEnum = -1;
			for (int i = 0; i < data.allEnums.length + 1; i++) {
				equivalentEnum = i;
				if (markers == data.allEnums[i])
					break;
			}
			assert(equivalentEnum < data.allEnums.length);
			if (word.length() < skipShorterThan && !word.equals("ل") && !word.equals("ب"))
				return;
			String[] stopWords = null;
        	for (int i = 0; i < afixes.length; i++) {
        		word = entry.getKey();
    			stopWords = vary.getStopWords(afixes[i]);
        		String word2 = vary.varyWord(word, afixes[i]);
        		if (stopWords != null && Arrays.binarySearch(stopWords, word) > -1)
        			return;
        		if (word2 != "") {
        			WordCounterData data2 = wordCount.get(word2);
        			if (data2 != null) {
        				if (data2.counts[0] == 0)
        					// already taken by some other combination
        					continue;
        				data = entry.getValue(); 
        				if (data.counts[0] == 0) {
        					while (toRemove.containsKey(word))
        						word = toRemove.get(word);
        					data = wordCount.get(word);
        					markers = data.allEnums[equivalentEnum];
        				}
        				data.add(data2);
        				markers.add(elementType.getEnumConstants()[i]);
//        				assert(Run.originalWordCount == WordCounterData.getCount(wordCount));
        				toRemove.put(word2, word);
        			}
        		}
        	}
		}
		
		public <E extends Enum<E> > void collectIrregular(Map.Entry<String, WordCounterData> entry, Map<String, String> join, Class<E> elementType) {
			String word = entry.getKey();
			if (word.length() < skipShorterThan && !word.equals("ل") && !word.equals("ب"))
				return;
			String joinTo = join.get(word);
			if (null != joinTo) {
    			WordCounterData dataJoinTo = wordCount.get(joinTo);
    			if (dataJoinTo != null) {
    				dataJoinTo.add(entry.getValue());
    				dataJoinTo.irregularJoin.add((IrregularJoin) elementType.getEnumConstants()[0]);
    				toRemove.put(word, joinTo);
    	            assert(originalWordCount == WordCounterData.getCount(wordCount));
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
	
	
	/**
	 * Old style procedural program.
	 * Open the file passed in args. Use a scanner to read it word by word and count them.
	 * @param args
	 * On argument: The file to process.
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: " + lineSeparator +
					"  java -jar word-counter.jar <Text-File> <x most frequent token> <max number of samples per token>." + lineSeparator +
					"  The output of this program is in XML format.");
			System.exit(0);
		}
		Path readFile = Paths.get(args[0]);
        ScannerWithDelimiterAccess s = null;
        
        int xMostFrequentToken = Integer.parseInt(args[1]);
        int numberOfSamplesPerToken = Integer.parseInt(args[2]);

        try {
        	// Open the file using the Scanner class, use UTF-8 as charset.
            s = new ScannerWithDelimiterAccess(Files.newBufferedReader(readFile, Charset.forName("UTF-8")));
            // Set what delimiters between words look like.
            // Delimiters may start with a closing bracket or a space.
            // After that there may be one or more entities &amp; or &gt; 
            // There are one or more full stops or commas, but only if they are not preceded by a digit.
            // There are one or more dashes, quotation marks, also arabic ones, parentheses, slashes, stars, colons, semicolons or ampersands
            // and Arabic varieties of these as well as spaces and left-to-right-markers.
            s.useDelimiter(Pattern.compile("[) ]?(?:(?:&lt)|(?:&gt)|(?:&amp)|(?:[,.%](?!\\d))|[-\\u06D4\\u2013\\u2014=|()<>\\u27E8\\u27E9'\\u2018\\u2019\"\\u2039\\u203A\\u201c\\u201d#&/*\\u2022;:?\\u061F!\\u060C\\s\\u200F\\u202E\\u202C\\u200D])+"));
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
            	String token = s.next();
        		if (!MasryConsts.someArabicCharacters.matcher(token).find()) 
        			context.add(new TokenAndType(token, TokenType.UNKNOWN));
        		else 
        			context.add(new TokenAndType(token, TokenType.WORD));
            	cprep++;
            	context.add(new TokenAndType(s.lastDelimiter(), TokenType.DELIMITER));
            	if (cprep++ == ((CONTEXT_LENGTH + 1) / 2) - 1)
            		break;
            }
            
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            System.out.println("<tokenlist xmlns=\"http://www.siam.homeunix.net/tokenlist\">");
            System.out.println("<comment>Processing text in " + args[0] + "</comment>");
            // the buffer is prefilled so now process the whole text (or some number of words plus delimiters)
            int overallTokenCount = 0;
            TokenAndType tt;
            String token;
            // for (int i = 0; i < 6000000; i++)
            for (int i = 0; true; i++)
            {
            	// The word which shall be counted is in the middle of the context buffer.
            	tt = context.get(((CONTEXT_LENGTH + 1) / 2) - 1);
            	if (s.hasNext()) {
            		token = s.next();
            		if (!MasryConsts.someArabicCharacters.matcher(token).find()) 
            			context.add(new TokenAndType(token, TokenType.UNKNOWN));
            		else 
            			context.add(new TokenAndType(token, TokenType.WORD));
            		context.add(new TokenAndType(s.lastDelimiter(), TokenType.DELIMITER));            		
            	}
            	else // process the last tokens
            		context.remove();
                if (tt == null) {
        			System.out.println("<comment>Processed " + i + " token and their delimiters.</comment>");
        			overallTokenCount = i;
            		break;
                }            	
            	// There shouldn't be any empty strings left as tokens!
            	if (tt.token.equals("")) {
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
            	WordCounterData data = wordCount.get(tt.token);
            	// If there this word was already counted increase,
            	if (data != null)
            		data.inc(context);
            	// else add the word with a count of 1.
            	else 
            		wordCount.put(tt.token, new WordCounterData(context, tt.token));
            }
            
//            StemmerI isri = isriFactory.createObject();
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//            	entry.getValue().stems[0] = isri.stem(entry.getKey()).toArray(entry.getValue().stems[0]);
//            }

            CollectRemovals removals = new CollectRemovals(wordCount);
            // Order does matter! TODO: How?
            removals.skipShorterThan = 0;
            originalWordCount = WordCounterData.getCount(wordCount, true);

            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_feminin_plural_postfixes, removals.femininRegularPlurals, entry.getValue().postFemininPluralMakrersFound, MasryConsts.PostFemininPluralMarkers.class);
            }
            
            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));

            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_feminin_postfixes, MasryConsts.postfixFemininWord, entry.getValue().postFemininMakrersFound, MasryConsts.PostFemininMarkers.class);
            }
            
            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));

            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographEndTatweel, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, MasryConsts.allographEndYa, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, MasryConsts.allographEndHa, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            }
            
            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));
            
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographAlifHamza, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            	removals.collect(entry, MasryConsts.masry_allograph, removals.allographAlifHamzaBelow, entry.getValue().alloGraphFound, MasryConsts.AlloGraphEnd.class);
            }
            
            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));

            removals.skipShorterThan = 2;

            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_prefixes_indet, removals.prefixWordIndet, entry.getValue().preNonMarkersFound, MasryConsts.PreNonMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_indet, removals.postfixWordIndet, entry.getValue().postNonMarkersFound, MasryConsts.PostNonMarkers.class);
            }

            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));

            //         // TODO: exchange verb, noun: better?
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_prefixes_verbs, removals.prefixWordVerb, entry.getValue().preVerbMarkersFound, MasryConsts.PreVerbMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_verbs, removals.postfixWordVerb, entry.getValue().postVerbMarkersFound, MasryConsts.PostVerbMarkers.class);
            }

            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));
            
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collect(entry, MasryConsts.masry_prefixes_nouns, removals.prefixWordNoun, entry.getValue().preNounMarkersFound, MasryConsts.PreNounMarkers.class);
            	removals.collect(entry, MasryConsts.masry_postfixes_nouns, removals.postfixWordNoun, entry.getValue().postNounMarkersFound, MasryConsts.PostNounMarkers.class);
            }

            for (String s1: removals.toRemove.keySet())
            	wordCount.remove(s1);
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));
            
            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
            	removals.collectIrregular(entry, removals.joinMap, MasryConsts.IrregularJoin.class);
            }

            for (String s1: removals.toRemove.keySet()) {
                wordCount.remove(s1);
            }
            removals.toRemove.clear();
            assert(originalWordCount == WordCounterData.getCount(wordCount, true));

////
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//            	removals.collect(entry, MasryConsts.masry_prefixes_indet, removals.prefixWordIndet, entry.getValue().preNonMarkersFound, MasryConsts.PreNonMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_prefixes_nouns, removals.prefixWordNoun, entry.getValue().preNounMarkersFound, MasryConsts.PreNounMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_prefixes_verbs, removals.prefixWordVerb, entry.getValue().preVerbMarkersFound, MasryConsts.PreVerbMarkers.class);
//            }
//
//            for (String s1: removals.toRemove)
//            	wordCount.remove(s1);
//            removals.toRemove.clear();
//
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//            	removals.collect(entry, MasryConsts.masry_postfixes_indet, removals.postfixWordIndet, entry.getValue().postNonMarkersFound, MasryConsts.PostNonMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_postfixes_nouns, removals.postfixWordNoun, entry.getValue().postNounMarkersFound, MasryConsts.PostNounMarkers.class);
//            	removals.collect(entry, MasryConsts.masry_postfixes_verbs, removals.postfixWordVerb, entry.getValue().postVerbMarkersFound, MasryConsts.PostVerbMarkers.class);
//            }
//
//            for (String s1: removals.toRemove)
//            	wordCount.remove(s1);
//            removals.toRemove.clear();

//            removals.skipShorterThan = 2;
//            for (Map.Entry<String, WordCounterData> entry: wordCount.entrySet()) {
//               	removals.collect(entry, MasryConsts.masry_feminin_postfixes, removals.postfixFemininWord, entry.getValue().postFemininMakrersFound, MasryConsts.PostFemininMarkers.class);
//            }
            			
            Map<String, WordCounterData> fixUps = new HashMap<String, WordCounterData>();
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
            Map<String, WordCounterData> sortedWordCount = new TreeMap<String, WordCounterData>(new WordCounterData.WordCounterDataValueComp(wordCount));
            sortedWordCount.putAll(wordCount);
            
            // print the x most frequent words
            int i = xMostFrequentToken;
            int lastWordCount = 0;
            int displayedTokenSum = 0;

        	Map<String, WordCounterData> tempMap = new HashMap<String, WordCounterData>();
        	Map<String, WordCounterData> sortedTempMap = new TreeMap<String, WordCounterData>(new WordCounterData.WordCounterDataValueComp(tempMap));
            for (String word: sortedWordCount.keySet()) {
            	WordCounterData data = wordCount.get(word);
            	int count = 0;
            	for (int c: data.counts)
            		count += c;
            	if (i-- <= 0 && count < lastWordCount) {
            		i++;
             		break;
            	}
            	displayedTokenSum += count;
            	System.out.print("<t count=\"" + count);
            	String atts = "";
            	for (@SuppressWarnings("rawtypes") EnumSet ESet: data.allEnums) {
            		String attsPart = ESet.toString();
            		if (attsPart != "[]")
            			atts = atts + attsPart;
            	}
            	if (atts != "") 
            	   System.out.print("\" att=\"" + atts);
            	System.out.print("\">" + word);

            	tempMap.clear();
            	sortedTempMap.clear();
            	for (int j = 0; j < data.words.length; j++)
            		tempMap.put(data.words[j], new WordCounterData(data.counts[j]));
             	sortedTempMap.putAll(tempMap);
             	for (Map.Entry<String, WordCounterData> entry: sortedTempMap.entrySet())
            		System.out.print("<form count=\"" + entry.getValue().counts[0] + "\">" + entry.getKey() + "</form>" + lineSeparator);

//				results next to useless
//            	for (String[] stema: data.stems) 
//            	   for (String stem: stema){
//            		System.out.print("<stem>" + stem + "</stem>" + lineSeparator);
//            	}
            	
            	StringBuilder sb = new StringBuilder();

            	for (WordCounterData.ContextData foundAmidst: WordCounterData.randomSample(data, numberOfSamplesPerToken)) {
            		sb.setLength(0);
            		String s2 = "";
            		for (int j = 0; j < foundAmidst.context.length; j++) {
            			s2 = foundAmidst.context[j];
            			if (s2 == null) break;
            			switch (foundAmidst.types[j]) {
						case WORD:
	            			sb.append("<w>");
	            			sb.append(s2.replaceAll("&", "&amp;").replaceAll("<","&lt;"));
	            			sb.append("</w>");															
							break;
						case DELIMITER:
	            			sb.append("<s>");
	            			sb.append(s2.replaceAll("&", "&amp;").replaceAll("<","&lt;"));
	            			sb.append("</s>");								
							break;

						case UNKNOWN:
	            			sb.append("<u>");
	            			sb.append(s2.replaceAll("&", "&amp;").replaceAll("<","&lt;"));
	            			sb.append("</u>");	
							break;
						}
            		}            		
            		System.out.println("<tic>" + sb.toString() + "</tic>");
            	}
            	System.out.print("</t>" + lineSeparator);
            	lastWordCount = count;
            }
            Thread.sleep(100);
       		System.err.println("<comment>These are the " + (xMostFrequentToken - i) + " most frequent token with possble variants.");
       		System.err.println("There were " + (overallTokenCount - displayedTokenSum) + " more token " +
       				"(including those declared unknown by regexp) found in the input text</comment>");
       		Thread.sleep(100);
            System.out.println("</tokenlist>");
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            if (s != null) {
                s.close();
            }
        }

	}

}
