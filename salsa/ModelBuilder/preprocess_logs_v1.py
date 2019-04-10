from __future__ import division,print_function
import sys
import os
import numpy
import datetime
import random

g_datetime_format = "%a %b %d %H:%M:%S CDT %Y"

if(len(sys.argv) != 4):
	print("Input arguments should be: <name of log file> <actor load i.e. number of actors created periodically> <NET if network logs processing, else BASIC>")
	sys.exit(0)
	
fileName = sys.argv[1]
counts_arr = []
network_data_arr = []
network_actor_count = []
num_samples=0
prev_bat = None
curr_bat = None
prev_timestamp = None
curr_timestamp = None
interval_start_time = None
total_error_network_data=0
screen_brightness_vals = []
cpu_usage=[]
expected_interval_size=[]
voltages=[]
num_intervals=0
actual_power_arr=[]
cpu_usages_arr=[]
network_data_debug=[]
cpu_freq_arr=[]

with open(fileName) as fp:
	for line in fp:
		battery_text="Battery level is "
		battery_ind = line.find(battery_text)		
		if(battery_ind != -1):
			num_samples += 1
			curr_bat = float(line[battery_ind + len(battery_text):].split(' ')[0])
			curr_timestamp = line[1:battery_ind-2]
			
			# This is the first record we are reading
			if(prev_bat == None):
				prev_bat = curr_bat
				interval_start_time = curr_timestamp
			if(curr_bat < prev_bat): # We hit a new battery interval
				start_time = datetime.datetime.strptime(interval_start_time, g_datetime_format)
				end_time = datetime.datetime.strptime(prev_timestamp, g_datetime_format)
				time_dur = end_time - start_time
				interval_length = datetime.timedelta.total_seconds(time_dur)

				# We overcounted on the actual samples and undercounted on expected (because we took time difference)
				error = (interval_length + 1) - (num_samples - 1)
				print(str(prev_bat) + " expected samples: " + str(interval_length + 1) + " actual samples: " + str(num_samples - 1) + " error= " + str(error))   
					
				prev_bat = curr_bat
				interval_start_time = curr_timestamp

				# Demarcate screen brightness list at drop of 1%
				screen_brightness_vals.append(-100)

				# Accumulate network data uploaded so far
				if(sys.argv[3]=="NET"):
					total_actor_count=sum(network_actor_count)
					expected_network_data=(total_actor_count)*int(sys.argv[2])*3762000
					if(expected_network_data > 1900000*total_actor_count):
						expected_network_data=1900000*total_actor_count
					
					network_data_debug.append(sum(network_data_arr))
					error_network_data=expected_network_data-sum(network_data_arr)
					percent_error_network_data=error_network_data/expected_network_data
					total_error_network_data += abs(percent_error_network_data)
					num_intervals += 1
					
					network_data_arr=[]
					network_actor_count=[]

				# Reset to 1 because you have already seen a sample for the current battery percentage
				num_samples = 1

			prev_timestamp = curr_timestamp		

		text="no active actors"
		state_ind=line.find(text)
		if(state_ind != -1):
			counts_arr.append(0)
			network_actor_count.append(1)
		else:	
			text="$State"
			state_ind=line.find(text)
			if(state_ind != -1):
				comma_ind=line.find(",", state_ind)
				num=int(line[state_ind+8 : comma_ind])
				counts_arr.append(num)
				network_actor_count.append(1)

		text="Network usage:"
		state_ind=line.find(text)
		if(state_ind != -1):
			num=int(line[state_ind+14 : -1])
			if(abs(num)<53000000):
			#if(abs(num)<5000):
				network_data_arr.append(num)
	
		text="CPU usage: "
		state_ind=line.find(text)
		if(state_ind != -1):
			next_a=line.find(" CPU freq", state_ind)
			num=float(line[state_ind+11:next_a])
			cpu_usage.append(num)
			cpu_usages_arr.append(num)

		text="Voltage: "
		state_ind=line.find(text)
		if(state_ind != -1):
			num=float(line[state_ind+9:-1])
			voltages.append(num)

		#text="brightness="
		#state_ind=line.find(text)
		#if(state_ind != -1):
		#	next_a_letter=line.find("a", state_ind)
		#	num=int(line[state_ind+11:next_a_letter])
		#	screen_brightness_vals.append(num)

print("Mean in actor counts per segment: " + str(numpy.mean(counts_arr)))
print("Std in actor counts per segment: " + str(numpy.std(counts_arr)))

#print("Total number of samples is: " + str(num_samples)
#print("Total erorr network data in percent is: " + str(total_error_network_data/num_intervals))
