#import csv
#import glob
import hashlib
#import json
import numpy as np
import os
#import pandas as pd
import re
import spacy
import string
import sys
#import time
import torch

from collections import OrderedDict
from math import ceil
#from multiprocessing import Pool, cpu_count
#from os import listdir, path
#from sklearn.feature_extraction.text import TfidfVectorizer
#from textscorer.textscorer import TextScorer
#from textscorer.scorers import autobertt
#from textscorer.scorers import autoberts
#from textscorer.scorers import she
#from textscorer.scorers import syn
#from tqdm import tqdm
#from utils import load_texts

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


class Text:
    def __init__(self, lang, text, text_id, load=False, mode=None):
        self.text_raw = text
        self.text = text
        self.text_id = text_id
        self.lang = lang
        self.mode = mode
        self.hash_hex = self._hashify()
        self.length = self._count_words()

        self.textimager_baseurl = "https://textimager.hucompute.org/rest/process"
        # TODO Fehler im TextImager, aktuell werden eigentlich nur Segmenter und Lemmatizer benötigt...
        self.pipelines = {
            "de": ["LanguageToolSegmenter", "StanfordPosTagger", "MarMoTLemma"],
            "en": ["StanfordSegmenter", "LanguageToolLemmatizer"]
        }

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
        #print(f'{bcolors.OKGREEN}text.q_p{bcolors.ENDC}')
            text = polyText(self.text, hint_language_code=self.lang)
            sentences = text.sentences
            self.n_sentences = len(sentences)
            for s in sentences:
                self.l_sentences.append(len(s.split(' ')))

            self.token = [x for x in list(text.words) if not self._is_control(x) 
                    and not self._is_punct(x)]
        except ValueError as e:
            print(e)
            #print(self.lang)
            #print(self.text)
        except Exception as e1:
            pass
            #print(self.lang, self.text)


    def _query_textimg(self):
        """Extract tokens and lemmas from TextImager"""
        for chunk in self.chunkify():
            payload = {
                "document": chunk,
                "language": self.lang,
                "outputFormat": "CONLLU",
                "pipeline": self.pipelines[self.lang]
            }

            r = requests.get(self.textimager_baseurl, params=payload)
            print(r.url)

            if r.status_code != requests.codes.ok:
                print("Error occured: ", r.status_code)
                sys.exit(1)

            for doc in r.json():
                for line in doc.split('\n'):
                    fields = line.strip().split('\t')
                    if len(fields) >= 2:
                        token = fields[1]
                        lemma = fields[2]
                        self.token.append(token)
                        self.lemma.append(lemma)

    def _count_words(self):
        """Calculate length (in words) of a text."""
        return len(self.text.split())

    def _chunkify(self):
        """Split text into chunks TextImager can handle"""
        # if the text is longer than 2500 chars, split it
        max_len = 2500
        
        sents = [w + '. ' for w in self.text.split('.') if len(w) != 0]
        chunk = ''
        chunks = []
        size = 0 
        for s in sents:
            if size + len(s) < max_len:
                chunk += s
                size += len(s)
            else: 
                chunk += s
                chunks.append(chunk)
                chunk = ''
                size = 0
        # letzten noch hinzufügen
        chunks.append(chunk)
        return chunks

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

# Baseclass for all Scorers
class TextScore:
    def id(self):
        pass

    def name(self):
        pass

    def score(self, text: Text):
        pass

#############################################################################################
## Scorers
class NPD(TextScore):
    def id(self):
        return "npd"

    def name(self):
        return "Number of Nouns per Document"

    def score(self, text: Text):
        pro = [pos for pos in text.pos if pos == 'NOUN']
        return len(pro)

#############################################################################################
## Misc
def hashify(text):
        """Calculate hash hex string."""
        hash_object = hashlib.sha1(str.encode(text))
        hash_hex = hash_object.hexdigest()
        return hash_hex

def lt_text(text, lang, scorers, mode):
    """
    Load texts and calculate scores async
    """
    text_id = hashify(text)
    t = TextScorer(lang, text, text_id, scorers=scorers, load=False, load_text=False,
            mode=mode)

    return t.calculate(), t.text.length, t.text.hash_hex
    
