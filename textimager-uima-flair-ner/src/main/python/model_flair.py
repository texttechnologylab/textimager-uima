import logging
from pathlib import Path
from typing import List
from glob import glob

import torch
from flair.data import Sentence
from flair.datasets import SentenceDataset
from flair.models import SequenceTagger
from flair.tokenization import SegtokTokenizer

torch.device('cpu')
log = logging.getLogger("flair")


class BaseModel:
    def __init__(
            self,
            path: str,
            mini_batch_size=32,
            embedding_storage_mode: str = "none",
            verbose: bool = False,
    ):
        self.tagger: SequenceTagger = SequenceTagger.load(Path(path))
        log.info("loaded model")
        self.mini_batch_size = mini_batch_size
        self.embedding_storage_mode = embedding_storage_mode
        self.verbose = verbose

    def _predict(self, sentences):
        tokenizer = SegtokTokenizer()
        dataset = SentenceDataset([Sentence(text, tokenizer) for text in sentences])
        self.tagger.predict(
            dataset,
            mini_batch_size=self.mini_batch_size,
            embedding_storage_mode=self.embedding_storage_mode,
            verbose=self.verbose
        )
        return [sentence for sentence in dataset]


class SpanModel(BaseModel):
    def tag(self, sentences, offsets) -> List[List[str]]:
        output = self._predict(sentences)

        annotations: List[List[str]] = []
        for i, offset in enumerate(offsets):
            output_sentence: Sentence = output[i]

            for span in output_sentence.get_spans(self.tagger.tag_type):
                tag, begin, end = span.tag, str(offset + span.start_pos), str(offset + span.end_pos)
                annotations.append([tag, begin, end])

        return annotations
        

class MultiModel:
    def __init__(
            self,
            path: str,
            mini_batch_size=32,
            embedding_storage_mode: str = "none",
            verbose: bool = False,
    ):
        self.models = glob(path)
        print("using", len(self.models), "models from", path)
        self.mini_batch_size = mini_batch_size
        self.embedding_storage_mode = embedding_storage_mode
        self.verbose = verbose
        
    def _predict(self, sentences, tagger):
        tokenizer = SegtokTokenizer()
        dataset = SentenceDataset([Sentence(text, tokenizer) for text in sentences])
        tagger.predict(
            dataset,
            mini_batch_size=self.mini_batch_size,
            embedding_storage_mode=self.embedding_storage_mode,
            verbose=self.verbose
        )
        return [sentence for sentence in dataset]

    def tag(self, sentences, offsets) -> List[List[str]]:
        annotations: List[List[str]] = []
        
        for model in self.models:
            print("loading model", model)
            tagger: SequenceTagger = SequenceTagger.load(Path(model))
        
            output = self._predict(sentences, tagger)

            for i, offset in enumerate(offsets):
                output_sentence: Sentence = output[i]

                for span in output_sentence.get_spans(tagger.tag_type):
                    tag, begin, end = span.tag, str(offset + span.start_pos), str(offset + span.end_pos)
                    annotations.append([tag, begin, end])
                    
            del tagger

        return annotations


class CachedMultiModel:
    def __init__(
            self,
            path: str,
            mini_batch_size=32,
            embedding_storage_mode: str = "none",
            verbose: bool = False,
    ):
        self.taggers = []
        models = glob(path)
        print("using", len(models), "models from", path)
        for model in models:
            print("caching model", model)
            tagger: SequenceTagger = SequenceTagger.load(Path(model))
            self.taggers.append((tagger, model))
        print("all models cached")
        
        self.mini_batch_size = mini_batch_size
        self.embedding_storage_mode = embedding_storage_mode
        self.verbose = verbose
        
    def _predict(self, sentences, tagger):
        tokenizer = SegtokTokenizer()
        dataset = SentenceDataset([Sentence(text, tokenizer) for text in sentences])
        tagger.predict(
            dataset,
            mini_batch_size=self.mini_batch_size,
            embedding_storage_mode=self.embedding_storage_mode,
            verbose=self.verbose
        )
        return [sentence for sentence in dataset]

    def tag(self, sentences, offsets) -> List[List[str]]:
        annotations: List[List[str]] = []
        
        for (tagger, model) in self.taggers:
            print("tagging model", model)        
            output = self._predict(sentences, tagger)

            for i, offset in enumerate(offsets):
                output_sentence: Sentence = output[i]

                for span in output_sentence.get_spans(tagger.tag_type):
                    tag, begin, end = span.tag, str(offset + span.start_pos), str(offset + span.end_pos)
                    annotations.append([tag, begin, end])
                    
            del tagger

        return annotations


class TokenModel(BaseModel):
    def tag(self, sentences, offsets) -> List[List[str]]:
        output = self._predict(sentences)

        annotations: List[List[str]] = []
        for i, offset in enumerate(offsets):
            output_sentence: Sentence = output[i]

            for token in output_sentence.tokens:
                token.tags_proba_dist
                tag = token.get_tag(self.tagger.tag_type).value
                begin, end = str(offset + token.start_position), str(offset + token.end_position)
                annotations.append([tag, begin, end])

        return annotations
