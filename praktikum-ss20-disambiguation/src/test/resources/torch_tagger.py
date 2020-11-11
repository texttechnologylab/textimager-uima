import torch
from transformers import BertConfig, BertTokenizerFast, BertForSequenceClassification
import numpy as np
import csv


class BertTagger:
    def __init__(self):
        print("Tagger initializing...", flush=True)
        model_name = 'bert-base-uncased'
        config = BertConfig.from_pretrained(model_name)
        config.output_hidden_states = False
        self.tokenizer = BertTokenizerFast.from_pretrained(pretrained_model_name_or_path = model_name, config = config)
        self.model = BertForSequenceClassification.from_pretrained("model_save")
        print("Tagger initialized...", flush=True)


    def tag(self, pretokenized_sentence):

        data = self.tokenizer(
            text=[pretokenized_sentence],
            add_special_tokens=True,
            max_length=100,
            truncation=True,
            padding=True,
            return_tensors='pt',
            return_token_type_ids = False,
            is_pretokenized=True,
            verbose = True)

        logits = self.model(data['input_ids'], token_type_ids=None, attention_mask=data['attention_mask'])[0].detach().numpy()

        return str(np.argmax(logits))




