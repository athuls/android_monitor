# Doing linear regression with leave one out cross val
from sklearn import linear_model
from sklearn import datasets
from sklearn.model_selection import cross_val_score, cross_validate, KFold, LeaveOneOut
# from pygam import LinearGAM
import numpy as np
import sys

if len(sys.argv) != 3:
	print("Arguments: training input file name, x_label_index")
	sys.exit(1)

# Including this to remind you that it is necessary to use numpy arrays rather 
# than lists otherwise you will get an error
x_end_index=int(sys.argv[2])
label_index=-1
x=[]
y=[]
file_ptr=open(sys.argv[1], "r")
for line in file_ptr:
	x.append(line.split(',')[:x_end_index])
	y.append(line.split(',')[label_index].strip('"'))

X_digits = np.array(x)
print(X_digits)
X_digits = np.nan_to_num(np.array(X_digits,dtype=np.float32))
Y_digits = np.array(y)
Y_digits = np.nan_to_num(np.array(Y_digits,dtype=np.float32))

# workload_data=np.loadtxt(sys.argv[1], dtype='float',comments='#', delimiter=",",converters=None, skiprows=1,usecols=None, unpack=False,ndmin=0)
# loo = cross_validation.LeaveOneOut(len(Y_digits))
# loo = LeaveOneOut(len(Y_digits))
#print(workload_data)

regr = linear_model.LinearRegression()
# lasso = linear_model.RANSACRegressor()

scores = cross_val_score(regr, X_digits, Y_digits, scoring='neg_mean_squared_error', cv=KFold(n_splits=8,shuffle=True))
# scores = cross_validate(regr, X_digits, Y_digits, scoring='neg_mean_squared_error', cv=KFold(n_splits=20, shuffle=True), return_train_score=True)
#scores = cross_validate(regr, X_digits, Y_digits, scoring='neg_mean_squared_error', cv=3, return_train_score=True)
#scores = cross_validate(regr, X_digits, Y_digits, scoring='neg_mean_squared_error', cv=KFold(n_splits=20, shuffle=True), return_train_score=True)

#gam_model=LinearGAM(n_splines=25,spline_order=3)
#gam=gam_model.gridsearch(X_digits, Y_digits, lam=np.logspace(-3, 3, 11), return_scores=True)

#print(gam_model)
# print(gam)

# scores = cross_validate(gam, X_digits, Y_digits, scoring='neg_mean_squared_error', cv=KFold(n_splits=20, shuffle=True), return_train_score=True)
#gam=LinearGAM(n_splines=10).gridsearch(X_digits,Y_digits)

# This will print the mean of the list of errors that were output and 
# provide your metric for evaluation

print scores
print np.mean(np.sqrt(abs(scores)))
print np.std(np.sqrt(abs(scores)))
# print("CV Results: " + str(scores['test_score'].mean()))
