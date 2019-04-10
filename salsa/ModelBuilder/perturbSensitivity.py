from sys import argv

import numpy as np
import pandas as pd

x_dists = []

def find_var_score(filename, x_tol, y_tol):
	f = open(filename, "r")
	lines = f.readlines()
	f.close()
	x  = []
	y = []
	for line in lines[1:]:
		x.append(line.split(',')[:-1])
		y.append(line.split(',')[-1].rstrip())
	x = np.array(x, dtype = np.float32)
	y = np.array(y, dtype = np.float32)
	
	num_pairs = 0

	for i in range(len(x)):
		for j in range(i+1,len(x)):
			x_dists.append(np.abs(x[i] - x[j]))

	diff = [0] * 24
	for j in range(24):
		for i in range(len(x_dists)):
			diff[j] += x_dists[i][j]

	new_diff = [diff_x/len(x_dists) for diff_x in diff]
	print(new_diff)	

def find_outliers(filename, x_tol, y_tol):
	#df = pd.read_csv(filename)

	#data = np.read_csv(filename)
	f = open(filename, "r")
	lines = f.readlines()
	f.close()
	x  = []
	y = []
	for line in lines[1:]:
		x.append(line.split(',')[:-1])
		y.append(line.split(',')[-1].rstrip())
	x = np.array(x, dtype = np.float32)
	y = np.array(y, dtype = np.float32)
	
	num_pairs = 0

	for i in range(len(x)):
		#for j in range(i,len(x)):
		for j in range(i+1,len(x)):
			x_dist = np.sum(np.abs(x[i] - x[j]))
			#x_dist = np.sum(np.abs(x[0] - x[0]))
			#x_dist += np.sum(np.abs(x[2:4] - x[2:4]))
			#x_dist += np.sum(np.abs(x[6:10] - x[6:10]))
			y_dist = np.abs(y[i]-y[j])
			#if x_dist <=x_tol and y_dist >y_tol:
			if x_dist <=x_tol:
				print "Pair of Points:"
				print x[i],y[i]
				print x[j],y[j]
				num_pairs += 1
	print("Number of pairs is " + str(num_pairs))	

if __name__ == "__main__":
	_,filename, x_tol, y_tol = argv
	#find_outliers(filename, float(x_tol), float(y_tol))
	find_var_score(filename, float(x_tol), float(y_tol))
