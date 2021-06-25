import spacy
import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel


class JavaData(BaseModel):
    text: str
    lang: str


nlps = {
    "de": spacy.load("de_core_news_sm"),
    "en": spacy.load("en_core_web_sm")
}


def process(data):
    text, lang = data.text, data.lang

    if lang in nlps:
        nlp = nlps[lang]
    else:
        nlp = nlps["en"]

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
        tokens_dict = {'idx': token.idx,
                       'length': len(token),
                       'is_space': token.is_space,
                       'token_text': token.text}
        tokens.append(tokens_dict)

        pos_dict = {'tag': token.tag_,
                    'idx': token.idx,
                    'length': len(token),
                    'is_space': token.is_space
                    }
        pos.append(pos_dict)

        deps_dict = {'dep': token.dep_,
                     'idx': token.idx,
                     'length': len(token),
                     'is_space': token.is_space,
                     'head': {'idx': token.head.idx,
                              'length': len(token.head),
                              'is_space': token.head.is_space
                              }
                     }
        deps.append(deps_dict)

    for sent in data.sents:
        sents_dict = {'begin': sent.start_char,
                      'end': sent.end_char}
        sents.append(sents_dict)

    for ent in data.ents:
        ents_dict = {'start_char': ent.start_char,
                     'end_char': ent.end_char,
                     'label': ent.label_
                     }
        ents.append(ents_dict)

    res_dict = {'tokens': tokens,
                'sents': sents,
                'pos': pos,
                'deps': deps,
                'ents': ents
                }

    #print('Tokens: \n', tokens, '\n Sents: \n', sents, '\n Pos: \n', pos, '\n Deps: \n', deps, '\n Ents: \n', ents)
    print(res_dict)
    # return tokens, sents, pos, deps, ents

    return res_dict


if __name__ == '__main__':
    app = FastAPI()


    @app.post("/data/")
    def read_from_java(data: JavaData):
        print(data.text)
        print(data.lang)
        return process(data)


    uvicorn.run(app, host='0.0.0.0', port=8000)
