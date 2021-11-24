from transformers import pipeline


for model_name in [
    "cardiffnlp/twitter-xlm-roberta-base-sentiment",
    "cardiffnlp/twitter-roberta-base-sentiment",
    "siebert/sentiment-roberta-large-english",
    "nlptown/bert-base-multilingual-uncased-sentiment",
    "finiteautomata/bertweet-base-sentiment-analysis"
]:
    print("loading", model_name)
    pipeline("sentiment-analysis", model=model_name, tokenizer=model_name)
