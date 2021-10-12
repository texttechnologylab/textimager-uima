import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel

from textblob import TextBlob
from textblob.sentiments import NaiveBayesAnalyzer
from textblob_de import TextBlobDE
from textblob_fr import PatternTagger as PatternTagger_fr
from textblob_fr import PatternAnalyzer as PatternAnalyzer_fr


class TextImagerSentence(BaseModel):
    text: str
    begin: int
    end: int


class TextImagerSelection(BaseModel):
    selection: str
    sentences: List[TextImagerSentence]


class TextImagerRequest(BaseModel):
    selections: List[TextImagerSelection]
    lang: str
    doc_len: int
    model: str


class SentenceSentiment(BaseModel):
    sentence: TextImagerSentence
    sentiment: float
    subjectivity: float


class SentimentSelection(BaseModel):
    selection: str
    sentences: List[SentenceSentiment]


class SentimentResponse(BaseModel):
    selections: List[SentimentSelection]


app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    # return info to TextImager if server is running
    return {
        "ready": True
    }


@app.post("/process")
def process(request: TextImagerRequest) -> SentimentResponse:
    processed_selections = []

    for selection in request.selections:
        processed_sentences = []

        for sentence in selection.sentences:
            sen = 0.0
            sub = 0.0

            if request.lang == "de":
                doc = TextBlobDE(sentence.text)
                sen = doc.sentiment.polarity
                sub = doc.sentiment.subjectivity
            elif request.lang == "fr":
                doc = TextBlob(sentence.text, pos_tagger=PatternTagger_fr(), analyzer=PatternAnalyzer_fr())
                sen = doc.sentiment[0]
                sub = doc.sentiment[1]
            else:
                if request.model == "NaiveBayesAnalyzer":
                    doc_temp = TextBlob(sentence.text, analyzer=NaiveBayesAnalyzer())
                    sen = doc_temp.sentiment.p_pos if doc_temp.sentiment.classification == "pos" else doc_temp.sentiment.p_neg*-1
                    sub = 0.0
                else:
                    doc = TextBlob(sentence.text)
                    sen = doc.sentiment.polarity
                    sub = doc.sentiment.subjectivity

            processed_sentences.append(SentenceSentiment(
                sentence=sentence,
                sentiment=sen,
                subjectivity=sub,
            ))

        # compute avg for this selection, if >1
        if len(processed_sentences) > 1:
            begin = 0
            end = request.doc_len

            sentiments = 0
            subjectivitys = 0
            for sentence in processed_sentences:
                sentiments += sentence.sentiment
                subjectivitys += sentence.subjectivity

            sentiment = sentiments / len(processed_sentences)
            subjectivity = subjectivitys / len(processed_sentences)

            processed_sentences.append(SentenceSentiment(
                sentence=TextImagerSentence(text="",
                                            begin=begin,
                                            end=end),
                sentiment=sentiment,
                subjectivity=subjectivity
            ))

        processed_selections.append(SentimentSelection(
            selection=selection.selection,
            sentences=processed_sentences
        ))

    response = SentimentResponse(selections=processed_selections)
    return response


if __name__ == '__main__':
    uvicorn.run('textblob_service:app',
                host='0.0.0.0',
                port=8000)
