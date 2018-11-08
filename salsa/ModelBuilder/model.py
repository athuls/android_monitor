from sklearn.base import BaseEstimator, TransformerMixin
from sklearn.pipeline import make_pipeline
from sklearn.svm import SVC, SVR
from sklearn.ensemble import GradientBoostingClassifier, GradientBoostingRegressor, RandomForestClassifier, RandomForestRegressor
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.model_selection import KFold, StratifiedKFold, GridSearchCV, ParameterGrid, train_test_split
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import cross_val_score
from sklearn.gaussian_process import GaussianProcessClassifier
from sklearn.gaussian_process.kernels import RBF
from sklearn.metrics import *
#import sys
import numpy as np
import cPickle as cpkl
import argparse
import keras
from keras import regularizers
from keras.models import Model, Sequential
from keras.layers import Input, Dense, Dropout, Activation, BatchNormalization
from keras.wrappers.scikit_learn import KerasClassifier, KerasRegressor
from keras.callbacks import EarlyStopping
from sys import argv
from collections import defaultdict
import ConfigParser

class ModelFactory(object):
    """
    Factory class to return different scikit-learn models
    """
	
    def __init__(self):
        pass

    @staticmethod
    def factory(algorithm):
        if algorithm == 'svm':
            return SVC(probability = True)
        elif algorithm == 'randomForest':
            return RandomForestClassifier(random_state=42, oob_score=True)
        elif algorithm == 'svm_regression':
            return SVR()
        elif algorithm == 'randomForest_regression':
            return RandomForestRegressor(random_state=42, oob_score=True)
        elif algorithm == 'gbm_regression':
            return GradientBoostingRegressor(random_state=42)
        elif algorithm == "naiveBayes":
            return GaussianNB()
        elif algorithm == 'mlp':
            return KerasClassifier(build_fn = create_mlp, verbose = 2)
        elif algorithm == 'mlp_regression':
            print "Regressor"
            return KerasRegressor(build_fn = create_mlp, regressor = True, verbose = True)
        elif algorithm == 'gbm':
            return GradientBoostingClassifier()
        elif algorithm == 'autoencoder':
            return KerasRegressor(build_fn = create_ae, verbose = 2)
        else:
	        assert 0, "Bad algorithm creation: " + algorithm



    
