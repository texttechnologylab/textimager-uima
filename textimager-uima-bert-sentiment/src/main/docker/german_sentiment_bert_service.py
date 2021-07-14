import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel
from germansentiment import SentimentModel


class TextImagerSentence(BaseModel):
    text: str
    begin: int
    end: int


class TextImagerSelection(BaseModel):
    selection: str
    sentences: List[TextImagerSentence]


class TextImagerRequest(BaseModel):
    selections: List[TextImagerSelection]


class BertSentimentSentence(BaseModel):
    sentence: TextImagerSentence
    sentiment: float


class BertSentimentSelection(BaseModel):
    selection: str
    sentences: List[BertSentimentSentence]


class BertSentimentResponse(BaseModel):
    selections: List[BertSentimentSelection]


model = SentimentModel()
app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


def sentiment_str_to_val(sentiment):
    if sentiment == "positive":
        return 1.0
    elif sentiment == "negative":
        return -1.0
    return 0.0


@app.post("/process")
def process(request: TextImagerRequest) -> BertSentimentResponse:
    processed_selections = []

    for selection in request.selections:
        texts = [s.text for s in selection.sentences]
        results = model.predict_sentiment(texts)

        processed_sentences = [
            BertSentimentSentence(
                sentence=s,
                sentiment=sentiment_str_to_val(r)
            )
            for s, r
            in zip(selection.sentences, results)
        ]

        if len(results) > 1:
            begin = processed_sentences[0].sentence.begin
            end = processed_sentences[-1].sentence.end

            sentiments = 0
            for sentence in processed_sentences:
                sentiments += sentence.sentiment

            sentiment = sentiments / len(processed_sentences)

            processed_sentences.append(BertSentimentSentence(
                sentence=TextImagerSentence(text="",
                                            begin=begin,
                                            end=end),
                sentiment=sentiment
            ))

        processed_selections.append(BertSentimentSelection(
            selection=selection.selection,
            sentences=processed_sentences
        ))

    response = BertSentimentResponse(selections=processed_selections)
    return response


if __name__ == '__main__':
    uvicorn.run('german_sentiment_bert_service:app',
                host='0.0.0.0',
                port=8000)
