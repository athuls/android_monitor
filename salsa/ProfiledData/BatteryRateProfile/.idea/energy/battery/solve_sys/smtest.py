import tensorflow as tf
from tensorflow.contrib import predictor

import numpy as np
import json

predict_fn = predictor.from_saved_model("savedmodel/1525008036")
print(predict_fn)
#
# feats = {'demo1.Nqueens': list(np.array([2., 43., 19.,  3.,  1.,  3.,  4.,  3.,  6.,  5.,  1.,  0.,  0.,
#      0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.]))}
# # print(feats)
#
# # f = tf.decode_json_example(json.dumps(feats))
# # print(f)
# arr = feats["demo1.Nqueens"][0]
#
# print(predict_fn({'demo1.Nqueens': [0]*25}))
