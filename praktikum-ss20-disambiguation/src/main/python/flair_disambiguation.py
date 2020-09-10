from flair.models import TextClassifier
from flair.tokenization import SpaceTokenizer
from flair.data import Sentence


class BaseModel:
    """Base Model for flair"""

    def __init__(self,
                 model_path: str = '',
                 mini_batch_size: int = 32,
                 verbose: bool = False
    ):
        """
        Base model for flair classifier to predict sense of prepositions

        :param model_path: path to the model
        :param mini_batch_size: mini batch size to use
        :param verbose: set to True to display a progress bar
        """

        self.__classifier = None
        self._load_classifier(model_path)

        self.__mini_batch_size = mini_batch_size
        self.__verbose = verbose

    def _load_classifier(self, model_path: str = ''):
        """
        Loading a classifier from file

        :param model_path: path to the model
        """

        try:
            self.__classifier = TextClassifier.load(model_path)
        except:
            print("No classifier '" + model_path + "' found", flush=True)



    def predict(self, sentence: str, model_path: str = ''):
        """
        Predict a sentences

        :param sentence: sentence to predict
        :param model_path: path to the model
        :return: sense id of the predicted preposition
        """

        # (Try to) load classifier if none has yet been loaded
        if self.__classifier is None:
            self._load_classifier(model_path)
            if self.__classifier is None:
                raise ValueError('Unable to load a classifier. Prediction not possible')

        # Tokenize sentence with space tokenizer
        sentence = Sentence(sentence, SpaceTokenizer())
        self.__classifier.predict(
            sentences=sentence,
            mini_batch_size=self.__mini_batch_size,
            verbose=self.__verbose
        )

        # Return sense id (number only)
        return str(sentence.labels).split(" ")[0].split("__")[2]
