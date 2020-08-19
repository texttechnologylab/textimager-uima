# abort at error
set -e

# params from java
CONDA_DIR="$1"
CONDA_VERSION="$2"
CONDA_INSTALL_DIR="$3"

echo "conda version: $CONDA_VERSION"
echo "conda base dir: $CONDA_DIR"
echo "conda install dir: $CONDA_INSTALL_DIR"

# download installer
echo "downloading conda installer..."
wget -P "$CONDA_DIR" "https://repo.anaconda.com/miniconda/Miniconda3-${CONDA_VERSION}-Linux-x86_64.sh"

# silent installation
echo "installing conda..."
bash "$CONDA_DIR/Miniconda3-${CONDA_VERSION}-Linux-x86_64.sh" -b -p "$CONDA_INSTALL_DIR/" -f
