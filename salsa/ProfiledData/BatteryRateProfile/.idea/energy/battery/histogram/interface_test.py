from .hist_interface import *

import collections
HistPowerProfile = collections.namedtuple('HistPowerProfile', ['histchange','powerchange'])

# param 1: file name of the histogram created

def generateHistogramPowerInfo(filename):
    #filename = sys.argv[1]

    histsInfo = getHistosFromFile(filename)
    hists = [x[0] for x in histsInfo]
    batteryFrac = [x[1] for x in histsInfo]

    for i in range(0, len(hists)):
        hists[i] = {int(k):int(v) for k,v in hists[i].items()}
    hists = normalize_histos(hists)

    histpowerprof = []
    for i in range(0, len(hists)-1):
        powerChange = abs(float(batteryFrac[i]) - float(batteryFrac[i+1]))
        histpowerprof.append(HistPowerProfile(histchange=simple_difference(hists[i], hists[i+1]), powerchange=powerChange))
        #print hists[0]
        #print hists[1]
    return histpowerprof