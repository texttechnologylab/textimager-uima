import spacy
from spacy.tokens import Doc

import json

from flask import Flask
from flask import request


app = Flask(__name__)

nlp_all = {
	"en": spacy.load("en"),
	"de": spacy.load("de")
}


# Filtert die übergebene Liste der Pipenames mit den existierenden
def _get_filtered_pipes_list(nlp, pipes_list):
	return list(filter(lambda x: x in nlp.pipe_names, pipes_list))


def get_spacy_nlp(lang):
	# TODO try to load not loaded languages
	return nlp_all[lang]


@app.route("/")
def hello():
	return "spaCy - REST Server - https://github.com/texttechnologylab"


# POS Tagging
@app.route("/tagger", methods=['POST'])
def tagger():
	lang = request.json["lang"]
	words = request.json["words"]
	spaces = request.json["spaces"]

	nlp = get_spacy_nlp(lang)		
	doc = Doc(nlp.vocab, words=words, spaces=spaces)
	nlp.tagger(doc)
	
	pos = [
		{
			#"pos": token.pos_,
			"tag": token.tag_,
			"idx": token.idx,
			"length": len(token),
			"is_space": token.is_space
		}
		for token in doc
	]
	
	return json.dumps({
		"text": doc.text,
		"pos": pos
	})


# Sentence Segmentation
@app.route("/sentence", methods=['POST'])
def sentence():
	lang = request.json["lang"]
	words = request.json["words"]
	spaces = request.json["spaces"]

	nlp = get_spacy_nlp(lang)		
	doc = Doc(nlp.vocab, words=words, spaces=spaces)
	nlp.parser(doc)
	
	sents = [
		{
			#"text": sent.text,
			#"start": sent.start,
			#"end": sent.end,
			"start_char": sent.start_char,
			"end_char": sent.end_char,
		}
		for sent in doc.sents
	]
	
	return json.dumps({
		"text": doc.text,
		"sents": sents
	})


# Tokenizer
@app.route("/tokenizer", methods=['POST'])
def tokenizer():
	lang = request.json["lang"]
	text = request.json["text"]

	nlp = get_spacy_nlp(lang)
	doc = nlp.tokenizer(text)
	
	tokens = [
		{
			#"text": token.text,
			#"text_with_ws": token.text_with_ws,
			#"whitespace_": token.whitespace_,
			"idx": token.idx,
			#"i": token.i,
			"length": len(token),
			"is_space": token.is_space
		}
		for token in doc
	]
	
	return json.dumps({
		"text": doc.text,
		"token": tokens
	})
