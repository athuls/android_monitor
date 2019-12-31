import sys
import matplotlib.pyplot as plt
import matplotlib
import numpy as np

matplotlib.rc('xtick', labelsize=14)
matplotlib.rc('ytick', labelsize=14)

fileName=sys.argv[1]
count=0
mins=[]
maxs=[]
means=[]
ratios=[]
with open(fileName,"r") as fp:
	for line in fp:
		if(count==0):
			count+=1		
			continue
		values=line.strip("\n").split(",")
		mins.append(float(values[1]) * 100)
		means.append(float(values[2]) * 100)
		maxs.append(float(values[3]) * 100)
		ratios.append(float(values[0]))

print(means)
print(maxs)
print(mins)
print(ratios)
means=np.asarray(means)
maxs=np.asarray(maxs)
mins=np.asarray(mins)
ratios=np.asarray(ratios)
plt.errorbar(ratios,means,[means-mins,maxs-means],fmt='.k',ecolor='blue',lw=2,markersize=12)
plt.xlabel("High Power State : Low Power State Ratio",fontsize=20)
plt.ylabel("Accuracy (RMSE) Gain Of SVR Relative To Linear(%)",fontsize=20)
plt.show()		
