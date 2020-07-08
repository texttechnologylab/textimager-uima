# abort at error
set -e

# params from java
CONDA_INSTALL_DIR="$1"
ENV_NAME="$2"
ENV_PYTHON_VERSION="$3"
DEPS_CONDA="$4"
DEPS_PIP="$5"

# activate "base" conda
echo "activating conda installation in $CONDA_INSTALL_DIR"
source "$CONDA_INSTALL_DIR/etc/profile.d/conda.sh"
conda activate

# create new env
echo "creating new conda env named $ENV_NAME"
conda create --name "$ENV_NAME" python="$ENV_PYTHON_VERSION" -y
conda activate "$ENV_NAME"

# install conda packages
if [ -z "$DEPS_CONDA" ]
then
	echo "no conda dependencies"
else
	echo "installing conda dependencies..."
	echo "$DEPS_CONDA"
	conda install $DEPS_CONDA -y
fi

# install pip packages
if [ -z "$DEPS_PIP" ]
then
	echo "no pip dependencies"
else
	echo "installing pip dependencies..."
	echo "$DEPS_PIP"
	pip install $DEPS_PIP --no-cache-dir --force-reinstall
fi

# always install jep
echo "installing jep package..."
JAVA_HOME=$6 pip install jep --no-cache-dir --force-reinstall
