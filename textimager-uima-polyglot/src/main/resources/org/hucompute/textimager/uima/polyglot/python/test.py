import sys
import polyglot
from polyglot.text import Text

def foo():
	inputText = Text("Das ist ein Test")
	print inputText.language
def main():
	if sys.argv[1]=="foo":
		foo()

if __name__ == "__main__":
	main()
