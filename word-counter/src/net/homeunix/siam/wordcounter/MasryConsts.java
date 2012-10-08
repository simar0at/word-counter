package net.homeunix.siam.wordcounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

public class MasryConsts {
	public static final String TA_MARBUTA = "ة";
	public static final String H = "ه";
	public static final String ALIF_MAQSURA = "ى";
	public static final String YA = "ي";
	
	// when iterated over them, those affixes that occur together with the other ones should be after them
	public static final String[] masry_allograph_end = {"ى", "ة"};
	public static final String[] masry_feminin_postfixes = {"تي", "تك", "ته", "تها", "تنا", "تكم", "تهم"};
	public static final String[] masry_prefixes_nouns = {"ل", "ال"};
	public static final String[] masry_postfixes_nouns = {"ين"};
	public static final String[] masry_prefixes_verbs = {"ما", "ح", "ب", "ا", "ت", "ي", "ن"};
	public static final String[] masry_postfixes_verbs = {"ش", "ني", "وا", "و", "ي"};
	public static final String[] masry_prefixes_indet = {"و", "ب"};
	public static final String[] masry_postfixes_indet = {"ي", "ك", "كي", "ه", "ها", "نا", "كم", "هم"};
	
	public static final Pattern someArabicCharacters = Pattern.compile("[\\u0600-\\u06FF]+");
	
	public enum AlloGraphEnd {HAS_ALLOGRAPH_YA_ALIF_MAQSURA, HAS_ALLOGRAPH_H_TA_MARBUTA};
	public enum PostFemininMarkers {POST_F_TI, POST_F_TAIK, POST_F_TU, POST_F_ITHA, POST_F_ITNA, POST_F_ITKUM, POST_F_ITHUM};
	public enum PreNounMarkers {PREF_N_LI, PREF_N_AL};
	public enum PostNounMarkers {POST_N_IN};
	public enum PreVerbMarkers {PREF_V_MA, PREF_V_HA, PREF_V_BI, PREF_V_A, PREF_V_TI, PREF_V_YI, PREF_V_NI};
	public enum PostVerbMarkers {POST_V_SH, POST_V_NI, POST_V_UU, POST_V_U, POST_V_I};
	public enum PreNonMarkers {PREF_I_W, PREF_I_BI};
	public enum PostNonMarkers {POST_I_I, POST_I_AK_IK, POST_I_KI, POST_I_H, POST_I_HA, POST_I_NA, POST_I_KUM, POST_I_HUM};
	
	public static final int CONTEXT_LENGTH = 15;	
	
	public static class WordCounterData {
		// If there is no word, there is no such object just null.
		public int count = 1;
		public List<String[]> contexts = new ArrayList<String[]>();
		public EnumSet<AlloGraphEnd> alloGraphEndFound = EnumSet.noneOf(AlloGraphEnd.class);
		public EnumSet<PostFemininMarkers> postFemininMakrersFound = EnumSet.noneOf(PostFemininMarkers.class);
		public EnumSet<PreNounMarkers> preNounMarkersFound = EnumSet.noneOf(PreNounMarkers.class);
		public EnumSet<PostNounMarkers> postNounMarkersFound = EnumSet.noneOf(PostNounMarkers.class);
		public EnumSet<PreVerbMarkers> preVerbMarkersFound = EnumSet.noneOf(PreVerbMarkers.class);
		public EnumSet<PostVerbMarkers> postVerbMarkersFound = EnumSet.noneOf(PostVerbMarkers.class);
		public EnumSet<PreNonMarkers> preNonMarkersFound = EnumSet.noneOf(PreNonMarkers.class);
		public EnumSet<PostNonMarkers> postNonMarkersFound = EnumSet.noneOf(PostNonMarkers.class);
		
		@SuppressWarnings("rawtypes")
		public EnumSet allEnums[] = new EnumSet[] {alloGraphEndFound, postFemininMakrersFound, preNounMarkersFound, postNounMarkersFound,
												   preVerbMarkersFound, postVerbMarkersFound, preNonMarkersFound, postNonMarkersFound};
		
		private void addTokenAndTypeBuffer(CircularBuffer<TokenAndType> context) {
			int length = context.size();
			int i = 0;
			String[] contextStrings = new String[length];
			for (TokenAndType tt: context) {
				contextStrings[i++] = tt.token;
			}
			contexts.add(contextStrings);
		}
		
		public WordCounterData(CircularBuffer<TokenAndType> context) {
			addTokenAndTypeBuffer(context);
		}
		
//		public static <T> T[] concat(T[] first, T[] second) {
//			  T[] result = Arrays.copyOf(first, first.length + second.length);
//			  System.arraycopy(second, 0, result, first.length, second.length);
//			  return result;
//			}

		public void add(WordCounterData data) {
			this.count += data.count;
			this.alloGraphEndFound.addAll(data.alloGraphEndFound);
			this.postFemininMakrersFound.addAll(data.postFemininMakrersFound);
			this.preNounMarkersFound.addAll(data.preNounMarkersFound);
			this.postNounMarkersFound.addAll(data.postNounMarkersFound);
			this.preVerbMarkersFound.addAll(data.preVerbMarkersFound);
			this.postVerbMarkersFound.addAll(data.postVerbMarkersFound);
			this.preNonMarkersFound.addAll(data.preNonMarkersFound);
			this.postNonMarkersFound.addAll(data.postNonMarkersFound);
			contexts.addAll(data.contexts);
			
		}
		
		public void inc(CircularBuffer<TokenAndType> context) {
			count++;
			addTokenAndTypeBuffer(context);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String[] context: contexts)
				sb.append(Arrays.deepToString(context));
			return "Count: " + count + " contexts: " + sb.toString() + " flags: unimplemented"; 
		}
	}
}
