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

# from sknn.mlp import Regressor, Layer
import tensorflow as tf


#
# def predict(histchange, powerchange, sizechange, m, b):
#     errors = []
#     for i in range(0, len(histchange)):
#         if sizechange[i] == 0:
#             errors.append("no size change")
#             continue
#         prediction = (m * histchange[i] + b)/float(sizechange[i])
#         # prediction = (m * histchange[i] + b)
#         if powerchange[i] != 0:
#             # errors.append("REL: " + str(abs(prediction - powerchange[i])/powerchange[i]))
#             errors.append(abs(prediction - powerchange[i])/powerchange[i])
#         else:
#             errors.append("ABS: " + str(abs(prediction - powerchange[i])))
#
#     return errors
#
# def get_predictors(histchange, powerchange, sizechange):
#     powernorm = [powerchange[i]*float(sizechange[i]) for i in range(len(powerchange))]
#     print("Powernorm")
#     for i in range(len(powernorm)):
#         s = "WRONG" if abs(histchange[i] - powernorm[i]) > 0.3 else "correct"
#         print(s, histchange[i], powernorm[i], sizechange[i])
#     # powernorm = [powerchange[i]*1 for i in range(len(powerchange))]
#     plt.scatter(histchange, powernorm)
#     m, b = np.polyfit(histchange, powernorm, 1)
#     print(m,b)
#     plt.show()
#     return m, b

def create_eqns(sizechange, histchange):
    eqns = []
    eqn = {}
    tm = 0
    prev = None

    # currently, tm represents the time, but that calculation might not be correct
    for i in range(0, len(histchange)):
        if sizechange[i] != prev:
            prev = sizechange[i]
            if i != 0:
                eqns.append((eqn, tm))
                eqn = {}
                tm = 0
        if histchange[i] in eqn:
            eqn[histchange[i]] += 1
        else:
            eqn[histchange[i]] = 1
        tm += 1

    return eqns


def features_labels(eqns):
    loads = set()
    count = 0
    for pair in eqns:
        for key in pair[0]:
            count += 1
            loads.add(key)
    loads = sorted(loads)

    feats = np.zeros((len(eqns), len(loads)))

    # Add data to the matrix
    for i in range(len(eqns)):
        const_dict = eqns[i][0]
        for j in range(len(loads)):
            if loads[j] in const_dict:
                feats[i][j] = loads[j] * const_dict[loads[j]]
            else:
                feats[i][j] = 0

    # # right side of linear system
    labs = np.zeros(len(eqns))
    for j in range(len(feats)):
        labs[j] = eqns[j][1]

    return feats, labs


def calc_eqns(seperate_test=False, filename_train="", filename_test="", test_prop=0.20):
    dir = os.path.dirname(__file__)
    filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
    in_window_size = 3
    actor_name='demo1.Nqueens'
    # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('../mobile_logs/Nqueens_heavy.txt', in_window_size, filename, range=(.50,.60))
    # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(.4,.5))
    newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/'+filename_train, actorname=actor_name, windowsize=in_window_size, outputfile=filename)
    newSplittingInstance.extract_windows()
    histpowerprof = interface.generateHistogramPowerInfo(filename)

    ### INITIAL DATA
    histchange = [x[0] for x in histpowerprof] # load values
    powerchange = [x[1] for x in histpowerprof] # power
    sizechange = [x[2] for x in histpowerprof] # num total actors in interval

    # create all the equations from the sizechange and histchange data
    eqns = create_eqns(sizechange, histchange)
    trainX, trainY = features_labels(eqns)

    if seperate_test: # Nqueens_heavy.txt
        newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/'+filename_test, actorname=actor_name, windowsize=in_window_size, outputfile=filename)
        newSplittingInstance.extract_windows()
        histpowerprof = interface.generateHistogramPowerInfo(filename)

        ### INITIAL DATA
        histchange = [x[0] for x in histpowerprof] # load values
        powerchange = [x[1] for x in histpowerprof] # power
        sizechange = [x[2] for x in histpowerprof] # num total actors in interval

        # create all the equations from the sizechange and histchange data
        eqns2 = create_eqns(sizechange, histchange)
        testX, testY = features_labels(eqns2)
    else:
        test_ind = len(trainX) - int(test_prop * len(trainX))

        testX = trainX[test_ind:]
        testY = trainY[test_ind:]
        trainX = trainX[0:test_ind]
        trainY = trainY[0:test_ind]




    return (trainX, trainY), (testX, testY)


