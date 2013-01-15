package net.homeunix.siam.wordcounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.homeunix.siam.wordcounter.Run.CollectRemovals.VaryWord;
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
	public static final String[] joinThese = new String[] {"كن", "قل", "شف", "شوف", "كون", "قول", "جيت", "جم", "جت", "يجي", "تجي", "جيب", "بقت", "بقو", "بقوا", "مصريين", "مواليد", "احداث", "بلاد", "السياس", "سياسى", "مدن", "كتب", "مصادر"};
	public static final String[] joinWithThese = new String[] {"كان", "قال", "شاف", "شاف", "كان", "قال", "جه", "جه", "جه", "جه", "جه", "جاب", "بقي", "بقي", "بقي", "بقي", "مصر", "مولود", "حدث", "بلد", "سياسه", "سياسه", "مدينه", "كتاب", "مصدر"};
	// when iterated over them, those affixes that occur together with the other ones should be after them
	public static final String[] masry_allograph = {"ى", "ة", "أ", "إ", "ـ"};
	
	public static final String[] masry_feminin_postfixes = {"تي", "تك", "ته", "تها", "تنا", "تكم", "تهم"};
	public static final String[] masry_feminin_plural_postfixes = {"ات", "اتي", "اتك", "اته", "اتها", "اتنا", "اتكم", "اتهم"};
	public static final String[] masry_prefixes_nouns = {"ل", "ال"};
	public static final String[][] masry_prefixes_nouns_stopwords = new String[][]{
		new String[] {"ب", "في", "عن", "علي", "دي", "ده", "هو", "هي", "ان", "كان", "او", "انو", "ما", "بن", "ال", "مع", "من", "بن", "ين", "كل", "كن", "يل", "اد", "قى", "بس", "غايه", "لو"},
		new String[] {"لي", "عن", "علي", "دي", "ده", "هو", "هي", "كان", "او", "عنو", "مع", "ل", "ب", "مش", "ام", "لما", "لا", "مان"}
	};
	public static final String[] masry_postfixes_nouns = {"ين"};
	public static final String[][] masry_postfixes_nouns_stopwords = new String[][]{
		new String[] {"ب", "من", "في", "عن", "علي", "الى", "او", "ال", "مع", "ما", "بعد", "مد", "يم", "ام", "حس", "لا"}
	};
	public static final String[] masry_prefixes_verbs = {"ما", "ح", "ا", "ت", "ي", "ن"};
	public static final String[][] masry_prefixes_verbs_stopwords = new String[][]{
		new String[] {"من", "عن", "علي", "الى", "ان", "ما", "ال", "ل", "ب", "ده", "لا", "لو", "يو"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "سن", "عد", "ما", "ال", "اسم", "مل", "ل", "ب", "ين", "صل", "اول", "يا", "زب", "كم", "رب", "سب", "لم", "كتر", "كبر", "لو", "مد", "سين", "بس", "حسن", "ير"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "هو", "هم", "اللي", "كان", "بعد", "لي", "ما", "ال", "سم", "حد", "ل", "ب", "كل", "صل", "بو", "نت", "مش", "لا", "مر", "صر", "نا", "شهر", "بق", "سر", "با", "بس", "مين", "دا", "حمد", "دار", "داره"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "بعد", "ال", "ل", "ب", "مش", "يم", "حت", "بق", "اع"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "بعد", "بن", "انو", "ال", "ل", "ب", "مش", "بق", "وم", "لا", "بق", "مين", "ها", "تيم"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "بعد", "جي", "او", "ما", "ال", "سم", "ل", "ب", "يل", "صر", "ااس", "سب", "بق", "اس", "فس", "يو", "جم"}
	};
	public static final String[] masry_postfixes_verbs = {"ش", "ني", "وا", "و", "ت"};
	public static final String[][] masry_postfixes_verbs_stopwords = new String[][]{
		new String[] {"هو", "هي", "دي", "او", "ان", "ال", "جي", "الجي"},
		new String[] {"ال", "مع", "تا"},
		new String[] {"ال", "مش", "بق"},
		new String[] {"دي", "ما", "ال", "زي", "هي", "ل", "ب", "الجي", "بق"},
		new String[] {"في", "من", "عن", "فيا", "اخ", "تح", "بق"}
	};
	public static final String[] masry_prefixes_indet = {"و", "ب"};
	public static final String[][] masry_prefixes_indet_stopwords = new String[][]{
		new String[] {"او", "صل", "يا", "لاد", "قت", "عمر", "يه", "جد", "جه"},
		new String[] {"في", "عن", "علي", "الى", "ان", "هو", "ال", "من", "عد", "عده", "هو", "هي", "ل", "ب", "ما", "ين", "اب", "لاد", "قي", "نا", "نت", "كر", "حر", "تاع", "يه", "تيه", "ها", "الى"}
	};
	public static final String[] masry_postfixes_indet = {"ي", "ك", "كي", "ه", "ها", "نا", "كم", "هم"};
	public static final String[][] masry_postfixes_indet_stopwords = new String[][]{
		new String[] {"الل", "هو", "هم", "عل", "ال", "سم", "او", "ما", "هي", "بن", "مش", "تان", "حوال", "حت", "كبر", "لا", "لو", "حسن", "بق"},
		new String[] {"اللي", "دي", "ده", "بن", "ما", "هي", "لين", "مل", "لو"},
		new String[] {"هي"},
		new String[] {"سن", "دي", "ال", "بن", "هي", "كد", "ام", "مر", "جه", "حاج", "مدين", "اللغ", "اى", "قاهر", "القاهر"},
		new String[] {},
		new String[] {"يا"},
		new String[] {"او", "انو", "تح"},
		new String[] {"لا"}		
	};
	
	public static final Pattern someArabicCharacters = Pattern.compile("[\\u0600-\\u06FF]+");
	
	public enum AlloGraphEnd {HAS_ALLOGRAPH_END_YA_ALIF_MAQSURA, HAS_ALLOGRAPH_END_H_TA_MARBUTA, HAS_ALLOGRAPH_ALIF_HAMZA, HAS_ALLOGRAPH_ALIF_HAMZA_BELOW, HAS_ALLOGRAPH_TATWEEL};
	public enum PostFemininMarkers {POST_F_TI, POST_F_TAIK, POST_F_TU, POST_F_ITHA, POST_F_ITNA, POST_F_ITKUM, POST_F_ITHUM};
	public enum PostFemininPluralMarkers {POST_FP, POST_FP_ATI, POST_FP_ATAIK, POST_FP_ATU, POST_FP_ATHA, POST_FP_ATNA, POST_FP_ATKUM, POST_FP_ATHUM};
	public enum PreNounMarkers {PREF_N_LI, PREF_N_AL};
	public enum PostNounMarkers {POST_N_IN};
	public enum PreVerbMarkers {PREF_V_MA, PREF_V_HA, PREF_V_BI, PREF_V_A, PREF_V_TI, PREF_V_YI, PREF_V_NI};
	public enum PostVerbMarkers {POST_V_SH, POST_V_NI, POST_V_UU, POST_V_U, POST_V_I};
	public enum PreNonMarkers {PREF_I_W, PREF_I_BI};
	public enum PostNonMarkers {POST_I_I, POST_I_AK_IK, POST_I_KI, POST_I_H, POST_I_HA, POST_I_NA, POST_I_KUM, POST_I_HUM};
	
	public enum IrregularJoin {IRREGULAR};
	
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
		
