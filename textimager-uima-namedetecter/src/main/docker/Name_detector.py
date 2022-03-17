import gzip
from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn


class TextImagerRequest(BaseModel):
    tokens: list


class NameDetect(BaseModel):
    tokens: dict


dict_names = {
        "proper": set(),
        "typonym": set(),
        "loaded": False
    }


def load_words() -> dict:
    base_dir = "/data"
    proper_name_dir = f"{base_dir}/personennamen.csv_json.gz"
    typonym_name_dir = f"{base_dir}/Toponymelist.csv_json.gz"
    geo_name_dir = f"{base_dir}/geonames.txt"
    if not dict_names["loaded"]:
        with gzip.open(proper_name_dir, "rt", encoding="UTF-8") as out_file:
            for i in out_file.readlines():
                name_proper = i.split("\t")[0]
                dict_names["proper"].add(name_proper)
        with gzip.open(typonym_name_dir, "rt", encoding="UTF-8") as out_file:
            for i in out_file.readlines():
                name_typo = i.replace("\n", "")
                dict_names["typonym"].add(name_typo)
        with open(geo_name_dir, "r", encoding="UTF-8") as out_file:
            for i in out_file.readlines():
                name_geo = i.split("\t")[0]
                dict_names["typonym"].add(name_geo)
        dict_names["loaded"] = True
    return dict_names


app = FastAPI()
@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/tagnames")
def process(request: TextImagerRequest) -> NameDetect:
    res_dict = {}
    name_dict = load_words()
    word_list = {}
    for token in request.tokens:
        word_list[token["text"]] = token
    word_set = set(word_list.keys())
    proper_intersection = word_set.intersection(name_dict["proper"])
    typonym_intersection = word_set.intersection(name_dict["typonym"])
    print(proper_intersection)
    print(typonym_intersection)
    for word in word_list:
        word_typo = False
        word_proper = False
        if word in proper_intersection:
            word_proper = True
        if word in typonym_intersection:
            word_typo = True
        info_dict = {
            "proper": word_proper,
            "typonym": word_typo,
            "begin": word_list[word]["begin"],
            "end": word_list[word]["end"]
        }
        res_dict[word] = info_dict
    response = NameDetect(tokens=res_dict)
    return response


if __name__ == '__main__':
    uvicorn.run('Namedetect_service:app',
                host='0.0.0.0',
                port=8000)
