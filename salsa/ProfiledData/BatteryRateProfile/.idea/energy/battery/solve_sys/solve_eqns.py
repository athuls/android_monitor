import unittest
import sys
import os
import filecmp
from scipy.stats.stats import pearsonr
import matplotlib.pyplot as plt
import numpy as np

dir = os.path.dirname(__file__)
moduledirname = os.path.join(dir, '../../battery')
sys.path.append(moduledirname)

from histogram import hist_percent_incr_batteryfrac as batt
from histogram import hist_percent_fixed_size as fixed_size
from histogram import interface_test as interface

from tensorflow.python.framework import dtypes
from tensorflow.python.ops import array_ops
from tensorflow.python.ops import parsing_ops

# from sknn.mlp import Regressor, Layer
import tensorflow as tf



def create_histos(actor_names, full_data):
    eqns = {}
    times = []
    for a in actor_names:
        eqns[a] = []
    for eq in full_data:
        time = len(eq)
        curr_eqn = {}
        for a in actor_names:
            curr_eqn[a] = {}
        for d in eq:
            for key in actor_names:
                if key in d:
                    if d[key] in curr_eqn[key]:
                        curr_eqn[key][d[key]] += 1
                    else:
                        curr_eqn[key][d[key]] = 1
                else:
                    if 0 in curr_eqn[key]:
                        curr_eqn[key][0] += 1
                    else:
                        curr_eqn[key][0] = 1
        for key in curr_eqn:
            eqns[key].append(curr_eqn[key])
        times.append(time)

    return eqns, times

def getTrainingTestingSeperateData(actor_names, eqns, eqns_test):
    return 0

def getTrainingTesting(actor_names, eqns, times, test_prop=0.20):
    X_vals = {}
    for a in actor_names:
        curr = eqns[a]
        LEN = 25
        feats = np.zeros((len(curr), LEN))
        # Add data to the matrix
        for i in range(len(curr)):
            const_dict = curr[i]
            for key in const_dict:
                if key >= LEN - 1:
                    feats[i][24] += const_dict[key]
                else:
                    feats[i][key] += const_dict[key]

        X_vals[a] = feats
    # # right side of linear system
    Y_vals = np.asarray(times)


    test_ind = int(len(times) * (1-test_prop))
    X_training = {}
    X_testing = {}
    for key in X_vals:
        X_training[key] = X_vals[key][0:test_ind]
        X_testing[key] = X_vals[key][test_ind:]

    Y_training = Y_vals[0:test_ind]
    Y_testing = Y_vals[test_ind:]

    return (X_training, Y_training), (X_testing, Y_testing)





def calc_eqns(seperate_test=False, filename_train="", filename_test="", test_prop=0.20):

    if not seperate_test:
        dir = os.path.dirname(__file__)
        filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size_train.txt')
        in_window_size = 3
        actor_name='demo1.Nqueens'


        newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/'+filename_train, actorname=actor_name, windowsize=in_window_size, outputfile=filename)
        newSplittingInstance.histograms = []
        newSplittingInstance.batteryFrac = []
        newSplittingInstance.temp_batteryPercent = []
        newSplittingInstance.extract_windows()

        actors = newSplittingInstance.get_actor_names()

        actor_counts_intervals = newSplittingInstance.get_counts()
        histos, times = create_histos(actors, actor_counts_intervals)
        train, test = getTrainingTesting(actors, histos, times, test_prop=test_prop)

    else:
        print("Not yet written")
        assert 0 == 1

    return train, test


