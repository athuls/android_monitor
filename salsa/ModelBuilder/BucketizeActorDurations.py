import os
import sys
import math

import numpy as np
from scipy import stats

if len(sys.argv) != 3:
	print("Error: Usage : python Bound.py fileName <Bound>  controlFileName,  Output : <input_filename>_out.txt")

bound = int(sys.argv[2])
fileName = sys.argv[1]
# controlFileName = sys.argv[3]

file_out = str((fileName.split("."))[0])+"_out.txt"
fW = open(file_out,"w")
dimensionToBucketMaps = None

with open(fileName,"r") as fp:
	for line in fp:
		val = line.strip("\n").split(",")
		
		# Initialize dimension to bucket map
		if(dimensionToBucketMaps == None):
			dimensionToBucketMaps = [None] * len(val)
			for i in range(0, len(val)):
				dimensionToBucketMaps[i] = {}
		#####################################

		val = [float(k) for k in val]
		text = ''
		for i in range(len(val)-1):
			k = int(val[i]/bound)
			if(abs(val[i] - 0.0) > 0.5):
				val[i] = (bound/2)+ k*bound
				
				# Update the bucketing map for each dimension
				if(val[i] not in dimensionToBucketMaps[i].keys()):
					dimensionToBucketMaps[i][val[i]] = []
				dimensionToBucketMaps[i][val[i]].append(val[-1])
				##########################################

			text += str(val[i])+","
		text+=str(val[-1])+"\n"
		fW.write(text)
fW.close()

print(dimensionToBucketMaps)
filteredDimensionToBucketMaps = [None] * len(dimensionToBucketMaps)
# Remove buckets which do not have at least 20 points (for t-test to be valid)
for index in range(0, len(dimensionToBucketMaps)):
	for key in list(dimensionToBucketMaps[index]):
		if len(dimensionToBucketMaps[index][key]) < 20:
			print(str(key) + " doesn't exist")
			del dimensionToBucketMaps[index][key]

print(dimensionToBucketMaps)

# Do statistical test
# Read control run 
for index in range(0, len(dimensionToBucketMaps)):
	if(len(dimensionToBucketMaps[index].keys()) >= 2):
		for key in list(dimensionToBucketMaps[index]):
			# print(str(index) + " : " + str(key) + " : " + str(np.std(dimensionToBucketMaps[index][key])))
			for val in dimensionToBucketMaps[index][key]:
				print(str(key) + "," + str(val))
