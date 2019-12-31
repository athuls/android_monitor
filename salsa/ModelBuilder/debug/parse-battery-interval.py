from sys import argv

import numpy as np
import datetime

filename = argv[1]
f = open(filename, "r")
lines = f.readlines()
f.close()

g_datetime_format = "%a %b %d %H:%M:%S GMT+05:30 %Y"

data = []
prevTime = None 
for line in lines[:]:
	batteryEntryTxt = "DEBUG_BATTERY_LEVEL"
	batteryEntry = line.find(batteryEntryTxt)
	if(batteryEntry != -1):
		battVal = line.split(' ')[-1]
		currBat = float(battVal)
		currTimeStr = line[batteryEntry+len(batteryEntryTxt)+2:len(line)-len(battVal)-1]
		currTime = datetime.datetime.strptime(currTimeStr, g_datetime_format)
		duration = 0
		if(prevTime is None):
			prevTime = currTime
		else:
			timeDur = currTime - prevTime
			intervalLength = datetime.timedelta.total_seconds(timeDur)
			print(str(intervalLength+1) + "," + str(currBat))
			prevTime = currTime
