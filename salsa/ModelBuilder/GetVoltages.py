import os
import sys
import json
import collections
from numpy import *
import datetime
import statistics
import numpy as np
import matplotlib.pyplot as plt

# Calculates the mean and standard deviation of duration for 1% battery drop intervals, based on raw log data

g_datetime_format = "%a %b %d %H:%M:%S CST %Y"

def read_volts(filename):
	curr_volt=0
	curr_bat=0
	ind=0
	batt_values=[]
	volt_values=[]
	indexes=[]
	with open(filename) as fp:
		for line in fp:
			text="Voltage= "
			volt_ind=line.find(text)
			if(volt_ind != -1):
				curr_volt=float(line[volt_ind+len(text):].split(',')[0])
				#curr_volt=float(line[volt_ind+len(text):].split(' Actor state')[0])
				volt_values.append(curr_volt)
				ind+=1
				indexes.append(ind)
                
			text = "Battery level is "
			bat_ind = line.find(text)
			if(bat_ind != -1):
				curr_bat = float(line[bat_ind+len(text):].split(' ')[0])
				batt_values.append(curr_bat)
				print(str(ind)+","+str(curr_volt)+","+str(curr_bat))	
	fig, ax1 = plt.subplots()

	color = 'tab:red'
	#ax1.set_ylabel('battery', color=color)  # we already handled the x-label with ax1
	#ax1.scatter(indexes, batt_values, color=color)
	#ax1.tick_params(axis='y', labelcolor=color)

	ax1.set_xlabel('index')
	ax1.set_ylabel('volts', color=color)
	ax1.scatter(indexes, volt_values, color=color)
	ax1.tick_params(axis='y', labelcolor=color)
	
	ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis
	color = 'tab:blue'
	ax2.set_ylabel('battery', color=color)  # we already handled the x-label with ax1
	ax2.scatter(indexes, batt_values, color=color)
	ax2.tick_params(axis='y', labelcolor=color)

	fig.tight_layout()  # otherwise the right y-label is slightly clipped
	plt.show()
	


filename=sys.argv[1]
read_volts(filename)
