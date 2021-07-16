import spacy
import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel


class JavaData(BaseModel):
    text: str
    lang: str


lang_nlps = {
            "en": "en_core_web_sm",         # English
            "de": "de_core_news_sm",        # German
            "zh": "zh_core_web_sm",         # Chinese
            "da": "da_core_news_sm",        # Danish
            "nl": "nl_core_news_sm",        # Dutch
            "fr": "fr_core_news_sm",        # French
            "el": "el_core_news_sm",        # Greek
            "it": "it_core_news_sm",        # Italian
            "ja": "ja_core_news_sm",        # Japanese
            "lt": "lt_core_news_sm",        # Lithuanian
            "xx": "xx_ent_wiki_sm",         # Multilingual
            "nb": "nb_core_news_sm",        # Norwegian Bokmal
            "pl": "pl_core_news_sm",        # Polish
            "pt": "pt_core_news_sm",        # Portugese
            "ro": "ro_core_news_sm",        # Romanian
            # "ru": "ru_core_news_sm",        # Russian
            "es": "es_core_news_sm"         # Spanish
            }
}


def process_multi(data):
    text, lang = data.text, data.lang

    if lang in lang_nlps:
        nlp = spacy.load(lang_nlps[lang])
    else:
        nlp = spacy.load(lang_nlps["xx"])

    if nlp.max_length < len(text):
        nlp.max_length = len(text) + 100

    # ggflls. pipeline verkleinern mit doc = nlp("The sentences we'd like to do lemmatization on", disable = ['ner', 'parser'])
    # https://datascience.stackexchange.com/questions/38745/increasing-spacy-max-nlp-limit/55725

    data = nlp(text)

    tokens = []
    sents = []
    pos = []
    deps = []
    ents = []

    for token in data:
        tokens_dict = {
            'idx': token.idx,
            'length': len(token),
            'is_space': token.is_space,
            'token_text': token.text
        }
        tokens.append(tokens_dict)

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

    for sent in data.sents:
        sents_dict = {
            'begin': sent.start_char,
            'end': sent.end_char
        }
        sents.append(sents_dict)

    for ent in data.ents:
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
        'ents': ents
    }

    #print('Tokens: \n', tokens, '\n Sents: \n', sents, '\n Pos: \n', pos, '\n Deps: \n', deps, '\n Ents: \n', ents)
    #print(res_dict)
    # return tokens, sents, pos, deps, ents

    return res_dict


if __name__ == '__main__':
    app = FastAPI()


    @app.get("/textimager/ready")
    def get_textimager():
        # return info to TextImager if server is running
        return {
            "ready": True
        }


    @app.post("/multi")
    def post_multi(data: JavaData):
        # print(data.text)
        # print(data.lang)
        return process_multi(data)


    uvicorn.run(app, host='0.0.0.0', port=8000)
