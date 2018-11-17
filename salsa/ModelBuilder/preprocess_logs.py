import sys
import os
import numpy
import datetime

g_datetime_format = "%a %b %d %H:%M:%S PDT %Y"

fileName = sys.argv[1]
counts_arr = []
num_samples=0
prev_bat = None
curr_bat = None
prev_timestamp = None
curr_timestamp = None
interval_start_time = None

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

				# Reset to 1 because you have already seen a sample for the current battery percentage
				num_samples = 1

			prev_timestamp = curr_timestamp		

		text="$State"
		state_ind=line.find(text)
		if(state_ind != -1):
			comma_ind=line.find(",", state_ind)
			num=int(line[state_ind+8 : comma_ind])
			counts_arr.append(num)
print(str(numpy.mean(counts_arr)))
print(str(numpy.std(counts_arr)))

print(str(num_samples))
