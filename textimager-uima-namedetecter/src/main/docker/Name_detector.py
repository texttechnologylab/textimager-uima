import gzip
from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn
import json


class TextImagerRequest(BaseModel):
    tokens: list
    lang: str
    label_wikidata: bool


class NameDetect(BaseModel):
    tokens: dict


dict_names = {
        "proper": set(),
        "typonym": set(),
        "organization": dict(),
        "organization_not_labels": dict(),
        "loaded": False
    }
labels_not_wikidata = set()

def load_words() -> dict:
    base_dir = "/data"
    proper_name_dir = f"{base_dir}/personennamen.csv_json.gz"
    typonym_name_dir = f"{base_dir}/Toponymelist.csv_json.gz"
    geo_name_dir = f"{base_dir}/geonames.txt"
    organization_dir = f"{base_dir}/Organization_names.json"
    organization_label_dir = f"{base_dir}/Organization_labels.json"
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
        with open(organization_label_dir, "r", encoding="UTF-8") as out_file:
            labels_wikidata = set(json.load(out_file))
        with open(organization_dir, "r", encoding="UTF-8") as out_file:
            organization_names = json.load(out_file)
            labels_not_wikidata = set(list(organization_names.keys())).difference(labels_wikidata)
            print(len(labels_not_wikidata))
            for qid in organization_names:
                for language in organization_names[qid]:
                    if language not in dict_names["organization"]:
                        dict_names["organization"][language] = dict()
                    dict_names["organization"][language][qid] = organization_names[qid][language]
            for qid in labels_not_wikidata:
                for language in organization_names[qid]:
                    if language not in dict_names["organization_not_labels"]:
                        dict_names["organization_not_labels"][language] = set()
                    dict_names["organization_not_labels"][language].add(organization_names[qid][language])
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
    language = request.lang
    for token in request.tokens:
        word_list[token["text"]] = token
    word_set = set(word_list.keys())
    proper_intersection = word_set.intersection(name_dict["proper"])
    typonym_intersection = word_set.intersection(name_dict["typonym"])
    if language in name_dict["organization"]:
        if not request.label_wikidata:
            organization_intersection = word_set.intersection(name_dict["organization_not_labels"][language])
        else:
            organization_intersection = word_set.intersection(name_dict["organization"][language].values())
        print(organization_intersection)
        print(len(name_dict["organization"][language]))
    else:
        organization_intersection = set()
    print(proper_intersection)
    print(typonym_intersection)
    for word in word_list:
        word_typo = False
        word_proper = False
        word_organization =  False
        if word in proper_intersection:
            word_proper = True
        if word in typonym_intersection:
            word_typo = True
        if word in organization_intersection:
            word_organization =True
        info_dict = {
            "proper": word_proper,
            "typonym": word_typo,
            "organization": word_organization,
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
