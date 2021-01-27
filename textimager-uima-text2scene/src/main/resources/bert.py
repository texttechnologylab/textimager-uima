import argparse
import copy
from collections import Counter
from pprint import pprint
from typing import List

import torch
from nltk import wordnet as wn, word_tokenize, pos_tag
from transformers import AutoModelForMaskedLM, AutoTokenizer


class Bert:
    __model_path = "./objectTagger"
    #__model_path = "/resources/nlp/models/text2scene"

    def __init__(self):
        self.__obj = wn.wordnet.synsets('object')[0]
        self.__living_thing = wn.wordnet.synsets('living_thing')[0]
        self.__abstract_entity = wn.wordnet.synsets('abstract_entity')[0]
        self.__tokenizer = AutoTokenizer.from_pretrained("distilbert-base-uncased")

    def process_text(self, text, top_n=1) -> List[List[str]]:
        result: List[List[str]] = []
        i = 0
        for mask in self.__preprocess_text(text):
            resultTexts, masked_tok = self.predict_masks(mask.maskedText, top_n, mask.masks)
            masked_tok = [[x[0].split('_')[0], x[0].split('_')[1], x[1], x[2]] if '_' in x[0] else ['', x[0], x[1], x[2]] for x in
                          masked_tok[0]]
            i += 1
            result += masked_tok
        pprint(result)
        return result

    top_n = 1

    def __preprocess_text(self, text):
        tags = pos_tag(word_tokenize(text), tagset='universal')
        tags = self.__add_indices(tags)

        confirmedObjects = []
        completeMasks = []
        tagger = []
        masked_words = []
        counter = 0
        for (word, pos, start, end) in tags:
            # print(len(masked_words) % 512 == 0)
            if len(tagger) >= 400 and len(tagger) > 0:
                completeMasks.append(MaskedText(masked_words, tagger))
                tagger = []
                masked_words = []
            if pos == 'NOUN':
                if word in confirmedObjects:
                    t = (f"{self.__tokenizer.mask_token}" + word, pos)
                    tagger.append(t)
                    t = (f"{self.__tokenizer.mask_token}" + word, pos, start, end)
                    masked_words.append(t)
                elif self.__is_object(word):
                    confirmedObjects.append(word)
                    t = (f"{self.__tokenizer.mask_token}" + word, pos)
                    tagger.append(t)
                    t = (f"{self.__tokenizer.mask_token}" + word, pos, start, end)
                    masked_words.append(t)
                else:
                    tagger.append((word, pos))
            else:
                tagger.append((word, pos))
            counter += 1
        completeMasks.append(MaskedText(masked_words, tagger))
        return completeMasks

    def __add_indices(self, tags):
        tagger = []
        last_index = 0
        for (word, pos) in tags:
            if "." in pos:
                last_index -= 1
            tagger.append((word, pos, last_index, last_index + len(word)))
            last_index += len(word) + 1

        return tagger

    def __is_object(self, word):
        isObj = False
        try:
            for synset in wn.wordnet.synsets(word, pos='n'):
                # isObj = isObj or synset.lowest_common_hypernyms(self.__obj)[0] == self.__obj
                isObj = (synset.lowest_common_hypernyms(self.__obj)[0] == self.__obj
                         and synset.lowest_common_hypernyms(self.__living_thing)[0] != self.__living_thing
                         and synset.lowest_common_hypernyms(self.__abstract_entity)[
                             0] != self.__abstract_entity)
                break
        finally:
            return isObj

    def predict_masks(self, text, top_n, masked_words):

        model = AutoModelForMaskedLM.from_pretrained(self.__model_path, return_dict=True)
        resultTexts: List[str] = []

        inputText = self.__tokenizer.encode(text, return_tensors="pt")
        mask_token_index = torch.where(inputText == self.__tokenizer.mask_token_id)[1]
        token_logits = model(inputText).logits
        mask_token_logits = token_logits[0, mask_token_index, :]

        top_5_all = torch.topk(mask_token_logits, top_n, dim=1).indices.tolist()
        top_5_result = []

        # reshape results
        for i in range(top_n):
            tup = []
            for j in range(len(top_5_all)):
                tup.append(top_5_all[j][i])
            top_5_result.append(tup)

        masks = []

        for masked_list in top_5_result:
            counter = 0
            res = copy.deepcopy(masked_words)

            for token in masked_list:
                if '#' not in self.__tokenizer.decode([token]) and len(self.__tokenizer.decode([token])) > 1:
                    res[counter] = (
                        res[counter][0].replace(self.__tokenizer.mask_token, self.__tokenizer.decode([token]) + "_", 1),
                        res[counter][2], res[counter][3])
                else:
                    res[counter] = (res[counter][0].replace(self.__tokenizer.mask_token, "", 1), res[counter][2], res[counter][3])
                counter += 1
            masks.append(res)

        # replace mask
        for masked_list in top_5_result:
            res = text
            for token in masked_list:
                if '#' not in self.__tokenizer.decode([token]):
                    res = res.replace(self.__tokenizer.mask_token,
                                      "<span style=\"color: red;\">" + self.__tokenizer.decode([token]) + "</span>_", 1)
                else:
                    res = res.replace(self.__tokenizer.mask_token, "", 1)
            resultTexts.append(res)
        return resultTexts, masks


class MaskedText:
    def __init__(self, masks, tags):
        self.masks = masks
        self.__tags = tags
        texter = ""
        for (word, pos) in tags:
            if pos == '.':
                texter += word
            else:
                texter += " " + word
        self.maskedText = texter[1:]
