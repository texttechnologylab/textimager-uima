import spacy
from pydantic import BaseModel


class JavaData(BaseModel):
    text: str
    lang: str


lang_nlps = {
            "en": "en_core_web_sm",         # English
            "de": "de_core_news_sm",        # German
            "zh": "zh_core_web_sm",         # Chinese
            "da": "da_core_news_sm",        # Danish
            "nl": "nl_core_news_sm",        # Dutch
            "fr": "fr_core_news_sm",        # French
            "el": "el_core_news_sm",        # Greek
            "it": "it_core_news_sm",        # Italian
            "ja": "ja_core_news_sm",        # Japanese
            "lt": "lt_core_news_sm",        # Lithuanian
            "xx": "xx_ent_wiki_sm",         # Multilingual
            "nb": "nb_core_news_sm",        # Norwegian Bokmal
            "pl": "pl_core_news_sm",        # Polish
            "pt": "pt_core_news_sm",        # Portugese
            "ro": "ro_core_news_sm",        # Romanian
            # "ru": "ru_core_news_sm",        # Russian
            "es": "es_core_news_sm"         # Spanish
            }


def process_multi(data):
    text, lang = data.text, data.lang

    if lang in lang_nlps:
        nlp = spacy.load(lang_nlps[lang])
    else:
        nlp = spacy.load(lang_nlps["xx"])

    if nlp.max_length < len(text):
        nlp.max_length = len(text) + 100

    # ggflls. pipeline verkleinern mit doc = nlp("The sentences we'd like to do lemmatization on", disable = ['ner', 'parser'])
    # https://datascience.stackexchange.com/questions/38745/increasing-spacy-max-nlp-limit/55725
    
    nlp.add_pipe('sentencizer')
    data = nlp(text)

    tokens = []
    sents = []
    pos = []
    deps = []
    ents = []

    for token in data:
        tokens.append((token.idx, token.idx + len(token)))
        pos.append(token.tag_)
        deps.append(token.dep_.upper() if token.dep_ != 'ROOT' else '--')

    for sent in data.sents:
        sents.append((sent.start_char, sent.end_char))

    for ent in data.ents:
        ents.append(ent.label_)
        
    token_string = ""
    for token in tokens:
    	token_string += '				new int[] ' + str(token) + ',\n'
    	
    sents_string = ''
    for sent in sents:
    	sents_string += '				new int[] ' + str(sent) + ',\n'

    out_string = "Tokens:\n" + token_string + '\n\n'\
    		+ "Sents:\n" + sents_string + '\n\n'\
    		+ "PoS:\n" + str(pos) + '\n\n'\
    		+ "DEPS:\n" + str(deps) + '\n\n'\
    		+ "ENTS:\n" + str(ents) + '\n\n'\
    #print(tokens)
    #print(sents)
    #print(pos)
    #print(deps)
    #print(ents)
    
    print(out_string.replace("\'", "\"").replace("(", "{").replace(")", "}"))


if __name__ == '__main__':
    data = JavaData
    data.text = "Мачката ја каса кучето. и не нѐ воведувај во искушение, \
но избави нѐ од лукавиот \
Зашто Твое е Царството и Силата и Славата, во вечни векови."
    data.lang = "mk"
    process_multi(data)