def lt(in_file, lang, scorers, mode):
    """
    Load texts and calculate scores async
    """
    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = TextScorer(lang, f.read(), text_id, scorers=scorers, load=False, load_text=False,
                mode=mode)
    return (t.calculate(), t.text.length, t.text.hash_hex)

def lt_bert(in_file, lang, scorers, mode):
    """
    Load and split texts for autobert
    """

    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = f.read()
        hash_hex = hashify(t)
        sentences = TextScorer(lang, t, text_id, scorers=scorers, 
                load_text=True, mode=mode).text.sentences
        #sentences = re.split('[?.!]', t)
    return (sentences, hash_hex, text_id)

def lt_syn(in_file, lang, mode):
    """
    Load and split texts for autobert
    """

    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = f.read()
        hash_hex = hashify(t)
        ts = TextScorer(lang, t, text_id, mode=mode)
        scores, names = syn.Syn().score(ts.text.doc, text_id)
        #sentences = re.split('[?.!]', t)
    return (scores, names, hash_hex, text_id)

def lt_she(in_file, lang, mode, corpus_tokens):
    """
    Load and split texts for autobert
    """

    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = f.read()
        hash_hex = hashify(t)
        ts = TextScorer(lang, t, text_id, mode=mode)
        scores, names = she.She().score(ts.text, text_id, corpus_tokens)
        #sentences = re.split('[?.!]', t)
    return (scores, names, hash_hex, text_id)

