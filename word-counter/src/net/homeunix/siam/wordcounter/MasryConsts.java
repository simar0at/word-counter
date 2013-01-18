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

import java.util.Arrays;
import java.util.regex.Pattern;

import net.homeunix.siam.wordcounter.Run.CollectRemovals.VaryWord;

public class MasryConsts {
	public static final String TA_MARBUTA = "ة"; 
	public static final String H = "ه";
	public static final String ALIF_MAQSURA = "ى";
	public static final String YA = "ي";
	public static final String ALIF = "ا";
	public static final String ALIF_HAMZA = "أ";
	public static final String ALIF_HAMZA_BELOW = "إ";
	public static final String TATWEEL = "ـ";
	public static final String[] joinThese = new String[]     {"هما", "دا", "كن", "قل",  "شف", "شوف", "كون", "رح", "روح", "قول", "مات", "جيت", "جم", "جت", "يجي", "تجي", "جيب", "بقت", "بقو", "بقوا", "يبقي", "يصل", "اصل", "اتوفى", "عاش", "مات", "مصريين", "مواليد", "احداث", "بلاد", "السياس", "سياسى", "مدن", "كتب", "مصادر", "تصانيف", "ولاد", "مناطق", "عصور", "إنسان", "أيام", "رجال", "أماكن", "أحوال", "حروف", "حقوق", "أحداث", "أعمال", "أقباط", "مماليك", "إيد", "الإسلام", "بإن", "معا", "إبن", "علشان", "ويكيپيديا", "لينكات"};
	public static final String[] joinWithThese = new String[] {"هم", "ده", "كان", "قال", "شاف", "شاف", "كان", "راح", "راح", "قال",  "موت", "جه", "جه", "جه",  "جه", "جه",  "جاب", "بقي", "بقي", "بقي", "بقي", "وصل", "وصل", "وفى", "عيش", "موت", "مصر",   "مولود", "حدث", "بلد",  "سياسه", "سياسه", "مدينه", "كتاب", "مصدر", "تصنيف", "ولد",  "منطق", "عصر",  "ناس", "يوم", "رجل",  "مكان", "حال",  "حرف", "حق",  "حديث",  "عمل",  "قبط", "مملوك",  "يد", "اسلام",  "إن",  "مع", "بن", "عشان",  "يكيبيديا", "لينك"};
	// when iterated over them, those affixes that occur together with the other ones should be after them
	public static final String[] masry_allograph = {"ى", "ة", "أ", "إ", "ـ"};
	
