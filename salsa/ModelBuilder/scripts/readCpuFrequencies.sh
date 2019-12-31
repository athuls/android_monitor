#!/usr/bin/bash 
# Creates a file called observerdFrequencies.txt which contains the observed frequencies sampled every second

count="0"
while [ $count -lt 60 ]
do
	adb shell cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq >> observedFrequenciesCpu0.txt
	adb shell cat /sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq >> observedFrequenciesCpu1.txt
	count=$[$count+1]
	sleep 1
done
