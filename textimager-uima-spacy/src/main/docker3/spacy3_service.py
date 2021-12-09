import os
import sys
import spacy
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
    model_name: str


class SpacyResponse(BaseModel):
    multitag: dict
    actual_name_model: str


# pipeline per lang and per tool
spacy_pipelines = {}

switch = {
    "ef":
        {
            "de": "de_core_news_sm",    # German
            "en": "en_core_web_sm",     # English
            "fr": "fr_core_news_sm",    # French
            "da": "da_core_news_sm",    # Danish
            "nl": "nl_core_news_sm",    # Dutch
            "el": "el_core_news_sm",    # Greek
            "it": "it_core_news_sm",    # Italian
            "zh": "zh_core_web_sm",     # Chinese
            "ja": "ja_core_news_sm",    # Japanese
            "lt": "lt_core_news_sm",    # Lithuanian
            "nb": "nb_core_news_sm",    # Norwegian Bokmal
            "pl": "pl_core_news_sm",    # Polish
            "pt": "pt_core_news_sm",    # Portugese
            "ro": "ro_core_news_sm",    # Romanian
            "ru": "ru_core_news_sm",    # Russian
            "es": "es_core_news_sm",    # Spanish
            "ca": "ca_core_news_sm",    # Catalan
            "mk": "mk_core_news_sm",    # Macedonian
            "multi": "xx_ent_wiki_sm",  # Multi-Language / Unknown Language
            "default": "xx_ent_wiki_sm",
        },
    "ac":
        {
            "zh": "zh_core_web_trf",
            "da": "da_core_news_lg",
            "nl": "nl_core_news_lg",
            "en": "en_core_web_trf",
            "fr": "fr_dep_news_trf",
            "de": "de_dep_news_trf",
            "el": "el_core_news_lg",
            "it": "it_core_news_lg",
            "ja": "ja_core_news_lg",
            "lt": "lt_core_news_lg",
            "nb": "nb_core_news_lg",
            "pl": "pl_core_news_lg",
            "pt": "pt_core_news_lg",
            "ro": "ro_core_news_lg",
            "ru": "ru_core_news_lg",
            "es": "es_dep_news_trf",
            "ca": "ca_core_news_trf",
            "mk": "mk_core_news_lg",
            "multi": "xx_sent_ud_sm",
            "default": "xx_sent_ud_sm",
        }
}


def spacy_get_pipeline(tool: str, model_name: str, lang: str, max_spacy: int = 1000000) -> spacy:
    if model_name == "":
        actual_name_model = switch["ef"][lang]
    else:
         actual_name_model = model_name
    if actual_name_model in spacy_pipelines and tool in spacy_pipelines[actual_name_model]:
        return spacy_pipelines[actual_name_model][tool]

    nlp = None

    # build pipeline
    try:
        if spacy_use_gpu:
            spacy.prefer_gpu()
        nlp = spacy.load(actual_name_model)
        nlp.max_length = max_spacy

        # cache and return
        if actual_name_model not in spacy_pipelines:
            spacy_pipelines[actual_name_model] = {}
        if tool not in spacy_pipelines[actual_name_model]:
            spacy_pipelines[actual_name_model][tool] = nlp
    except:
        print("Unexpected error:", sys.exc_info()[0])

    return nlp, actual_name_model


spacy_use_gpu = os.environ.get("TEXTIMAGER_SPACY_USE_GPU", False)
print("using gpu?", spacy_use_gpu)

app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/multi")
def process(request: TextImagerRequest) -> SpacyResponse:
    nlp, actual_name_model = spacy_get_pipeline("Multitagger", model_name=request.model_name,  lang=request.lang)

    res_dict = {}
    if nlp is not None:
        # adjust max text length
        text_len = len(request.text)
        if nlp.max_length <= text_len:
            nlp.max_length = text_len + 100

        doc = nlp(request.text)

        tokens = []
        sents = []
        pos = []
        deps = []
        ents = []
        morphs = []
        lemmas = []

        for token in doc:
            tokens_dict = {
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space,
                'token_text': token.text,
            }
            tokens.append(tokens_dict)

            morph_dict = {
                'morph': list(token.morph),
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            morphs.append(morph_dict)

            lemma_dict = {
                'lemma_text': token.lemma_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            lemmas.append(lemma_dict)

            pos_dict = {
                'tag': token.tag_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            pos.append(pos_dict)

            deps_dict = {
                'dep': token.dep_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space,
                'head': {
                    'idx': token.head.idx,
                    'length': len(token.head),
                    'is_space': token.head.is_space
                }
            }
            deps.append(deps_dict)

        for sent in doc.sents:
            sents_dict = {
                'begin': sent.start_char,
                'end': sent.end_char
            }
            sents.append(sents_dict)

        for ent in doc.ents:
            ents_dict = {
                'start_char': ent.start_char,
                'end_char': ent.end_char,
                'label': ent.label_
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
        print("not pipeline found for spacy lang", request)

    response = SpacyResponse(multitag=res_dict, actual_name_model=actual_name_model)
    return response


if __name__ == '__main__':
    uvicorn.run('spacy3_service:app',
                host='0.0.0.0',
                port=8000)
