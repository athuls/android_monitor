# param 1: log file to be processed
# param 2: time interval in seconds

import sys
import json
import re
import bisect
from datetime import datetime
from datetime import timedelta

actor_counts = []
interval = int(sys.argv[2])
batteryChangeIntervals = []
currentBattery = 0
previousBattery = 0
start_datetime = None
end_datetime = None
with open(sys.argv[1]) as fp:
    for line in fp:
	matchObj = re.match(r'\[(.*)\] Battery level is (\d+\.\d+)', line, re.I)
	if matchObj:
		if(start_datetime is None):
			start_datetime = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
		end_datetime = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
		currentBattery = matchObj.group(2)
		if (currentBattery != previousBattery):
			previousBattery = currentBattery
			datetime_obj = datetime.strptime(matchObj.group(1), "%b %d,%Y %H:%M:%S")
			batteryChangeIntervals.append(datetime_obj)
			#print currentBattery, " ", matchObj.group(1)

batteryChangeIntervals.append(end_datetime)
previous_datetime = start_datetime
next_datetime = start_datetime + timedelta(seconds=interval)
#print start_datetime, " ", next_datetime, " ", end_datetime
#print len(batteryChangeIntervals), end_datetime
intervalBatteryFraction = []
while (next_datetime <= end_datetime):
	i = bisect.bisect(batteryChangeIntervals, previous_datetime)
	j = bisect.bisect(batteryChangeIntervals, next_datetime)
	#print previous_datetime, " ", i, " ", next_datetime, " ", j
	lenBatteryChangeIntervals = len(batteryChangeIntervals)
	if(i == j and (next_datetime == batteryChangeIntervals[lenBatteryChangeIntervals-2] or j < (lenBatteryChangeIntervals - 1))):
		#print previous_datetime, " ", i, " ", i-1
		batteryduration = batteryChangeIntervals[i] - batteryChangeIntervals[i-1]
		fraction = interval/batteryduration.total_seconds()
		print fraction
		intervalBatteryFraction.append(fraction)
	elif(i == j-1 and (next_datetime == batteryChangeIntervals[lenBatteryChangeIntervals-2] or j < (lenBatteryChangeIntervals - 1))):
		#print "diff ", previous_datetime, " ", i, " ", i - 1, " ", next_datetime, " ", j, " ", j-1
		intervalduration1 = batteryChangeIntervals[i] - previous_datetime
		batteryduration1 = batteryChangeIntervals[i] - batteryChangeIntervals[i-1]
		intervalduration2 = next_datetime - batteryChangeIntervals[j-1]
		batteryduration2 = batteryChangeIntervals[j] - batteryChangeIntervals[j-1]
		fraction = (intervalduration1.total_seconds()/batteryduration1.total_seconds()) + (intervalduration2.total_seconds()/batteryduration2.total_seconds())
		print fraction
		intervalBatteryFraction.append(fraction)
	previous_datetime = next_datetime
	next_datetime =  next_datetime + timedelta(seconds=interval)

#datetime_query = datetime.strptime("Apr 04,2017 10:23:10", "%b %d,%Y %H:%M:%S")
#i = bisect.bisect(batteryChangeIntervals, datetime_query)
#print i
#print batteryChangeIntervals[i-1], " ", batteryChangeIntervals[i], " ", batteryChangeIntervals[i+1]
		#print matchObj.group(1)
	#else:
		#print "Nonel"
        #ind = line.find('$State')
        #if(line[0] == '[' and line.find(']') != -1):
        #    actor_counts.append(0)
        #elif(ind != -1):
        #    comma_ind = line.find(',', ind)
        #    num = int(line[ind + 8 : comma_ind])
        #    actor_counts.append(num)

#interval = int(sys.argv[2])
#size = len(actor_counts)

#histos = []

#for i in range(0, size, interval):
#    maxi = 0
#    for j in range(i, min(i+interval, size)):
        #print actor_counts[j]
#        if(actor_counts[j] > maxi):
#            maxi = actor_counts[j]
    #print maxi
#    histogram = [0] * (maxi+1)
#    for j in range(i, min(i+interval, size)):
#        num_actors = actor_counts[j]
#        histogram[num_actors] += 1
#    histos.append(histogram)


#dict_hists = []
#start = 0
#for hi in histos:
#    dict_hists.append(dict(enumerate(hi)))

#with open('histograms.txt', 'w') as outfile:
#    for hists in dict_hists:
#        outfile.write(str(hists))
#        outfile.write("\n")