class CVClassifierWrapper(object):
    """
    Wrapper around model to carry out nested cross validation
    """
    def __init__(self, classifier, scoring, refit, parameters):
        """
        classifier: scikit-learn classifier object
        scoring: list of strings for differnt scikit-learn scoring functions
        refit: scoring function to use on the refit of all folds
        """
        self.classifier = classifier
        if scoring:
            self.scoring = scoring
        else:
            self.scoring = ['accuracy']

        if refit:
            self.refit = refit
        else:
            self.refit = self.scoring[0]

        self.parameters = parameters
        self.scorers_ = {'accuracy': accuracy_score, 'f1_score': f1_score, 'neg_mean_absolute_error': mean_absolute_error,
				                    'neg_mean_squared_error': mean_squared_error}
    

    def pipeline_param_dict(self):
        prefix = self.classifier.__class__.__name__.lower() + '__'
        params = self.parameters.keys()
        for param in params:
            self.parameters[prefix + param] = self.parameters.pop(param)
        print self.parameters

    def run_cv(self, x, y, outer_folds = 4, inner_folds = 3):
        print x.shape, y.shape
        models = []
        test_accs = []
        if outer_folds == 0 and inner_folds == 0:
            print "Need atleast outer or inner_folds >0"
            exit()

        if not self.parameters:
            num_folds = max(inner_folds, outer_folds)
            for tr_index, ts_index in KFold(n_splits = num_folds, shuffle = True).split(x,y):
                scaler = StandardScaler().fit(x[tr_index])
                x_train = scaler.transform(x[tr_index])
                x_test = scaler.transform(x[ts_index])
                self.classifier.fit(x_train, y[tr_index])
                predictions = self.classifier.predict(x_test)
                test_score = self.scorers_[self.scoring](predictions, y[ts_index])
                test_accs.append(test_score)
            print "\n\n CV Results: {0} {1}\n\n".format(np.mean(test_accs), np.std(test_accs))
            self.classifier.fit(x,y)
            return self.classifier

        self.pipeline_param_dict()
        if inner_folds == 0 or outer_folds == 0:
            num_folds = max(inner_folds, outer_folds)
            
            grid = GridSearchCV(make_pipeline(StandardScaler(), self.classifier), cv = KFold(n_splits=num_folds, shuffle=True),
                                        param_grid=self.parameters, verbose=100,
                                        n_jobs=1, scoring=self.scoring, refit=self.refit)
            grid.fit(x,y)

	    # The best score is negative because the scoring function is being maximized during grid search
            print "CV Results: {0}\n\n".format(grid.best_score_)
            scaler = StandardScaler().fit(x)
            print "Mean:\n", ','.join(map(str,scaler.mean_))
            print "Scale:\n", ','.join(map(str,scaler.scale_))
            return grid.best_estimator_

        for tr_index, ts_index in KFold(n_splits = outer_folds, shuffle=True).split(x,y):
					# , random_state=42).split(x,y):
            
            grid = GridSearchCV(make_pipeline(StandardScaler(),self.classifier), cv = inner_folds, 
                                       param_grid=self.parameters, verbose=100,
                                       n_jobs=1, scoring=self.scoring, refit=self.refit)
            grid.fit(x[tr_index], y[tr_index])
            best_model = grid.best_estimator_
            scaler = StandardScaler().fit(x[tr_index])
            predictions = best_model.predict(x[ts_index])
            test_score = self.scorers_[self.scoring](predictions, y[ts_index])
		#test_score = best_model.score(x[ts_index],y[ts_index])
		
		# The test score should be positive because it is just the mean squared error on test set
            print "[CustomDebug] {0} is test score and {1} is the best score from grid\n".format(test_score, grid.best_score_)
            models.append(best_model)
            test_accs.append(test_score)
            
        scaler = StandardScaler().fit(x)
        print "Mean:\n", ','.join(map(str,scaler.mean_))
        print "Scale:\n", ','.join(map(str,scaler.scale_))

        best_test_acc = max(test_accs)
        best_test_model = models[test_accs.index(best_test_acc)]
        print "\n\n Nested CV Results: {0} {1}\n\n".format(np.mean(test_accs), np.std(test_accs))
        return best_test_model



    
def create_mlp(num_features=3000,num_classes=20,encoding_dims=[2000], 
                 regs=[0.00001],init='uniform',optimizer='adam',
                 regressor = False):
    
    model = Sequential() 
    model.add(Dense(encoding_dims[0], input_shape=(num_features,),activation = 'relu'))
    model.add(BatchNormalization())
    model.add(Dropout(regs[0]))
    for dim, reg in zip(encoding_dims[1:],regs[1:]):
        model.add(Dense(dim, kernel_initializer = init, activation = 'relu'))
        model.add(BatchNormalization())
        
        model.add(Dropout(reg))
    if regressor:
        model.add(Dense(1, activation = 'linear')) 
        model.compile(loss='mean_squared_error',
                       optimizer=optimizer,
                       metrics=['mse'])
    else:
        model.add(Dense(num_classes, activation = 'softmax'))
        model.compile(loss = 'categorical_cross_entropy',
                       optimizer = optimizer,
                       metrics = ['accuracy'])
    print model.summary()
        
    return model



def create_ae(num_features = 3000, encoding_dims = [2000], 
               regs = [0.00001], init = 'uniform', optimizer = 'adam'):
    
    input_img = Input(shape = (num_features,))
    encoded = input_img
    
    for dim, reg in zip(encoding_dims, regs):
        encoded = Dense(dim, kernel_initializer = init,
                         activity_regularizer = regularizers.l1(reg),
                         activation = 'relu')(encoded)
    
    decoded = Dense(num_features, kernel_initializer = init,
                    activity_regularizer = regularizers.l1(reg),
                    activation = 'sigmoid')(encoded)
    
    autoencoder = Model(input_img, decoded)
    
    autoencoder.compile(loss = 'mean_squared_error',
                         optimizer = optimizer,
                         metrics = ['accuracy'])
    return autoencoder


