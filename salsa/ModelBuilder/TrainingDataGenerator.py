import os
import sys
import json
import matplotlib.pyplot as plt
import collections
from numpy import *

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

                    self.log_time_stamps.append(line[1:bat_ind-2])

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
                    if actor_name in self._actor_name_list:
                        curr.append(num)
        vals_per_drop.append(curr)
        self.actor_names_with_counts.append(actor_counts_list)
        # print(self.actor_names_with_counts)
        # self.traverse_actor_names()
        return vals_per_drop
