# param 1: log file to be processed
# param 2: time interval in seconds
# param 3: number of samples per second

import json
import re
from datetime import datetime

def generate_histograms(fileName, interval, samples_per_sec):
	actor_counts = []
	
	previous_datetime = None
	current_datetime = None
	with open(fileName) as fp:
	    for line in fp:
	        ind = line.find('$State')
	        if(line[0] == '[' and line.find('no') != -1):
	            actor_counts.append(0)
		    print len(actor_counts)
	        elif(ind != -1):
	            comma_ind = line.find(',', ind)
	            num = int(line[ind + 8 : comma_ind])
	            actor_counts.append(num)
		    print len(actor_counts)
	
	size = len(actor_counts)
	print size
	
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
	
	with open('histograms.txt', 'w') as outfile:
	    for hists in dict_hists:
	        outfile.write(str(hists))
	        outfile.write("\n")
	
	return dict_hists
