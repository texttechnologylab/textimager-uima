#!/usr/bin/env python
import sys
import numpy as np
import polyglot
from polyglot.text import Text, Word
from polyglot.mapping import Embedding
reload(sys)
sys.setdefaultencoding('utf-8')

def language(text):
	inputText = Text(text)
	print inputText.language
def token(text):
	inputText = Text(text)
	for x in inputText.words:		
		print x
def sentence(text):
	inputText = Text(text)
	for x in inputText.sentences:	
		print x
def pos(text):
	inputText = Text(text)
	for x in inputText.pos_tags:	
		for y in x:
			print y
def ner(text):
	inputText = Text(text)
	for entity in inputText.entities:
		for x in entity:
			print entity.tag, x
def sentiment(text):
	inputText = Text(text)
	for x in inputText.words:
		print x, x.polarity
def morphology(text, langCode):	
	inputText = Text(text)
	inputText.language = langCode
	for x in inputText.morphemes:
		print x
def transliteration(text, toLangCode):
	inputText = Text(text)
	for x in inputText.transliterate(toLangCode):
  		print(x)
def embedding(text, embeddingPATH):
	embeddings = Embedding.load(embeddingPATH)
	neighbors = embeddings.nearest_neighbors(text)
	for w, d in zip(neighbors, embeddings.distances(text, neighbors)):
  		print("{}\n{}".format(w,d))
		
if sys.argv[1]=="language":
	language(sys.argv[2])
elif sys.argv[1]=="token":
	token(sys.argv[2])
elif sys.argv[1]=="sentence":
	sentence(sys.argv[2])
elif sys.argv[1]=="pos":
	pos(sys.argv[2])
elif sys.argv[1]=="ner":
	ner(sys.argv[2])
elif sys.argv[1]=="sentiment":
	sentiment(sys.argv[2])
elif sys.argv[1]=="embedding":
	embedding(sys.argv[2], sys.argv[3])
elif sys.argv[1]=="morphology":
	morphology(sys.argv[2], sys.argv[3])
elif sys.argv[1]=="transliteration":
	transliteration(sys.argv[2], sys.argv[3])
