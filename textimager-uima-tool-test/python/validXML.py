from pathlib import Path
from lxml import etree
from io import StringIO
import sys
from xml.sax.handler import ContentHandler
from xml.sax import make_parser
from glob import glob
import sys

dir = "/resources/corpora/Gutenberg_Edition_13/tei_raw/tei/"
filePattern = "*.tei"

def parsefile(file):
    parser = make_parser(  )
    parser.setContentHandler(ContentHandler(  ))
    parser.parse(file)


#print(len(list(Path(dir).rglob(filePattern))))
count = 0
total = 31239
for path in Path(dir).rglob(filePattern):
    if(count % 100 == 0):
        print(count)
    count = count + 1
    try:
        parsefile(str(path))
    except Exception as e:
        print(str(path))
