import sys
import os
import numpy
import datetime

g_datetime_format_cdt = "%a %b %d %H:%M:%S CDT %Y"
g_datetime_format_pdt = "%a %b %d %H:%M:%S PDT %Y"
g_datetime_format=""

#fileName = sys.argv[1]
prev_bat = None
curr_bat = None
prev_timestamp = None
curr_timestamp = None
drop_interval_durations={}

def extract_battery_interval_durations(fileName,prev_bat=None,curr_bat=None,prev_timestamp=None,curr_timestamp=None,drop_interval_durations={}):
	with open(fileName) as fp:
		for line in fp:
			if "PDT" in line:
				g_datetime_format=g_datetime_format_pdt
			else:
				g_datetime_format=g_datetime_format_cdt

			battery_text="Battery level is "
			battery_ind = line.find(battery_text)		
			if(battery_ind != -1):
				curr_bat = float(line[battery_ind + len(battery_text):].split(' ')[0])
				curr_timestamp = line[1:battery_ind-2]
				if curr_bat==0.26:
					print(curr_timestamp)
				
				# This is the first record we are reading
				if(prev_bat == None):
					prev_bat = curr_bat
					prev_timestamp=curr_timestamp
				if(curr_bat < prev_bat): # We hit a new battery interval
					end_time = datetime.datetime.strptime(curr_timestamp, g_datetime_format)
					start_time = datetime.datetime.strptime(prev_timestamp, g_datetime_format)
					time_dur = end_time - start_time
					print(prev_bat,start_time,end_time)
					interval_length = datetime.timedelta.total_seconds(time_dur)
					drop_interval_durations[prev_bat]=interval_length
					prev_bat = curr_bat
					
					# Reset to 1 because you have already seen a sample for the current battery percentage
					prev_timestamp=curr_timestamp
	return drop_interval_durations
#print(drop_interval_durations)
