# param 1: log file to be processed
# param 2: time interval in seconds

import sys
import json
import re
import bisect
from datetime import datetime
from datetime import timedelta
from battery_change_intervals import LogIntervalSplitter

intervalSplitter = LogIntervalSplitter()
batteryChangeIntervals=intervalSplitter.returnBatteryChangeIntervals()
actor_counts = []
interval = int(sys.argv[2])
previous_datetime = intervalSplitter.start_datetime
next_datetime = intervalSplitter.start_datetime + timedelta(seconds=interval)
intervalBatteryFraction = []
while (next_datetime <= intervalSplitter.end_datetime):
    i = bisect.bisect(batteryChangeIntervals, previous_datetime)
    j = bisect.bisect(batteryChangeIntervals, next_datetime)
    lenBatteryChangeIntervals = len(batteryChangeIntervals)
    if(i == j and (next_datetime == batteryChangeIntervals[lenBatteryChangeIntervals-2] or j < (lenBatteryChangeIntervals - 1))):
        print(previous_datetime, " ", i, " ", i-1)
        batteryduration = batteryChangeIntervals[i] - batteryChangeIntervals[i-1]
        fraction = interval/batteryduration.total_seconds()
        print(fraction)
        intervalBatteryFraction.append(fraction)
    elif(i == j-1 and (next_datetime == batteryChangeIntervals[lenBatteryChangeIntervals-2] or j < (lenBatteryChangeIntervals - 1))):
        print ("diff ", previous_datetime, " ", i, " ", i - 1, " ", next_datetime, " ", j, " ", j-1)
        intervalduration1 = batteryChangeIntervals[i] - previous_datetime
        batteryduration1 = batteryChangeIntervals[i] - batteryChangeIntervals[i-1]
        intervalduration2 = next_datetime - batteryChangeIntervals[j-1]
        batteryduration2 = batteryChangeIntervals[j] - batteryChangeIntervals[j-1]
        fraction = (intervalduration1.total_seconds()/batteryduration1.total_seconds()) + (intervalduration2.total_seconds()/batteryduration2.total_seconds())
        print(fraction)
        intervalBatteryFraction.append(fraction)
        previous_datetime = next_datetime
    next_datetime =  next_datetime + timedelta(seconds=interval)