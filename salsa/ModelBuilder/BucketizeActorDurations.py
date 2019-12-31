import os
import sys
import math

import numpy as np
from scipy import stats

if len(sys.argv) != 3:
	print("Error: Usage : python Bound.py fileName <Bound>,  Output : <input_filename>_out.txt")

bound = int(sys.argv[2])
fileName = sys.argv[1]
# controlFileName = sys.argv[3]

file_out = str((fileName.split("."))[0])+"_bucketized.txt"
fW = open(file_out,"w")
dimensionToBucketMaps = None
idleTime = None

quantized_data = []
current_row = []

with open(fileName,"r") as fp:
	for line in fp:
		val = line.strip("\n").split(",")
		
		# Initialize dimension to bucket map
		if(dimensionToBucketMaps == None):
			dimensionToBucketMaps = [None] * (len(val) - 1)
			idleTime = [None] * (len(val) - 1)
			for i in range(0, len(val) - 1):
				dimensionToBucketMaps[i] = {}
				idleTime[i] = {}
		#####################################

		val = [float(k) for k in val]
		original_vals = list(val)
		text = ''
		for i in range(len(val)-1):
			k = int(val[i]/bound)
			if(abs(val[i] - 0.0) > 0.5):
				val[i] = (bound/2)+ k*bound
				
				# Update the bucketing map for each dimension
				if(val[i] not in dimensionToBucketMaps[i].keys()):
					dimensionToBucketMaps[i][val[i]] = []
					idleTime[i][val[i]] = []
				dimensionToBucketMaps[i][val[i]].append(val[-1])
				idleTime[i][val[i]].append(abs(original_vals[-1] - original_vals[i])/original_vals[-1])

				current_row.append(val[i])
				##########################################
			else:
				current_row.append(0.0)

		current_row.append(val[-1])
		quantized_data.append(current_row)
		current_row = []

#			text += str(val[i])+","
#		text+=str(val[-1])+"\n"
#		fW.write(text)
#fW.close()

filteredDimensionToBucketMaps = [None] * len(dimensionToBucketMaps)

# Remove buckets which do not have at least 20 points (for t-test to be valid)
for index in range(0, len(dimensionToBucketMaps)):
	for key in list(dimensionToBucketMaps[index]):
		avgIdleTime = np.mean(idleTime[index][key])
		#if ((len(dimensionToBucketMaps[index][key]) < 20) and (avgIdleTime > 0.8)):
		#if (len(dimensionToBucketMaps[index][key]) < 20):
		if (False):
			print(str(index) + " is index, " + str(key) + " doesn't exist and " + str(avgIdleTime) + " is idle time fraction")
			#dimensionToBucketMaps[index][0] = dimensionToBucketMaps[index].pop(key)
			del dimensionToBucketMaps[index][key]

			# Iterate through the quantized data and eliminate buckets which are irrelevant
			for i_row in range(0, len(quantized_data)):
				print("Index to eliminate bucket is " + str(i_row) + " " + str(index))
				if(quantized_data[i_row][index] == key):
					quantized_data[i_row][index] = 0.0


# Do statistical test
# Read control run 
for index in range(0, len(dimensionToBucketMaps)):
	#if(len(dimensionToBucketMaps[index].keys()) >= 2):
	if(True):
		for key in list(dimensionToBucketMaps[index]):
			# print(str(index) + " : " + str(key) + " : " + str(np.std(dimensionToBucketMaps[index][key])))
			for val in dimensionToBucketMaps[index][key]:
				print(str(index) + "," + str(key) + "," + str(val))
				#fW.write(str(key) + "," + str(val) + "\n")
	else:
		print("Index is " + str(index) + " " + str(len(dimensionToBucketMaps[index].keys())))
		# Iterate quantized data and eliminate buckets which are irrelevant
		for i_row in range(0, len(quantized_data)):
			quantized_data[i_row][index] = 0.0

print(quantized_data)
print("Going to create output bucketized file")

# Write out quantized data to file
for i_row in range(0, len(quantized_data)):
	for i_column in range(0, len(quantized_data[i_row])-1):	
		fW.write(str(quantized_data[i_row][i_column])+",")
	fW.write(str(quantized_data[i_row][-1]) + "\n")

fW.close()
