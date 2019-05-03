from __future__ import division,print_function
import sys
import os
import numpy
import datetime
import random

if(len(sys.argv) < 2):
	print("Input arguement is : <name of cleaned logs>")
	sys.exit(0)

fileName = sys.argv[1]
fp = open(fileName)
dataFile = fp.readlines()
fp.close()

for lines in dataFile:
	lines = lines.strip('\n')
	lines = lines.strip('\t')
	lines_new = lines.strip(" ")
	myData = lines_new.split(',')
	myDataVal = [float(i) for i in myData]
	if(myDataVal[-1] > 200):
		myDataVal[-1]=200
	text=''
	for i in range(len(myDataVal)-1):
		text+=str(myDataVal[i])+','
	text+=str(myDataVal[-1])
	print(text)
	#Sum_val = sum(myDataVal) - myDataVal[-1]
	#if Sum_val> 0:
		#print(lines)
