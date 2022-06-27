from __future__ import division
import preprocess_logs as ppl
import re
import sys
import numpy as np

fileName = str(sys.argv[1])
train_file=str(sys.argv[2])
avg_durs_per_config_only=bool(sys.argv[3])
filter_boundary_records=bool(sys.argv[4])

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

filtered_interval_durs=ppl.extract_battery_interval_durations(fileName,average_only=True,filter_boundary_recs=filter_boundary_records)
print("Printing battery interval durations:")
print(filtered_interval_durs)
print("------------Printing battery interval durations-----------")

avg_op_dur=0.0
if avg_durs_per_config_only:
	sum_op_durs=0
	count_op_durs=0
	effective_count_op_durs=0
	total_op_durs=len(operator_durations)
	for key in operator_durations:
		count_op_durs+=1
		if filter_boundary_records:
			if not (count_op_durs==1 or count_op_durs==2 or count_op_durs==total_op_durs):
				sum_op_durs+=operator_durations[key]
				effective_count_op_durs+=1
		else:
			sum_op_durs+=operator_durations[key]			
			effective_count_op_durs+=1
	avg_op_dur=sum_op_durs/effective_count_op_durs

# open training file in append mode
record_count=0
total_records=len(filtered_interval_durs)
with open(train_file,"a") as train_fp:
	if avg_durs_per_config_only:
		avg_op_dur_sec=avg_op_dur/1000000000	
		interval_durs_first_val=list(filtered_interval_durs.items())[0][1]
		train_fp.write(str(avg_op_dur_sec)+","+str(interval_durs_first_val)+"\n")
		print("avg interval duration: "+str(interval_durs_first_val)+", avg operator duration: "+str(avg_op_dur_sec)+"\n")
	else:
		for key in sorted(filtered_interval_durs):
			record_count+=1
			print("bat:" + str(key)+", interval duration:" + str(filtered_interval_durs[key])+", operator_duration:"+str(operator_durations[key]))
			op_duration_in_secs=operator_durations[key]/1000000000
			train_fp.write(str(op_duration_in_secs)+","+str(filtered_interval_durs[key])+"\n")
#			train_fp.write(str(key)+","+str(interval_durs[key])+","+str(operator_durations[key])+"\n")
