# Similar to histograms.py, but each histogram is created for
# a fixed-size sliding window (independent of battery drop
# intervals)
# param 1: log file to be processed
# param 2: window size (tumbling fashion)
# ex: python histograms.py log_heavy_clean.txt 10

import os
import sys
import json

class SplitFixedWindowsTumbling:
    histograms = []
    batteryFrac = []

    def __init__(self, filename, windowsize, outputfile):
        self.filename = filename
        self.window_size = int(windowsize)
        self.outputfile = outputfile

    def getBatteryFracs(self, counts1, prevWindowCounts):
        print("Window size and prev window count ", len(counts1), len(prevWindowCounts))
        effective_counts = prevWindowCounts + counts1

        # We have already accounted for the first window
        possible_windows = (len(effective_counts) // self.window_size)
        if(len(prevWindowCounts) > 0):
            possible_windows -= 1
            initialPartialWindowFrac = (self.window_size - len(prevWindowCounts)) / len(counts1)
            firstWindowFrac = self.batteryFrac[-1] + initialPartialWindowFrac
            self.batteryFrac[-1] = firstWindowFrac

        const_power = self.window_size / len(counts1)

        for i in range(0, possible_windows):
            self.batteryFrac.append(const_power)

        remaining_window_size = len(effective_counts) % self.window_size
        print("remaining window size:", remaining_window_size)
        # Capture the remaining actor counts in current battery
        # drop interval
        remaining_window = []
        if(remaining_window_size != 0):
            remaining_window_battery_frac = remaining_window_size / len(counts1)
            self.batteryFrac.append(remaining_window_battery_frac)
            remaining_window_start_ind = len(effective_counts) - remaining_window_size
            for ind in range(remaining_window_start_ind, len(effective_counts)):
                remaining_window.append(effective_counts[ind])

        return remaining_window

    def splitIntoWindows(self, counts1, prevWindowCounts):
        effective_counts = prevWindowCounts + counts1
        possible_windows  = len(effective_counts) // self.window_size

        hist = {}

        current_bins = 0
        current_windows = 0
        for val in effective_counts:
            if(current_windows == possible_windows):
                break
            if(val in hist):
                hist[val] += 1
            else:
                hist[val] = 1

            # Start a new window
            if(current_bins == self.window_size):
                self.histograms.append(dict(hist))
                current_windows += 1
                current_bins = 0
                hist = {}
            current_bins += 1

        remaining_window_size = len(effective_counts) % self.window_size

        # Capture the remaining actor counts in current battery
        # drop interval
        remaining_window = []
        remaining_window_start_ind = len(effective_counts) - remaining_window_size
        for ind in range(remaining_window_start_ind, len(effective_counts)):
            remaining_window.append(effective_counts[ind])
        return remaining_window

    def read_file(self):
        curr = []
        curr_bat = None
        vals_per_drop = []
        with open(self.filename) as fp:
            for line in fp:
                text = "Battery level is "
                bat_ind = line.find(text)
                if(bat_ind != -1):
                    bat = float(line[bat_ind+len(text):].split(' ')[0])
                    if(curr_bat == None): # first iteration
                        curr_bat = bat
                    if(bat < curr_bat):
                        vals_per_drop.append(curr)
                        curr = []
                        curr_bat = bat

                ind = line.find('$State')

                if(line[0] == '[' and line.find('no') != -1):
                    curr.append(0)
                elif(ind != -1):
                    comma_ind = line.find(',', ind)
                    num = int(line[ind + 8 : comma_ind])
                    curr.append(num)
        return vals_per_drop

    def extract_windows(self):
        battery_drops = self.read_file()
        # skipping the first index and last index, since they might not be full percent drop
        pending_window = []
        pending_batteryFrac = []
        for ind in range(1, len(battery_drops)-1):
            pending_window = self.splitIntoWindows(battery_drops[ind], pending_window)
            pending_batteryFrac = self.getBatteryFracs(battery_drops[ind], pending_batteryFrac)
        self.write_results()

    def write_results(self):
        with open(self.outputfile, 'w+') as outfile:
            for i in range(0, len(self.histograms)):
                outfile.write(json.dumps(self.histograms[i]))
                powerVal = self.batteryFrac[i] / self.window_size
                outfile.write("\t" + str(json.dumps(powerVal)))
                outfile.write("\n")


dir = os.path.dirname(__file__)
filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
newSplittingInstance = SplitFixedWindowsTumbling('/home/athuls89/Desktop/OSL/osl/MobileCloud/android_monitor/salsa/ProfiledData/BatteryRateProfile/.idea/energy/battery/mobile_logs/Nqueens_light.txt',3, filename)
newSplittingInstance.extract_windows()