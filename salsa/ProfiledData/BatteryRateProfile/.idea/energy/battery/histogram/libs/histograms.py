# param 1: log file to be processed
# param 2: time interval in seconds
# param 3: number of samples per second

import json
import re
import os
from datetime import datetime, timedelta

def generate_histograms(fileName, interval, samples_per_sec):
    actor_counts = []

    #############Debugging##########
    previous_datetime = None
    current_datetime = None
    #############Debugging##########
    with open(fileName) as fp:
        for line in fp:
            #############Debugging##########
            matchObj = re.match(r'\[(.*)\] Battery level is (\d+\.\d+)', line, re.I)
            if matchObj:
                current_datetime = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
                #if previous_datetime is not None:
                #       if (current_datetime - previous_datetime).total_seconds() > timedelta(seconds=1).total_seconds():
                #               print (current_datetime - previous_datetime).total_seconds()
                previous_datetime = current_datetime
            #############Debugging##########

            ind = line.find('$State')
            if(line[0] == '[' and line.find('no') != -1):
                actor_counts.append(0)
            elif(ind != -1):
                comma_ind = line.find(',', ind)
                num = int(line[ind + 8 : comma_ind])
                actor_counts.append(num)

    size = len(actor_counts)

    full_int = interval * samples_per_sec

    histos = []

    for i in range(0, size, full_int):
        maxi = 0
        #print i
        for j in range(i, min(i+full_int, size)):
            if(actor_counts[j] > maxi):
                maxi = actor_counts[j]
        #print maxi
        histogram = [0] * (maxi+1)
        for j in range(i, min(i+full_int, size)):
            num_actors = actor_counts[j]
            histogram[num_actors] += 1
        histos.append(histogram)

    dict_hists = []
    start = 0
    for hi in histos:
        dict_hists.append(dict(enumerate(hi)))

    dir = os.path.dirname(__file__)
    filename = os.path.join(dir, '../../output/histogram/histograms.txt')


    with open(filename, 'w') as outfile:
        for hists in dict_hists:
            outfile.write(str(hists))
            outfile.write("\n")

    return dict_hists