import os
import sys
import json
import collections
from numpy import *
import datetime
import statistics

# Calculates the mean and standard deviation of duration for 1% battery drop intervals, based on raw log data

g_datetime_format = "%a %b %d %H:%M:%S CST %Y"

def read_file(filename):
	numSamples=0
        samplesPerDrop = []
	durationsList = []
	timeStamp=[]
	prevBat=None
        with open(filename) as fp:
            for line in fp:
                text = "Battery level is "
                bat_ind = line.find(text)
                if(bat_ind != -1):
                    currBat = float(line[bat_ind+len(text):].split(' ')[0])
                    timeStamp.append(line[1:bat_ind-2])
		    if(prevBat==None):
			prevBat=currBat
                    if(currBat < prevBat):
                        samplesPerDrop.append(numSamples)
			startTime = datetime.datetime.strptime(timeStamp[0], g_datetime_format)
			endTime = datetime.datetime.strptime(timeStamp[-1], g_datetime_format)
			duration = endTime-startTime
			durationInSecs=datetime.timedelta.total_seconds(duration)
			durationsList.append(durationInSecs)
			timeStamp=[]
                    	timeStamp.append(line[1:bat_ind-2])
			numSamples=0
		    prevBat=currBat
		    numSamples+=1

	# The last sample may be incomplete so will be discarded
        #samplesPerDrop.append(numSamples)

	# Print durations computed from timestamp
	print("Printing durations from timestamp")
	for duration_val in durationsList[1:]:
		print(duration_val)
	
	print("Printing durations from samples")
	# Print raw battery drop values
	for sampleDrop in samplesPerDrop[1:]:
		print(sampleDrop)
	print("Summary from timestamps")
        print("Average battery drop length is ",statistics.mean(durationsList[1:]))
	print("Std dev battery drop length is ",statistics.stdev(durationsList[1:]))
	print("Summary from samples")
        print("Average battery drop length is ",statistics.mean(samplesPerDrop[1:]))
	print("Std dev battery drop length is ",statistics.stdev(samplesPerDrop[1:]))


filename=sys.argv[1]
read_file(filename)
