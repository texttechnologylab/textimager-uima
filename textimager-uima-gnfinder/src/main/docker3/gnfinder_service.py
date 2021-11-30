import json
import os
import sys
import subprocess as s
import uvicorn

from fastapi import FastAPI
from pathlib import Path
from pydantic import BaseModel

# TODO extra libs:
# ru
# ukr
# ...


class TextImagerRequest(BaseModel):
    lang: str
    text: str


class SpacyResponse(BaseModel):
    multitag: dict


app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/multi")
def process(request: TextImagerRequest) -> SpacyResponse:
    with open('tmp.txt', 'w') as f:
        f.write(request.text)

    s.run(['./gnfinder' + ' tmp.txt' + ' -f pretty' + ' > tmp.json'], shell=True)    #subprocess.call('./gnfinder tmp.txt -U -l deu -f pretty > tmp.json')
    try:
        with open('tmp.json', 'r') as f:
            result = json.load(f)
    except FileNotFound:
        result = {'status': 'error'}

    file_names = [f for f in Path('./').iterdir()]
    res_dict = {'result': result}


    response = SpacyResponse(multitag=res_dict)
    return response


if __name__ == '__main__':
    uvicorn.run('gnfinder_service:app',
                host='0.0.0.0',
                port=8000)
