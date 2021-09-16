import os
import sys
import stanza
import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel


# TODO extra libs:
# ru
# ukr
# ...


class TextImagerRequest(BaseModel):
    lang: str
    text: str


class StanzaResponse(BaseModel):
    multitag: dict


# pipeline per lang and per tool
stanza_pipelines = {}
set_language = {'en', 'de' 'lz'}


def stanza_get_pipeline(tool: str, lang: str = "de") -> stanza:
    if lang in stanza_pipelines and tool in stanza_pipelines[lang]:
        return stanza_pipelines[lang][tool]

    nlp = None

    # build pipeline
    try:
        stanza.download(lang)
        nlp = stanza.Pipeline(lang, use_gpu=stanza_use_gpu)

        # cache and return
        if lang not in stanza_pipelines:
            stanza_pipelines[lang] = {}
        if tool not in stanza_pipelines[lang]:
            stanza_pipelines[lang][tool] = nlp
    except:
        print("Unexpected error:", sys.exc_info()[0])

    return nlp


stanza_use_gpu = os.environ.get("TEXTIMAGER_STANZA_USE_GPU", False)
print("using gpu?", stanza_use_gpu)

app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/multi")
def process(request: TextImagerRequest) -> StanzaResponse:
    nlp = stanza_get_pipeline("Multitagger", lang=request.lang)

    res_dict = {}
    if nlp is not None:
        doc = nlp(request.text)
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
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'token_text': word.text,
                    }
                    tokens.append(tokens_dict)
                    morph_list = []
                    if word.feats is not None:
                        morph_list = word.feats.split("|")
                    morph_dict = {
                        'morph': list(morph_list),
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                    }
                    morphs.append(morph_dict)

                    lemma_dict = {
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'lemma': word.lemma,
                    }
                    lemmas.append(lemma_dict)

                    pos_dict = {
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'upos': word.upos,
                        'xpos': word.xpos,
                    }
                    pos.append(pos_dict)

                    deps_dict = {
                        'start_char': token.start_char,
                        'end_char': token.end_char,
                        'length': len(word.text),
                        'dep': word.deprel,
                    }
                    deps.append(deps_dict)
            """
            sents_dict = {
                'begin': tokens.start_char,
                'end': tokens.end_char
            }
            sents.append(sents_dict)
            """
            ents_dict = {
                    'start_char': token.start_char,
                    'end_char': token.end_char,
                    'label': token.ner
            }
            ents.append(ents_dict)

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

    response = StanzaResponse(multitag=res_dict)
    return response


if __name__ == '__main__':
    uvicorn.run('stanza_service:app',
                host='0.0.0.0',
                port=8000)
