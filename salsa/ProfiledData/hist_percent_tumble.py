# Similar to histograms.py, but each histogram is created on percent drop
# param 1: log file to be processed
import sys
import json


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


for act_counts in vals_per_drop:
    hist = {}
    for val in act_counts:
        if(val in hist):
            hist[val] += 1
        else:
            hist[val] = 1
    histograms.append(hist)




with open('hist_percent_tumble.txt', 'w') as outfile:
    for hists in histograms:
        outfile.write(str(hists))
        outfile.write("\n")
