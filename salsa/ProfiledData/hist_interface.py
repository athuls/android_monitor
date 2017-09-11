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
        if(len(dict_hists[i])-1 > maxi):
                maxi = len(dict_hists[i])-1
    for i in range(0, len(dict_hists)):
        while(len(dict_hists[i]) - 1 < maxi):
            newIndex = len(dict_hists[i])
            dict_hists[i][newIndex] = 0
    return dict_hists

def simple_difference(hist_1, hist_2):
    total = 0
    val = 0
    for key in hist_1:
        val += abs(hist_1[key] - hist_2[key])
        total += 1.0
    return val / total
