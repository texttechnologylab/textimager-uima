# coding=utf-8
"""
Flask adaptation of the Neuralnetwork NER Tagger from Guillaume Lample (https://github.com/glample/tagger) with german models obtained by Sajawel Ahmed (https://github.com/FID-Biodiversity/GermanWordEmbeddings-NER) for the TextImager (https://textimager.hucompute.org/).

Loads a model and runs the tagger on the words given by POST.

Original works:
    - Lample, Guillaume, Miguel Ballesteros, Sandeep Subramanian, Kazuya Kawakami, and Chris Dyer. "Neural architectures for named entity recognition." in Proceedings of the 2016 Conference of the North American Chapter of the Association for Computational Linguistics: Human Language Technologies, http://aclweb.org/anthology/N/N16/N16-1030.pdf

    - Sajawel Ahmed and Alexander Mehler, "Resource-Size matters: Improving Neural Named Entity Recognition with Optimized Large Corpora" in Proceedings of the 17th IEEE International Conference on Machine Learning and Applications (ICMLA), 2018. accepted, https://arxiv.org/pdf/1807.10675.pdf

    - W. Hemati, T. Uslu, and A. Mehler, “TextImager: a Distributed UIMA-based System for NLP,” in Proceedings of the COLING 2016 System Demonstrations, 2016, https://www.texttechnologylab.org/textimager/
"""

__author__ = 'Manuel Stoeckel'

import codecs
import json
import os
import time

import numpy as np

from loader import prepare_sentence
from model import Model
from utils import create_input, iobes_iob, iob_ranges, zero_digits

import json

from flask import Flask
from flask import request

app = Flask(__name__)

# The four models obtained by S. Ahmed.
models = {
    'conll2010-tuebadz': 'train-conll2010-tuebadz-11_2018-07-11_20-03-32-214819',
    'conll-germeval-tuebadz-europarl': 'train-conll-germeval-tuebadz-europarl_2018-07-14_15-36-18-296362',
    'conll': 'train-conll.txt_2018-06-27_19-48-21-329017',
    'germeval': 'train-germeval.txt_2018-06-26_21-16-14-908069',

    'train-conll.txt_2018-06-27_19-48-21-329017': 'train-conll.txt_2018-06-27_19-48-21-329017',
    'train-conll-germeval-tuebadz-europarl_2018-07-14_15-36-18-296362': 'train-conll-germeval-tuebadz-europarl_2018-07-14_15-36-18-296362',
    'train-conll2010-tuebadz-11_2018-07-11_20-03-32-214819': 'train-conll2010-tuebadz-11_2018-07-11_20-03-32-214819',
    'train-germeval.txt_2018-06-26_21-16-14-908069': 'train-germeval.txt_2018-06-26_21-16-14-908069'
}


@app.route("/")
def hello():
    return "Neural Architectures for Named Entity Recognition - REST Server - https://github.com/texttechnologylab\n"


# NER
@app.route("/ner", methods=['POST'])
def ner():
    model_name = request.json["model"]
    words = request.json["words"]
    begin_end = request.json["begin_end"]

    ## Model loading
    print "Loading model " + model_name + ".."
    model = Model(model_path="models/" + models[model_name])
    parameters = model.parameters

    # Load reverse mappings
    word_to_id, char_to_id, tag_to_id = [
        {v: k for k, v in x.items()}
        for x in [model.id_to_word, model.id_to_char, model.id_to_tag]
    ]

    # Load the model
    _, f_eval = model.build(training=False, **parameters)
    model.reload()

    # Lowercase sentence
    if parameters['lower']:
        words = [w.lower() for w in words]
    # Replace all digits with zeros
    if parameters['zeros']:
        words = [zero_digits(w) for w in words]
    words = [w if not w.isupper() else w.title() for w in words]

    # Prepare input
    sentence = prepare_sentence(words, word_to_id, char_to_id, lower=parameters['lower'])
    input = create_input(sentence, parameters, False)

    # Decoding
    if parameters['crf']:
        y_preds = np.array(f_eval(*input))[1:-1]
    else:
        y_preds = f_eval(*input).argmax(axis=1)
    y_preds = [model.id_to_tag[y_pred] for y_pred in y_preds]

    # Output tags in the IOB2 format
    if parameters['tag_scheme'] == 'iobes':
        y_preds = iobes_iob(y_preds)

    # Write tags
    assert len(y_preds) == len(words)  # TODO:remove assert?

    ents = [
        {
            "start_char": b,
            "end_char": e,
            "label": label
        }
        for (b, e), label in zip(begin_end, y_preds)
        if label != "O"
    ]

    return json.dumps({
        "ents": ents
    })
