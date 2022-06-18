import re
import sys
import numpy as np

fileName = str(sys.argv[1])

print(fileName)
#stringtoCheck = "detectInImage"
stringtoCheck = "processLatestImage"
initBat = 1.0
if len(sys.argv) > 2:
	stringtoCheck = str(sys.argv[2])

if len(sys.argv) > 3:
	initBat = float(sys.argv[3])

myList= []
myTots = []
dataVal = []


def processList(bat):
	if len(myList) == 0:
		dataVal.append((bat,-1.0,-1.0, -1.0, -1.0))
		#return
	else:
		avg = np.mean(myList)
		mstd = np.std(myList)
		mymax = max(myList)
		mymin = min(myList)
		dataVal.append((bat,avg,mstd, mymin,mymax))
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
	print("bat :" + str(val[0])+ ",mean :"+str(val[1])+ ",std :"+str(val[2])+",min :"+str(val[3])+",max :"+str(val[4]))
