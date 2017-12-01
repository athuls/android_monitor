

# TODO:
# 1) What happens if our sliding window spans 3 battery drop ranges?

import os
import sys
import json
import collections
from .interface_test import *
from .file_reader import *

import csv



from pydoc import help

def countsToHists(counts1, counts2, divs, ind):
    int1 = len(counts1) // divs
    int2 = len(counts2) // divs

    ########################################################
    # This is for capturing battery drop fractions (time wise) with each histogram
    drop1Total = len(counts1)
    drop2Total = len(counts2)
    drop1WindowSize = 0
    drop2WindowSize = 0
    ########################################################
    ########################################################
    # This is for capturing battery drop fractions (number of actors wise) with each histogram
    actors1Total = sum(counts1)
    actors2Total = sum(counts2)
    actors1Window = 0
    actors2Window = 0
    ########################################################

    histograms = []

    hist = {}
    for val in counts1:
        if(val in hist):
            hist[val] += 1
        else:
            hist[val] = 1

    index_1 = 0
    index_2 = 0
    for i in range(0, divs):
        for j in range(index_1, index_1+int1):
            hist[counts1[j]] -= 1
            drop1WindowSize += 1
            actors1Window += counts1[j]

        for j in range(index_2, index_2+int2):
            if counts2[j] in hist:
                hist[counts2[j]] += 1
            else:
                hist[counts2[j]] = 1
            drop2WindowSize += 1
            actors2Window += counts2[j]

        #################################################################
        # This fractions the battery drops in an interval using time only
        # batteryFrac = ((drop1Total - drop1WindowSize)/drop1Total) + (drop2WindowSize/drop2Total)
        # batteryPower = batteryFrac/(drop1Total - drop1WindowSize + drop2WindowSize)
        #################################################################

        #################################################################
        # This fractions the battery drops in an interval using number of actors in the interval
        if(actors1Total == 0 or actors2Total == 0):
            if(actors1Total == 0 and actors2Total == 0):
                batteryFrac = 0
            elif(actors2Total == 0):
                batteryFrac = (actors1Total - actors1Window)/actors1Total
            elif(actors1Total == 0):
                batteryFrac = actors2Window/actors2Total
        else:
            batteryFrac = ((actors1Total - actors1Window)/actors1Total) + (actors2Window/actors2Total)
            # batteryPower = batteryFrac/(actors1Total - actors1Window + actors2Window)
        batteryPower = 0
        if(drop1Total-drop1WindowSize+drop2WindowSize == 0):
            print(drop1Total, drop1WindowSize, drop2WindowSize)
        else:
            batteryPower = batteryFrac/(drop1Total - drop1WindowSize + drop2WindowSize)
        #################################################################

        #print(batteryFrac, batteryPower)
        histograms.append(HistInfo(hist=dict(hist),batteryfrac=batteryFrac, size=(drop1Total - drop1WindowSize + drop2WindowSize)))
        index_1 += int1
        index_2 += int2
    return histograms

def generatePlotDataForHistProfileMultipleActors(filename, divs):
    vals_per_drop = parseLog(filename, ["demo1.Nqueens", "demo1.Nqueens2"])
    histpowerprof = {}
    for key, value in vals_per_drop.items():
        # skipping the first index, since it might not be full percent drop
        histograms = []
        for ind in range(1, len(value)-1):
            histograms += countsToHists(value[ind], value[ind+1], divs, ind)


        # append last histogram individually, since not handled by function
        hist = {}
        for val in value[-1]:
            if(val in hist):
                hist[val] += 1
            else:
                hist[val] = 1

        # Ignore the last one for testing
        # histograms.append(HistInfo(hist=hist, batteryfrac=1/len(vals_per_drop[-1]), size=len(vals_per_drop[-1])))

        dir = os.path.dirname(__file__)
        filename = os.path.join(dir, '../output/histogram/hist_percent_incr.'+key+'.txt')

        with open(filename, 'w') as outfile:
            for hists in histograms:
                outfile.write(json.dumps(hists.hist))
                outfile.write("\t"+str(hists.batteryfrac))
                outfile.write("\t"+str(hists.size))
                outfile.write("\n")

        # Now that we have histogram information, generate battery drops
        histpowerprof[key] = generateHistogramPowerInfo(filename)
    return histpowerprof
