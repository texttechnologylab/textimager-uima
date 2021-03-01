#import csv
#import glob
import dcor
import hashlib
import hdbscan
import itertools
#import json
import math
import networkx as nx
import nolds
import numpy as np
import os
import pandas as pd
import re
import spacy
import string
import sys
#import time
import torch
import warnings

from collections import OrderedDict
from nltk import ngrams
from scipy.special import binom
from scipy.stats import entropy
from sklearn.metrics.pairwise import cosine_similarity
from statsmodels.tsa.stattools import acf
from transformers import BertForMaskedLM
from transformers import BertForNextSentencePrediction
from transformers import BertModel
from transformers import BertTokenizer

warnings.filterwarnings('ignore')

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def softmax(z):
    """Softmax function"""
    z = np.array(z)
    e_z = np.exp(z - np.max(z))
    return e_z / e_z.sum(axis=0, keepdims = True)

def l2p(l):
    """Convert logits to probabilites"""
    odds = np.exp(l)
    return odds / (1 + odds)

class Text:
    def __init__(self, lang, text, text_id, load=False, mode=None):
        self.text_raw = text
        self.text = text
        self.text_id = text_id
        self.lang = lang
        self.mode = mode
        self.hash_hex = self._hashify()
        self.length = self._count_words()

        self.n_sentences = 0
        self.l_sentences = []

        self.sentences = []
        self.syllables = []

        self.token = []
        self.token_unique = []
        self.token_unique_indices = []
        self.token_frequencies = []

        self.lemma = []
        self.lemma_unique = []
        self.lemma_unique_indices = []
        self.lemma_frequencies = []

        self.pos = []
        self.stops = 0
        self.span = []
        self.corrupted = False
        self.load = load

        self.doc = None
        # since sentencizer is confised by {}b etc, save corresponding tokens
        self.semantic_dict = dict()

    def _query_spacy(self):
        if self.lang =='en':
            nlp = spacy.load('en_core_web_lg')
        elif self.lang =='de':
            nlp = spacy.load('de_core_news_lg')
        elif self.lang =='es':
            nlp = spacy.load('es_core_news_md')
        elif self.lang =='fr':
            nlp = spacy.load('fr_core_news_md')
        elif self.lang =='it':
            nlp = spacy.load('it_core_news_sm')
        elif self.lang =='nl':
            nlp = spacy.load('nl_core_news_sm')
        elif self.lang =='ja':
            nlp = spacy.load('ja_core_news_lg')
        else:
            print(f'Language you specified is not supported.')
        
        text = self.text
        #text = re.sub('1_sec_pause|2_sec_pause|3_sec_pause|multiSecPause|/', '', text)
        #text = re.sub(r'\([^)]*\)', '', text)
        #self.text = text

        syllables = None
        if self.mode == 'SHE':
            try:
                syllables = SpacySyllables(nlp)
                nlp.add_pipe(syllables, after='tagger')
            except NotImplementedError:
                pass
            sentencizer = nlp.create_pipe("sentencizer")
            nlp.add_pipe(sentencizer)

        doc = nlp(text)
        self.doc_raw = nlp(self.text_raw)
        self.doc = doc

        self.token = list(self.token)
        for t in doc:
            self.token.append(t.text)
            self.lemma.append(t.lemma_)
            self.pos.append(t.pos_)
            if self.mode == 'SHE':
                str_p = list(string.punctuation) + ['\n'] + [' ']
                if not syllables is None and not t.is_punct and not t.is_space and not t.text in str_p:
                    if t._.syllables_count is None:
                        self.syllables.append(0)
                    else:
                        self.syllables.append(t._.syllables_count)
                    #self.syllables.append((t.text, t._.syllables_count))
                else:
                    #TODO
                    self.syllables.append(np.nan)

        #token = [t.text for t in doc if t.pos_ != 'PUNCT']
        #self.nmw = len([t for t in token if spellchecker.spell(t)==False])

        self.sentences = [str(s) for s in doc.sents]# if len(s) != 1]
        ## sentence length
        self.l_sentences = [len([doc[i] for i in range(sent.start, sent.end) 
            if doc[i].pos_ != 'PUNCT'])
                    for sent in doc.sents]
        self.n_sentences = len(self.sentences)

    def _query_polyglot(self):
        """
        Use polyglot tokenization (omitting lemmatization)
        """
        try:
            self.text = ''.join(x for x in self.text if x.isprintable())
            text = polyText(self.text, hint_language_code=self.lang)
            sentences = text.sentences
            self.n_sentences = len(sentences)
            for s in sentences:
                self.l_sentences.append(len(s.split(' ')))

            self.token = [x for x in list(text.words) if not self._is_control(x) 
                    and not self._is_punct(x)]
        except ValueError as e:
            print(e)
        except Exception as e1:
            pass


    def _count_words(self):
        """Calculate length (in words) of a text."""
        return len(self.text.split())

    def _hashify(self):
        """Calculate hash hex string."""
        hash_object = hashlib.sha1(str.encode(self.text))
        hash_hex = hash_object.hexdigest()
        return hash_hex

    def _save(self):
        """Save Tokens and lemmas to disk."""
        #print(f'Saving {self.hash_hex}')
        path = 'data/hash/'
        if not os.path.exists(path):
            os.makedirs(path)
        path = os.path.join(path, self.hash_hex)
        data = {"tokens": [w for w in self.token],
                "lemmas": [w for w in self.lemma],
                "pos": [w for w in self.pos],
                "sentences": [w for w in self.sentences],
                "l_sentences": [w for w in self.l_sentences],
                "n_sentences": self.n_sentences,
                }
                #"nmw": self.nmw,
                #"doc_s": self.doc_s}
        
    def _filter_punctuation(self):
        str_p = list(string.punctuation) + ['\n'] + [' ']
        self.token = [w for w in self.token if w not in str_p]
        self.lemma = [w for w in self.lemma if w not in str_p]
        self.pos = [w for w in self.pos if w not in ['SPACE', 'PUNCT']]

    def preprocess(self):
        """Load data from disk or run tokenizer/lemmatizer"""

        if self.mode == 'FTD':
            self.text = re.sub('B: |I: ', '', self.text)
            self.text = re.sub('1_sec_pause|2_sec_pause|3_sec_pause|multiSecPause|/', 
                    '', self.text)
            self.text = re.sub(r'\([^)]*\)', '', self.text)
        elif self.mode == 'SHE':
            glws = re.findall('\{[^\}]+\}g', self.text)
            mses = re.findall('\{[^\}]+\}b', self.text)
            et1s = re.findall('\{[^\}]+\}y', self.text)
            et2s = re.findall('\{[^\}]+\}r', self.text)
            for glw in glws:
                g = re.sub(r'{|}[a-z]|⬜', '', glw)
                self.semantic_dict[g] = 'g'
            for mse in mses:
                m = re.sub(r'{|}[a-z]|⬜', '', mse)
                self.semantic_dict[m] = 'b'
            for et1 in et1s:
                e1 = re.sub(r'{|}[a-z]|⬜', '', et1)
                self.semantic_dict[e1] = 'y'
            for et2 in et2s:
                e2 = re.sub(r'{|}[a-z]|⬜', '', et2)
                self.semantic_dict[e2] = 'r'

            self.text = re.sub(r'{|}[a-z]|⬜|A\.|B\.|C\.|D\.', '', self.text)
            self.text = re.sub(r'bzw.', 'beziehungsweise', self.text)

        self.text = ''.join(x for x in self.text if x.isprintable())

        spacy_langs = ['de', 'en', 'es', 'fr', 'it', 'nl', 'ja']
        
        if self.lang in spacy_langs:
            self._query_spacy()
        else:
                self._query_polyglot()

        self._filter_punctuation()

        self.token_unique, self.token_unique_indices, self.token_frequencies = \
                np.unique(self.token, return_counts=True, return_index=True)
        self.lemma_unique, self.lemma_unique_indices, self.lemma_frequencies = \
                np.unique(self.lemma, return_counts=True, return_index=True)
        #self.token_frequencies[::-1].sort()
        self.token = np.array(self.token)
        self.lemma = np.array(self.lemma)
        self.pos = np.array(self.pos)
        self.token_frequencies, self.token_unique_indices =  \
            (np.asarray(list(l)) for l in zip(*sorted(zip(self.token_frequencies,
                self.token_unique_indices), reverse=True)))


