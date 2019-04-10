from __future__ import division,print_function
import sys
import os
import numpy
import datetime
import random

g_datetime_format = "%a %b %d %H:%M:%S GMT+05:30 %Y"

if(len(sys.argv) != 4):
	print("Input arguments should be: <name of log file> <actor load> <NET if network logs processing, else BASIC>")
	sys.exit(0)
	
fileName = sys.argv[1]
counts_arr = []
network_data_arr = []
network_data_full_list = []
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
cpu_freq_full_arr=[]
first_cpu_usage_entry=True
mem_usage=[]
first_mem_usage_entry=True

cpu_usage_temp=[]


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
				percent_error = error/(interval_length+1)
				print(str(prev_bat) + " expected samples: " + str(interval_length + 1) + " actual samples: " + str(num_samples - 1) + " error= " + str(error)+" percent error="+str(percent_error))   
				#print(str(interval_length + 1))   
				#print(str(numpy.mean(cpu_usage_temp)) + "," + str(numpy.std(cpu_usage_temp)))
				expected_interval_size.append(interval_length+1)		
					
				actual_power=(63/(interval_length+1))*numpy.mean(voltages)
				#actual_power=(63/(num_samples-1))*3800
				#print(str(len(expected_interval_size)) + "," + str(interval_length+1) + "," + str(numpy.mean(voltages))+","+str(actual_power)+","+str(numpy.mean(cpu_freq_arr)))
				#print(str(len(expected_interval_size)) + "," + str(interval_length+1) + "," + str(numpy.mean(voltages))+","+str(actual_power))
				#print(str(len(expected_interval_size)) + "," + str(interval_length+1) + "," + str(3.8)+","+str(actual_power)+","+str(numpy.mean(cpu_freq_arr)))
				actual_power_arr.append(actual_power/1000)

				# Demarcate screen brightness and CPU usage at drop of 1%
				screen_brightness_vals.append(-100)
				cpu_usage.append(-100)
				mem_usage.append(-100)
				cpu_freq_full_arr.append(-100)
				network_data_full_list.append(-100)

				voltages=[]
				cpu_freq_arr=[]
				cpu_usage_temp=[]

				prev_bat = curr_bat
				interval_start_time = curr_timestamp

				# Accumulate network data uploaded so far
				if(sys.argv[3]=="NET"):
					total_actor_count=sum(network_actor_count)
					expected_network_data=(total_actor_count)*int(sys.argv[2])*3762000
					#expected_network_data=(total_actor_count)*int(sys.argv[2])*10033856
					if(expected_network_data > 1900000*total_actor_count):
					#if(expected_network_data > 7780615*total_actor_count):
						expected_network_data=1900000*total_actor_count
					#expected_network_data=(total_actor_count)*int(sys.argv[2])*6659

					#print("network data mean is: " + str(numpy.mean(network_data_arr)) + "network data std is: " + str(numpy.std(network_data_arr)))
					error_network_data=expected_network_data-sum(network_data_arr)
					percent_error_network_data=error_network_data/expected_network_data

					#print(str(percent_error_network_data))
					total_error_network_data += abs(percent_error_network_data)
					num_intervals += 1
					
					#print(str(sum(network_data_arr)) + " " + str(expected_network_data))
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
				network_data_full_list.append(num)
	
		text="CPU usage: "
		state_ind=line.find(text)
		if(state_ind != -1):
			# Ignore the first entry as we collect CPU usage for previous segment
			if(not first_cpu_usage_entry):
				next_a=line.find(", CPU freq", state_ind)
				#next_a=line.find(" V", state_ind)
				num=float(line[state_ind+11:next_a])
				#print(str(num))
				cpu_usage.append(num)
				cpu_usages_arr.append(num)
				cpu_usage_temp.append(num)
			first_cpu_usage_entry=False

		text="CPU freq= "
		state_ind=line.find(text)
		if(state_ind != -1):
			next_a=line.find(", V", state_ind)
			#num=float(line[state_ind+10:next_a])
			freqs=line[state_ind+10:next_a]
			next_space=freqs.find(" ")
			num=float(freqs[0:next_space])
			cpu_freq_arr.append(num)
			cpu_freq_full_arr.append(num)

		text="Voltage= "
		#text="Voltage: "
		state_ind=line.find(text)
		if(state_ind != -1):
			next_a=line.find(", Mem", state_ind)
			num=float(line[state_ind+9:next_a])
			voltages.append(num)
	
		text="Mem= "
		state_ind=line.find(text)
		if(state_ind != -1):
			if(not first_mem_usage_entry):
				num=float(line[state_ind+5:-1])
				mem_usage.append(num)
			first_mem_usage_entry=False
		#text="brightness="
		#state_ind=line.find(text)
		#if(state_ind != -1):
		#	next_a_letter=line.find("a", state_ind)
		#	num=int(line[state_ind+11:next_a_letter])
		#	screen_brightness_vals.append(num)

