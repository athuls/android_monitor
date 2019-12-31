from sys import argv

import datetime

filename = argv[1]
f = open(filename, "r")
lines = f.readlines()
f.close()


data = []
prevTime = None 
for line in lines[:]:
	duration = line.split(',')
	#if(float(duration[5]) > 65 and float(duration[5]) < 90):
	if(float(duration[6]) > 10):
	#if(((float(duration[6]) > 10) or (float(duration[7]) > 10)) and (float(duration[2]) <= 10 and float(duration[3]) <= 10)):
		for i in range(0, len(duration)-1):
			print(duration[i] + ",", end = '')
		print(duration[-1].rstrip())