def nn(training, testing):
    training_X = training[0]
    training_Y = training[1]

    features1 = training_X
    labels1 = training_Y


    train = tf.data.Dataset.from_tensor_slices((dict(features1), labels1))


    testing_X  = testing[0]
    testing_Y = testing[1]

    features2 = testing_X
    labels2 = testing_Y

    test = tf.data.Dataset.from_tensor_slices((dict(features2), labels2))

    print(testing_X)

    def input_train():
        return train.shuffle(len(training_Y)).batch(5).repeat().make_one_shot_iterator().get_next()

    def input_test():
        return test.batch(len(testing_Y)).make_one_shot_iterator().get_next()

    feature_columns = []
    for actor in testing_X:
        feature_columns.append(tf.feature_column.numeric_column(key=actor, shape=( len(testing_X[actor][0]) ) ))

    # print("testing", len(testing_X[0]))
    # print("training", len(training_X[0]))

    # Build a DNNRegressor, with 2x20-unit hidden layers, with the feature columns
    # defined above as input.
    model = tf.estimator.DNNRegressor(
      hidden_units=[40, 40], feature_columns=feature_columns,
      optimizer=tf.train.ProximalAdagradOptimizer(
        learning_rate=0.01,
        l1_regularization_strength=0.001
      ),
      model_dir="model_foldr"
    )



    # Train the model.
    STEPS = 200
    model.train(input_fn=input_train, steps=STEPS)




    feature_spec = {}
    for actor in testing_X:
        feature_spec[actor] = tf.FixedLenFeature(25, tf.int64)

    # def serving_input_receiver_fn():
    #     serialized_tf_example = array_ops.placeholder(dtype=dtypes.string,
    #                                               shape=[None],
    #                                               name='input_example_tensor')
    #     receiver_tensors = {'examples': serialized_tf_example}
    #     features = parsing_ops.parse_example(serialized_tf_example, feature_spec)
    #     print(features)
    #     return tf.estimator.export.ServingInputReceiver(features, receiver_tensors)

    # def serving_input_receiver_fn():
    #   """Build the serving inputs."""
    #   # The outer dimension (None) allows us to batch up inputs for
    #   # efficiency. However, it also means that if we want a prediction
    #   # for a single instance, we'll need to wrap it in an outer list.
    #   inputs = {"demo1.Nqueens": tf.placeholder(shape=25, dtype=tf.int64)}
    #   inputs1 = {"demo1.Nqueens": tf.FixedLenFeature(25, tf.int64)}
    #
    #   return tf.estimator.export.ServingInputReceiver(input1, inputs)
    #
    #
    # model.export_savedmodel("savedmodel", serving_input_receiver_fn)

    print("TRAINING COMPLETE")

    # Evaluate how the model performs on data it has not yet seen.
    eval_result = model.evaluate(input_fn=input_test)
    predictions = list(model.predict(input_fn=input_test))

    total = 0
    total2 = 0

    losses = np.zeros(len(testing_Y))

    for i in range(len(testing_Y)):
        print("Prediction:", predictions[i]["predictions"][0], "Actual:", testing_Y[i])
        e = abs(predictions[i]["predictions"][0] - testing_Y[i])
        print("Error: ", e)
        losses[i] = e
        total += (e**2)
        total2 += e
    print("\n\n")
    print("mean of losses: ", np.mean(losses))
    print("STD of losses: ", np.std(losses))
    print("\n\n")

    #
    # print(total2/len(testing_Y))
    print("RMSE: ", (total/len(testing_Y))**0.5)
    print(eval_result)


    # The evaluation returns a Python dictionary. The "average_loss" key holds the
    # Mean Squared Error (MSE).
    average_loss = eval_result["average_loss"]

    # Convert MSE to Root Mean Square Error (RMSE).
    # print("\n" + 80 * "*")
    print("\nRMS error for the test set: ", average_loss**0.5)

    print()

    return average_loss**0.5






training, testing = calc_eqns(seperate_test=False, filename_train='Nqueens_heavy.txt', test_prop=0.20)
for key in training[0]:
    print(len(training[0][key]))

iterations = 1
losses = np.zeros(iterations)


for i in range(0, iterations):
    print(i)
    losses[i] = nn(training, testing)

print(losses)
print("Average RMS Error across", iterations, ", runs:", np.mean(losses))
print("Standard dev of error:", np.std(losses))
