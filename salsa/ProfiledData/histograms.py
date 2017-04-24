# param 1: log file to be processed
# param 2: time interval in seconds

import sys
import json


actor_counts = []


with open(sys.argv[1]) as fp:
    for line in fp:
        ind = line.find('$State')
        if(line[0] == '[' and line.find('no') != -1):
            actor_counts.append(0)
        elif(ind != -1):
            comma_ind = line.find(',', ind)
            num = int(line[ind + 8 : comma_ind])
            actor_counts.append(num)

interval = int(sys.argv[2])
size = len(actor_counts)

histos = []

for i in range(0, size, interval):
    maxi = 0
    for j in range(i, min(i+interval, size)):
        #print actor_counts[j]
        if(actor_counts[j] > maxi):
            maxi = actor_counts[j]
    #print maxi
    histogram = [0] * (maxi+1)
    for j in range(i, min(i+interval, size)):
        num_actors = actor_counts[j]
        histogram[num_actors] += 1
    histos.append(histogram)


dict_hists = []
start = 0
for hi in histos:
    dict_hists.append(dict(enumerate(hi)))

with open('histograms.txt', 'w') as outfile:
    for hists in dict_hists:
        outfile.write(str(hists))
        outfile.write("\n")
