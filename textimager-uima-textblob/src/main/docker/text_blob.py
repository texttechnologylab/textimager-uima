import uvicorn
from fastapi import FastAPI
from pydantic import BaseModel


class JavaData(BaseModel):
    text: str
    lang: str


def process_selection(text, ref):
    text = ""
    begin = 0
    end = 0

    if ref is None:
        text = text
        begin = 0
        end = len(text)
    else:
        text = "something with covered text?"
        begin = "get begin from ref?"
        end = "get end from ref?"

    doc = TextBlob(text)
    doc_sentiment = {
        "sentiment" : doc.sentiment.polarity,
        "subjectivity" : doc.sentiment.subjectivity
        }

    return doc_sentiment


def process(data):
    text, lang = data.text, data.lang
    # same formatting as in spacy, use lang_blobs(currently DE, FR, EN?) instead
    if lang == "de":
        from textblob_de import TextBlobDE as TextBlob
    elif lang == "fr":
        from textblob_fr import TextBlobFR as TextBlob
    else:
        from textblob import TextBlod

    selections = text.split(",")

    listed_selections = []

    for sel in selections:
        if sel == "text":
            listed_selections.append(process_selection(text, ref))
        else:
            # another for
            pass

    return listed_selections


if __name__ == '__main__':
    app = FastAPI()

    @app.get("/textimager/ready")
    def get_textimager():
        # return info to TextImager if server is running
        return {
            "ready": True
        }

    @app.post("/multi")
    def post_multi(data: JavaData):
        # print(data.text)
        # print(data.lang)
        return process(data)


    uvicorn.run(app, host='0.0.0.0', port=8000)
