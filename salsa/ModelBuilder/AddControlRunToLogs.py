import sys

if len(sys.argv) != 3:
	print("Input arguments: control run file name, number of input dimensions")
	sys.exit(0)

line_count = 0
fileName = sys.argv[1]
inpdimensions = int(sys.argv[2])
with open(fileName,"r") as fp:
	for line in fp:
		dataItems=line.split(',')
		for i in range(0,inpdimensions):
			print("0,", end="")
		print(dataItems[0].rstrip())
		#print(str(idleTime/(idleTime+activeTime))+","+str(idleTime))
