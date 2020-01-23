import stanfordnlp


class TextImagerStanfordNLP:
	def __init__(self, model_dir, use_gpu):
		self.model_dir = model_dir
		self.use_gpu = use_gpu
		self.pipelines = {}


	def get_pipeline(self, lang, pipeline):
		if lang not in self.pipelines:
			self.pipelines[lang] = {}
	
		if pipeline not in self.pipelines[lang]:
			self.pipelines[lang][pipeline] = stanfordnlp.Pipeline(processors=pipeline, lang=lang, models_dir=self.model_dir, use_gpu=self.use_gpu)
	
		return self.pipelines[lang][pipeline]
	
	
	def tokenize(self, lang, text):
		nlp = self.get_pipeline(lang, 'tokenize')
	
		doc = nlp(text)
	
		sentences = []
		for i, sentence in enumerate(doc.sentences):
			tokens = []
			for token in sentence.tokens:
				tokens.append(token.text)
			sentences.append(tokens)
	
		return sentences    
