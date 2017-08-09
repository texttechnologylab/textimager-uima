import sys
import numpy as np
import polyglot
from polyglot.text import Text
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
			
def main():
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

if __name__ == "__main__":
	main()
