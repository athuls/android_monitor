# param 1: log file to be processed
# param 2: number of samples per second

import sys
import numpy as np

import matplotlib.pyplot as plt

first = True

time_int = 1.0/float(sys.argv[2])


curr_int = [] #current actor count array
curr_sec = [] #current time array

graphs = []

time = 0

with open(sys.argv[1]) as fp:
    prev = 0
    for line in fp:
        #append to battery level percentage
        ib = line.find('Battery')
        if(ib != -1):
            indBat = ib + 17
            indBatEnd = line.find(' ', indBat)
            batteryVal = float(line[indBat : indBatEnd])
            if(first):
                prev = batteryVal
                first = False
            elif(prev != batteryVal):
                prev = batteryVal
                graphs.append((curr_sec, curr_int))
                curr_int = []
                curr_sec = []
                time = 0
        #append to actor count
        ind = line.find('$State')
        if(line[0] == '[' and line.find('no') != -1):
            curr_int.append(0)
            curr_sec.append(time)
            time += time_int
        elif(ind != -1):
            comma_ind = line.find(',', ind)
            num = int(line[ind + 8 : comma_ind])
            curr_int.append(num)
            curr_sec.append(time)
            time += time_int

regs = []

for graph in graphs:
    m,b = np.polyfit(graph[0], graph[1], 1)
    regs.append((m,b))


areas = []

for i in range(0, len(regs)):
    x_st = graphs[i][0][0]
    x_end = graphs[i][0][-1]


    x = x_end - x_st

    y_st = regs[i][0]*x_st + regs[i][1]
    y_end = regs[i][0]*x_end + regs[i][1]

    a = 0.5*(y_st + y_end)*x
    areas.append(a)

print areas
