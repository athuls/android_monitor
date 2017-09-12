# Similar to histograms.py, but each histogram is created on percent drop
# param 1: log file to be processed
# param 2: # of parts to divide each histogram into
# ex: python histograms.py log_heavy_clean.txt 5

import os
import sys
import json

divs = int(sys.argv[2])

def countsToHists(counts1, counts2):
    int1 = len(counts1) / divs
    int2 = len(counts2) / divs

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
        histograms.append(dict(hist))
        for j in range(index_1, index_1+int1):
            hist[counts1[j]] -= 1

        for j in range(index_2, index_2+int2):
            if counts2[j] in hist:
                hist[counts2[j]] += 1
            else:
                hist[counts2[j]] = 1
        index_1 += int1
        index_2 += int2
    return histograms





vals_per_drop = []

curr = []
curr_bat = None

with open(sys.argv[1]) as fp:
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

    histograms += countsToHists(vals_per_drop[ind], vals_per_drop[ind+1])


# append last histogram individually, since not handled by function
hist = {}
for val in vals_per_drop[-1]:
    if(val in hist):
        hist[val] += 1
    else:
        hist[val] = 1

histograms.append(hist)

dir = os.path.dirname(__file__)
filename = os.path.join(dir, '../scripts_output/histogram/hist_percent_incr.txt')

with open(filename, 'w') as outfile:
    for hists in histograms:
        outfile.write(json.dumps(hists))
        outfile.write("\n")