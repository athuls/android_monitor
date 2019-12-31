import os
import math
import sys
import matplotlib.pyplot as plt
import numpy as np

fileName=sys.argv[1]
avgLinear=[]
avgSvm=[]
avgError=[]
with open(fileName, "r") as fp:
	for line in fp:
		if("Avg" in line):
			vals = line.strip("\n").split(",")
			avgLinear.append(float(vals[0].split(":")[1]))
			avgSvm.append(float(vals[1].split(":")[1].rstrip('\"')))
			print(avgLinear)
			print(avgSvm)
			avgError.append((avgLinear[-1]-avgSvm[-1])/avgLinear[-1])

print(np.amin(avgError))
print(np.mean(avgError))
print(np.amax(avgError))
print(avgError)
plt.plot(avgError)
plt.show()
