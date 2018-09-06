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


# NER
@app.route("/ner", methods=['POST'])
def ner():
	lang = request.json["lang"]
	words = request.json["words"]
	spaces = request.json["spaces"]

	nlp = get_spacy_nlp(lang)		
	doc = Doc(nlp.vocab, words=words, spaces=spaces)

	# TODO better solution?	
	for name, proc in nlp.pipeline:
		if name == "ner":
			doc = proc(doc)
	
	ents = [
		{
			#"text": ent.text,
			"start_char": ent.start_char,
			"end_char": ent.end_char,
			"label": ent.label_
		}
		for ent in doc.ents
	]
	
	return json.dumps({
		#"text": doc.text,
		"ents": ents
	})
	
	
# Dependency Parser
@app.route("/parser", methods=['POST'])
def parser():
	lang = request.json["lang"]
	words = request.json["words"]
	spaces = request.json["spaces"]

	nlp = get_spacy_nlp(lang)		
	doc = Doc(nlp.vocab, words=words, spaces=spaces)
	nlp.parser(doc)
	
	deps = [
		{
			#"text": token.text,
			"dep": token.dep_,
			"idx": token.idx,
			"length": len(token),
			"is_space": token.is_space,
			"head": {
				#"text": token.head.text,
				"idx": token.head.idx,
				"length": len(token.head),
				"is_space": token.head.is_space
			}
		}
		for token in doc
	]
	
	return json.dumps({
		"text": doc.text,
		"deps": deps
	})
	
	
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
		#"text": doc.text,
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
		#"text": doc.text,
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
		#"text": doc.text,
		"token": tokens
	})
