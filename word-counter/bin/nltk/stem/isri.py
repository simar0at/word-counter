#rem -*- coding: utf-8 -*-
#rem
#rem Natural Language Toolkit: The ISRI Arabic Stemmer
#rem
#rem Copyright (C) 2001-2012 NLTK Proejct
#rem Algorithm: Kazem Taghva, Rania Elkhoury, and Jeffrey Coombs (2005)
#rem Author: Hosam Algasaier <hosam_hme@yahoo.com>
#rem URL: <http://www.nltk.org/>
#rem For license information, see LICENSE.TXT

"""
ISRI Arabic Stemmer

The algorithm for this stemmer is described in:

Taghva, K., Elkoury, R., and Coombs, J. 2005. Arabic Stemming without a root dictionary.
Information Science Research Institute. University of Nevada, Las Vegas, USA.

The Information Science Research Institute’s (ISRI) Arabic stemmer shares many features
with the Khoja stemmer. However, the main difference is that ISRI stemmer does not use root
dictionary. Also, if a root is not found, ISRI stemmer returned normalized form, rather than
returning the original unmodified word.

Additional adjustments were made to improve the algorithm:

1- Adding 60 stop words.
2- Adding the pattern (تفاعيل) to ISRI pattern set.
3- The step 2 in the original algorithm was normalizing all hamza. This step is discarded because it
increases the word ambiguities and changes the original root.

"""
import re

from api import StemmerI

# from net.homeunix.siam.stemmer import StemmerI

