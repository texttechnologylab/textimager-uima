import os
import sys
import spacy
import uvicorn
from fastapi import FastAPI
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


# pipeline per lang and per tool
spacy_pipelines = {}

switch = {
    "ef":
        {
            "de": "de_core_news_sm",
            "en": "en_core_web_sm",
            "fr": "fr_core_news_sm",
            "da": "da_core_news_sm",
            "nl": "nl_core_news_sm",
            "el": "el_core_news_sm",
            "it": "it_core_news_sm",
            "zh": "zh_core_web_sm",
            "ja": "ja_core_news_sm",
            "lt": "lt_core_news_sm",
            "nb": "np_core_news_sm",
            "pl": "pl_core_news_sm",
            "pt": "pt_core_news_sm",
            "ro": "ro_core_news_sm",
            "ru": "ru_core_news_sm",
            "es": "es_core_news_sm",
            "ca": "ca_core_news_sm",
            "mk": "mk_core_news_sm",
            "multi": "xx_ent_wiki_sm",
            "default": "xx_ent_wiki_sm",
        },
    "ac":
        {
            "zh": "zh_core_web_trf",
            "da": "da_core_news_lg",
            "nl": "nl_core_news_lg",
            "en": "en_core_web_trf",
            "fr": "fr_dep_news_trf",
            "de": "de_dep_news_trf",
            "el": "el_core_news_lg",
            "it": "it_core_news_lg",
            "ja": "ja_core_news_lg",
            "lt": "lt_core_news_lg",
            "nb": "nb_core_news_lg",
            "pl": "pl_core_news_lg",
            "pt": "pt_core_news_lg",
            "ro": "ro_core_news_lg",
            "ru": "ru_core_news_lg",
            "es": "es_dep_news_trf",
            "ca": "ca_core_news_trf",
            "mk": "mk_core_news_lg",
            "multi": "xx_sent_ud_sm",
            "default": "xx_sent_ud_sm",
        }
}


def spacy_get_pipeline(tool: str, format_spacy: str = "ac", lang: str = "de", max_spacy: int = 1000000) -> spacy:
    if lang in spacy_pipelines and tool in spacy_pipelines[lang]:
        return spacy_pipelines[lang][tool]

    nlp = None

    # build pipeline
    try:
        if spacy_use_gpu:
            spacy.prefer_gpu()
        nlp = spacy.load(switch[format_spacy][lang])
#         nlp = spacy.load("de_dep_news_trf")
        nlp.max_length = max_spacy

        # cache and return
        if lang not in spacy_pipelines:
            spacy_pipelines[lang] = {}
        if tool not in spacy_pipelines[lang]:
            spacy_pipelines[lang][tool] = nlp
    except:
        print("Unexpected error:", sys.exc_info()[0])

    return nlp


spacy_use_gpu = os.environ.get("TEXTIMAGER_SPACY_USE_GPU", False)
print("using gpu?", spacy_use_gpu)

app = FastAPI()


@app.get("/textimager/ready")
def get_textimager():
    return {
        "ready": True
    }


