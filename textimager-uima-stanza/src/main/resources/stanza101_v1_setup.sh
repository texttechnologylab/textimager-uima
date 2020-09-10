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
echo "installing stanza models..."
python3 -c 'import stanza; stanza.download("en")'
python3 -c 'import stanza; stanza.download("de")'
