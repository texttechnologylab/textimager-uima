import json

from flask import Flask
from flask import request
from flask_cors import CORS

from cltk.tokenize.word import WordTokenizer

from cltk.corpus.utils.importer import CorpusImporter
from cltk.tokenize.sentence import TokenizeSentence

corpus_importer = CorpusImporter('latin')
#corpus_importer.list_corpora
corpus_importer.import_corpus('latin_models_cltk')


app = Flask(__name__)
CORS(app)


languages = {
	"la": "latin"
}

def get_language(inlang):
	return languages[inlang];


@app.route("/tokenizer", methods=['POST'])
def tokenizer():
	lang = request.json["lang"]
	text = request.json["text"]
	
	word_tokenizer = WordTokenizer(get_language(lang))
	
	return json.dumps({
		"token": word_tokenizer.tokenize(text)
	}) 
	
	
@app.route("/sentence", methods=['POST'])
def sentence():
	lang = request.json["lang"]
	text = request.json["text"]
	
	tokenizer = TokenizeSentence(get_language(lang))
	
	return json.dumps({
		"sents": tokenizer.tokenize_sentences(text)
	}) 


@app.route("/segmenter", methods=['POST'])
def segmenter():
	lang = request.json["lang"]
	text = request.json["text"]
	
	word_tokenizer = WordTokenizer(get_language(lang))
	tokenizer = TokenizeSentence(get_language(lang))
	
	return json.dumps({
		"token": word_tokenizer.tokenize(text),
		"sents": tokenizer.tokenize_sentences(text)
	}) 
	

@app.route("/")
def hello():
	return "CLTK - REST Server - https://github.com/texttechnologylab"
