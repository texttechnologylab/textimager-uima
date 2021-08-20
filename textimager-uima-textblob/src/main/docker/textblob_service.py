import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel

from textblob_de import TextBlobDE
from textblob import TextBlob


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
            if request.lang == "de":
                doc = TextBlobDE(sentence.text)
            else:
                doc = TextBlob(sentence.text)

            processed_sentences.append(SentenceSentiment(
                sentence=sentence,
                sentiment=doc.sentiment.polarity,
                subjectivity=doc.sentiment.subjectivity,
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
