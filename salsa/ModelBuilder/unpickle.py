import  cPickle as pickle

pickleFile = open("new_exsort_rfr_regression_model", 'rb')
test = pickle.load(pickleFile)

print (test.estimators_[0].feature_importances_)
# print ("Original pkl data is : ", test)
# test['n_estimators'] = [20, 50, 100, 150]
# print ("Updated pkl data is : ", test)
pickleFile.close()

# newPickleFile = open("randomforest_param_dict_updated.cpkl", 'wb')
# pickle.dump(test, newPickleFile)
# newPickleFile.close()


