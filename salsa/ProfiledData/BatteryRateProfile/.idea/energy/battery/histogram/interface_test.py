from .hist_interface import *

import collections
from .histogram_to_list import *
import matplotlib.pyplot as plt
import plotly as py
py.tools.set_credentials_file(username='sandur2', api_key='67gqBp1eOw7nzF3kO7RU')

HistPowerProfile = collections.namedtuple('HistPowerProfile', ['histchange','powerchange','size'])

# param 1: file name of the histogram created

def generateHistogramPowerInfo(filename):
    histsInfo = getHistosFromFile(filename)
    hists = [x[0] for x in histsInfo]
    batteryFrac = [x[1] for x in histsInfo]
    sizes = [x[2] for x in histsInfo]

    for i in range(0, len(hists)):
        hists[i] = {int(k):int(v) for k,v in hists[i].items()}
    hists = normalize_histos(hists)
    histpowerprof = []
    powerreference = float(batteryFrac[0])
    histreference = hists[0]
    maxpower=0
    maxDistHist=None
    minpower = float(batteryFrac[30])
    minDistHist=None
    maxSize=0
    minSize=0
    maxi = 0
    mini=0

    for i in range(0, len(hists)-1):
        #print(hists[i])
        # powerChange = abs(float(batteryFrac[i]) - float(batteryFrac[i+1]))
        #powerChange = float(batteryFrac[i]) - float(batteryFrac[i+1])
        powerChange = float(batteryFrac[i]) - powerreference
        # powerChange = float(batteryFrac[i]) - 0
        #histpowerprof.append(HistPowerProfile(histchange=simple_difference(hists[i], hists[i+1]), powerchange=powerChange, size=sizes[i]))
        histpowerprof.append(HistPowerProfile(histchange=simple_difference(hists[i], histreference), powerchange=powerChange, size=sizes[i]))
        if(abs(powerChange) > maxpower):
            maxpower = abs(powerChange)
            maxDistHist = hists[i]
            maxSize=sizes[i]
            maxi = i
        if(abs(powerChange) < minpower):
            minpower = abs(powerChange)
            minDistHist = hists[i]
            minSize=sizes[i]
            mini=i
    print("Maximum histogram\n")
    #print(histreference)
    #histreference_as_list = convertToList(histreference)
    maxHist_as_list = convertToList(maxDistHist)
    #print(maxDistHist)
    # print("[max power] Battery drop interval", maxSize)
    # print("max hist interval index " + str(maxi))
    minHist_as_list = convertToList(minDistHist)
    #print(minDistHist)
    # print("[min power] Battery drop interval", minSize)
    # print("min hist interval index " + str(mini))

    #################################################################
    # Now that we have min and max hists, find the closest hists to
    # them over time
    # for i in range(0,len(hists)):
    #     # print(i, simple_variance(hists[i], maxDistHist))
    #     if (simple_difference(hists[i], maxDistHist) == 0):
    #         print(hists[i])
    #################################################################

    #plt.hist([histreference_as_list, maxHist_as_list, minHist_as_list])
    plt.hist([maxHist_as_list, minHist_as_list])
    plt.xlabel('Value')
    plt.ylabel('Frequency')
    fig = plt.gcf()
    #plot_url = py.plotly.plot_mpl(fig, filename='mpl-basic-histogram')
    plt.show()

    #print(minDistHist)
    #print(minpower)
    #print(maxDistHist)
    #print(maxpower)
    return histpowerprof