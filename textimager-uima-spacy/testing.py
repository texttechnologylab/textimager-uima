import spacy
import os
import sys
import json


# Read .json file
# Set variables needed (see initialize() ) with data from .json


with open("data.json", "r") as f:
    data = json.load(f)


def initialize():
    try:
        nlps = {}
        nlps["de"] = spacy.load("de_core_news_sm")
        nlps["en"] = spacy.load("en_core_web_sm")
        lang = json_lang # retrieve data from .json
        text = json_text # retrieve text data

    except:
        os.system("python3.8 -m spacy download de_core_news_sm")
        os.system("python3.8 -m spacy download en_core_web_sm")
        nlps["de"] = spacy.load("de_core_news_sm")
        nlps["en"] = spacy.load("en_core_news_sm")


def process():




if __name__ == '__main__':
    initialize()