class TextScorer:
    def __init__(self, lang, text, text_id, scorers=None, load=False, load_text=False,
            mode=None):
        self.load = load
        self.path = 'data/scores/individual'
        self.text_id = text_id
        self.text = Text(lang, text, text_id, load=load_text, mode=mode)
        self.text.preprocess()

        self.scorers = scorers


    def calculate(self):
        scores = OrderedDict({
            scorer.id(): {
                'id': scorer.id(),
                'name': scorer.name(),
                'text_id' : self.text_id,
                'score': scorer.score(self.text)
            }
            for scorer in self.scorers
        })
        return scores


## in case of multiple GPUs get the GPU with max free memory
def get_gpu():
    exit_code = os.system('nvidia-smi -q -d memory | grep -A4 GPU | grep Free > tmp')
    if not exit_code:
        memory_available = [int(x.split()[2]) for x in open('tmp', 'r').readlines()]
        return np.argmax(memory_available)
    else:
        return -1
#############################################################################################
# Baseclass for all Scorers
class TextScore:
    def id(self):
        pass

    def name(self):
        pass

    def score(self, text: Text):
        pass

## Scorers
class ADJPD(TextScore):
    def id(self):
        return "adjpd"

    def name(self):
        return "Number of Adjectives per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'ADJ']
        return len(pro)


class AdjustedModulus(TextScore):
    def id(self):
        return "A"

    def name(self):
        return "Adjusted Modulus"

    def score(self, text: Text):
        m_score = (np.amax(text.token_frequencies)) ** 2 + len(text.token_unique) ** 2
        y = HPoint().score(text)
        # TODO Fehler oder kann das wirklich vorkommen?
        if y == 0:
            return 0
        m_score = (m_score ** .5) / y
        x = math.log10(len(text.token))
        # TODO Fehler oder kann das wirklich vorkommen?
        if x == 0:
            return 0
        return m_score / x


class ADVPD(TextScore):
    def id(self):
        return "advpd"

    def name(self):
        return "Number of Adverbs per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'ADV']
        return len(pro)


class Alpha(TextScore):
    def id(self):
        return "alpha"

    def name(self):
        return "Writers View"

    def score(self, text: Text):
        hp = HPoint().score(text)
        v = len(np.unique(text.token))
        tf = text.token_frequencies[0] 
        cos1 = -1 * ((hp - 1) * (tf - hp) + (hp - 1) * (v - hp))
        cos2 = ((hp - 1) ** 2 + (tf - hp) ** 2 ) ** 0.5 
        cos3 = ((hp - 1) ** 2 + (v - hp) ** 2) ** 0.5

        try:
            cos = cos1 / cos2 / cos3
        except UnboundLocalError:
            return 0
            #print("IsNaN")
        arccos = np.arccos(cos)
        if math.isnan(arccos):
            return 0
        else:
            return arccos


class APD(TextScore):
    def id(self):
        return "apd"

    def name(self):
        return "Number of Auxiliary Words per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'AUX']
        return len(pro)


class ASL(TextScore):
    def id(self):
        return "ASL"

    def name(self):
        return "Average Sentence Length"

    def score(self, text: Text):
        score = np.sum(text.l_sentences)/text.n_sentences

        return score


class ATL(TextScore):
    def id(self):
        return "ATL"

    def name(self):
        return "Average Token Length"

    def score(self, text: Text):
        tokens = [str(t) for t in text.token]
        tl = []
        for t in tokens:
            tl.append(len(list(t)))
        return np.average(tl)

