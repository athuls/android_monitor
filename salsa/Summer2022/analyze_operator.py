import re
import sys
import numpy as np
import preprocess_logs as ppl

fileName = str(sys.argv[1])
train_file=str(sys.argv[2])

print("raw file: "+fileName)

#stringtoCheck = "detectInImage"
stringtoCheck = "processLatestImage"
initBat = 1.0

myList= []
myTots = []
dataVal = []
operator_durations={}

def processList(bat):
	if len(myList) == 0:
		dataVal.append((bat,-1.0,-1.0,-1.0, -1.0, -1.0))
		#return
	else:
		avg = np.mean(myList)
		mstd = np.std(myList)
		mymax = max(myList)
		mymin = min(myList)
		mysum = sum(myList)
		dataVal.append((bat,mysum,avg,mstd, mymin,mymax))
	myList.clear()


def processData(data):
	data = data.lstrip('(')
	data = data.split(',')
	time = data[0]
	if stringtoCheck in data[1]:
		myList.append(float(time))	
		myTots.append(float(time))

with open(fileName, 'r') as fp:
	line = fp.readline()
	while (line):
		if 'Segment' in line:
			lineT = line.split('Segment')
			battery = lineT[0].split('is ')[1].split(' ')[0]
			battery = float(battery)
			#print(battery)
			if initBat != battery:
				processList(initBat)
				initBat = battery
			#print(lineT[0])
			myData = lineT[1].lstrip(' [').rstrip().rstrip(']')
			#print(myData)
			myLen = len(myData)
			#print(myLen)
			curr = 0
			while curr >= 0 and curr< myLen:
				myIdx = myData.find('])',curr)
				if myIdx == -1:
					break
				processData(myData[curr:myIdx+1])
				curr = myIdx +4
				#print("Dip "+myData[curr:])	
		line = fp.readline()
		
processList(initBat)
# print the data
totavg = np.mean(myTots)
totstd = np.std(myTots)
print("Total data avg :"+str(totavg)+",std :"+str(totstd) +",min :"+ str (min(myTots)) + ",max :"+ str ( max(myTots)))

for val in dataVal:
	operator_durations[val[0]]=val[1]
	print("bat :" + str(val[0])+ ",sum :"+str(val[1])+ ",mean :"+str(val[2])+",std :"+str(val[3])+",min :"+str(val[4])+", max:"+str(val[5]))

interval_durs=ppl.extract_battery_interval_durations(fileName)
print(interval_durs)

# open training file in append mode
with open(train_file,"a") as train_fp:
	for key in interval_durs:
		print("bat:" + str(key)+", interval duration:" + str(interval_durs[key])+", operator_duration:"+str(operator_durations[key]))
		train_fp.write(str(key)+","+str(interval_durs[key])+","+str(operator_durations[key])+"\n")
