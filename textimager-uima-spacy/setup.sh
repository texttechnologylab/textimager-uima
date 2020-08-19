#!/bin/bash
source activate $HOME/.textimager/miniconda
conda create --name spacy
conda activate spacy
conda install pip
pip install --no-cache-dir spacy jep
python -m spacy download en_core_web_sm
python -m spacy download de_core_news_sm
