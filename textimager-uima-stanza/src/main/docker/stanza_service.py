import os

import stanza
import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel


class TextImagerSentence(BaseModel):
    tokens: List[str]
    begin: int
    end: int


class TextImagerSelection(BaseModel):
    selection: str
    sentences: List[TextImagerSentence]


class TextImagerRequest(BaseModel):
    selections: List[TextImagerSelection]
    lang: str
    doc_len: int

class TextImagerRequestMulti(BaseModel):
    text: str
    lang: str


class StanzaSentimentSentence(BaseModel):
    sentence: TextImagerSentence
    sentiment: float


class StanzaSentimentSelection(BaseModel):
    selection: str
    sentences: List[StanzaSentimentSentence]


class StanzaSentimentResponse(BaseModel):
    selections: List[StanzaSentimentSelection]

class StanzaResponseMulti(BaseModel):
    multitag: dict



# pipeline per lang and per tool
stanza_pipelines = {}


def stanza_get_pipeline(lang, tool):
    if lang in stanza_pipelines and tool in stanza_pipelines[lang]:
        return stanza_pipelines[lang][tool]

    nlp = None

    # build pipeline
    if tool == "sentiment":
        if lang == "de" or lang == "en":
            nlp = stanza.Pipeline(lang,
                                  processors='tokenize,sentiment',
                                  tokenize_pretokenized=True,
                                  use_gpu=stanza_use_gpu
                                  )

    elif tool == "multitagger":
        pass
        #todo

    # cache and return
    if lang not in stanza_pipelines:
        stanza_pipelines[lang] = {}
    if tool not in stanza_pipelines[lang]:
        stanza_pipelines[lang][tool] = nlp
    return nlp


stanza_use_gpu = os.environ.get("TEXTIMAGER_STANZA_USE_GPU", False)
print("using gpu?", stanza_use_gpu)

app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


def map_sentiment_results(result):
    if result == 0:
        # negative
        return -1
    elif result == 2:
        # positive
        return 1

    # neutral
    return 0


@app.post("/sentiment")
def process(request: TextImagerRequest) -> StanzaSentimentResponse:
    processed_selections = []

    nlp = stanza_get_pipeline(request.lang, "sentiment")
    if nlp is not None:
        for selection in request.selections:
            # build stanza doc from pretokenized data
            doc_data = [
                [
                    token
                    for token in sent.tokens
                ]
                for sent in selection.sentences
            ]
            doc = nlp(doc_data)

            processed_sentences = []
            if len(doc.sentences) == len(selection.sentences):
                for stanza_sentence, ti_sentence in zip(doc.sentences, selection.sentences):
                    sentence = StanzaSentimentSentence(
                        sentence=ti_sentence,
                        sentiment=map_sentiment_results(stanza_sentence.sentiment)
                    )
                    processed_sentences.append(sentence)
            else:
                # TODO return error message if not equal length
                print("error: stanza processed doc and textimager provided doc do not contain equal number of sentences!")

            # compute avg for this selection, if >1
            if len(processed_sentences) > 1:
                begin = 0
                end = request.doc_len

                sentiments = 0
                for sentence in processed_sentences:
                    sentiments += sentence.sentiment

                sentiment = sentiments / len(processed_sentences)

                processed_sentences.append(StanzaSentimentSentence(
                    sentence=TextImagerSentence(tokens=[],
                                                begin=begin,
                                                end=end),
                    sentiment=sentiment
                ))

            processed_selections.append(StanzaSentimentSelection(
                selection=selection.selection,
                sentences=processed_sentences
            ))
    else:
        # TODO return error message
        print("not pipeline found for sentiment lang", request)

    response = StanzaSentimentResponse(selections=processed_selections)
    return response

@app.post("/multi")
def process(request: TextImagerRequestMulti) -> StanzaResponseMulti:
    nlp = stanza.Pipeline(lang=request.lang)
    text = request.text

    res_dict = {}
    if nlp is not None:
        doc = nlp(text)
        tokens = []
        sents = []
        pos = []
        deps = []
        ents = []
        morphs = []
        lemmas = []

        for count, sentence in enumerate(doc.sentences):
            for token in sentence.tokens:
                for word in token.words:
                    tokens_dict = {
                        'senctence_id': count,
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'word_text': word.text,
                        'token_text': token.text,
                    }
                    tokens.append(tokens_dict)
                    morph_list = []
                    if word.feats is not None:
                        morph_list = word.feats.split("|")
                    morph_dict = {
                        'senctence_id': count,
                        'morph': list(morph_list),
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                    }
                    morphs.append(morph_dict)

                    lemma_dict = {
                        'senctence_id': count,
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'lemma': word.lemma,
                    }
                    lemmas.append(lemma_dict)

                    pos_dict = {
                        'senctence_id': count,
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'upos': word.upos,
                        # The treebank-specific part-of-speech of this word. Example: ‘NNP'
                        'xpos': word.xpos,
                    }
                    pos.append(pos_dict)

                    deps_dict = {
                        'senctence_id': count,
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'dep': word.deprel,
                    }
                    deps.append(deps_dict)
                ents_dict = {
                    'senctence_id': count,
                    'start_char': token.start_char,
                    'end_char': token.end_char,
                    'label': token.ner
                }
                ents.append(ents_dict)
            end = len(sentence.tokens) - 1
            sents_dict = {
                'begin': sentence.tokens[0].start_char,
                'end': sentence.tokens[end].end_char
            }
            sents.append(sents_dict)

        res_dict = {
            'tokens': tokens,
            'sents': sents,
            'pos': pos,
            'deps': deps,
            'ents': ents,
            'morphs': morphs,
            'lemmas': lemmas
        }

    else:
        # TODO return error message
        print("not pipeline found for stanza lang", request)

    response = StanzaResponseMulti(multitag=res_dict)
    return response


if __name__ == '__main__':
    uvicorn.run('stanza_service:app',
                host='0.0.0.0',
                port=8000)