class AutoBERTS(TextScore):
    def id(self):
        return "AutoBERTS"

    def name(self):
        return "Autocorrelation coefficient based on BERT next sentence prediction probabilities."
    
    def __adc(self, x, t=1):
        return np.array([1] + [dcor.distance_correlation(x[:-i], x[i:])
            for i in range(1, t + 1)])

    def _adc(self, x, t=1):
        #res = np.array([1] + [dcor.distance_correlation(x[:-i], x[i:])
        #    for i in range(1, t + 1)])
        res = [1]
        for i in range(1, t + 1):
            if len(x[:-i]) <= 1:
                break
            res += [dcor.distance_correlation(x[:-i], x[i:])]
        return res

    def _rac(self, x):
        while len(x) >= 2:
            if len(x) == 2:
                return x[1]
            if len(x) > 2:
                x = acf(x, len(x), fft=True)[:-1]

    def _radc(self, x):
        while len(x) >= 3:
            if len(x) == 3:
                return x[1]
            if len(x) > 3:
                x = self.__adc(x, len(x))[:-2]
                #x = x[x!=0]

    def _autodtw(self, x, t=1):
        x = np.array(x)
        return dtw(x[:-t], x[t:])

    def _save(self, scores, hash_hex, path):
        """Save scores to disk."""
        
        path = os.path.join(path, hash_hex)
        f = open(path, "w")
        json.dump(scores, f)
        f.close()

    def score(self, text, hash_hex, text_id, label, lang, n_lags=10):
        
        texts = [text]
        
        names_bsac = ['bsac' + str(i+1) for i in range(n_lags)]
        names_bsadc = ['bsadc' + str(i+1) for i in range(n_lags)]

        names = ['bstsim', 'bsesim', 'bsacn', 'bsH', 'bslH',
                'bsrac', 'bsradc'] + names_bsac + names_bsadc
        ## create scores array
        scores = np.full((len(texts), 7 + 2 * n_lags), np.nan)

        device_num = get_gpu()
        if device_num == -1:
            device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        else:
            device = torch.device(f'cuda:{device_num}' if torch.cuda.is_available() else 'cpu')

        if lang == 'de':
            tokenizer = BertTokenizer.from_pretrained('dbmdz/bert-base-german-cased')
            model = BertForNextSentencePrediction.from_pretrained(
                    'dbmdz/bert-base-german-cased').to(device)
            model2 = BertModel.from_pretrained(
                    'dbmdz/bert-base-german-cased').to(device)
        elif lang == 'en':
            tokenizer = BertTokenizer.from_pretrained('bert-base-cased')
            model = BertForNextSentencePrediction.from_pretrained(
                    'bert-base-cased').to(device)
            model2 = BertModel.from_pretrained(
                    'bert-base-cased').to(device)
        elif lang == 'ja':
            tokenizer = BertTokenizer.from_pretrained(
                    'cl-tohoku/bert-base-japanese-whole-word-masking')
            model = BertForNextSentencePrediction.from_pretrained(
                    'cl-tohoku/bert-base-japanese-whole-word-masking').to(device)
            model2 = BertModel.from_pretrained(
                    'cl-tohoku/bert-base-japanese-whole-word-masking').to(device)

        else:
            tokenizer = BertTokenizer.from_pretrained('bert-base-multilingual-cased')
            model = BertForNextSentencePrediction.from_pretrained(
                    'dbmdz/bert-base-multilingual-cased').to(device)
            model2 = BertModel.from_pretrained(
                    'dbmdz/bert-base-multilingual-cased').to(device)


        idx = 0
        for text in texts:

            l_sentences = []

            tokenized_text = [tokenizer.encode(s, add_special_tokens=True) for s in text]

            max_len = 0
            for i in tokenized_text:
                if len(i) > max_len:
                    max_len = len(i)

            padded = np.array([i + [0]*(max_len-len(i)) for i in tokenized_text])
            cos_enc = np.average(cosine_similarity(padded)[0])
            ## bstsim
            scores[idx, 0] = cos_enc

            attention_mask = np.where(padded != 0, 1, 0)
            input_ids = torch.tensor(padded).to(device)
            attention_mask = torch.tensor(attention_mask).to(device)

            with torch.no_grad():
                last_hidden_states = model2(input_ids, attention_mask=attention_mask)


            cls_embeddings = last_hidden_states[0][:, 0, :].detach().cpu().numpy()
            df = pd.DataFrame(cls_embeddings)
            dfcor = df.T.corr(method=dcor.distance_correlation)
            clusterer = hdbscan.HDBSCAN(metric='precomputed', min_cluster_size=2)
            
            
            ## bsesim
            cos_emb = np.average(cosine_similarity(cls_embeddings)[0])
            scores[idx, 1] = cos_emb

            ## in case of only 1 noise cluster, to neglect the differences due to 
            ## the number of sentences substract (-1) the noise cluster
            try:
                clusterer.fit(dfcor)
                bsacn = (len(np.unique(clusterer.labels_)) - 1)/len(tokenized_text)
            except ValueError:
                bsacn = np.nan

            scores[idx, 2] = bsacn
            
            examples = self._create_examples(text)
            for e in examples:
                prompt = e[0]
                next_sentence = e[1]
                
                encoding = tokenizer.encode_plus(prompt, next_sentence, 
                        return_tensors='pt').to(device)

                with torch.no_grad():
                    logits = model(**encoding)[0]
               
                l_sentences.append(logits[0,0])
                
            p_sentences = []
            ls = []
            for l in l_sentences:
                p_sentences.append(l2p(l.item()))
                ls.append(l.item())

            ps = np.array(p_sentences)
            ls = np.array(ls)
            
            ## entropy
            scores[idx, 3] = entropy(ps, base=2)
            try:
                scores[idx, 4] = entropy(softmax(ls), base=2)
            except ValueError:
                pass

            ## bsrac
            scores[idx, 5] = self._radc(ps)

            ## bsradc
            scores[idx, 6] = self._rac(ps)
            

            lps = len(ps)
            
            ## bert autocorrelation
            try:
                res = acf(ps, fft=True, nlags=n_lags)[1:]
                scores[idx, 7: 7 + len(res)] = res
            except Exception as e:
                print(e)
            
            try:
                res = self._adc(ps, n_lags)[1:]
                scores[idx, 7 + n_lags: 7 + n_lags + len(res)] = res
            except Exception as e:
                print(e)
                sys.exit(-1)

            idx += 1
                
        return (scores, names)
 
    def _create_examples(self, lines):
        """Creates examples for the training and dev sets."""
        examples = []
        for (i, line) in enumerate(lines):
            if i == len(lines) - 1:
                break
            text_a = lines[i]
            text_b = lines[i+1]
            examples.append((text_a, text_b))
        return examples