//		public static <T> T[] concat(T[] first, T[] second) {
//			  T[] result = Arrays.copyOf(first, first.length + second.length);
//			  System.arraycopy(second, 0, result, first.length, second.length);
//			  return result;
//			}

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
	}
	
	private static class PostfixFemininWord implements VaryWord {
		public String varyWord(String inputWord, String afix) {
			if (!(inputWord.endsWith(MasryConsts.TA_MARBUTA)))
				return "";
			return inputWord.substring(0, inputWord.length() - 1) + afix;
		}

		@Override
		public String[] getStopWords(String afix) {
			String[] result = new String[] {"الة",
//											"اله"
			}; 
			Arrays.sort(result);
			return result;
		}
	}
	
	private static class AllographEndYa implements VaryWord {
		public String varyWord(String inputWord, String afix) {
			if ((afix != MasryConsts.ALIF_MAQSURA) || !(inputWord.endsWith(MasryConsts.YA)))
				return "";
			return inputWord.substring(0, inputWord.length() - 1) + afix;
		}

		@Override
		public String[] getStopWords(String afix) {
			String[] result = new String[] {"علي", // cala upon; caly more likely a name
			}; 
			Arrays.sort(result);
			return result;
		}
	}
	
	private static class AllographEndHa implements VaryWord {
		public String varyWord(String inputWord, String afix) {
			if ((afix != MasryConsts.TA_MARBUTA) || !(inputWord.endsWith(MasryConsts.H)))
				return "";
			return inputWord.substring(0, inputWord.length() - 1) + afix;
		}

		@Override
		public String[] getStopWords(String afix) {
			String[] result = new String[] {"فيه", // fih there is; fiya group
											"وفيه",
											"منه",
											"ومنه"
											}; 
			Arrays.sort(result);
			return result;
		}
	}
	
	public static VaryWord allographEndYa = new MasryConsts.AllographEndYa();
	public static VaryWord allographEndHa = new MasryConsts.AllographEndHa();
	public static VaryWord postfixFemininWord = new MasryConsts.PostfixFemininWord();

}
