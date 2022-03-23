import uvicorn
import fasttext
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel


FASTTEXT_LABEL_PREFIX = "__wd_qid__"

TEXT2WIKI_MODEL_NAMES = {
    "de": {
        "hs": "/mnt/rawindra/vol/public/baumartz/text2wiki/data/text_classifier/2021-11-26/2021-12-02_08-34/simple-bert5/strict-strict-0.15/2022_01_25/simple-bert5_strict-strict-0.15_2021-12-02_11-59-16-882162_dewiki-v8-token-nopunct.model.hs.bin",
        "ova": "/mnt/rawindra/vol/public/baumartz/text2wiki/data/text_classifier/2021-11-26/2021-12-02_08-34/simple-bert5/strict-strict-0.15/2022_01_25/simple-bert5_strict-strict-0.15_2021-12-02_11-59-16-882162_dewiki-v8-token-nopunct.model.ova.bin"
    },
    "en": {
        "hs": "/mnt/rawindra/vol/public/baumartz/text2wiki/data/text_classifier/2021-11-26/2021-12-02_08-34/simple-bert5/strict-strict-0.15/2022_02_08/simple-bert5_strict-strict-0.15_2021-12-02_11-59-16-882162_enwiki-v8-token.model.hs.bin",
        "ova": "/mnt/rawindra/vol/public/baumartz/text2wiki/data/text_classifier/2021-11-26/2021-12-02_08-34/simple-bert5/strict-strict-0.15/2022_02_08/simple-bert5_strict-strict-0.15_2021-12-02_11-59-16-882162_enwiki-v8-token.model.ova.bin"
    }
}

TEXT2WIKI_MODELS = {}
print("loading models...")
for model_lang in TEXT2WIKI_MODEL_NAMES:
    if model_lang not in TEXT2WIKI_MODELS:
        TEXT2WIKI_MODELS[model_lang] = {}
    for model_name in TEXT2WIKI_MODEL_NAMES[model_lang]:
        model_path = TEXT2WIKI_MODEL_NAMES[model_lang][model_name]
        print("loading", model_lang, model_name, model_path)
        TEXT2WIKI_MODELS[model_lang][model_name] = fasttext.load_model(model_path)
print("finished model loading")


class TextImagerRequest(BaseModel):
    lang: str
    model: str
    k: int
    th: float
    text: str


class LabelScore(BaseModel):
    label: str
    score: float


class TextImagerResponse(BaseModel):
    lang: str
    labels_scores: List[LabelScore]


app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/process")
def post_process(request: TextImagerRequest) -> TextImagerResponse:
    label_scores = []

    if request.lang in TEXT2WIKI_MODELS:
        if request.model in TEXT2WIKI_MODELS[request.lang]:
            model = TEXT2WIKI_MODELS[request.lang][request.model]

            labels, scores = model.predict(request.text, k=request.k, threshold=request.th)

            for label, score in zip(labels, scores):
                ls = LabelScore(
                    label=label[len(FASTTEXT_LABEL_PREFIX):],
                    score=score
                )
                label_scores.append(ls)

    response = TextImagerResponse(
        lang=request.lang,
        labels_scores=label_scores
    )
    return response


if __name__ == '__main__':
    uvicorn.run('text2wiki_service:app',
                host='0.0.0.0',
                port=8000)
