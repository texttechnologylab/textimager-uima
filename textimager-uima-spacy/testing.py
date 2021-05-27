import spacy
import os
import uvicorn
from fastapi import FastAPI
import sys
import json


# Read .json file
# Set variables needed (see initialize() ) with data from .json


# with open("data.json", "r") as f:
#    data = json.load(f)

def initialize():
    try:
        nlps = {}
        nlps["de"] = spacy.load("de_core_news_sm")
        nlps["en"] = spacy.load("en_core_web_sm")
        lang = json_lang  # retrieve data from .json
        text = json_text  # retrieve text data

    except:
        os.system("python3.8 -m spacy download de_core_news_sm")
        os.system("python3.8 -m spacy download en_core_web_sm")
        nlps["de"] = spacy.load("de_core_news_sm")
        nlps["en"] = spacy.load("en_core_news_sm")


def process():
    pass


if __name__ == '__main__':
    test = "Das ist ein Beispielsatz."
    nlp = spacy.load("de_core_news_sm")
    data = nlp(test)
    """
    for token in tokens:
        print(token.text, token.pos_, token.dep_)
    # initialize()
    print(type(tokens))
    print("das sind die tokens: ", tokens)
    """
    tokens = []
    sents = []
    pos = []
    deps = []
    ents = []

    for token in data:
        token_dict = {'idx': token.idx,
                    'length': len(token),
                    'is_space': token.is_space,
                    'token_text': token.text}
        tokens.append(token_dict)

    for sent in data.sents:
        sents_dict = {'begin': sent.start_char,
                      'end': sent.end_char}
        sents.append(sents_dict)

    app = FastAPI()


    @app.get("/")
    async def root():

        return tokens, sents


    uvicorn.run(app)