class AutoBERTT(TextScore):
    def id(self):
        return "AutoBERTT"

    def name(self):
        return "Autocorrelation coefficients based on BERT token prediction probabilities."
    
    def _adc(self, x, t=1):
        return np.array([1] + [dcor.distance_correlation(x[:-i], x[i:])
            for i in range(1, t + 1)])
    
    def _rac(self, x):
        while len(x) >= 2:
            if len(x) == 2:
                return x[1]
            if len(x) > 2:
                x = acf(x, len(x), fft=True)[:-1]

    def _radc(self, x):
        while len(x) >= 3:
            if len(x) == 3:
                return x[1]
            if len(x) > 3:
                x = self._adc(x, len(x))[:-2]
                #x = x[x!=0]

    def _autodtw(self, x, t=1):
        x = np.array(x)
        return dtw(x[:-t], x[t:])

    def _save(self, scores, hash_hex, path):
        """Save scores to disk."""
        if not os.path.exists(path):   
             os.makedirs(path)

        path = os.path.join(path, hash_hex)
        f = open(path, 'w')
        json.dump(scores, f)
        f.close()


    def score(self, text, hash_hex, text_id, label, lang, n_lags=10):
        texts = [text]
        
        names_btac = ['btac' + str(i+1) for i in range(n_lags)]
        names_btadc = ['btadc' + str(i+1) for i in range(n_lags)]

        #names = names_tpad + names_tpdc + ['aatdw', 'taa', 'tddc', 'abtly']
        names = ['btH', 'btlH', 'btsH', 'btlsH', 'bth', 'btdfa', 'btly', 'btrac', 
                'btradc'] + names_btac + names_btadc
 

        ## create scores array
        scores = np.full((len(texts), 9 + n_lags * 2), np.nan)
        
        p_tokens = []

        device_num = get_gpu()
        if device_num == -1:
            device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        else:
            device = torch.device(f'cuda:{device_num}' if torch.cuda.is_available() else 'cpu')

        ts = self._create_examples(texts)
        if lang == 'de':
            # eval() by default
            tokenizer = BertTokenizer.from_pretrained(
                    'dbmdz/bert-base-german-cased')
            model = BertForMaskedLM.from_pretrained(
                    'dbmdz/bert-base-german-cased').to(device)
        elif lang == 'en':
            tokenizer = BertTokenizer.from_pretrained(
                    'bert-base-cased')
            model = BertForMaskedLM.from_pretrained(
                    'bert-base-cased').to(device)
        elif lang == 'ja':
            tokenizer = BertTokenizer.from_pretrained(
                    'cl-tohoku/bert-base-japanese-whole-word-masking')
            model = BertForMaskedLM.from_pretrained(
                    'cl-tohoku/bert-base-japanese-whole-word-masking').to(device)

        else:
            tokenizer = BertTokenizer.from_pretrained(
                    'bert-base-multilingual-cased')
            model = BertForMaskedLM.from_pretrained(
                    'bert-base-multilingual-cased').eval().to(device)

        def run(tokens_tensor, segments_tensors, masked_token):
            with torch.no_grad():
                outputs = model(tokens_tensor, token_type_ids=segments_tensors)
                predictions = outputs[0][0]
                return predictions[0, masked_token]

        idx = 0
        for text in ts:
            tokenized_text = tokenizer.tokenize(' '.join(text))
            tokenized_text_ids = tokenizer.convert_tokens_to_ids(tokenized_text)
            
            logits = []
            ps = []
            
            masked_token = tokenized_text[0]
            for i in range(1, len(tokenized_text) - 1):
                tokenized_text[i-1] = masked_token
                masked_token = tokenized_text[i]
                tokenized_text[i] = '[MASK]'
                indexed_tokens = tokenizer.convert_tokens_to_ids(tokenized_text)
                segments_ids = [0 for i in range(len(tokenized_text))]
                tokens_tensor = torch.tensor([indexed_tokens])
                segments_tensors = torch.tensor([segments_ids])
                tokens_tensor = tokens_tensor.to(device)
                segments_tensors = segments_tensors.to(device)
                t_ids = tokenizer.convert_tokens_to_ids([masked_token])
                logits.append(run(tokens_tensor, segments_tensors, t_ids))
            
            ls = []
            for l in logits:
                l_ = l.item()
                ls.append(l_)
                ps.append(l2p(l_))

            ps = np.array(ps)
            ls = np.array(ls)

            lps = len(ps)
            ## entropy
            scores[idx, 0] = entropy(ps, base=2)
            scores[idx, 1] = entropy(softmax(ls), base=2)
            ## sample entropy
            try:
                scores[idx, 2] = nolds.sampen(ps)
            except ValueError:
                pass
            try:
                scores[idx, 3] = nolds.sampen(softmax(ls))
            except ValueError:
                pass

            ## hurst
            try:
                scores[idx, 4] = nolds.hurst_rs(ps)
            except ValueError:
                pass
            ## dfa
            try:
                scores[idx, 5] = nolds.dfa(ps)
            except ValueError:
                pass
            ## abtly largest Lyapunov exponent
            try:
                scores[idx, 6] = nolds.lyap_r(ps)
            except Exception as e:
                pass

            ## recursive auto  corr
            scores[idx, 7] = self._rac(ps)

            ## recursive auto dist corr
            scores[idx, 8] = self._radc(ps)

            ## bert autocorrelation
            try:
                res = acf(ps, fft=True, nlags=n_lags)[1:]
                ## btac
                scores[idx, 9: 9 + len(res)] = res
            except Exception as e:
                print(e)
            
            try:
                res = self._adc(ps, n_lags)[1:]
                ##abtadc
                scores[idx, 9 + n_lags: 9 + n_lags + len(res)] = res
            except Exception as e:
                print(e)
                sys.exit(-1)

            idx += 1

        for i in range(len(scores)):
            if scores[i].all() == 0:
                continue
            results = OrderedDict({
                    names[j]: {
                        'id': names[j],
                        'score': scores[i, j]
                    }
                for j in range(len(scores[i]))
                })

        return (scores, names)
    
    def _create_examples(self, texts):
        """Creates examples for the training and dev sets."""
        texts_out = []
        for t in texts:
            tmp_t = []
            for s in t:
                tmp_t.append(s)
                tmp_t.append('[SEP]')
            texts_out.append(['[CLS]'] + tmp_t)

        return texts_out
    
    

class CurveLength(TextScore):
    def id(self):
        return "L"

    def name(self):
        return "Curve Length"

    def score(self, text: Text):
        l_score = (np.diff(text.token_frequencies) ** 2 + 1) ** 0.5
        return np.sum(l_score)


class DPD(TextScore):
    def id(self):
        return "dpd"

    def name(self):
        return "Number of Determiners per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'DET']
        return len(pro)


class Entropy(TextScore):
    def id(self):
        return "H"

    def name(self):
        return "Entropy"

    def score(self, text: Text):
        n = len(text.token)
        h = np.log2(n) - ((1 / n) * np.sum(f * np.log2(f) for f in text.token_frequencies))
        return h


class Gini(TextScore):
    def id(self):
        return "G"

    def name(self):
        return "Gini Coefficient"

    def score(self, text: Text):
        m_score = 0
        for i in range(len(text.token_frequencies)):
            m_score = m_score + (i + 1) * text.token_frequencies[i]
        m_score = m_score / len(text.token)
        n = len(np.unique(text.token))

        return (1 / n) * (n + 1 - 2 * m_score)


