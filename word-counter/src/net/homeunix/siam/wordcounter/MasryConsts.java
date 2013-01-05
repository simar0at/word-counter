package net.homeunix.siam.wordcounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

import net.homeunix.siam.wordcounter.TokenAndType.TokenType;

public class MasryConsts {
	public static final String TA_MARBUTA = "ة";
	public static final String H = "ه";
	public static final String ALIF_MAQSURA = "ى";
	public static final String YA = "ي";
	public static final String ALIF = "ا";
	public static final String ALIF_HAMZA = "أ";
	public static final String ALIF_HAMZA_BELOW = "إ";
	public static final String TATWEEL = "ـ";
	// when iterated over them, those affixes that occur together with the other ones should be after them
	public static final String[] masry_allograph = {"ى", "ة", "أ", "إ", "ـ"};
	public static final String[] masry_feminin_postfixes = {"تي", "تك", "ته", "تها", "تنا", "تكم", "تهم"};
	public static final String[] masry_prefixes_nouns = {"ل", "ال"};
	public static final String[] masry_postfixes_nouns = {"ين"};
	public static final String[] masry_prefixes_verbs = {"ما", "ح", "ب", "ا", "ت", "ي", "ن"};
	public static final String[] masry_postfixes_verbs = {"ش", "ني", "وا", "و", "ي"};
	public static final String[] masry_prefixes_indet = {"و", "ب"};
	public static final String[] masry_postfixes_indet = {"ي", "ك", "كي", "ه", "ها", "نا", "كم", "هم"};
	
	public static final Pattern someArabicCharacters = Pattern.compile("[\\u0600-\\u06FF]+");
	
	public enum AlloGraphEnd {HAS_ALLOGRAPH_END_YA_ALIF_MAQSURA, HAS_ALLOGRAPH_END_H_TA_MARBUTA, HAS_ALLOGRAPH_ALIF_HAMZA, HAS_ALLOGRAPH_ALIF_HAMZA_BELOW, HAS_ALLOGRAPH_TATWEEL};
	public enum PostFemininMarkers {POST_F_TI, POST_F_TAIK, POST_F_TU, POST_F_ITHA, POST_F_ITNA, POST_F_ITKUM, POST_F_ITHUM};
	public enum PreNounMarkers {PREF_N_LI, PREF_N_AL};
	public enum PostNounMarkers {POST_N_IN};
	public enum PreVerbMarkers {PREF_V_MA, PREF_V_HA, PREF_V_BI, PREF_V_A, PREF_V_TI, PREF_V_YI, PREF_V_NI};
	public enum PostVerbMarkers {POST_V_SH, POST_V_NI, POST_V_UU, POST_V_U, POST_V_I};
	public enum PreNonMarkers {PREF_I_W, PREF_I_BI};
	public enum PostNonMarkers {POST_I_I, POST_I_AK_IK, POST_I_KI, POST_I_H, POST_I_HA, POST_I_NA, POST_I_KUM, POST_I_HUM};
	
	public static final int CONTEXT_LENGTH = 15;	
	
	public static class WordCounterData {
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
		// If there is no word, there is no such object just null.
		public int[] counts = {1};
		public String[] words = new String[] {""};
		public String[][] stems = new String[][] {{""}};
		public List<ContextData> contexts = new ArrayList<ContextData>();
		public EnumSet<AlloGraphEnd> alloGraphFound = EnumSet.noneOf(AlloGraphEnd.class);
		public EnumSet<PostFemininMarkers> postFemininMakrersFound = EnumSet.noneOf(PostFemininMarkers.class);
		public EnumSet<PreNounMarkers> preNounMarkersFound = EnumSet.noneOf(PreNounMarkers.class);
		public EnumSet<PostNounMarkers> postNounMarkersFound = EnumSet.noneOf(PostNounMarkers.class);
		public EnumSet<PreVerbMarkers> preVerbMarkersFound = EnumSet.noneOf(PreVerbMarkers.class);
		public EnumSet<PostVerbMarkers> postVerbMarkersFound = EnumSet.noneOf(PostVerbMarkers.class);
		public EnumSet<PreNonMarkers> preNonMarkersFound = EnumSet.noneOf(PreNonMarkers.class);
		public EnumSet<PostNonMarkers> postNonMarkersFound = EnumSet.noneOf(PostNonMarkers.class);
		
		@SuppressWarnings("rawtypes")
		public EnumSet allEnums[] = new EnumSet[] {alloGraphFound, postFemininMakrersFound, preNounMarkersFound, postNounMarkersFound,
												   preVerbMarkersFound, postVerbMarkersFound, preNonMarkersFound, postNonMarkersFound};
		
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
		
//		public static <T> T[] concat(T[] first, T[] second) {
//			  T[] result = Arrays.copyOf(first, first.length + second.length);
//			  System.arraycopy(second, 0, result, first.length, second.length);
//			  return result;
//			}

		public void add(WordCounterData data) {
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
			this.words = Arrays.copyOf(words, words.length + data.words.length);
			this.stems = Arrays.copyOf(stems, stems.length + data.stems.length);
			this.counts = Arrays.copyOf(counts, counts.length + data.counts.length); 
			for (int i = 0; i < data.words.length; i++) {
			  this.words[oldLength + i] = data.words[i];
			  this.stems[oldLength + i] = data.stems[i];
			  this.counts[oldLength + i] = data.counts[i];
			}
		}
		
		public void inc(CircularBuffer<TokenAndType> context) {
			counts[0]++;
			addTokenAndTypeBuffer(context, words[0]);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			String wordsList = "";
			String stemsList = "";
			int count = 0;
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
			for (int c: counts)
				count += c;
			return "Words: " + wordsList + " stems: " + stemsList + " count: " + count + " contexts: " + sb.toString() + " flags: unimplemented"; 
		}
	}
}
