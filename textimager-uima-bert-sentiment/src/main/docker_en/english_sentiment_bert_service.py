import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline
from typing import Union, List, Dict


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
    model_name: str
    sentiment_mapping: Dict[str, float]


class BertSentimentSentence(BaseModel):
    sentence: TextImagerSentence
    sentiment: float


class BertSentimentSelection(BaseModel):
    selection: str
    sentences: List[BertSentimentSentence]


class BertSentimentResponse(BaseModel):
    selections: List[BertSentimentSelection]


sentiment_analysis = None
app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


def sentiment_to_number(sentiment_output: Dict[str, Union[str, float]], sentiment_mapping: Dict[str, float]) -> float:
    sentiment_value = 0.0

    if sentiment_output["label"] in sentiment_mapping:
        sentiment_value = sentiment_mapping[sentiment_output["label"]]

    return sentiment_value


@app.post("/process")
def process(request: TextImagerRequest) -> BertSentimentResponse:
    global sentiment_analysis
    if sentiment_analysis is None:
        sentiment_analysis = pipeline("sentiment-analysis", model=request.model_name, tokenizer=request.model_name)

    processed_selections = []

    for selection in request.selections:
        texts = [s.text for s in selection.sentences]

        # truncate input, same as german lib
        results = sentiment_analysis(texts, truncation=True)

        processed_sentences = [
            BertSentimentSentence(
                sentence=s,
                sentiment=sentiment_to_number(r, request.sentiment_mapping)
            )
            for s, r
            in zip(selection.sentences, results)
        ]

        if len(results) > 1:
            begin = 0
            end = request.doc_len

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
    uvicorn.run('english_sentiment_bert_service:app',
                host='0.0.0.0',
                port=8000)
