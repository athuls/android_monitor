from sys import argv

import numpy as np
import datetime

filename = argv[1]
f = open(filename, "r")
lines = f.readlines()
f.close()


data = []
prevTime = None 
for line in lines[:]:
	duration = line.split(',')
	if(float(duration[0]) > 10):
		print(duration[0] + "," + duration[1], end = '')