class ISRIStemmer(StemmerI):
    '''
    ISRI Arabic stemmer based on algorithm: Arabic Stemming without a root dictionary.
    Information Science Research Institute. University of Nevada, Las Vegas, USA.

    A few minor modifications have been made to ISRI basic algorithm.
    See the source code of this module for more information.

    isri.stem(token) returns Arabic root for the given token.

    The ISRI Stemmer requires that all tokens have Unicode string types.
    If you use Python IDLE on Arabic Windows you have to decode text first
    using Arabic '1256' coding.
    '''

    def __init__(self):
        self.stm = 'defult none'

        self.p3 = [u'كال', u'بال',
                   u'ولل', u'وال']    #rem length three prefixes
        self.p2 = [u'ال', u'لل']    #rem length two prefixes
        self.p1 = [u'ل', u'ب', u'ف', u'س', u'و',
                   u'ي', u'ت', u'ن', u'ا']   #rem length one prefixes

        self.s3 =  [u'تمل', u'همل',
                    u'تان', u'تين',
                    u'كمل']  #rem length three suffixes
        self.s2 = [u'ون', u'ات', u'ان',
                   u'ين', u'تن', u'كم',
                   u'هن', u'نا', u'يا',
                   u'ها', u'تم', u'كن',
                   u'ني', u'وا', u'ما',
                   u'هم']   #rem length two suffixes
        self.s1 = [u'ة', u'ه', u'ي', u'ك', u'ت',
                   u'ا', u'ن']   #rem length one suffixes

        self.pr4 = {0: [u'م'], 1:[u'ا'],
                    2: [u'ا', u'و', u'ي'], 3:[u'ة']}   #rem groups of length four patterns
        self.pr53 = {0: [u'ا', u'ت'],
                     1: [u'ا', u'ي', u'و'],
                     2: [u'ا', u'ت', u'م'],
                     3: [u'م', u'ي', u'ت'],
                     4: [u'م', u'ت'],
                     5: [u'ا', u'و'],
                     6: [u'ا', u'م']}   #rem Groups of length five patterns and length three roots

        self.re_short_vowels = re.compile(ur'[ً-ْ]')
        self.re_hamza = re.compile(ur'[ءؤئ]')
        self.re_intial_hamza = re.compile(ur'^[آأإ]')

        self.stop_words = [] 
        self.stop_wordsx= [u'يكون',
                           u'يقول',
                           u'وليس',
                           u'وكان',
                           u'كذلك',
                           u'التي',
                           u'وبين',
                           u'عليها',
                           u'مساء',
                           u'الذي',
                           u'وكانت',
                           u'ولكن',
                           u'والتي',
                           u'تكون',
                           u'اليوم',
                           u'اللذين',
                           u'عليه',
                           u'كانت',
                           u'لذلك',
                           u'أمام',
                           u'هناك',
                           u'منها',
                           u'مازال',
                           u'لازال',
                           u'لايزال',
                           u'مايزال',
                           u'اصبح',
                           u'أصبح',
                           u'أمسى',
                           u'امسى',
                           u'أضحى',
                           u'اضحى',
                           u'مابرح',
                           u'مافتئ',
                           u'ماانفك',
                           u'لاسيما',
                           u'ولايزال',
                           u'الحالي',
                           u'اليها',
                           u'الذين',
                           u'فانه',
                           u'والذي',
                           u'وهذا',
                           u'لهذا',
                           u'فكان',
                           u'ستكون',
                           u'اليه',
                           u'يمكن',
                           u'بهذا',
                           u'الذى']


    def stem(self, token):
        """
        Stemming a word token using the ISRI stemmer.
        """

        self.stm = token
        self.norm(1)       #rem  remove diacritics which representing Arabic short vowels
        if self.stm in self.stop_words: return [self.stm, ]      #rem exclude stop words from being processed
        self.pre32()        #rem remove length three and length two prefixes in this order
        self.suf32()        #rem remove length three and length two suffixes in this order
        self.waw()          #rem remove connective ‘و’ if it precedes a word beginning with ‘و’
        self.norm(2)       #rem normalize initial hamza to bare alif
        if len(self.stm)<=3: return [self.stm, ]      #rem return stem if less than or equal to three

        if len(self.stm)==4:       #rem length 4 word
            self.pro_w4()
            return [self.stm, ] 
        elif len(self.stm)==5:     #rem length 5 word
            self.pro_w53()
            self.end_w5()
            return [self.stm, ] 
        elif len(self.stm)==6:     #rem length 6 word
            self.pro_w6()
            self.end_w6()
            return [self.stm, ] 
        elif len(self.stm)==7:     #rem length 7 word
            self.suf1()
            if len(self.stm)==7:
                self.pre1()
            if len(self.stm)==6:
                self.pro_w6()
                self.end_w6()
                return [self.stm, ]
        return [self.stm, ]              #rem if word length >7 , then no stemming

    def norm(self, num):
        """
        normalization:
        num=1  normalize diacritics
        num=2  normalize initial hamza
        num=3  both 1&2
        """
        self.k = num

        if self.k == 1:
            self.stm = self.re_short_vowels.sub('', self.stm)
            return self.stm
        elif self.k == 2:
            self.stm = self.re_intial_hamza.sub(ur'ا',self.stm)
            return self.stm
        elif self.k == 3:
            self.stm = self.re_short_vowels.sub('', self.stm)
            self.stm = self.re_intial_hamza.sub(ur'ا',self.stm)
            return self.stm

    def pre32(self):
        """remove length three and length two prefixes in this order"""
        if len(self.stm)>=6:
            for pre3 in self.p3:
                if self.stm.startswith(pre3):
                    self.stm = self.stm[3:]
                    return self.stm
                elif len(self.stm)>=5:
                    for pre2 in self.p2:
                        if self.stm.startswith(pre2):
                            self.stm = self.stm[2:]
                            return self.stm

    def suf32(self):
        """remove length three and length two suffixes in this order"""
        if len(self.stm)>=6:
            for suf3 in self.s3:
                if self.stm.endswith(suf3):
                    self.stm = self.stm[:-3]
                    return self.stm
                elif len(self.stm)>=5:
                    for suf2 in self.s2:
                        if self.stm.endswith(suf2):
                            self.stm = self.stm[:-2]
                            return self.stm


    def waw(self):
        """remove connective ‘و’ if it precedes a word beginning with ‘و’ """
        if (len(self.stm)>=4)&(self.stm[:2] == u'وو'):
            self.stm = self.stm[1:]
            return self.stm

    def pro_w4(self):
        """process length four patterns and extract length three roots"""
        if self.stm[0] in self.pr4[0]:      #rem  مفعل
            self.stm = self.stm[1:]
            return self.stm
        elif self.stm[1] in self.pr4[1]:      #rem   فاعل
            self.stm = self.stm[0]+self.stm[2:]
            return self.stm
        elif self.stm[2] in self.pr4[2]:     #rem    فعال   -   فعول    - فعيل
            self.stm = self.stm[:2]+self.stm[3]
            return self.stm
        elif self.stm[3] in self.pr4[3]:      #rem     فعلة
            self.stm = self.stm[:-1]
            return self.stm
        else:
            self.suf1()   #rem do - normalize short sufix
            if len(self.stm)==4:
                self.pre1()    #rem do - normalize short prefix
            return self.stm

    def pro_w53(self):
        """process length five patterns and extract length three roots"""
        if ((self.stm[2] in self.pr53[0]) & (self.stm[0] == u'ا' )):    #rem  افتعل   -  افاعل
            self.stm = self.stm[1]+self.stm[3:]
            return self.stm
        elif ((self.stm[3] in self.pr53[1]) & (self.stm[0] == u'م')):     #rem مفعول  -   مفعال  -   مفعيل
            self.stm = self.stm[1:3]+self.stm[4]
            return self.stm
        elif ((self.stm[0] in self.pr53[2]) & (self.stm[4] == u'ة')):      #rem  مفعلة  -    تفعلة   -  افعلة
            self.stm = self.stm[1:4]
            return self.stm
        elif ((self.stm[0] in self.pr53[3]) & (self.stm[2] == u'ت')):        #rem  مفتعل  -    يفتعل   -  تفتعل
            self.stm = self.stm[1]+self.stm[3:]
            return self.stm
        elif ((self.stm[0] in self.pr53[4]) & (self.stm[2] == u'ا')):      #remمفاعل  -  تفاعل
            self.stm = self.stm[1]+self.stm[3:]
            return self.stm
        elif ((self.stm[2] in self.pr53[5]) & (self.stm[4] == u'ة')):    #rem     فعولة  -   فعالة
            self.stm = self.stm[:2]+self.stm[3]
            return self.stm
        elif ((self.stm[0] in self.pr53[6]) & (self.stm[1] == u'ن')):     #rem     انفعل   -   منفعل
            self.stm = self.stm[2:]
            return self.stm
        elif ((self.stm[3] == u'ا') & (self.stm[0] == u'ا')):     #rem   افعال
            self.stm = self.stm[1:3]+self.stm[4]
            return self.stm
        elif ((self.stm[4] == u'ن') & (self.stm[3] == u'ا')):      #rem   فعلان
            self.stm = self.stm[:3]
            return self.stm
        elif ((self.stm[3] == u'ي') & (self.stm[0] == u'ت')):        #rem    تفعيل
            self.stm = self.stm[1:3]+self.stm[4]
            return self.stm
        elif ((self.stm[3] == u'و') & (self.stm[1] == u'ا')):       #rem     فاعول
            self.stm = self.stm[0]+self.stm[2]+self.stm[4]
            return self.stm
        elif ((self.stm[2] == u'ا') & (self.stm[1] == u'و')):             #rem     فواعل
            self.stm = self.stm[0]+self.stm[3:]
            return self.stm
        elif ((self.stm[3] == u'ئ') & (self.stm[2] == u'ا')):     #rem  فعائل
            self.stm = self.stm[:2]+self.stm[4]
            return self.stm
        elif ((self.stm[4] == u'ة') & (self.stm[1] == u'ا')):           #rem   فاعلة
            self.stm = self.stm[0]+self.stm[2:4]
            return self.stm
        elif ((self.stm[4] == u'ي') & (self.stm[2] == u'ا')):     #rem فعالي
            self.stm = self.stm[:2]+self.stm[3]
            return self.stm

        else:
            self.suf1()   #rem do - normalize short sufix
            if len(self.stm)==5:
                self.pre1()   #rem do - normalize short prefix
            return self.stm

    def pro_w54(self):
        """process length five patterns and extract length four roots"""
        if (self.stm[0] in self.pr53[2]):       #remتفعلل - افعلل - مفعلل
            self.stm = self.stm[1:]
            return self.stm
        elif (self.stm[4] == u'ة'):      #rem فعللة
            self.stm = self.stm[:4]
            return self.stm
        elif (self.stm[2] == u'ا'):     #rem فعالل
            self.stm = self.stm[:2]+self.stm[3:]
            return self.stm

    def end_w5(self):
        """ending step (word of length five)"""
        if len(self.stm)==3:
            return self.stm
        elif len(self.stm)==4:
            self.pro_w4()
            return self.stm
        elif len(self.stm)==5:
            self.pro_w54()
            return self.stm

    def pro_w6(self):
        """process length six patterns and extract length three roots"""
        if ((self.stm.startswith(u'است')) or (self.stm.startswith(u'مست'))):   #rem   مستفعل   -    استفعل
            self.stm= self.stm[3:]
            return self.stm
        elif (self.stm[0]== u'م' and self.stm[3]== u'ا' and self.stm[5]== u'ة'):      #rem     مفعالة
            self.stm = self.stm[1:3]+self.stm[4]
            return self.stm
        elif (self.stm[0]== u'ا' and self.stm[2]== u'ت' and self.stm[4]== u'ا'):      #rem     افتعال
            self.stm = self.stm[1]+self.stm[3]+self.stm[5]
            return self.stm
        elif (self.stm[0]== u'ا' and self.stm[3]== u'و' and self.stm[2]==self.stm[4]):      #rem    افعوعل
            self.stm = self.stm[1]+self.stm[4:]
            return self.stm
        elif (self.stm[0]== u'ت' and self.stm[2]== u'ا' and self.stm[4]== u'ي'):      #rem     تفاعيل    new pattern
            self.stm = self.stm[1]+self.stm[3]+self.stm[5]
            return self.stm
        else:
            self.suf1()    #rem do - normalize short sufix
            if len(self.stm)==6:
                self.pre1()    #rem do - normalize short prefix
            return self.stm

    def pro_w64(self):
        """process length six patterns and extract length four roots"""
        if (self.stm[0] and self.stm[4])==u'ا':      #rem  افعلال
            self.stm=self.stm[1:4]+self.stm[5]
            return self.stm
        elif (self.stm.startswith(u'مت')):     #rem   متفعلل
            self.stm = self.stm[2:]
            return self.stm

    def end_w6(self):
        """ending step (word of length six)"""
        if len(self.stm)==3:
            return self.stm
        elif len(self.stm)==5:
            self.pro_w53()
            self.end_w5()
            return self.stm
        elif len (self.stm)==6:
            self.pro_w64()
            return self.stm

    def suf1(self):
        """normalize short sufix"""
        for sf1 in self.s1:
            if self.stm.endswith(sf1):
                self.stm = self.stm[:-1]
                return self.stm

    def pre1(self):
        """normalize short prefix"""
        for sp1 in self.p1:
            if self.stm.startswith(sp1):
                self.stm = self.stm[1:]
                return self.stm


if __name__ == "__main__":
    stemmer = ISRIStemmer()
    stemmer.stem("يقول")
    
