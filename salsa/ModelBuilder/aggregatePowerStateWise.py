#!/usr/bin/python
import sys
import numpy

highAvg = []
lowAvg = []
noneAvg = []

highInputs = []
lowInputs = []
noneInputs = []

highDist = 0
lowDist = 0
noneDist = 0

with open(sys.argv[1]) as fobj:
	for line in fobj:
		row = line.split(',')
		if(row[-1].rstrip() == "high"):
			#highAvg += float(row[len(row) - 2])
			highAvg.append(float(row[len(row) - 2]))
			highInputs.append(row[:-2])
		elif(row[-1].rstrip() == "low"):
			#lowAvg += float(row[len(row) - 2])
			lowAvg.append(float(row[len(row) - 2]))
			lowInputs.append(row[:-2])
		else:
			#noneAvg += float(row[len(row) - 2])
			noneAvg.append(float(row[len(row) - 2]))
			noneInputs.append(row[:-2])
		
highInputs = numpy.array(highInputs, dtype = numpy.float32)
lowInputs = numpy.array(lowInputs, dtype = numpy.float32)
noneInputs = numpy.array(noneInputs, dtype = numpy.float32)

for i in range(len(highInputs)):
	for j in range(i, len(highInputs)):
		highDist += numpy.sum(numpy.abs(highInputs[i] - highInputs[j]))

for i in range(len(highInputs)):
	for j in range(len(noneInputs)):
		noneDist += numpy.sum(numpy.abs(highInputs[i] - noneInputs[j]))

print(str(highDist) + " is the intra high distance")
print(str(noneDist) + " is the inter high-none distance")
print(str(numpy.mean(highAvg)) + " " + str(numpy.mean(lowAvg)) + " " + str(numpy.mean(noneAvg)))
