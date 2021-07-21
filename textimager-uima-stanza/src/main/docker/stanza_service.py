import os

import stanza
import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel


class TextImagerToken(BaseModel):
    text: str
    begin: int
    end: int


class TextImagerSentence(BaseModel):
    tokens: List[TextImagerToken]
    begin: int
    end: int


class TextImagerRequest(BaseModel):
    lang: str
    sentences: List[TextImagerSentence]


class StanzaSentimentSentence(BaseModel):
    sentence: TextImagerSentence
    sentiment: float


class StanzaSentimentResponse(BaseModel):
    sentences: List[StanzaSentimentSentence]


# pipeline per lang and per tool
stanza_pipelines = {}


def stanza_get_pipeline(lang, tool):
    if lang in stanza_pipelines and tool in stanza_pipelines[lang]:
        return stanza_pipelines[tool][lang]

    nlp = None

    # build pipeline
    if tool == "sentiment":
        if lang == "de" or lang == "en":
            nlp = stanza.Pipeline(lang,
                                  processors='tokenize,sentiment',
                                  tokenize_pretokenized=True,
                                  use_gpu=stanza_use_gpu
                                  )

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
    sentences = []

    nlp = stanza_get_pipeline(request.lang, "sentiment")
    if nlp is not None:
        # build stanza doc from pretokenized data
        doc_data = [
            [
                token.text
                for token in sent.tokens
            ]
            for sent in request.sentences
        ]
        doc = nlp(doc_data)

        if len(doc.sentences) == len(request.sentences):
            for stanza_sentence, ti_sentence in zip(doc.sentences, request.sentences):
                sentence = StanzaSentimentSentence(
                    sentence=ti_sentence,
                    sentiment=map_sentiment_results(stanza_sentence.sentiment)
                )
                sentences.append(sentence)
        else:
            # TODO return error message if not equal length
            print("error: stanza processed doc and textimager provided doc do not contain equal number of sentences!")
    else:
        # TODO return error message
        print("not pipeline found for sentiment lang", request)

    response = StanzaSentimentResponse(sentences=sentences)
    return response


if __name__ == '__main__':
    uvicorn.run('stanza_service:app',
                host='0.0.0.0',
                port=8000)
