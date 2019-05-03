import sys

line_count = 0
with open(sys.argv[1]) as fp:
	for line in fp:
		if line_count == 0:
			line_count += 1
			continue
		dataItems=line.split(',')
		activeTime = 0
		for i in range(len(dataItems)-1):
			activeTime+=float(dataItems[i])
		idleTime=float(dataItems[-1])-activeTime
		if((idleTime/(idleTime+activeTime) <= 0.3)):
			print line
		#print(str(idleTime/(idleTime+activeTime))+","+str(idleTime))
