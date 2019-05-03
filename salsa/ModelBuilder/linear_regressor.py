# Doing linear regression with leave one out cross val
from sklearn import linear_model
from sklearn import datasets
from sklearn.model_selection import cross_val_score, cross_val_predict, cross_validate, KFold, LeaveOneOut
from sklearn.preprocessing import StandardScaler
# from pygam import LinearGAM
import numpy as np
import sys
import statsmodels.stats.stattools as st
import matplotlib.pyplot as plt
from sklearn.linear_model import RANSACRegressor

from sklearn.metrics import r2_score

if len(sys.argv) != 2:
	print("Arguments: training input file name")
	sys.exit(1)

# Including this to remind you that it is necessary to use numpy arrays rather 
# than lists otherwise you will get an error

label_index=-1
x=[]
y=[]
file_ptr=open(sys.argv[1], "r")
for line in file_ptr:
	x_end_index = len(line.split(',')) - 1
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

#regr = linear_model.LinearRegression()
#regr = linear_model.Lasso(alpha=0.1)
regr = linear_model.Lasso(alpha=0.6)
ransac_regr = RANSACRegressor(base_estimator=regr, residual_threshold=200)
# lasso = linear_model.RANSACRegressor()

#kf = KFold(n_splits=8,shuffle=False)
#for train_index_i, test_index_i in kf.split(Y_digits):
#	print("Train: ", train_index_i, "Test:", test_index_i)

scaler_obj = StandardScaler().fit(X_digits)
X_normalized = scaler_obj.transform(X_digits)

# scores = cross_val_predict(regr, X_normalized, Y_digits,  cv=KFold(n_splits=4,shuffle=True))
#scores = cross_val_score(regr, X_normalized, Y_digits, scoring='neg_mean_squared_error', cv=KFold(n_splits=8,shuffle=True))
#scores = cross_val_score(regr, X_normalized, Y_digits, scoring='r2', cv=KFold(n_splits=8,shuffle=True), verbose=1)

#kf=KFold(n_splits=8,shuffle=True, random_state=100)
kf=KFold(n_splits=8,shuffle=True)
#scores = cross_val_score(regr, X_digits, Y_digits, scoring='r2', cv=kf)
scores = cross_val_score(regr, X_normalized, Y_digits, scoring='neg_mean_squared_error', cv=kf)
#for train_idx,test_idx in kf.split(X_normalized, Y_digits, None):
#	print("TRAIN: ", train_idx, "TEST: ", test_idx)
for train_idx,test_idx in kf.split(X_normalized, Y_digits, None):
	print("TRAIN: ", train_idx, "TEST: ", test_idx)

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
regr_fit = regr.fit(X_digits, Y_digits)

print regr_fit.coef_
print regr_fit.intercept_
print "Scores:"
print abs(scores)
print np.mean(np.sqrt(abs(scores)))
print np.std(np.sqrt(abs(scores)))


print "Finding R-squared"
y_pred = regr.predict(X_digits)
print("Durbin-Watson statistic:" + str(st.durbin_watson(Y_digits-y_pred)))
plt.plot((Y_digits-y_pred))
plt.show()
#print("Variance score: %.2f" % r2_score(y_pred,Y_digits))
# print("CV Results: " + str(scores['test_score'].mean()))
