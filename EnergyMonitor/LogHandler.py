import os
import sys
import json
import collections
from numpy import *
import datetime
# import statistics
# from Lib import statistics
if len(sys.argv) < 3:
	print("python LogHandler.py <file-Name> <0:difference(curr-prev), 1: intersection, 2: prev-curr> <optional : timetoIgnore>")
	exit()

def printSet(new_set):
	for val in new_set:
		print(val)
	print("\n")

def readFile(filename, typeOper, timetoIgnore):
	initCounter = 0
	# check for a pattern and keep on reading till you have that 
	curr = set()
	prev = set()
	with open(filename) as fp:
		for line in fp:
			line = line.strip()
			if line.find("GMT+05:30") != -1:
				if initCounter < timetoIgnore:
					initCounter += 1
					continue

				else:
					# Modify the sets and print things and carry on
					# Doing the difference
					diffSet = set()
					# diffSet = curr.intersection(prev)
					if typeOper == 0:
						diffSet = curr.difference(prev)
					elif typeOper == 1:
						diffSet = curr.intersection(prev)
					elif typeOper == 2:
						diffSet = prev.difference(curr)
					else:
						print("Incorrect operation , exiting .....")

					print("Time : ",line)
					printSet(diffSet)

					# After the operation move curr set to prev
					prev.clear()
					prev = curr.copy()
					curr.clear()
			else:
				# Add in the current set
				curr.add(line)

		else: # End of file
			diffSet = set()
			# diffSet = curr.intersection(prev)
			if typeOper == 0:
				diffSet = curr.difference(prev)
			elif typeOper == 1:
				diffSet = curr.intersection(prev)
			elif typeOper == 2:
				diffSet = prev.difference(curr)
			else:
				print("Incorrect operation , exiting .....")

			print("Time : ",line)
			printSet(diffSet)

	return




filename = sys.argv[1]
typeOper = int(sys.argv[2])
timetoIgnore = 0
if len(sys.argv) > 3:
	timetoIgnore = int(sys.argv[3])

readFile(filename, typeOper, timetoIgnore)