# Answer for nqueens
# [  1.09594154   2.88503623   3.71927777  -1.48925566  10.57336939
#    1.91006365   2.23769466  12.14960698   2.09156284  -7.82088665
#   -2.91121207   0.73506705  16.5699962    7.53777839   2.05335134
#   -3.50175638  -0.16120979   3.00514738  -2.75026329  -5.03038861
#  -13.02197047  -1.15272535   1.75750494  -0.89161274  -0.81428029
#   -7.72688371   2.56652909  -7.38764943  -0.27128653  -8.15615503
#   -0.20174837 -19.06524896  14.66686926   0.11864814 -12.87241584
#   -0.72420079   0.30015075   1.26873283 -17.63163686  -0.74602315
#    4.23297345  -0.88327187   1.2630644   -0.59011749   4.64110987
#   -5.59660922  -3.12377514   1.83287542  12.71159752  -0.60936807
#    0.94627536  -1.26180539  -1.58626963]

# Answer for Fibonacci
# [  0.76935249  11.48459212 -12.11332409  20.2810882   28.61148258
# -41.57984684 -33.70531468]

def nn():
    training, testing = calc_eqns(seperate_test=True, filename_train='Nqueens_heavy.txt', filename_test='Nqueens_heavy_2.txt')

    training_X = training[0]
    training_Y = training[1]


    features1 = {'Loads': training_X}
    labels1 = training_Y

    train = tf.data.Dataset.from_tensor_slices((dict(features1), labels1))


    testing_X  = testing[0]
    testing_Y = testing[1]


    features2 = {'Loads': testing_X}
    labels2 = testing_Y

    test = tf.data.Dataset.from_tensor_slices((dict(features2), labels2))


    def input_train():
        return train.shuffle(len(training_X)).batch(5).repeat().make_one_shot_iterator().get_next()


    def input_test():
        return test.batch(len(testing_Y)).make_one_shot_iterator().get_next()


    feature_columns = [
        tf.feature_column.numeric_column(key="Loads", shape=(len(testing_X[0])))
    ]

    # Build a DNNRegressor, with 2x20-unit hidden layers, with the feature columns
    # defined above as input.
    model = tf.estimator.DNNRegressor(
      hidden_units=[40, 40, 40], feature_columns=feature_columns,
      optimizer=tf.train.ProximalAdagradOptimizer(
        learning_rate=0.01,
        l1_regularization_strength=0.001
      )
    )

    # Train the model.
    STEPS = 50000
    model.train(input_fn=input_train, steps=STEPS)

    print("TRAINING COMPLETE")

    # Evaluate how the model performs on data it has not yet seen.
    eval_result = model.evaluate(input_fn=input_test)
    predictions = list(model.predict(input_fn=input_test))

    total = 0
    total2 = 0

    for i in range(len(testing_Y)):
        print("Prediction:", predictions[i]["predictions"][0], "Actual:", testing_Y[i])
        e = abs(predictions[i]["predictions"][0] - testing_Y[i])
        print("Error: ", e)
        total += (e**2)
        total2 += e

    print(total2/len(testing_Y))
    print("RMSE: ", (total/len(testing_Y))**0.5)
    print(eval_result)


    # The evaluation returns a Python dictionary. The "average_loss" key holds the
    # Mean Squared Error (MSE).
    average_loss = eval_result["average_loss"]

    # Convert MSE to Root Mean Square Error (RMSE).
    # print("\n" + 80 * "*")
    print("\nRMS error for the test set: ", average_loss**0.5)

    print()




nn()
