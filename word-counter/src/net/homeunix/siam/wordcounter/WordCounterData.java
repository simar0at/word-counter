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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import net.homeunix.siam.wordcounter.MasryConsts.AlloGraphEnd;
import net.homeunix.siam.wordcounter.MasryConsts.IrregularJoin;
import net.homeunix.siam.wordcounter.MasryConsts.PostFemininMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PostFemininPluralMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PostNonMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PostNounMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PostVerbMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PreNonMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PreNounMarkers;
import net.homeunix.siam.wordcounter.MasryConsts.PreVerbMarkers;
import net.homeunix.siam.wordcounter.TokenAndType.TokenType;

public class WordCounterData {

	/**
	 * To get a word/frequency map sorted by the frequency of a word this seems to be the most simple solution.
	 * Obviously needs an unsorted key/value map as an input. Sorts by frequency and then alphabetically.
	 * 
	 * @author Omar Siam
	 *
	 */
	public static class WordCounterDataValueComp implements Comparator<String>
	{

		Map<String, WordCounterData> base;

		public WordCounterDataValueComp(Map<String, WordCounterData> baseMap) {
			base = baseMap;
		}

		@Override
		public int compare(String o1, String o2) {
			// Sort the most frequently used first.
			int countO1 = 0;
			int countO2 = 0;
			for (int c: base.get(o1).counts)
				countO1 += c;
			for (int c: base.get(o2).counts)
				countO2 += c;
			int ret = -Integer.compare(countO1, countO2);
			// If frequency is the same sort alphabetically ascending
			if (0 == ret) {
				ret = o1.compareTo(o2);
			}
			return ret;
		}

	}
	public class ContextData {
		public TokenType[] types;
		public String[] context;
		public String word;
		ContextData (String[] context, TokenType[] types, String word) {
			this.types = types;
			this.context = context;
			this.word = word;
		}
	}
	private int countCache = -1;
	// If there is no word, there is no such object just null.
	public int[] counts = {1};
	public String[] words = new String[] {""};
	public String[][] stems = new String[][] {{""}};
	public List<ContextData> contexts = new ArrayList<ContextData>();
	public EnumSet<AlloGraphEnd> alloGraphFound = EnumSet.noneOf(AlloGraphEnd.class);
	public EnumSet<PostFemininMarkers> postFemininMakrersFound = EnumSet.noneOf(PostFemininMarkers.class);
	public EnumSet<PostFemininPluralMarkers> postFemininPluralMakrersFound = EnumSet.noneOf(PostFemininPluralMarkers.class);
	public EnumSet<PreNounMarkers> preNounMarkersFound = EnumSet.noneOf(PreNounMarkers.class);
	public EnumSet<PostNounMarkers> postNounMarkersFound = EnumSet.noneOf(PostNounMarkers.class);
	public EnumSet<PreVerbMarkers> preVerbMarkersFound = EnumSet.noneOf(PreVerbMarkers.class);
	public EnumSet<PostVerbMarkers> postVerbMarkersFound = EnumSet.noneOf(PostVerbMarkers.class);
	public EnumSet<PreNonMarkers> preNonMarkersFound = EnumSet.noneOf(PreNonMarkers.class);
	public EnumSet<PostNonMarkers> postNonMarkersFound = EnumSet.noneOf(PostNonMarkers.class);

	public EnumSet<IrregularJoin> irregularJoin = EnumSet.noneOf(IrregularJoin.class);

	@SuppressWarnings("rawtypes")
	public EnumSet allEnums[] = new EnumSet[] {alloGraphFound, postFemininMakrersFound, postFemininPluralMakrersFound, preNounMarkersFound,
		postNounMarkersFound, preVerbMarkersFound, postVerbMarkersFound, preNonMarkersFound, postNonMarkersFound,
		irregularJoin};

	private void addTokenAndTypeBuffer(CircularBuffer<TokenAndType> context, String word) {
		int length = context.size();
		int i = 0;
		String[] contextStrings = new String[length];
		TokenType[] contextTypes = new TokenType[length];
		for (TokenAndType tt: context) {
			contextStrings[i] = tt.token;
			contextTypes[i++] = tt.type;
		}
		contexts.add(new ContextData(contextStrings, contextTypes, word));
		if (words[0].equals(""))
			words[0] = word;
	}

	public WordCounterData(CircularBuffer<TokenAndType> context, String word) {
		addTokenAndTypeBuffer(context, word);
	}

	public WordCounterData(int count) {this.counts[0] = count;}