def lt_she_c(in_file, lang, mode):
    """
    Load and split texts for autobert
    """

    text_id = in_file.split('/')[-1]
    with open(in_file, "r", encoding="UTF-8") as f:
        t = f.read()
        ts = TextScorer(lang, t, text_id, mode=mode)
    return ts.text.token

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
            bert_load=True, syn=False, she=False, stats=False, mode=None, cpus=None, text=None, label=None, language=None):
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
        self.scorers = scorers
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

        self.tfidf = tfidf
        self.tfidf_max = tfidf_max
        self.bert = berts or bertt
        self.bertt = bertt
        self.berts = berts
        self.bert_load = bert_load
        self.bert_n_lags = bert_n_lags
        self.syn = syn
        self.she = she
        self.stats = stats

        self.text = text
        self.label = label
        self.language = language

        self.ling = False
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
        if self.scores is None:
            self.scores = np.asarray(scores)
            self.text_hash = np.asarray(text_hash)
            self.names = names
        else:
            #print(scores.shape, self.scores.shape)
            self.scores = np.concatenate((self.scores, scores), 0)
            self.text_hash = np.concatenate((self.text_hash, text_hash))

        
        return scores, names, text_hash


    def _calculate(self, lang, label, text):
        """
        Calculate indices.
        """
        scores = []
        names = []
        hash_list = []
        #use 80% of available processors
        #if self.cpus is None:
        #    cpus = ceil(cpu_count()*0.8)
        #elif isinstance(self.cpus, int):
        #    cpus = self.cpus
        #else:
        #    cpus = ceil(cpu_count()*self.cpus)
        ##calculate good scores
        #try:
        #    files =  [item for sublist in [[os.path.join(d,f) for f in listdir(d)
        #                                                if os.path.isfile(os.path.join(d, f))] 
        #                                for d in in_dir] for item in sublist]
        #except FileNotFoundError:
        #    print(f'{bcolors.FAIL}Folder, you specified, does not exist.{bcolors.ENDC}')
        #    sys.exit(-1)

        #if len(files) == 0:
        #    print(f'{bcolors.FAIL}No files found in {in_dir}{bcolors.ENDC} '
        #            f'Make sure, the folder is not empty, and try again.')
        #    sys.exit(-1)

        #pbar for tracking progress via callback of apply_async

        #if self.stats:
        #    result = [lt_text(text, lang, self.mode)
        #    
        #    an_tok = np.average(results[:, 0]).astype(int)
        #    asent_l = np.average(results[:, 1]).astype(int)
        #    atok_l = np.average(results[:, 2]).astype(int)
        #    print(an_tok, asent_l, atok_l)
                

        #Calculate scores using pool 
        if self.ling:
            #texts = [pool.apply_async(lt, [f, lang, self.scorers, self.mode]) 
            #        for f in files]
            #results = [e.get() for e in texts]
            stats, num_tokens, text_hash = lt_text(text, lang, self.scorers, self.mode)
            #Do not use multiprocessing for BERT, as it fully utilizes the GPU with one process
            scores = [stat["score"] for stat in stats.values()]
            names = [stat["id"] for stat in stats.values()]

        if self.syn:
            texts = [pool.apply_async(lt_syn, [f, lang, self.mode]) 
                    for f in files]
            pool.close()
            pool.join()
            pbar.close()
            results = [e.get() for e in texts]
            stats_list = [x for x, y, z, w in results]
            names_list = [y for x, y, z, w in results]
            hash_list = [z for x, y, z, w in results]
            scores_syn = [[stat for stat in stats] for stats in stats_list]
            names_syn = [name for name in names_list][-1]
            if len(scores) == 0:
                scores = scores_syn
            else:
                scores = np.concatenate((np.array(scores), scores_syn), axis=1)

            names += names_syn
        # Integrating Computational Linguistic Analyses of... Mehler et. al 2017
        if self.she:
            ## get all tokens of the corpus
            texts = [pool.apply_async(lt_she_c, [f, lang, self.mode]) 
                    for f in files]
            pool.close()
            pool.join()
            results = [e.get() for e in texts]
            corpus_tokens = []
            for r in results:
                corpus_tokens += list(r)

            pool = Pool(cpus)
            texts = [pool.apply_async(lt_she, [f, lang, self.mode, corpus_tokens], 
                callback=update) for f in files]
            pool.close()
            pool.join()
            pbar.close()
            results = [e.get() for e in texts]
            stats_list = [x for x, y, z, w in results]
            names_list = [y for x, y, z, w in results]
            hash_list = [z for x, y, z, w in results]
            scores_she = [[stat for stat in stats] for stats in stats_list]
            names_she = [name for name in names_list][-1]
            if len(scores) == 0:
                scores = scores_she
            else:
                scores = np.concatenate((np.array(scores), scores_she), axis=1)

            names += names_she

        
        #calculate bert scores only if bert=True
        if self.bertt:
            pbar = tqdm(total=len(files))
            texts = [pool.apply_async(lt_bert, [f, lang, self.scorers, self.mode], 
                callback=update) for f in files]
            pool.close()
            pool.join()
            pbar.close()

            results = [e.get() for e in texts]
            hash_list = [y for x, y, z  in results]
            ab = autobertt.AutoBERTT()
            scores_bertt, names_bertt = ab.score(results, label, lang, load=self.bert_load)
            if len(scores) == 0:
                scores = scores_bertt
            else:
                scores = np.concatenate((np.array(scores), scores_bertt), axis=1)

            names += names_bertt

        #calculate bert scores only if bert=True
        if self.berts:
            ab = autoberts.AutoBERTS()
            ## reuse prepared texts
            if self.bertt:
                scores_berts, names_berts = ab.score(results, label, lang,
                        load=self.bert_load)
                scores = np.concatenate((np.array(scores), scores_berts), axis=1)
                names += names_berts

            else:
                pool = Pool(cpus)
                pbar = tqdm(total=len(files))
                texts = [pool.apply_async(lt_bert, [f, lang, self.scorers, self.mode], 
                    callback=update) for f in files]
                pool.close()
                pool.join()
                pbar.close()

                results = [e.get() for e in texts]
                hash_list = [y for x, y, z  in results]
                scores_berts, names_berts = ab.score(results, label, lang, n_lags=self.bert_n_lags,
                        load=self.bert_load)
                if len(scores) == 0:
                    scores = scores_berts
                else:
                    scores = np.concatenate((np.array(scores), scores_berts), axis=1)
                    
                names += names_berts

        ## add bert scores
        #if not self.ling:
        #    if self.bertt and self.berts:
        #        scores = np.concatenate((scores_bertt, scores_berts), axis=1)
        #        names += names_bertt + names_berts
        #    elif self.bertt:
        #        scores = scores_bertt
        #        names += names_bertt
        #    elif self.berts:
        #        scores = scores_berts
        #        names += names_berts
                
        #scores = list(scores)

                
        return scores, names, text_hash 

