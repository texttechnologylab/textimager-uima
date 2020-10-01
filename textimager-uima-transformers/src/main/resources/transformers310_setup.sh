#!/bin/bash
source activate $HOME/.textimager/miniconda
conda create --name transformers
conda activate transformers
conda install pip

echo "installing transformers jep..."
pip install --no-cache-dir transformers jep

echo "installing transformer models..."
pip install transformers ner
pip install transformers sentiment-analysis
pip install transformers summarization
pip install transformers bert_de_ner
pip install transformers bert-large-cased-finetuned-conll03-english