	public void add(WordCounterData data) {
		assert(this.counts[0] != 0);
		this.alloGraphFound.addAll(data.alloGraphFound);
		this.postFemininMakrersFound.addAll(data.postFemininMakrersFound);
		this.preNounMarkersFound.addAll(data.preNounMarkersFound);
		this.postNounMarkersFound.addAll(data.postNounMarkersFound);
		this.preVerbMarkersFound.addAll(data.preVerbMarkersFound);
		this.postVerbMarkersFound.addAll(data.postVerbMarkersFound);
		this.preNonMarkersFound.addAll(data.preNonMarkersFound);
		this.postNonMarkersFound.addAll(data.postNonMarkersFound);
		contexts.addAll(data.contexts);
		int oldLength = words.length;
		this.words = Arrays.copyOf(words, this.words.length + data.words.length);
		this.counts = Arrays.copyOf(counts, this.counts.length + data.counts.length); 
		this.stems = Arrays.copyOf(stems, this.stems.length + data.stems.length);
		for (int i = 0; i < data.words.length; i++) {			
			this.words[oldLength + i] = data.words[i];
			this.stems[oldLength + i] = data.stems[i];
			this.counts[oldLength + i] = data.counts[i];
			data.counts[i] = 0;
		}
		data.countCache = 0;
		countCache = -1;
		getCount(true);
	}

	public void inc(CircularBuffer<TokenAndType> context) {
		counts[0]++;
		countCache = -1;
		addTokenAndTypeBuffer(context, words[0]);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String wordsList = "";
		String stemsList = "";

		for (String s: words) {
			sb.append(s);
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		wordsList = sb.toString();
		sb.setLength(0);
		for (String[] sa: stems) 
			for (String s: sa){
				sb.append(s);
				sb.append(", ");
			}
		sb.setLength(sb.length() - 2);
		wordsList = sb.toString();
		sb.setLength(0);
		for (ContextData context: contexts)
			sb.append(Arrays.deepToString(context.context));
		return "Words: " + wordsList + " stems: " + stemsList + " count: " + countCache + " contexts: " + sb.toString() + " flags: unimplemented"; 
	}

	public int getCount() {return getCount(false);}

	public int getCount(boolean checkNotZero) {
		if (countCache == -1) {
			int sum = 0;
			for (int c: counts) {
				if (checkNotZero)
					assert(c != 0);
				sum += c;
			}
			countCache = sum;
			return sum;
		}
		return countCache;
	}

	public static int getCount(Map<String, WordCounterData> map) {return getCount(map, false);}

	public static int getCount(Map<String, WordCounterData> map, boolean checkNotZero) {
		int sum = 0;
		for (Map.Entry<String, WordCounterData> entry: map.entrySet())
			sum += entry.getValue().getCount(checkNotZero);
		return sum;
	}
	
	public static Random rnd = new Random(22);
	
	public static List<WordCounterData.ContextData> randomSample(WordCounterData counterData, int m) {
		rnd = new Random(22);
		if (counterData.counts.length == 1)
			return randomSample(counterData.contexts, m);
		else {
			long[] ratios = new long[counterData.counts.length];
			List<WordCounterData.ContextData> result = new ArrayList<WordCounterData.ContextData>();
			int sum = counterData.getCount();
			double ratio = 1.0;
			if (sum > m)
				ratio = (double)m / (double)sum;
			for (int i = 0; i < ratios.length; i++)
				ratios[i] = Math.round((double)counterData.counts[i] * ratio);
			long ratioSum = 0;
			for (long r: ratios)
				ratioSum += r;
			long biggestR = 0, smallestR = 1;
			for (int i = 0; i < ratios.length; i++)
			{
				biggestR =  ratios[i] > biggestR ? ratios[i]: biggestR;
				smallestR = ratios[i] > 0 && ratios[i] < smallestR ? ratios[i]: smallestR;
			}
			for (int i = 0; i < ratios.length; i++)
			   if (ratios[i] == biggestR) {
				   biggestR = i;
				   break;
			   }
			for (int i = 0; i < ratios.length; i++)
				   if (ratios[i] == smallestR) {
					   smallestR = i;
					   break;
				   }
			if (ratioSum > m)
				ratios[(int)biggestR] -= 1;
			else if (ratioSum < m)
				ratios[(int)smallestR] += 1;
			int lastStart = 0;
			for (int i = 0; i < counterData.counts.length; i++) {
				int contextLength = counterData.counts[i];
				result.addAll(randomSample(counterData.contexts.subList(lastStart, lastStart + contextLength), (int)ratios[i]));
				lastStart += contextLength;
			}
			return result;
		}
	}
	
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
}