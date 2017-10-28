# Similar to histograms.py, but each histogram is created on percent drop
# param 1: log file to be processed
# param 2: # of parts to divide each battery drop interval into
# ex: python histograms.py log_heavy_clean.txt 5

# TODO:
# 1) What happens if our sliding window spans 3 battery drop ranges?

import os
import sys
import json
import collections
from .interface_test import *
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
        if(drop1Total-drop1WindowSize+drop2WindowSize == 0):
            print(drop1Total, drop1WindowSize, drop2WindowSize)
        batteryPower = batteryFrac/(drop1Total - drop1WindowSize + drop2WindowSize)
        #################################################################

        #print(batteryFrac, batteryPower)
        histograms.append(HistInfo(hist=dict(hist),batteryfrac=batteryPower, size=(drop1Total - drop1WindowSize + drop2WindowSize)))
        index_1 += int1
        index_2 += int2
    return histograms

def generatePlotDataForHistProfile(filename, divs):
    vals_per_drop = []

    curr = []
    curr_bat = None

    with open(filename) as fp:
        for line in fp:
            text = "Battery level is "
            bat_ind = line.find(text)
            if(bat_ind != -1):
                bat = float(line[bat_ind+len(text):].split(' ')[0])
                if(curr_bat == None): # first iteration
                    curr_bat = bat
                if(bat < curr_bat):
                    vals_per_drop.append(curr)
                    curr = []
                    curr_bat = bat

            ind = line.find('$State')

            if(line[0] == '[' and line.find('no') != -1):
                curr.append(0)
            elif(ind != -1):
                comma_ind = line.find(',', ind)
                num = int(line[ind + 8 : comma_ind])
                curr.append(num)

    histograms = []

    # skipping the first index, since it might not be full percent drop
    for ind in range(1, len(vals_per_drop)-1):
        histograms += countsToHists(vals_per_drop[ind], vals_per_drop[ind+1], divs, ind)


    # append last histogram individually, since not handled by function
    hist = {}
    for val in vals_per_drop[-1]:
        if(val in hist):
            hist[val] += 1
        else:
            hist[val] = 1

    # Ignore the last one for testing
    # histograms.append(HistInfo(hist=hist, batteryfrac=1/len(vals_per_drop[-1]), size=len(vals_per_drop[-1])))

    dir = os.path.dirname(__file__)
    filename = os.path.join(dir, '../output/histogram/hist_percent_incr.txt')

    with open(filename, 'w') as outfile:
        for hists in histograms:
            outfile.write(json.dumps(hists.hist))
            outfile.write("\t"+str(hists.batteryfrac))
            outfile.write("\t"+str(hists.size))
            outfile.write("\n")

    # Now that we have histogram information, generate battery drops
    histpowerprof = generateHistogramPowerInfo(filename)
    return histpowerprof