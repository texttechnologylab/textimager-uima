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
python3 -m spacy download de_core_news_sm
python3 -m spacy download en_core_web_sm
python3 -m spacy download zh_core_web_sm
python3 -m spacy download da_core_news_sm
python3 -m spacy download nl_core_news_sm
python3 -m spacy download fr_core_news_sm
python3 -m spacy download el_core_news_sm
python3 -m spacy download it_core_news_sm
python3 -m spacy download ja_core_news_sm
python3 -m spacy download lt_core_news_sm
python3 -m spacy download nb_core_news_sm
python3 -m spacy download pl_core_news_sm
python3 -m spacy download pt_core_news_sm
python3 -m spacy download ro_core_news_sm
python3 -m spacy download es_core_news_sm

# install models
echo "installing textblob/nltk corpora..."
python -m textblob.download_corpora