def evaluate_params(clf, x, y, param_dict):
    """
    Function to evaluate individual parameters as a
    first pass to identify accuracy trends for each
    parameter in an effort to narrow down parameter ranges 
    before running on all parameter combinations.
    """
	#clf_func=locals[clf_func]
    g=[] 
    for param in param_dict:
        g.append({param:[param_dict[param]]})		

        clf.parameters = ParameterGrid(g)
        grid=clf.run_cv(x, y, ParameterGrid(g))

    return grid

def cross_validation_split(n_splits, shuffle):
    return StratifiedKFold(n_splits=n_splits,shuffle=shuffle, random_state=42)

def prepData(x,scaler=None):
    """
    Normalize data
    """
    if not scaler:
        scaler = StandardScaler().fit(x)
    x_transformed= scaler.transform(x)
    return x_transformed,scaler

def dictionaryEncoder(y, label_dict):
    """
    This is used when more than one label map to a single class
    """

    y=[label_dict[label] for label in y]
    return y

def loadData(filename, x_end_index=None, label_index=None, label_dict=None, regression = False):
    """
    Function to load data from cPickle, npy or csv files into a numpy array.
    label_func: Function to convert character label to numeric labels
    x_end_index: column indices for the features range from 0, x_end_index-1
    label_index: column to use as label
    label_dict: If defined, dictionaryEncoder is used with the label_dict.
                Or else, sklearn LabelEncoder is used. This is useful when
               more than one label maps to a single class
    """
	
    if label_index == None:
        label_index = -1
    else:
        label_index = int(label_index)
    
    if x_end_index == None:
        x_end_index = -2
    else:
        x_end_index = int(x_end_index)
    
    x = []
    y = []
    print x_end_index,label_index

    #cPickle file input
    if filename.endswith("cpkl"):
        x, y = cpkl.load(open(filename,"r"))
    #Numpy array file input        
    elif filename.endswith("npy"):
        data = np.load(filename)
        x = data[:,:x_end_index]
        y = data[:,label_index]
    # treat the file as csv
    else:
        file_ptr = open(filename, "r")
        for line in file_ptr:
            x.append(line.split(',')[:x_end_index])
            y.append(line.split(',')[label_index].strip('"'))


    if label_dict:
        f = open(label_dict, 'r')
        label_dict = cpkl.load(f)
        f.close()
        y = dictionaryEncoder(y, label_dict)

    # Label encoding not needed for regression tasks
    elif not regression:
        encoder = LabelEncoder()
        encoder.fit(y)
        y = encoder.transform(y)
    else:
        pass

    x = np.nan_to_num(np.array(x,dtype = np.float32))
    y = np.nan_to_num(np.array(y,dtype = np.float32))
    
    print x.shape
    return x,y

def parse_config(config_file):
    """
    Parse the config file.
    Requrired configurations are-
    train_input: filename to extract training data
    test_size: should be between [0.1]. Fraction of the training data
    algorithm: Classification or Regression algorithm to use

    Optional configurations are-
    """
    config = ConfigParser.ConfigParser()
    config.read(config_file)

    args = defaultdict(None)
    #Reading required section
    try:
        args['train_input'] = config.get('Required', 'train_input')
        #args['test_size'] = config.getint('Required', 'test_size')
        args['algorithm'] = config.get('Required', 'algorithm')
        args['outer_folds'] = config.getint('Required', 'outer_folds')
        args['inner_folds'] = config.getint('Required', 'inner_folds')
            #args['param_dict'] = config.get('Required', 'param_dict')
    except ConfigParser.NoOptionError:
        print "Required Config option missing"
        exit()

    #Reading optional section
    optional_args = defaultdict(lambda: None, config.items('Optional'))
    return args, optional_args


