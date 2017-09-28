from .hist_interface import *

import collections
from .histogram_to_list import *
import matplotlib.pyplot as plt
import plotly as py
py.tools.set_credentials_file(username='sandur2', api_key='67gqBp1eOw7nzF3kO7RU')

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
    powerreference = float(batteryFrac[0])
    histreference = hists[0]
    maxpower=0
    maxDistHist=None
    minpower = float(batteryFrac[0])
    minDistHist=None

    #for i in range(0, len(hists)-1):
    for i in range(1, len(hists)-1):
        #powerChange = abs(float(batteryFrac[i]) - float(batteryFrac[i+1]))
        #powerChange = float(batteryFrac[i]) - float(batteryFrac[i+1])
        powerChange = float(batteryFrac[i]) - powerreference
        #histpowerprof.append(HistPowerProfile(histchange=simple_difference(hists[i], hists[i+1]), powerchange=powerChange))
        histpowerprof.append(HistPowerProfile(histchange=simple_difference(hists[i], histreference), powerchange=powerChange))
        if(abs(powerChange) > maxpower):
            maxpower = abs(powerChange)
            maxDistHist = hists[i]
        if(abs(powerChange) < minpower):
            minpower = abs(powerChange)
            minDistHist = hists[i]
    print("Maximum histogram\n")
    print(histreference)
    histreference_as_list = convertToList(histreference)
    maxHist_as_list = convertToList(maxDistHist)
    print(maxDistHist)
    minHist_as_list = convertToList(minDistHist)
    print(minDistHist)
    plt.hist([histreference_as_list, maxHist_as_list, minHist_as_list])
    plt.xlabel('Value')
    plt.ylabel('Frequency')
    fig = plt.gcf()
    plot_url = py.plotly.plot_mpl(fig, filename='mpl-basic-histogram')
    plt.show()

    #print(minDistHist)
    #print(minpower)
    #print(maxDistHist)
    #print(maxpower)
    return histpowerprof