class HL(TextScore):
    def id(self):
        return "hl"

    def name(self):
        return "Hapax Legomenon Percentage"

    def score(self, text: Text):
        n = len(text.token)

        tokens, frequencies = np.unique(text.token, return_counts=True)

        return len(frequencies[np.where(frequencies == 1)]) / n


class HPoint(TextScore):
    def id(self):
        return "h"

    def name(self):
        return "h-point"

    def score(self, text: Text):
        #point, where r > f
        bp = 0
        # there is a r == f(r)
        for ri, f in enumerate(text.token_frequencies):
            r = ri + 1
            # r starts at 0
            if r == f:
                return r
            if r > f:
                bp = r
                break


        f1 = text.token_frequencies[bp - 1 - 1]
        f2 = text.token_frequencies[bp - 1]
        try:
            h = (f1*bp - f2*(bp-1)) / (bp - (bp - 1) + f1 - f2)
        except ZeroDivisionError:
            h = 0

        return h


class IPD(TextScore):
    def id(self):
        return "ipd"

    def name(self):
        return "Number of Interjections per Document"

    def score(self, text: Text):
        res = [pos for pos in text.pos if pos == 'INTJ']

        return len(res)


class NPD(TextScore):
    def id(self):
        return "npd"

    def name(self):
        return "Number of Nouns per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'NOUN']
        return len(pro)

class Lambda(TextScore):
    def id(self):
        return "lmbd"

    def name(self):
        return "Lambda"

    def score(self, text: Text):
        l_score = CurveLength().score(text)
        lambda_score = l_score * math.log10(len(text.token)) / len(text.token)
        return lambda_score


class lmbd(TextScore):
    def id(self):
        return "lmbd"

    def name(self):
        return "Lambda"

    def score(self, text: Text):
        n = len(text.token)
        l = (np.diff(text.token_frequencies) ** 2 + 1) ** 0.5

        return np.sum(l) * np.log10(n) / n


class NDW(TextScore):
    def id(self):
        return "NDW"

    def name(self):
        return "Number of difficult words (more than 8 letters)"

    def score(self, text: Text):
        dw = [t for t in text.token if len(t) > 8]

        return len(dw)


class PPD(TextScore):
    def id(self):
        return "ppd"

    def name(self):
        return "Number of Pronouns per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'PRON']
        return len(pro)


class PREPPD(TextScore):
    def id(self):
        return "preppd"

    def name(self):
        return "Number of Prepositions per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'ADP']
        return len(pro)


class Q(TextScore):
    def id(self):
        return "Q"

    def name(self):
        return "Activity"

    def score(self, text: Text):
        pos = text.pos
        if len(pos) == 0:
            return 0
        p = zip(text.token, text.pos)
        v = len([p for p in text.pos if p == 'VERB'])
        a = len([p for p in text.pos if p == 'ADJ'])
        try:
            return v / (v + a)
        except:
            return 0


class R1(TextScore):
    def id(self):
        return "R1"

    def name(self):
        return "Vocabulary Richness"

    def score(self, text: Text):
        n = len(text.token)
        #h = math.floor(HPoint().score(text))
        h = HPoint().score(text)
        fh = 0
        tokens, frequencies = np.unique(text.token, return_counts=True)
        frequencies[::-1].sort()

        if len(frequencies) < h + 2:
            return 0
        
        for i in range(math.floor(h)):
            fh += frequencies[i]

        #h = frequencies[h + 1]
        #fh += h
        return 1 - (fh / n - h ** 2 / 2 / n)


class RR(TextScore):
    def id(self):
        return "RR"

    def name(self):
        return "Repeat Rate"

    def score(self, text: Text):
        n = len(text.token)
        tokens, frequencies = np.unique(text.token, return_counts=True)

        return np.sum(frequencies ** 2) / n ** 2


class RRR(TextScore):
    def id(self):
        return "RRR"

    def name(self):
        return "Relative Repeat Rate"

    def score(self, text: Text):
        rr = RR().score(text)
        n = len(np.unique(text.token))
        return (1 - rr ** 0.5)/(1 - n ** -0.5)


class STC(TextScore):
    def id(self):
        return "stc"

    def name(self):
        return "Secondary Thematic Concentration"

    def score(self, text: Text):
        thematic_ws = []

        h = HPoint().score(text)
        h_floor = math.floor(h*2)

        if h_floor == 0:
            return 0

        df = pd.DataFrame(list(l) for l in zip(np.arange(1, 
                len(text.token_frequencies[:h_floor])+1), text.token_frequencies))
        ll = list(list(l) for l in zip(np.arange(1, len(text.token_frequencies[:h_floor])+1), text.token_frequencies))
        df = df.groupby(1).mean().sort_index(ascending=False)
        

        r = 0
        for i in text.token_unique_indices[:h_floor]:
            try:
                f = text.token_frequencies[r]
                if text.pos[i] in ['NOUN', 'VERB', 'ADJ']:
                    rank = r + 1
                    for j, row in df.iterrows():
                        if f == j:
                            rank = row[0]
                        
                    thematic_ws.append((rank, text.token_frequencies[r]))
            except IndexError:
                return 0
            r += 1

        if len(thematic_ws) == 0:
            return 0

        stc = 0 
        for r, f in thematic_ws:
            stc += (2*h - r) * f / (h* (2*h-1)*text.token_frequencies[0])
            
        return stc


##helpers for Syn
def gini(x):
    """Calculate the Gini coefficient of a numpy array."""
    # based on bottom eq: http://www.statsdirect.com/help/content/image/stat0206_wmf.gif
    # from: http://www.statsdirect.com/help/default.htm#nonparametric_methods/gini.htm
    x = np.array(x, dtype='float64')
    x = x.flatten() #all values are treated equally, arrays must be 1d
    if np.amin(x) < 0:
        x -= np.amin(x) #values cannot be negative
    x += 0.0000001 #values cannot be 0
    x = np.sort(x) #values must be sorted
    index = np.arange(1,x.shape[0]+1) #index per array element
    n = x.shape[0]#number of array elements
    res = ((np.sum((2 * index - n  - 1) * x)) / (n * np.sum(x)))

    return res

def rac(x):
    while len(x) >= 2:
        if len(x) == 2:
            return x[1]
        if len(x) > 2:
            x = acf(x, len(x), fft=True)[:-1]
def __adc(x, t=1):
    return np.array([1] + [dcor.distance_correlation(x[:-i], x[i:]) for i in range(1, t+1)])
  