	public static final String[] masry_feminin_postfixes = {"تي", "تك", "ته", "تها", "تنا", "تكم", "تهم"};
	public static final String[] masry_feminin_plural_postfixes = {"ات", "اتي", "اتك", "اته", "اتها", "اتنا", "اتكم", "اتهم"};
	public static final String[] masry_prefixes_nouns = {"ل", "ال"};
	public static final String[][] masry_prefixes_nouns_stopwords = new String[][]{
		new String[] {"ب", "في", "عن", "علي", "دي", "ده", "هو", "هي", "ان", "كان", "او", "انو", "ما", "بن", "ال", "مع", "من", "بن", "ين", "كل", "كن", "يل", "اد", "قى", "بس", "غايه", "لو", "يس", "ون", "دا"},
		new String[] {"لي", "عن", "علي", "دي", "ده", "هو", "هي", "كان", "او", "عنو", "مع", "ل", "ب", "مش", "أم", "لما", "لا", "مان"}
	};
	public static final String[] masry_postfixes_nouns = {"ين"};
	public static final String[][] masry_postfixes_nouns_stopwords = new String[][]{
		new String[] {"ب", "من", "في", "عن", "علي", "الى", "او", "ال", "مع", "ما", "بعد", "مد", "يم", "أم", "حس", "لا", "دا"}
	};
	public static final String[] masry_prefixes_verbs = {"ما", "ح", "ا", "ت", "ي", "ن"};
	public static final String[][] masry_prefixes_verbs_stopwords = new String[][]{
		new String[] {"من", "عن", "علي", "الى", "ان", "لو", "ما", "ال", "ل", "ب", "ده", "لا", "لو", "يو", "دا"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "سن", "عد", "ما", "ال", "اسم", "مل", "ل", "ب", "ين", "صل", "اول", "يا", "زب", "كم", "رب", "سب", "لم", "كتر", "كبر", "لو", "مد", "سين", "بس", "حسن", "ير"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "هو", "هم", "اللي", "كان", "بعد", "لي", "ما", "ال", "سم", "حد", "ل", "ب", "كل", "صل", "بو", "نت", "مش", "لا", "مر", "صر", "نا", "شهر", "بق", "سر", "با", "بس", "مين", "دا", "حمد", "دار", "داره", "ولاد"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "بعد", "ال", "ل", "ب", "مش", "يم", "حت", "بق", "اع", "حسين"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "جي", "بعد", "بن", "انو", "ال", "ل", "ب", "مش", "بق", "وم", "لا", "بق", "مين", "ها", "تيم"},
		new String[] {"من", "في", "عن", "علي", "الى", "ان", "بعد", "جي", "او", "ما", "ال", "سم", "ل", "ب", "يل", "صر", "ااس", "سب", "بق", "اس", "فس", "يو", "جم", "شر"}
	};
	public static final String[] masry_postfixes_verbs = {"ش", "ني", "وا", "و", "ت"};
	public static final String[][] masry_postfixes_verbs_stopwords = new String[][]{
		new String[] {"هو", "هي", "دي", "او", "ان", "ال", "جي", "لو", "عا", "عي", "الجي"},
		new String[] {"ال", "مع", "تا", "حس"},
		new String[] {"ال", "مش", "بق", "دا"},
		new String[] {"دي", "ما", "ال", "زي", "هي", "ل", "ب", "الجي", "بق", "دا"},
		new String[] {"في", "من", "عن", "فيا", "اخ", "تح", "بق", "صل", "بن", "ان", "لس", "دا"}
	};
	public static final String[] masry_prefixes_indet = {"و", "ب"};
	public static final String[][] masry_prefixes_indet_stopwords = new String[][]{
		new String[] {"او", "صل", "يا", "لاد", "قت", "عمر", "يه", "جد", "جدي", "جده", "جه", "صف", "احده", "وده"},
		new String[] {"في", "عن", "علي", "الى", "ان", "دا", "هو", "ال", "من", "عد", "عده", "هو", "هي", "ل", "ب", "ما", "ين", "اب", "لاد", "قي", "نا", "نت", "كر", "حر", "در", "تاع", "يه", "تيه", "ها", "الى", "ابا", "ده", "ودا"}
	};
	public static final String[] masry_postfixes_indet = {"ي", "ك", "كي", "ه", "ها", "نا", "كم", "هم"};
	public static final String[][] masry_postfixes_indet_stopwords = new String[][]{
		new String[] {"الل", "هو", "هم", "عل", "ال", "إل", "سم", "أو", "ما", "هي", "بن", "مش", "تان", "حوال", "حت", "كبر", "لا", "لو", "حسن", "بق", "صدق", "دا"},
		new String[] {"اللي", "دي", "ده", "بن", "ما", "هي", "لين", "مل", "لو", "شر", "الامري", "هنا", "حر"},
		new String[] {"هي", "الامري"},
		new String[] {"سن", "دي", "ال", "بن", "هي", "كد", "مر", "جه", "حاج", "مدين", "اللغ", "اى", "صل", "قاهر", "لست", "القاهر", "جامع", "الجامع", "لجامع", "للجامع", "ثور", "أم", "كلم", "لس", "حمل", "شوي", "مد", "فتر", "ودا"},
		new String[] {"ودا"},
		new String[] {"يا"},
		new String[] {"او", "انو", "تح", "حا"},
		new String[] {"لا", "ودا"}		
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
	
	private static class PostfixFemininWord implements VaryWord {
		public String varyWord(String inputWord, String afix) {
			if (!(inputWord.endsWith(MasryConsts.TA_MARBUTA)))
				return "";
			return inputWord.substring(0, inputWord.length() - 1) + afix;
		}

		@Override
		public String[] getStopWords(String afix) {
			String[] result = new String[] {"الة",
//											"اله",
											"ودة"
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
											"وعلي",
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
