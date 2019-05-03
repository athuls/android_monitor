from __future__ import division,print_function
import sys
import os
import numpy
import datetime
import random

g_datetime_format = "%a %b %d %H:%M:%S CST %Y"

if(len(sys.argv) < 3):
	print("Input arguments should be: <name of log file> <Battery percent to delete>")
	sys.exit(0)
	
fileName = sys.argv[1]
num_samples=0
curr_bat = None
remove_bat_level=[]
for i in range(2,len(sys.argv)):
	remove_bat_level.append(float(sys.argv[i]))

remove_log_line=False

with open(fileName) as fp:
	for line in fp:
		battery_text="Battery level is "
		battery_ind = line.find(battery_text)		
		if(battery_ind != -1):
			num_samples += 1
			curr_bat = float(line[battery_ind + len(battery_text):].split(' ')[0])
			if(curr_bat in remove_bat_level):
				remove_log_line=True
			else:
				remove_log_line=False
			
		if(remove_log_line==False):
			print(line, end="")