def run(args, optional_args):
    """
    args, optinal_args are parsed using ConfigParser from the supplied
    config files' "Required" and "Optional" sections respectively.
    Required args
    ===============================================================
    train_input: File with data in either csv or npy ndarray or cpkl
                             file with tuple of two arrays- x,y
    algorithm: Model type to train

    Optional Arguments
    ================================================================
    param_dict: Dictionary of parameters on which to run grid search
                cross-validation

    test_size: Should be between 0-1 and it denotes the fraction of 
               the data to be set aside for test. Useful if not 
               doing nested cross validation.

    eval_params: If set, i.e any value is associated with it in the config file, 
                 instead of running a grid search, run cross-validation on different
                 values of one parameter at a time. Useful to understand trends for
                 each parameter in the model before setting a smaller grid for
                 search.
                 
    output_file: Filename to save model object
    
    x_end_index: Last column of features. Features are assumed to be in
                 continuous columns starting from 0. Default -2.
    
    label_index: Label column. 0-indexed. Default -1.

    label_dict:  Dictionary mapping values in label column to categorical
                 values. Useful when more than one value corresponds to
                 the same class. Saves time recreating data with correct
                 labels in such a case. Default None.

    """
    if args['algorithm'].endswith('regression'):
        regression = True
    else:
        regression = False
    x, y = loadData(args['train_input'], x_end_index=optional_args['x_end_index'],
                        label_index=optional_args['label_index'],
                        label_dict=optional_args['label_dict'], regression = regression)

    
    x_test = None
    if optional_args['test_size']:
        test_size = float(optional_args['test_size'])
        

        x_train, x_test, y_train, y_test = train_test_split(x, y, test_size = test_size,
                                                                        random_state=42)

	#Moving this to inside CV
    #x_train, scaler = prepData(x_train)
    #x_train = np.nan_to_num(x_train)
    #if x_test is not None:
    #	    x_test, _ = prepData(x_test, scaler)
    #	    x_test = np.nan_to_num(x_test)

    #print "Mean:\n" 
    #print ','.join(map(str,scaler.mean_))
    #print "Variance:\n"
    #print ','.join(map(str,scaler.var_))
    method = ModelFactory()
    if optional_args['param_dict']:
        param_dict = cpkl.load(open(optional_args['param_dict'],'r'))
        if args['algorithm'] == 'mlp_regression' or args['algorithm'] == 'mlp':
            param_dict['num_features'] = [x_train.shape[1]]
    else:
        param_dict = None
    
    print "Loading Data...."
    
    clf = CVClassifierWrapper(method.factory(args['algorithm']), optional_args['scoring'], 
                                 optional_args['refit'], param_dict)
    
	


    if optional_args['eval_params']:
        model = evaluate_params(clf, x_train, y_train, param_dict)
    else:
        model = clf.run_cv(x_train, y_train, args['outer_folds'], args['inner_folds'])

    if test_size > 0:
        test_score = model.score(x_test, y_test)
        print "Test score: "+str(test_score)

    if optional_args['output_file']:
        if args['algorithm'] == 'mlp' or args['algorithm'] == 'mlp_regression':
            json_string = model.model.to_json()
            f = open(optional_args['output_file']+'.json', 'w')
            f.write(json_string)
            f.close()
            model.model.save(optional_args['output_file']+'.h5')
        else:
            f = open(optional_args['output_file'], 'w')
            cpkl.dump(model, f, -1)
            f.close()

	    # Sanity check the model written to pickle file
	    verify_model(optional_args, args, x_train, y_train)

def verify_model(optional_args, args, x_train_verify, y_train_verify):
    #TODO: find function to load keras models and use that to verify them
    pickle_model = None
    # scaler = StandardScaler().fit(x_train_verify)
    # normalized_xtrain = scaler.transform(x_train_verify)

    if optional_args['output_file'] and args['algorithm'] != 'mlp' and args['algorithm'] != 'mlp_regression':
        with open(optional_args['output_file'], 'rb') as file:
	    pickle_model = cpkl.load(file) 
	y_predict_verify = pickle_model.predict(x_train_verify)
	for test_i in range(0, len(y_train_verify)):
		print("Expected: {0:.2f} and Actual: {1:.2f}".format(y_train_verify[test_i], y_predict_verify[test_i]))

if __name__=="__main__":
    print "starting script"
    name, config_file = argv
    args, optional_args = parse_config(config_file)
    run(args, optional_args)