def _adc(x, t=1):
    #return np.array([1] + [dcor.distance_correlation(x[:-i], x[i:]) for i in range(1, t+1)])
    res = []
    for i in range(1, t + 1):
        if len(x[:-i]) <= 1:
            break
        res += [dcor.distance_correlation(x[:-i], x[i:])]
    return res


def radc(x):
    while len(x) >= 3:
        if len(x) == 3:
            return x[1]
        if len(x) > 3:
            x = __adc(x, len(x))[:-2]
            #x = x[x!=0]

def autodcorr(x, t=1, e=1):
    x = np.array(x)
    return dcor.distance_correlation(x[:-t], x[t:], exponent=e)

def autoadcorr(x, t=1):
    x = np.array(x)
    return dcor.distance_correlation_af_inv(x[:-t], x[t:])

def autodtw(x, t=1):
    x = np.array(x)
    return dtw(x[:-t], x[t:])

def create_graphs(doc):
    Gs = []
    gls = []
    rs = []

    for sent in doc.sents:
        labels = {}
        edges = []
        nodes = []
        r = None
        for token in sent:
            #if token.pos_ == 'PUNCT':
            #    continue
            if not (token.dep_ == 'ROOT' and token.pos_ == 'PUNCT'):
                if token.pos_ != 'SPACE':
                    nodes.append(token.i)
                    labels[token.i] = token.text

            if token.dep_ == 'ROOT' and token.pos_ != 'PUNCT':
                r = token.i
            for child in token.children:
                if child.pos_ != 'SPACE': #and child.pos_ != 'PUNCT':
                    edges.append((token.i, child.i))
                    current_token = child.i

        G = nx.DiGraph(edges)
        for node in nodes:
            if not G.has_node(node):
                G.add_node(node)


        if G.number_of_nodes() > 0 and not r is None:
            gls.append(labels)
            Gs.append(G)
            rs.append(r)
    return Gs, rs, gls

def tree_height(G, root):
    sps = nx.shortest_path_length(G, root)
    max_l = max(sps.values())

    return max_l
    


def calculate(Gs, rs, gls):
    ###############################################################################
    ## LDE
    gleafs = []
    for G in Gs:
        leafs = [x for x in G.nodes() if G.out_degree(x)==0 and G.in_degree(x)==1]
        gleafs.append(leafs)

    n_leafs = []
    lsps = []
    sps = []
    for leafs in gleafs:
        n_leafs.append(len(leafs))

    for r, G, leafs in zip(rs, Gs, gleafs):
        sp = nx.shortest_path_length(G, r)
        sps.append(sp)

        #lsp = [sp[x] for x in leafs]
        lsp = []
        for x in leafs:
            try:
                lsp.append(sp[x])
            except KeyError:
                pass

        lsps.append(lsp)
    LDEs = []
    for lsp, n_leaf in zip(lsps, n_leafs):
        L = {}
        for l in lsp:
            try:
                d = L[l]
                L[l] = L[l] + 1
            except KeyError:
                L[l] = 1
        LDE = 0
        n_sets = len(L)
        if n_sets > 1:
            for l in L.values():
                p = l/n_leaf
                LDE += p * np.log2(p)
            LDE = -1 * LDE / np.log2(n_sets)

        LDEs.append(LDE)
    ###############################################################################
    ## depend
    depends = []
    MDDs = []
    DDEs = []
    TCIs = []
    imbalances = []
    L_hats = []
    comps = []
    W_hats = []
    widths = []
    levels = []
    
    for r, G, sp, l in zip(rs, Gs, sps, gls):
        res = 0
        h = tree_height(G, r)
        for i in range(1, h+2):
            path = nx.single_source_shortest_path_length(G, r, cutoff=i-1)
            vs = len(np.argwhere(np.fromiter(path.values(), dtype=int) == i-1))
            res += i * vs
        depends.append(2 * res / G.number_of_nodes() / (G.number_of_nodes() + 1))
    ###############################################################################
    ## MDD & DDE
        n_e = nx.number_of_edges(G)
        DDs = dict(zip(l.keys(), range(len(l.keys()))))



        DD = 0
        for node in G.nodes():
            pred = list(G.predecessors(node))
            if len(pred) > 0:
                try:
                    DD += abs(DDs[node] - DDs[pred[0]])
                except KeyError:

                    pass
        try:
            MDD = DD / (G.number_of_nodes() - 1)
        except:
            MDD = 0

        MDDs.append(MDD)
        asp = dict(nx.all_pairs_shortest_path_length(G))

        D = {}
        for sp in asp.values():
            for p in sp.values():
                if p > 0:
                    try:
                        d = D[p]
                        D[p] = D[p] + 1
                    except KeyError:
                        D[p] = 1
        len_D = len(D)
        DDE = 0
        if len_D > 1:
            for d in D.values():
                p = d / n_e
                DDE += p * np.log2(p)
            DDE = -1 * DDE / np.log2(len_D)
        DDEs.append(DDE)
    ###############################################################################
    ## TCI & imbalance & length analysis

    ###############################################################################
    ## width, level, W_hat Formulas (23, (24), and (25)

    
        # all leafs of a graph
        leafs = [x for x in G.nodes() if G.out_degree(x)==0 and G.in_degree(x)==1]
        # number of nodes with degree 1 (see paper Formula (16))
        l = len([1 for i in G.nodes if G.out_degree[i] == 1]) + len(leafs)
        TCI = 0
        imbalance = 0 #Formula (17)
        if l > 3:
            d_lcp = 0
            # all pairs of leafs
            for n1, n2 in itertools.combinations(leafs, 2):
                # 
                lcp = nx.lowest_common_ancestor(G, n1, n2)
                
                if r == lcp:
                    d_lcp = 0
                else:
                    try:
                        d_lcp = nx.shortest_path_length(G, r, target=lcp)
                    except nx.exception.NetworkXNoPath:
                        pass
            try:
                TCI += d_lcp
            except TypeError:
                print(f'{n1}, {n2}, common ancestor: {lcp}, depth: {d_lcp}')
            imbalance = TCI/binom(l, 3)
        L_hats.append(len(leafs)/G.number_of_nodes())
        TCIs.append(TCI)
        imbalances.append(imbalance)

        n_n = G.number_of_nodes()
        max_n = 0
        max_l = 0
        bfs = dict(nx.bfs_successors(G, source=r))
        for i, level in enumerate(bfs.values()):
            if i == 0:
                comps.append(len(level)/n_n)

            current = len(level)
            if  current > max_n:
                max_n = current
                max_l = i + 1
        widths.append(max_n)
        levels.append(max_l)
        W_hats.append(max_n/G.number_of_nodes())
        
    return LDEs, depends, MDDs, DDEs, TCIs, imbalances, L_hats, W_hats, widths, levels, comps


