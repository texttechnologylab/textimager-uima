# abort at error
set -e

# params from java
CONDA_INSTALL_DIR="$1"
ENV_NAME="$2"

# activate "base" conda
echo "activating conda installation in $CONDA_INSTALL_DIR"
source "$CONDA_INSTALL_DIR/etc/profile.d/conda.sh"
conda activate

# activate env
echo "activating conda env in $ENV_NAME"
conda activate "$ENV_NAME"

# install models
echo "installing spacy models..."

MODELS_DOWNLOADED="/resources/nlp/models/spacy/de_core_news_sm-2.3.0.tar.gz"
if [ ! -e $MODELS_DOWNLOADED ]; then
	echo "downloading models..."
	python3 -m spacy download de_core_news_lg
	python3 -m spacy download en_core_web_lg
	python3 -m spacy download ja_core_news_lg
else
	echo "using predownloaded models"
fi

echo "installing torch for amd"
pip3 install /home/mx/Gits/tools/pytorch/dist/*.whl
