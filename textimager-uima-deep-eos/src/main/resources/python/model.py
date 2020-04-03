import os
from typing import List

import numpy as np
from utils import Utils

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
os.environ['CUDA_VISIBLE_DEVICES'] = ''
import tensorflow as tf
from keras.models import load_model

util = Utils()


def print_flag(content, big=True):
    l = len(content)
    bar = '#' * (8 + l)
    if big:
        print(f'{bar}\n'
              f'### {content} ###\n'
              f'{bar}')
    else:
        print(f'### {content} ###')


class DeepEosModel:
    def __init__(self, model_base_path, window_size=4, batch_size=32):
        print_flag('Loading deep-eos model')
        self.char_2_id_dict = util.load_vocab(model_base_path + ".vocab")
        if os.path.exists(model_base_path + ".hdf5"):
            self.deep_eos_model = load_model(model_base_path + ".hdf5")
        else:
            self.deep_eos_model = load_model(model_base_path + ".model")
        self.deep_eos_graph = tf.get_default_graph()
        self.window_size = window_size
        self.batch_size = batch_size
        print(self.deep_eos_model.summary())

    def tag(self, text) -> List[int]:
        potential_eos_list = util.build_potential_eos_list(text, self.window_size)

        eos_pos = []
        for i in range(0, len(potential_eos_list), self.batch_size):
            batch = potential_eos_list[i:i + self.batch_size]

            eos_positions = [eos_position for eos_position, _ in batch]
            char_sequences = [(-1.0, char_sequence) for _, char_sequence in batch]
            data_set = util.build_data_set(char_sequences, self.char_2_id_dict, self.window_size)
            features = np.array([i[1] for i in data_set])

            predicted = self.deep_eos_model.predict(features)
            for j in range(len(predicted) if type(predicted) is list else predicted.shape[0]):
                if predicted[j][0] >= 0.5:
                    eos_pos.append(int(eos_positions[j]))

        return eos_pos
