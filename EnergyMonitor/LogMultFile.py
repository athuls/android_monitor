import os
import sys
import json
import collections
from numpy import *
import datetime
# import statistics
# from Lib import statistics
if len(sys.argv) < 3:
	print("python LogHandler.py <file-Name1> <file-name2> <0:difference(curr-prev), 1: intersection, 2: prev-curr> <optional : timetoIgnore>")
	exit()

def printSet(new_set):
	for val in new_set:
		print(val)
	print("\n")

def readFile(filename1,filename2, typeOper, timetoIgnore):
	initCounter1 = 0
	initCounter2 = 0
	# check for a pattern and keep on reading till you have that 
	curr1 = set()
	curr2 = set()
	with open(filename1) as fp, open(filename2) as fp2:
		lines1 = fp.readlines()
		lines2 = fp2.readlines()
		idx1 = 0
		idx2 = 0
		currIdx1 = idx1
		currIdx2 = idx2

		size1 = len(lines1)
		size2 = len(lines2)
		timeStamp1 = ''
		timeStamp2 = ''

		####### run a while loop
		while True:
			currIdx1 = idx1
			currIdx2 = idx2

			for line in lines1[idx1:]:
				currIdx1 = currIdx1+ 1
				line = line.strip()
				if line.find("GMT+05:30") != -1:
					if initCounter1 < timetoIgnore:
						initCounter1 += 1
						continue

					else:
						timeStamp1 = line
						break
						# Modify the sets and print things and carry on
						# Doing the difference
						# diffSet = set()
						# # diffSet = curr.intersection(prev)
						# if typeOper == 0:
						# 	diffSet = curr.difference(prev)
						# elif typeOper == 1:
						# 	diffSet = curr.intersection(prev)
						# elif typeOper == 2:
						# 	diffSet = prev.difference(curr)
						# else:
						# 	print("Incorrect operation , exiting .....")

						# print("Time : ",line)
						# printSet(diffSet)

						# After the operation move curr set to prev
						# prev.clear()
						# prev = curr.copy()
						# curr.clear()
				else:
					# Add in the current set
					curr1.add(line)

			for line in lines2[idx2:]:
				currIdx2 = currIdx2+ 1
				line = line.strip()
				if line.find("GMT+05:30") != -1:
					if initCounter2 < timetoIgnore:
						initCounter2 += 1
						continue

					else:
						timeStamp2 = line
						break
						# Modify the sets and print things and carry on
						# Doing the difference
						# diffSet = set()
						# # diffSet = curr.intersection(prev)
						# if typeOper == 0:
						# 	diffSet = curr.difference(prev)
						# elif typeOper == 1:
						# 	diffSet = curr.intersection(prev)
						# elif typeOper == 2:
						# 	diffSet = prev.difference(curr)
						# else:
						# 	print("Incorrect operation , exiting .....")

						# print("Time : ",line)
						# printSet(diffSet)

						# After the operation move curr set to prev
						# prev.clear()
						# prev = curr.copy()
						# curr.clear()
				else:
					# Add in the current set
					curr2.add(line)	


			idx1 = currIdx1
			idx2 = currIdx2

			diffSet = set()
			# diffSet = curr.intersection(prev)
			if typeOper == 0:
				diffSet = curr1.difference(curr2)
			elif typeOper == 1:
				diffSet = curr1.intersection(curr2)
			elif typeOper == 2:
				diffSet = curr2.difference(curr1)
			else:
				print("Incorrect operation , exiting .....")

			print("Time file1 : ",timeStamp1)
			# print("Time file2 : ",timeStamp2)
			printSet(diffSet)
			curr1.clear()
			curr2.clear()



			if idx1 >= size1 or idx2 >= size2:
				break
		# else: # End of file
		# 	diffSet = set()
		# 	# diffSet = curr.intersection(prev)
		# 	if typeOper == 0:
		# 		diffSet = curr.difference(prev)
		# 	elif typeOper == 1:
		# 		diffSet = curr.intersection(prev)
		# 	elif typeOper == 2:
		# 		diffSet = prev.difference(curr)
		# 	else:
		# 		print("Incorrect operation , exiting .....")

		# 	print("Time : ",line)
		# 		printSet(diffSet)

	return




filename1 = sys.argv[1]
filename2 = sys.argv[2]
typeOper = int(sys.argv[3])
timetoIgnore = 0
if len(sys.argv) > 4:
	timetoIgnore = int(sys.argv[4])

readFile(filename1, filename2, typeOper, timetoIgnore)