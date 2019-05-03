import sys
from scipy.stats.stats import pearsonr

data = []
target = []
with open(sys.argv[1]) as fobj:
	for line in fobj:
		row = line.split(',')
		float_row = [float(i) for i in row]
	        data.append(float_row[:-1])
       		target.append(float_row[-1])
	
for i in range(0,len(data[0])):
	x = [col[i] for col in data]
	corr = pearsonr(x, target)
	print(corr)
