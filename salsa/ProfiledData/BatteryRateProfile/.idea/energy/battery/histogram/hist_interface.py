import sys
import json
import collections

HistInfo = collections.namedtuple('HistInfo', ['hist','batteryfrac'])

def getHistosFromFile(filename):
    data = []
    f = open(filename, 'r')
    for line in f:
        extractLine = line.strip('\n')
        hist,power = extractLine.split('\t')
        data.append(HistInfo(hist=json.loads(hist), batteryfrac=power))
    f.close()
    return data

def normalize_histos(dict_hists):
    maxi = 0
    for i in range(0, len(dict_hists)):
        for key in dict_hists[i]:
            if(key > maxi):
                maxi = key
    for i in range(0, len(dict_hists)):
        for j in range(0, maxi+1):
            if(j not in dict_hists[i]):
                dict_hists[i][j] = 0
    return dict_hists

def simple_difference(hist_1, hist_2):
    total = 0
    val = 0

    # Get max key
    maxkey = 0
    for key in hist_1:
        if(key > maxkey):
            maxkey = key

    for key in hist_1:
        #val += abs(hist_1[key] - hist_2[key])
        #val += abs(hist_1[key] - hist_2[key]) * (key + 1)
        val += hist_1[key] - hist_2[key]
        #val += ((hist_1[key] - hist_2[key]) * (key/(maxkey)))
        total += 1.0
    return val / total