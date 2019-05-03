# Doing linear regression with leave one out cross val
from sklearn import linear_model
from sklearn import datasets
from sklearn.model_selection import cross_val_score, cross_val_predict, cross_validate, KFold, LeaveOneOut
from sklearn.preprocessing import StandardScaler
# from pygam import LinearGAM
import numpy as np
import sys
from sklearn.metrics import r2_score
import random

if len(sys.argv) < 1:
	print("Arguments: training input file name1, training input file name2, ..., destination file")
	sys.exit(1)

# Including this to remind you that it is necessary to use numpy arrays rather 
# than lists otherwise you will get an error

points_from_each_file=int(100/(len(sys.argv)-2))
print(str(points_from_each_file)+ " " + str(len(sys.argv)))
final_file=[]
for i in range(1,len(sys.argv)-1):
	print("i is "+str(i))
	file_ptr=open(sys.argv[i],"r")
	file_content=[]
	for line in file_ptr:
		file_content.append(line)
	data_samples=random.sample(range(0,len(file_content)-1), points_from_each_file)
	for data_sample in data_samples:
		print("idx is "+str(data_sample))
		final_file.append(file_content[data_sample])

	#for j in range(0,points_from_each_file):
		#idx=random.randint(0,len(file_content)-1)
		#print("idx is "+str(idx))
		#final_file.append(file_content[idx])	

dest_file=sys.argv[-1]
with open(dest_file,'w') as f:
	for line in final_file:
		f.write(line)
