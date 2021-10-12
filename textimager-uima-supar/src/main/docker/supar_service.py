import uvicorn
from typing import List
from fastapi import FastAPI
from pydantic import BaseModel
from supar import Parser


class TextImagerToken(BaseModel):
    begin: int
    end: int
    text: str


class TextImagerSentence(BaseModel):
    begin: int
    end: int
    tokens: List[TextImagerToken]


class TextImagerRequest(BaseModel):
    lang: str
    model: str
    sentences: List[TextImagerSentence]


class SuparDepSentence(BaseModel):
    sentence: TextImagerSentence
    arcs: List[int]
    rels: List[str]


class SuparDepResponse(BaseModel):
    sentences: List[SuparDepSentence]


parser_cache = {}
app = FastAPI()


def get_parser(lang, model_name):
    # TODO cache models on shared disk outside of container
    # Downloading:
    # https://github.com/yzhangcs/parser/releases/download/v1.1.0/ud.biaffine.dep.xlmr.zip
    # to
    # /home/daniel/.cache/supar/ud.biaffine.dep.xlmr.zip

    # cached?
    if lang in parser_cache:
        if model_name in parser_cache[lang]:
            return parser_cache[lang][model_name]

    # load
    print("loading parser:", lang, model_name)
    parser = Parser.load(model_name)
    print("loading finished")

    # cache for later
    if lang not in parser_cache:
        parser_cache[lang] = {}
    parser_cache[lang][model_name] = parser

    return parser


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/dep")
def process(request: TextImagerRequest) -> SuparDepResponse:
    parser = get_parser(request.lang, request.model)

    results = []
    for sentence in request.sentences:
        sentence_text = [
            token.text
            for token in sentence.tokens
        ]

        # lang should be None for pretokenized text
        dataset = parser.predict(sentence_text,
                                 lang=None,
                                 prob=True,
                                 verbose=False)

        supar_sentence = SuparDepSentence(
            sentence=sentence,
            arcs=dataset.sentences[0].arcs,
            rels=dataset.sentences[0].rels
        )
        results.append(supar_sentence)

    response = SuparDepResponse(sentences=results)
    return response


if __name__ == '__main__':

    uvicorn.run('supar_service:app',
                    host='0.0.0.0',
                    port=8000)