import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np

matplotlib.rc('xtick', labelsize=25)
matplotlib.rc('ytick', labelsize=25)

fileName=sys.argv[1]
count=0
accuracies=[]
sizes=[]
with open(fileName,"r") as fp:
	for line in fp:
		if(count==0):
			count+=1		
			continue
		values=line.strip("\n").split(",")
		accuracies.append(float(values[1]) * 100)
		sizes.append(float(values[0]))

accuracies=np.asarray(accuracies)
sizes=np.asarray(sizes)
#plt.errorbar(ratios,means,[means-mins,maxs-means],fmt='.k',ecolor='blue',lw=2,markersize=12)
plt.plot(sizes,accuracies)
plt.xlabel("Training Data Size (# Discharge Intervals)",fontsize=30)
plt.ylabel("Accuracy (RMSE) Gain Of SVR\nRelative To Linear(%)",fontsize=30,multialignment='center')
plt.show()		