class Syn(TextScore):
    def id(self):
        return "syn"

    def name(self):
        return "Syntactic Features"

    def score(self, doc, text_id):
        Gs, rs, gls = create_graphs(doc)
        LDEs, depends, MDDs, DDEs, TCIs, imbalances, L_hats, W_hats, widths, levels, comps = \
                calculate(Gs, rs, gls)

        df = pd.DataFrame(list(zip(LDEs, depends, MDDs, DDEs, TCIs, 
            imbalances, L_hats, W_hats, widths, levels, comps)), columns=['LDEs', 
                'depends', 'MDDs', 'DDEs', 'TCIs', 'imbalances', 'L_hats', 'W_hats',
                'widths', 'levels', 'comps'])
        mu = df.apply(lambda x: np.mean(x))
        G = df.apply(lambda x: gini(x))
        #H = df.apply(lambda x: entropy(x, base=2))
        H = df.apply(lambda x: entropy(softmax(x), base=2))
        rc = df.apply(lambda x: rac(x))
        try:
            adc = df.apply(lambda x: _adc(x))
        except ValueError:
            adc = np.full((11, ), np.nan)
        rdc = df.apply(lambda x: radc(x))
        try:
            adtw = df.apply(lambda x: autodtw(x))
        except Exception as e:
            adtw = np.full((11, ), np.nan)
 
        scores = np.full((77, ), np.nan)
        scores[0:len(mu)] = mu
        scores[11:11+len(G)] = G
        scores[22:22+len(H)] = H
        scores[33:33+len(rc)] = rc
        try:
            scores[44:44+len(adc)] = adc
        except ValueError:
            # Something Happend
            pass
        scores[55:55+len(rdc)] = rdc
        scores[66:66+len(adtw)] = adtw

        names = ['LDEmu', 'depmu', 'MDDmu', 'DDEmu', 'TCImu', 'imbmu', 
                'Lmu','Wmu', 'wmu', 'lmu', 'cmu',

                'LDEG', 'depG', 'MDDG', 'DDEG', 'TCIG', 'imbG',
                'LG', 'WG', 'wG', 'lG', 'cG',

                'LDEH', 'depH', 'MDDH', 'DDEH', 'TCIH', 
                'imbH', 'LH', 'WH', 'wH', 
                'lH', 'cH',
        
                'LDErac', 'deprac', 'MDDrac', 'DDErac', 'TCIrac', 'imbrac',
                'Lrac','Wrac', 'wrac', 'lrac', 'crac',

                'LDEadc', 'depadc', 'MDDadc', 'DDEadc', 'TCIadc', 'imbadc',
                'Ladc','Wadc', 'wadc', 'ladc', 'cadc',
                
                'LDEradc', 'depradc', 'MDDradc', 'DDEradc', 'TCIradc', 'imbradc',
                'Lradc','Wradc', 'wradc', 'lradc', 'cradc',

                'LDEadtw', 'depadtw', 'MDDadtw', 'DDEadtw', 'TCIadtw', 
                'imbadtw', 'Ladtw', 'Wadtw', 'wadtw', 
                'ladtw', 'cadtw',
                
                ]

        return scores, names


class TC(TextScore):
    def id(self):
        return "tc"

    def name(self):
        return "Thematic Concentration"

    def score(self, text: Text):
        thematic_ws = []

        h = HPoint().score(text)
        h_floor = math.floor(h)
        
        df = pd.DataFrame(list(l) for l in zip(np.arange(1, 
                len(text.token_frequencies[:h_floor])+1), text.token_frequencies))
        df = df.groupby(1).mean().sort_index(ascending=False)

        r = 0
        for i in text.token_unique_indices[:h_floor]:
            try:
                f = text.token_frequencies[r]
                if text.pos[i] in ['NOUN', 'VERB', 'ADJ']:
                    rank = r + 1
                    for j, row in df.iterrows():
                        if f == j:
                            rank = row[0]
                        
                        
                    thematic_ws.append((rank, text.token_frequencies[r]))
            except IndexError:
                 return 0
               
            r += 1

        if len(thematic_ws) == 0:
            return 0

        tc = 0 
        for r, f in thematic_ws:
            tc += 2*(h - r) * f / (h* (h-1)*text.token_frequencies[0])
            
        return tc


class TypeTokenRatio(TextScore):
    def id(self):
        return "ttr"

    def name(self):
        return "Type Token Ratio"

    def score(self, text: Text):
        return len(text.token_unique) / len(text.token)


class uniquegrams(TextScore):
    def id(self):
        return "UG"

    def name(self):
        return "Unique Grams"

    def score(self, text: Text):
        length = len(text.token)
        grams = list()

        for token in text.token_unique:
            grams += self.get_gram(token)
        
        return len(set(grams)) / length

    def get_gram(self, tokens):
        grams = list()

        if len(tokens) < 3:
            return grams

        gram_generator = ngrams(tokens, 3)

        for gram in gram_generator:
            grams.append(gram)

        return grams


class VD(TextScore):
    def id(self):
        return "VD"

    def name(self):
        return "Verb Distances"

    def score(self, text: Text):
        junk = ['PUNCT', 'SPACE']
        pos = [str(w) for w in text.pos if w not in junk]
        if len(pos) == 0:
            return 0
        vi = []
        for i in range(len(pos)):
            if pos[i] == 'VERB':
                vi.append(i)
        diff = [x - vi[i - 1] for i, x in enumerate(vi)][1:]

        if len(diff) == 0:
            return 0
        
        try:
            return np.average(diff)
        except FloatingPointError:
            return 0


class VPD(TextScore):
    def id(self):
        return "vpd"

    def name(self):
        return "Number of Verbs per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'VERB']
        return len(pro)

#############################################################################################
## Misc
def hashify(text):
        """Calculate hash hex string."""
        hash_object = hashlib.sha1(str.encode(text))
        hash_hex = hash_object.hexdigest()
        return hash_hex

def lt(text, lang, scorers, mode):
    """
    Load texts and calculate scores async
    """
    text_id = hashify(text)
    t = TextScorer(lang, text, text_id, scorers=scorers, load=False, load_text=False,
            mode=mode)

    return t.calculate(), t.text.length, t.text.hash_hex
    