@app.post("/multi")
def process(request: TextImagerRequest) -> SpacyResponse:
    nomen_art, nomen_att = set(), set()
    with open('./nomen_Artefakt.txt', 'r') as f:
        for line in f:
            nomen_art.add(line.replace('\n', '').lower())
    with open('nomen_Attribut.txt', 'r') as f:
        for line in f:
            nomen_att.add(line.replace('\n', '').lower())

    nlp = spacy_get_pipeline("Multitagger", lang=request.lang)

    def traverse_subtree4G(node):
        if not node.children or node.pos_ == 'VERB' or node.tag_ == 'VAFIN':
            return
        result = [[node.text, node.idx, node.idx + len(node.text)]] if node.pos_ in ['NOUN', 'PROPN'] else []
        for child in node.children:
            grandchildren = traverse_subtree4G(child)
            if not grandchildren is None:
                result.extend(grandchildren)
        return result

    def traverse_subtree(node):
        if not node.children:
            return

        result = [[node, node.idx, node.idx + len(node.text)]]
        for child in node.children:
            result.extend(traverse_subtree(child))

        return result

    res_dict = {}
    if nlp is not None:
        doc = nlp(request.text)
        tokens = []
        sents = []
        pos = []
        psrs = []
        deps = []
        ents = []
        morphs = []
        lemmas = []

        relations = []

        for token in doc:
            tokens_dict = {
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space,
                'token_text': token.text,
            }
            tokens.append(tokens_dict)

            morph_dict = {
                'morph': list(token.morph),
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            morphs.append(morph_dict)

            lemma_dict = {
                'lemma_text': token.lemma_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            lemmas.append(lemma_dict)

            pos_dict = {
                'tag': token.tag_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space
            }
            pos.append(pos_dict)

            deps_dict = {
                'dep': token.dep_,
                'idx': token.idx,
                'length': len(token),
                'is_space': token.is_space,
                'head': {
                    'idx': token.head.idx,
                    'length': len(token.head),
                    'is_space': token.head.is_space
                }
            }
            deps.append(deps_dict)

        for sent in doc.sents:
            sents_dict = {
                'begin': sent.start_char,
                'end': sent.end_char
            }
            sents.append(sents_dict)

            verbs = [t for t in sent if t.pos_=='VERB' or t.tag_ == 'VAFIN']
            noun_chunks = set(doc.noun_chunks)
            for token in verbs:
                rel = ['', '', '', '', '', '', '', '']
            #                 mo = []
                rels_dict = {}
                mos = []
                rels_dict['PRED'] = {
                   'start_char': token.idx,
                   'end_char': token.idx + len(token.text),
                   'sentence_begin': sent.start_char,
                   'sentence_end': sent.end_char
                   }

                for child in token.children:
                    if child.dep_ == 'mo':
                        mos.extend(traverse_subtree(child))
                        ##ARG2 heuristics
                        for mo in mos:
                            if mo[0].text.lower() in nomen_art or mo[0].text.lower() in nomen_att:
                                rels_dict['ARG2'] = {
                                   'start_char': mo[0].idx,
                                   'end_char': mo[0].idx + len(mo[0].text)}

                    if child.dep_ == 'ng':
                        rels_dict['PRED']['comment'] = 'negation'
                    if child.dep_ in ['sb', 'sbp', 'ep']:#, 'svp']:
                        if token.dep_ == 'rc' and child.tag_ == 'PRELS':
                           ## switch places if passive
                            if token.tag_ == 'VVPP':
                                rel[2] = token.head.lemma_
                                rels_dict['ARG1'] = {
                               'start_char': child.idx,
                               'end_char': child.idx + len(child.text)}
                            else:
                                rel[1] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG0'] = {
                                'start_char': child.idx,
                                'end_char': child.idx + len(child.text)}
                        else:
                                rel[1] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG0'] = {
                                'start_char': child.idx,
                                'end_char': child.idx + len(child.text)}
                    elif child.dep_ in ['oa']:
                        if token.dep_ == 'rc' and child.tag_ == 'PRELS':
                           ## switch places if passive
                            if token.tag_ == 'VVPP':
                                rel[2] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG0'] = {
                                   'start_char': child.idx,
                                   'end_char': child.idx + len(child.text)}
                            else:
                                rel[2] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG1'] = {
                                   'start_char': child.idx,
                                   'end_char': child.idx + len(child.text)}
                        else:
                            rel[2] = (child.lemma_, child.text, child.idx)
                            rels_dict['ARG1'] = {
                               'start_char': child.idx,
                               'end_char': child.idx + len(child.text)}
                    elif child.dep_ in ['da', 'og', 'op', 'pd', 'ph']:
                        if child.dep_ == 'pd' and token.pos_ == 'AUX' and token.tag_ == 'VAFIN':
                            try:
                                arg0 = rels_dict['ARG0']
                                rels_dict['ARG1'] = arg0
                                del rels_dict['ARG0']
                            except KeyError:
                                pass
                        rel[3] = (child.lemma_, child.text, child.idx)
                        rels_dict['ARG2'] = {
                           'start_char': child.idx,
                           'end_char': child.idx + len(child.text)}

                if token.dep_ == 'oc' and token.head.pos_ == 'AUX':
                    for child in token.head.children:
                        if child.dep_ == 'ng':
                            rels_dict['PRED']['comment'] = 'negation'
                        if child.dep_ in ['sb', 'sbp'] and rel[1] == '':
                            if token.tag_ == 'VVPP':
                                rel[2] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG1'] = {
                                   'start_char': child.idx,
                                   'end_char': child.idx + len(child.text)}
                            else:
                                rel[1] = (child.lemma_, child.text, child.idx)
                                rels_dict['ARG0'] = {
                                   'start_char': child.idx,
                                   'end_char': child.idx + len(child.text)}
                       #########################
                        if child.dep_ == 'oc' and child.pos_ == 'VERB' and rel[2] == '':
                            for c in child.children:
                                if c.dep_ == 'oc' and c.pos_ == 'VERB' and rel[2] == '':
                                    pass
                                elif c.dep_ == 'oc' and c.pos_ == 'AUX' and rel[2] == '':
                                    for c2 in c.children:
                                        if c2.dep_ == 'oc' and c2.pos_ == 'VERB' and rel[2] == '':
                                            rel[2] = (c2.lemma_, c2.text, c2.idx)
                                            rels_dict['ARG1'] = {
                                               'start_char': c2.idx,
                                               'end_char': c2.idx + len(c2.text),
                                               'comment': 'recursive'}
                if rel[2] == '':
                    for child in token.children:
                        if child.dep_ == 'oc' and child.pos_ == 'VERB':
                            rel[2] = (child.lemma_, child.text, child.idx)
                            rels_dict['ARG1'] = {
                                'start_char': child.idx,
                                'end_char': child.idx + len(child.text),
                                'comment': 'recursive'}
                       #########################
                if token.dep_ == 'cj' and rel[1] == '':
                    for child in token.head.head.children:
                        if child.dep_ in ['sb']:
                            rel[1] = (child.lemma_, child.text, child.idx)
                            rels_dict['ARG0'] = {
                               'start_char': child.idx,
                               'end_char': child.idx + len(child.text)}
                    if token.head.head.dep_ == 'oc':
                        for child in token.head.head.head.children:
                            if child.dep_ in ['sb', 'sbp'] and rel[1] == '':
                                if token.tag_ == 'VVPP':
                                    rel[2] = (child.lemma_, child.text, child.idx)
                                    rels_dict['ARG1'] = {
                                       'start_char': child.idx,
                                       'end_char': child.idx + len(child.text)}
                                else:
                                    rel[1] = (child.lemma_, child.text, child.idx)
                                    rels_dict['ARG0'] = {
                                       'start_char': child.idx,
                                       'end_char': child.idx + len(child.text)}
                 ##ARG2 heuristics
                for child in token.children:
                    if child.dep_ == 'mo':
                        mos.extend(traverse_subtree(child))
   #                         print('----', child.text.lower())
                        for mo in mos:
   #                             mo_cand = mo[0].text.lower()
                            if mo[0].text.lower() in nomen_art or mo[0].text.lower() in nomen_att:
                                for arg in ['PRED', 'ARG0', 'ARG1']:
                                    try:
                                        start = rels_dict[arg]
                                        if start != mo[0].idx:
                                            rels_dict['ARG2'] = {
                                               'start_char': mo[0].idx,
                                               'end_char': mo[0].idx + len(mo[0].text)}
                                    except KeyError:
                                        pass
                ##GEOs
                geos = []
                for verb_child in token.children:
                    verb_children = traverse_subtree4G(verb_child)
                    if not verb_children is None:
                        geos.extend(verb_children)
                    if verb_child.pos_ in ['NOUN', 'PROPN']:
                        new_entry = [verb_child.text, verb_child.idx, verb_child.idx + len(verb_child.text)]
                        if not new_entry in geos:
                            geos.append(new_entry)

#                 for k, v in rels_dict.items():
#                     if k != 'PRED':
#                         token_text = sent.text[v['start_char']:v['end_char']]
#                         new_entry = [token_text, v['start_char'], v['end_char']]
#                         if not new_entry in geos:
#                             geos.append(new_entry)
                rels_dict['geos'] = geos
                mos = [[l[0].text, l[1], l[2]] for l in mos]
                rels_dict['mos'] = mos

                if len(rels_dict) > 1:
                    psrs.append(rels_dict)


        for ent in doc.ents:
            ents_dict = {
                'start_char': ent.start_char,
                'end_char': ent.end_char,
                'label': ent.label_
            }
            ents.append(ents_dict)

        res_dict = {
            'tokens': tokens,
            'sents': sents,
            'pos': pos,
            'deps': deps,
            'ents': ents,
            'morphs': morphs,
            'lemmas': lemmas,
            'psrs': psrs
        }

    else:
        # TODO return error message
        print("not pipeline found for spacy lang", request)
        res_dict = {'status': 'nlp not found'}


    response = SpacyResponse(multitag=res_dict)
    return response


if __name__ == '__main__':
    uvicorn.run('spacy3_service:app',
                host='0.0.0.0',
                port=8000)
