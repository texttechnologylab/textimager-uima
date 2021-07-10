import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel
from gervader import vaderSentimentGER


class TextImagerSentence(BaseModel):
    text: str
    begin: int
    end: int


class TextImagerRequest(BaseModel):
    sentences: List[TextImagerSentence]


class SentenceSentiment(BaseModel):
    sentence: TextImagerSentence

    # Info from GerVADER:
    # The 'compound' score is computed by summing the valence scores of each word in the lexicon, adjusted
    # according to the rules, and then normalized to be between -1 (most extreme negative) and +1 (most extreme positive).
    # This is the most useful metric if you want a single unidimensional measure of sentiment for a given sentence.
    # Calling it a 'normalized, weighted composite score' is accurate.
    compound: float

    # Info from GerVADER:
    # The 'pos', 'neu', and 'neg' scores are ratios for proportions of text that fall in each category (so these
    # should all add up to be 1... or close to it with float operation).  These are the most useful metrics if
    # you want multidimensional measures of sentiment for a given sentence.
    pos: float
    neu: float
    neg: float


class SentimentResponse(BaseModel):
    # Info from GerVADER:
    # VADER works best when analysis is done at the sentence level (but it can work on single words or entire novels).
    # You could use NLTK to break the paragraph into sentence tokens for VADER, then average the results for the paragraph.....
    sentences: List[SentenceSentiment]


analyzer = vaderSentimentGER.SentimentIntensityAnalyzer()
app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    # return info to TextImager if server is running
    return {
        "ready": True
    }


@app.post("/process")
def process(request: TextImagerRequest):
    processed_sentences = []

    for sentence in request.sentences:
        vs = analyzer.polarity_scores(sentence.text)

        processed_sentences.append(SentenceSentiment(
            sentence=sentence,
            compound=vs["compound"],
            pos=vs["pos"],
            neu=vs["neu"],
            neg=vs["neg"],
        ))

    response = SentimentResponse(sentences=processed_sentences)
    return response


if __name__ == '__main__':
    uvicorn.run('gervader_service:app',
                host='0.0.0.0',
                port=8000)
