import sys
import json
def getHistosFromFile(filename):
    data = []
    f = open(filename, 'r')
    for line in f:
        data.append(json.loads(line.strip('\n')))
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
    for key in hist_1:
        val += abs(hist_1[key] - hist_2[key])
        total += 1.0
    return val / total