print("Mean in actor counts per segment: " + str(numpy.mean(counts_arr)))
print("Std in actor counts per segment: " + str(numpy.std(counts_arr)))

sum_brightness_energy=0
three_brightness=0
onefifty_brightness=0
twofiftyfive_brightness=0
for i in range(0,len(screen_brightness_vals)):
	# End of battery interval
	if(screen_brightness_vals[i]==-100):
		#print(str(three_brightness),end=",")

		#print(str(onefifty_brightness),end=",")
		#print(str(twofiftyfive_brightness),end=",")		
		#print(str(393120+random.randrange(-5000,5000)))
		sum_brightness_energy=0
		three_brightness=0
		onefifty_brightness=0
		twofiftyfive_brightness=0
	if(screen_brightness_vals[i]==3):
		three_brightness+=1
		sum_brightness_energy+=3.9997115
	if(screen_brightness_vals[i]==150):
		onefifty_brightness+=1
		sum_brightness_energy+=90.00058
	if(screen_brightness_vals[i]==255):
		twofiftyfive_brightness+=1
		sum_brightness_energy+=409.99823

#print("Total number of samples is: " + str(num_samples)
#print("Total erorr network data in percent is: " + str(total_error_network_data/num_intervals))

if(len(network_data_full_list)>0):
	network_data_full_list = numpy.nan_to_num(numpy.array(network_data_full_list,dtype=numpy.float32))
	drain_network=0
	num_segments=0
	interval_idx=0
	for i in range(0,len(network_data_full_list)):
                if(network_data_full_list[i]==-100):
                        print(str(drain_network)+","+str(expected_interval_size[interval_idx]))
			drain_network=0
                        interval_idx+=1
                        num_segments=0
                        continue
		drain_network+=network_data_full_list[i]
                num_segments+=1


if(len(cpu_usage) > 0):
	print("The cpu usages are")
	cpu_usage = numpy.nan_to_num(numpy.array(cpu_usage,dtype=numpy.float32))
	network_data_full_list = numpy.nan_to_num(numpy.array(network_data_full_list,dtype=numpy.float32))
	drain_cpu_usage=0
	drain_mem_usage=0
	drain_cpu_freq=0
	drain_network=0
	interval_idx=0
	num_segments=0
	cpu_usage_vals=[]
	power_errors=[]
	for i in range(0,len(cpu_usage)):
		#if(cpu_usage[i]!=-100):
		#	print(str(i) + "," + str(cpu_usage[i]))
		if(cpu_usage[i]==-100):
			#print(str(drain_cpu_usage) + " " + str(233.1/expected_interval_size[interval_idx]))
			cpu_usage_vals.append(drain_cpu_usage/num_segments)
			expected_power=(0.0155*(drain_cpu_usage/2)+0.7097)/num_segments
			#expected_power=(-0.0142*((drain_cpu_usage/num_segments)/1)+3.339)
			power_error=(expected_power-actual_power_arr[interval_idx])/expected_power
			power_errors.append(power_error)
			#print(str(drain_cpu_usage/2)+","+str(actual_power_arr[interval_idx])+","+str(expected_power)+","+str(expected_interval_size[interval_idx]))
			#print(str(drain_cpu_usage/2)+","+str(drain_mem_usage)+","+str(drain_cpu_freq/num_segments)+","+str(drain_network)+","+str(expected_interval_size[interval_idx]))
			#print(str((drain_cpu_usage/num_segments)/2)+","+str(actual_power_arr[interval_idx]))
			#print(str(network_data_debug[interval_idx])+","+str(actual_power_arr[interval_idx]))
			interval_idx+=1
			drain_cpu_usage=0
			drain_mem_usage=0
			drain_cpu_freq=0
			drain_network=0
			num_segments=0
			continue
		drain_cpu_usage+=cpu_usage[i]
		drain_mem_usage+=abs(mem_usage[i])
		drain_cpu_freq+=cpu_freq_full_arr[i]
		drain_network+=network_data_full_list[i]
		num_segments+=1
	print(str(numpy.mean(power_errors))+","+str(numpy.std(power_errors)))
