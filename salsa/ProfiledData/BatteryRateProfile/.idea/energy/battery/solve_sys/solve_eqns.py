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


def calc_eqns():
    dir = os.path.dirname(__file__)
    filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
    in_window_size = 3
    actor_name='demo1.Nqueen'
    # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling('../mobile_logs/Nqueens_heavy.txt', in_window_size, filename, range=(.50,.60))
    # newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(.4,.5))
    newSplittingInstance = fixed_size.SplitFixedWindowsTumbling(filename='../mobile_logs/Nqueens_heavy.txt', actorname=actor_name, windowsize=in_window_size, outputfile=filename, range=(0.40,0.6))
    newSplittingInstance.extract_windows()
    histpowerprof = interface.generateHistogramPowerInfo(filename)

    ### INITIAL DATA
    histchange = [x[0] for x in histpowerprof] # load values
    powerchange = [x[1] for x in histpowerprof] # power
    sizechange = [x[2] for x in histpowerprof] # num total actors in interval


    # create all the equations from the sizechange and histchange data
    eqns = create_eqns(sizechange, histchange)



    # initialize the matrix

    loads = set()
    for pair in eqns:
        for key in pair[0]:
            loads.add(key)
    loads = sorted(loads)

    M = np.zeros((len(eqns), len(loads)))


    # Add data to the matrix
    for i in range(len(eqns)):
        const_dict = eqns[0][0]
        for j in range(len(loads)):
            if loads[j] in const_dict:
                M[i][j] = loads[j] * const_dict[loads[j]]
            else:
                M[i][j] = 0

    # right side of linear system
    c = np.zeros(len(eqns))
    for j in range(len(c)):
        c[j] = 1.0 / eqns[j][1]

    # solve the system
    x, res, rank, s = np.linalg.lstsq(M,c)
    print(x)




calc_eqns()
