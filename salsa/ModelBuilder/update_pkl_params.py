import  cPickle as pickle

pickleFile = open("randomforest_param_dict.cpkl", 'rb')
test = pickle.load(pickleFile)

print ("Original pkl data is : ", test)
test['n_estimators'] = [10, 50, 100]
print ("Updated pkl data is : ", test)
pickleFile.close()

newPickleFile = open("randomforest_param_dict_updated.cpkl", 'wb')
pickle.dump(test, newPickleFile)
newPickleFile.close()


