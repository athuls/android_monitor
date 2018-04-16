# Similar to histograms.py, but each histogram is created for
# a fixed-size sliding window (independent of battery drop
# intervals)
# param 1: log file to be processed
# param 2: window size (tumbling fashion)
# ex: python histograms.py log_heavy_clean.txt 10

import os
import sys
import json
import matplotlib.pyplot as plt
import collections
from numpy import *

# TODO: We are losing accuracy for power as soon as we add the power values into this tuple
# This is due to floating point precision and causes the observed error in actual and estimated
# battery drop interval sizes
HistWithBatteryDrop = collections.namedtuple('HistWithBatteryDrop', ['power','intervalsize'])

class SplitFixedWindowsTumbling:
    histograms = []
    batteryFrac = []
    temp_batteryPercent = []

    def __init__(self, filename, actorname, windowsize, outputfile, range=(-1,1.0)):
        self.filename = filename
        self.actor_name = actorname
        self.actor_names_with_counts = []
        self.prob_multiactor_req_type = []
        self.window_size = int(windowsize)
        self.outputfile = outputfile
        self.battery_drops = None
        self.low = range[0]
        self.high = range[1]
        self.final_residue = 0

        # Stores the individual window power values for battery estimate validaton purposes
        self.validation_drop_list = []

    # Just uses window size as a fraction of time over battery drop interval size.
    # Does not use actor count information
    def getBatteryFracs(self, counts1, prevWindowCounts):
        #print("Window size and prev window count ", len(counts1), len(prevWindowCounts))
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
        #print("remaining window size:", remaining_window_size)
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

    # Accounts for actor counts in windows for computing battery drops
    def getBatteryFracsActorBased(self, counts1, prevWindowCounts):
        sumInInterval = 0
        #print("Window size and prev window count ", len(counts1), len(prevWindowCounts))
        effective_counts = prevWindowCounts + counts1

        len_battery_interval = len(counts1)

        intervalTotalActorCount = sum(counts1)
        prevWindowActorCount = sum(prevWindowCounts)

        possible_windows = (len(effective_counts) // self.window_size)

        # remaining_counts1 is used to get updated list of interval counts
        # to process, if previous window counts are non-empty
        remaining_counts1 = counts1
        if(len(prevWindowCounts) > 0):
            # We have already accounted for the first window
            possible_windows -= 1
            size_in_interval = self.window_size - len(prevWindowCounts)
            fractionInFirstWindow = sum(counts1[0:size_in_interval])/intervalTotalActorCount
            firstWindowFrac = self.batteryFrac[-1].power + fractionInFirstWindow
            self.batteryFrac[-1]._replace(power=firstWindowFrac)
            self.batteryFrac[-1]._replace(intervalsize=intervalTotalActorCount)
            # self.validation_drop_list.append(self.batteryFrac[-1][0])
            remaining_counts1 = counts1[size_in_interval:len(counts1)]

        for i in range(0, possible_windows):
            windowPowerFrac = sum(remaining_counts1[i * self.window_size:(i+1) * self.window_size])/intervalTotalActorCount
            # self.batteryFrac.append(windowPowerFrac)
            self.batteryFrac.append(HistWithBatteryDrop(power=windowPowerFrac, intervalsize=intervalTotalActorCount))
            # self.validation_drop_list.append(self.batteryFrac[-1][0])

        remaining_window_size = len(effective_counts) % self.window_size

        # Capture the remaining actor counts in current battery
        # drop interval
        remaining_window = []
        if(remaining_window_size != 0):
            startIndx = len(effective_counts) - remaining_window_size
            remaining_window_battery_frac = sum(effective_counts[startIndx:len(effective_counts)])/intervalTotalActorCount
            # self.batteryFrac.append(remaining_window_battery_frac)
            self.final_residue = remaining_window_battery_frac * self.window_size
            self.batteryFrac.append(HistWithBatteryDrop(power=remaining_window_battery_frac, intervalsize=intervalTotalActorCount))
            for ind in range(startIndx, len(effective_counts)):
                remaining_window.append(effective_counts[ind])

        return remaining_window

    def getIntervalTotalActorTypeCount(self, actor_names_counts):
        total = 0
        for actor_map in actor_names_counts:
            total += actor_map[self.actor_name]
        return total

    def getIntervalTotalAllActorTypeCount(self, actor_names_counts):
        total = 0
        for actor_map in actor_names_counts:
            for actor_name in actor_map:
                total += actor_map[actor_name]

        return total

    # Computes power values for logs with multiple actor types, using actor counts in windows for computing battery drops
    def getBatteryFracsMultipleActorBased(self, actor_names_counts, prevWindowCounts):
        sumInInterval = 0
        #print("Window size and prev window count ", len(counts1), len(prevWindowCounts))
        effective_counts = prevWindowCounts + actor_names_counts

        len_battery_interval = len(actor_names_counts)

        intervalTotalActorCount = getIntervalTotalActorTypeCount(actor_names_counts)
        intervalTotalAllActorCount = getIntervalTotalAllActorTypeCount(actor_names_counts)
        prevWindowActorCount = sum(prevWindowCounts)

        possible_windows = (len(effective_counts) // self.window_size)

        # remaining_counts1 is used to get updated list of interval counts
        # to process, if previous window counts are non-empty
        remaining_counts1 = counts1
        if(len(prevWindowCounts) > 0):
            # We have already accounted for the first window
            possible_windows -= 1
            size_in_interval = self.window_size - len(prevWindowCounts)
            fractionInFirstWindow = sum(counts1[0:size_in_interval])/intervalTotalActorCount
            firstWindowFrac = self.batteryFrac[-1].power + fractionInFirstWindow
            self.batteryFrac[-1]._replace(power=firstWindowFrac)
            self.batteryFrac[-1]._replace(intervalsize=intervalTotalActorCount)
            # self.validation_drop_list.append(self.batteryFrac[-1][0])
            remaining_counts1 = counts1[size_in_interval:len(counts1)]

        for i in range(0, possible_windows):
            windowPowerFrac = sum(remaining_counts1[i * self.window_size:(i+1) * self.window_size])/intervalTotalActorCount
            # self.batteryFrac.append(windowPowerFrac)
            self.batteryFrac.append(HistWithBatteryDrop(power=windowPowerFrac, intervalsize=intervalTotalActorCount))
            # self.validation_drop_list.append(self.batteryFrac[-1][0])

        remaining_window_size = len(effective_counts) % self.window_size

        # Capture the remaining actor counts in current battery
        # drop interval
        remaining_window = []
        if(remaining_window_size != 0):
            startIndx = len(effective_counts) - remaining_window_size
            remaining_window_battery_frac = sum(effective_counts[startIndx:len(effective_counts)])/intervalTotalActorCount
            # self.batteryFrac.append(remaining_window_battery_frac)
            self.final_residue = remaining_window_battery_frac * self.window_size
            self.batteryFrac.append(HistWithBatteryDrop(power=remaining_window_battery_frac, intervalsize=intervalTotalActorCount))
            for ind in range(startIndx, len(effective_counts)):
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

            current_bins += 1
            # Start a new window
            if(current_bins == self.window_size):
                count_test = 0
                for key in hist:
                    count_test += hist[key]
                if(count_test != self.window_size):
                    print("Something is WRONG, window size not as expected")
                self.histograms.append(dict(hist))
                current_windows += 1
                current_bins = 0
                hist = {}

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
        actor_counts_list = []
        actor_count_map = {}
        curr_bat = None
        vals_per_drop = []
        with open(self.filename) as fp:
            for line in fp:
                text = "Battery level is "
                bat_ind = line.find(text)
                if(bat_ind != -1):
                    bat = float(line[bat_ind+len(text):].split(' ')[0])
                    if bat >= self.high:
                        continue
                    if bat <= self.low:
                        break

                    self.temp_batteryPercent.append(bat)


                    if(curr_bat == None): # first iteration, don't add last actor_count_map
                        curr_bat = bat
                    else:
                        # Add the current actor map everytime you hit battery log
                        actor_counts_list.append(dict(actor_count_map))
                        actor_count_map = {}
                    if(bat < curr_bat):
                        vals_per_drop.append(curr)
                        curr = []
                        curr_bat = bat
                        self.actor_names_with_counts.append(list(actor_counts_list))
                        actor_counts_list = []
                        actor_count_map = {}

                ind = line.find('$State')

                if(line[0] == '[' and line.find('no') != -1):
                    curr.append(0)
                    actor_count_map = {}
                elif(ind != -1):
                    comma_ind = line.find(',', ind)
                    actor_name = str(line[0:ind])
                    num = int(line[ind + 8 : comma_ind])
                    actor_count_map[actor_name] = num
                    if actor_name == self.actor_name:
                        curr.append(num)
        vals_per_drop.append(curr)
        self.actor_names_with_counts.append(actor_counts_list)
        # print(self.actor_names_with_counts)
        # self.traverse_actor_names()
        return vals_per_drop

    def get_counts(self):
        return self.actor_names_with_counts

    def get_actor_names(self):
        s = set()
        for hash_list in self.actor_names_with_counts:
            for hash_item in hash_list:
                for key in hash_item:
                    s.add(key)
        return list(s)

    def traverse_actor_names(self):
        print('Starting traversal')
        for hash_list in self.actor_names_with_counts:
            print('')
            for hash_item in hash_list:
                for key in hash_item:
                    print(str(key) + " " + str(hash_item[key]))

    def temp_plotBatterDrops(self):
        plt.plot(self.temp_batteryPercent, 'bo')
        plt.title('BatteryDrop')
        plt.xlabel('Time')
        plt.ylabel('Battery drops')
        plt.show()

    def extract_windows(self):
        battery_drops = self.battery_drops = self.read_file()
        actor_names_counts = self.actor_names_with_counts
        #self.temp_plotBatterDrops()
        # skipping the first index and last index, since they might not be full percent drop
        pending_window = []
        pending_batteryFrac = []
        # print("Actual battery drop length or energy " + str(len(battery_drops) - 2))
        for ind in range(1, len(battery_drops)-1):
            total_actor_count = sum(battery_drops[ind])
            if(total_actor_count != 0):
                pending_window = self.splitIntoWindows(battery_drops[ind], pending_window)
                pending_batteryFrac = self.getBatteryFracsActorBased(battery_drops[ind], pending_batteryFrac)
        self.write_results()

    def write_results(self):
        with open(self.outputfile, 'w+') as outfile:
            for i in range(0, len(self.histograms)):
                outfile.write(json.dumps(self.histograms[i]))
                powerVal = self.batteryFrac[i].power / self.window_size
                outfile.write("\t" + str(json.dumps(powerVal)))
                outfile.write("\t" + str(json.dumps(int(self.batteryFrac[i].intervalsize))))
                # Write dummy size value
                #outfile.write("\t" + str(0))
                outfile.write("\n")


# dir = os.path.dirname(__file__)
# filename = os.path.join(dir, '../output/histogram/hist_percent_fixed_size.txt')
# newSplittingInstance = SplitFixedWindowsTumbling('/home/athuls89/Desktop/OSL/osl/MobileCloud/android_monitor/salsa/ProfiledData/BatteryRateProfile/.idea/energy/battery/mobile_logs/Nqueens_light.txt',3, filename)
# newSplittingInstance.extract_windows()
