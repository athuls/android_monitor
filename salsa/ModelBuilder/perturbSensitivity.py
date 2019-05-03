from sys import argv

import numpy as np
import pandas as pd

x_dists = []

def find_var_score(filename, x_tol, y_tol, num_x_dims):
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

	diff = [0] * num_x_dims
	for j in range(num_x_dims):
		for i in range(len(x_dists)):
			diff[j] += x_dists[i][j]

	new_diff = [diff_x/len(x_dists) for diff_x in diff]
	normalize_avg_diff = np.sum(new_diff)/len(new_diff)
	print(new_diff)	
	print(normalize_avg_diff)

def find_outliers(filename, x_tol, y_tol):
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
			#x_dist = np.sum(np.abs(x[i][24:48] - x[j][24:48]))
			x_dist = np.sum(np.abs(x[i] - x[j]))
			#x_dist = np.sum(np.abs(x[0] - x[0]))
			#x_dist += np.sum(np.abs(x[2:4] - x[2:4]))
			#x_dist += np.sum(np.abs(x[6:10] - x[6:10]))
			y_dist = np.abs(y[i]-y[j])
			#if x_dist <=x_tol and y_dist >y_tol:
			if x_dist <=x_tol:
				#print "Pair of Points:"
				#print x[i],y[i]
				#print x[j],y[j]
				#print(x[i] - x[j], y[i] - y[j])
				num_pairs += 1
	print("Number of pairs is " + str(num_pairs))	

if __name__ == "__main__":
	_,filename, x_tol, y_tol, x_dims = argv
	find_outliers(filename, float(x_tol), float(y_tol))
	#find_var_score(filename, float(x_tol), float(y_tol), int(x_dims))
