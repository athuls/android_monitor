import sys
import json
import collections
from math import pow, sqrt

HistInfo = collections.namedtuple('HistInfo', ['hist','batteryfrac','size'])

def getHistosFromFile(filename):
    data = []
    f = open(filename, 'r')
    for line in f:
        extractLine = line.strip('\n')
        #hist,power = extractLine.split('\t')
        hist,power,size = extractLine.split('\t')
        data.append(HistInfo(hist=json.loads(hist), batteryfrac=power, size=size))
    f.close()
    return data

def normalize_histos(dict_hists):
    maxi = 0
    sum_actors = []
    for i in range(0, len(dict_hists)):
        sum_actors_hist = 0
        for key in dict_hists[i]:
            sum_actors_hist += dict_hists[i][key]
            if(key > maxi):
                maxi = key
        sum_actors.append(sum_actors_hist)

    for i in range(0, len(dict_hists)):
        for j in range(0, maxi+1):
            if(j not in dict_hists[i]):
                dict_hists[i][j] = 0

    # Now we convert the frequencies to probabilities for #actors
    dict_hists_temp = []
    for i in range(0, len(dict_hists)):
        hist_temp = {}
        for j in range(0, maxi+1):
            # print(sum_actors[i])
            hist_temp[j] = dict_hists[i][j]/sum_actors[i]
        dict_hists_temp.append(dict(hist_temp))

    return dict_hists_temp

def simple_window_size(hist_1, hist_2):
    val = 0

    for key in hist_1:
        #val += abs(hist_1[key] - hist_2[key])
        #val += abs(hist_1[key] - hist_2[key]) * (key + 1)
        #val += hist_1[key] - hist_2[key]
        val += hist_1[key]
        #val += ((hist_1[key] - hist_2[key]) * (key/(maxkey)))
    return val

# This returns the expected actor count in a given window
def simple_difference(hist_1, hist_2):
    total = 0
    val = 0

    # Get max key
    maxkey = 0
    for key in hist_1:
        if(key > maxkey):
            maxkey = key

    for key in hist_1:
        # val += abs(hist_1[key] - hist_2[key])
        # if(key != 0):
        val += key * hist_1[key]
        #     val += abs(hist_1[key] - hist_2[key]) * (key)
        #val += hist_1[key] - hist_2[key]
        #val += ((hist_1[key] - hist_2[key]) * (key/(maxkey)))
        total += hist_1[key]
    return val/total

def simple_variance(hist_1, hist_2):
    total = 0
    val = 0

    # Get mean
    mean = simple_difference(hist_1, hist_2)
    for key in hist_1:
        #val += abs(hist_1[key] - hist_2[key])
        #val += abs(hist_1[key] - hist_2[key]) * (key + 1)
        #val += hist_1[key] - hist_2[key]
        val += pow(hist_1[key] - mean, 2)
        #val += ((hist_1[key] - hist_2[key]) * (key/(maxkey)))
        total += hist_1[key]
    return sqrt(val / total)