def lt_bert(text, lang, scorers, mode):
    """
    Load and split texts for autobert
    """

    text_id = hashify(text)
    ## TODO text_id?
    hash_hex = text_id
    sentences = TextScorer(lang, text, text_id, scorers=scorers, 
            load_text=True, mode=mode).text.sentences

    return (sentences, hash_hex, text_id)

def lt_syn(text, lang, mode):
    """
    Load and split texts for autobert
    """

    text_id = hashify(text)
    hash_hex = hashify(text)
    ts = TextScorer(lang, text, text_id, mode=mode)
    scores, names = Syn().score(ts.text.doc, text_id)

    return (scores, names, hash_hex, text_id)

def stats(in_file, lang, mode):
    """
    Load and split texts for autobert
    """

    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = f.read()
        hash_hex = hashify(t)
        text = TextScorer(lang, t, text_id, scorers=None, 
                load_text=True, mode=mode).text
        n_tok = len(text.token)
        tok_l = np.average([len(list(token)) for token in text.token])
        sent_l = np.sum(text.l_sentences)/text.n_sentences

    return n_tok, sent_l, tok_l


#############################################################################################
## Scorer
class Scorer():
    """
    Scorer class using multiple precessors
    
    ...
    Attributtes
    -----------
    in_lab_dir : [(str, [str])]
        List of tuples containing corresponding label, language, and list of directories.
    out_dir : str, optional
        Directory for saved scores (default is ''), if the default value isn't
        changed, the scores will be saved in data/scores.
    scorers : [scorer] or [str]
            List of scorer objects or list of keywords (e.g. all_but_bert, autobert)

    scores : [str]
        Calculated scores
    labels : [str]
        Class labels
    names : [str]
        names
    lengths : [int]
        lengths
    """

    def __init__(self, scorers, out_dir='', out_file=None, tfidf=False, tfidf_max=10,
            bertt=False, berts=False, bert_n_lags=5, 
            bert_load=True, syn=False, stats=False, mode=None, cpus=None, text=None, label=None, language=None):
        """
        Parameters
        ----------
        in_lab_dir : [(str, str, [str])]
            List of tuples containing corresponding label, language, and list of directories.
        scorers : [scorer]
            List of scorer objects
        tfidf : True/False
            Calculate tfidf features (Yes/No)?
        tfidf_max : Int
            Maximum numer of tfidf features
        bertt : True/False
            Calculate BERT features based on token probabilities (Yes/No)?
        berts : Bool
            Calculate BERT features based on sentence probabilities (Yes/No)?
        bert_load : Bool
            Load BERT features (Yes/No)?
        syn : Bool
            Calculate syntactic features (Yes/No)?
        she : Bool
            Calculate features from Mehler et al. (2017) (Yes/No)?
        out_dir : str, optional
            Directory for saved scores (default is ''), if the default value isn't
            changed, the scores will be saved in data/scores.
        """
        #self.id = 'scorer'
        #self.scorers = scorers
        self.mode = mode
        self.cpus = cpus
        self.out_dir = './'#os.path.join('data/scores/cumulative', out_dir)
        self.out_file = out_file
        
        self.scores = None
        self.labels = None
        self.names = None
        self.names_tfidf = None
        self.text_ids = None
        self.text_hash = None
        self.text_ids_ext = None

        self.bertt = False
        self.berts = False
        self.bert_load = bert_load
        self.bert_n_lags = bert_n_lags
        self.syn = False
        self.stats = stats

        self.text = text
        self.label = label
        self.language = language

        self.ling = False

        self.scorers = []
        for s in scorers:
            if isinstance(s, Syn):
                self.syn = True
            elif isinstance(s, AutoBERTT):
                self.bertt = True
            elif isinstance(s, AutoBERTS):
                self.berts = True
            else:
                self.scorers.append(s)
        if not scorers is None and len(scorers) != 0:
            self.ling = True

    def _read(self, in_dir):
        files =  [item for sublist in [[os.path.join(d,f) for f in listdir(d)
                                        if os.path.isfile(os.path.join(d, f))] 
                                        for d in in_dir] for item in sublist]
        texts = []
        for f in files:
            with open(f, 'r') as tf:
                text = ' '.join(tf.read().splitlines())
                texts.append(text)
        
        return texts

    def run(self, lang, label, text):
        """
        Wrapper function to iterate through input dirs
        """
        
        scores, names, text_hash = self._calculate(lang, label, text)
        self.scores = np.asarray(scores)
        self.text_hash = np.asarray(text_hash)
        self.names = names

        
        return scores, names, text_hash


    def _calculate(self, lang, label, text):
        """
        Calculate indices.
        """
        scores = []
        names = []
        hash_list = []

        #Calculate scores using pool 
        if self.ling:
            #texts = [pool.apply_async(lt, [f, lang, self.scorers, self.mode]) 
            #        for f in files]
            #results = [e.get() for e in texts]
            stats, num_tokens, text_hash = lt(text, lang, self.scorers, self.mode)
            #Do not use multiprocessing for BERT, as it fully utilizes the GPU with one process
            scores = [stat["score"] for stat in stats.values()]
            names = [stat["id"] for stat in stats.values()]

        if self.syn:
            scores_syn, names_syn, text_hash, _ = lt_syn(text, lang, self.mode)
            if len(scores) == 0:
                scores = scores_syn
            else:
                scores = np.concatenate((np.array(scores), scores_syn))

            names += names_syn
        
        #calculate bert scores only if bert=True
        if self.bertt:
            sentences, hash_hex, text_id = lt_bert(text, lang, self.scorers, self.mode)
            ab = AutoBERTT()
            scores_bertt, names_bertt = ab.score(sentences, hash_hex, text_id, label, lang)
            if len(scores) == 0:
                scores = scores_bertt
            else:
                scores = np.concatenate((np.array(scores), scores_bertt.squeeze()))

            names += names_bertt

        #calculate bert scores only if bert=True
        if self.berts:
            ab = AutoBERTS()
            ## reuse prepared texts
            if self.bertt and len(scores) != 0:
                scores_berts, names_berts = ab.score(sentences, hash_hex, text_id, label, lang)
                scores = np.concatenate((np.array(scores), scores_berts.squeeze()))
                names += names_berts

            else:
                sentences, hash_hex, _ = lt_bert(text, lang, self.scorers, self.mode)
                scores_berts, names_berts = ab.score(sentences, label, lang, n_lags=self.bert_n_lags)
                if len(scores) == 0:
                    scores = scores_berts
                else:
                    scores = np.concatenate((np.array(scores), scores_berts))
                    
                names += names_berts

        scores = list(map(float, scores))
                
        return scores, names, text_hash 

