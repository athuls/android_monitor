#python kmeans.py <file name> <delimiter> <number of centers>
import sys
import numpy as np
from numpy import *
import random
 
num_iter=0

def cluster_points(X, mu):
    clusters  = {}
    for x in X:
        bestmukey = min([(i[0], np.linalg.norm(x-mu[i[0]])) \
                    for i in enumerate(mu)], key=lambda t:t[1])[0]
        try:
            clusters[bestmukey].append(x)
        except KeyError:
            clusters[bestmukey] = [x]
    return clusters
 
def reevaluate_centers(mu, clusters):
    newmu = []
    keys = sorted(clusters.keys())
    print "old length is", len(mu)
    for k in keys:
        newmu.append(np.mean(clusters[k], axis = 0))
    print "new length is", len(newmu)
    return newmu

def has_converged(mu, oldmu):
    return (set([tuple(a) for a in mu]) == set([tuple(a) for a in oldmu]))
    #global num_iter
    #num_iter=num_iter+1
    #return (num_iter > 40)
 
def find_centers(X, K):
    # Initialize to K random centers
    oldmu = random.sample(X, K)
    mu = random.sample(X, K)
    while not has_converged(mu, oldmu):
        oldmu = mu
	#print mu
        # Assign all points in X to clusters
        clusters = cluster_points(X, mu)
        # Reevaluate centers
        mu = reevaluate_centers(oldmu, clusters)
    return(mu, clusters)

X = loadtxt(sys.argv[1],delimiter=sys.argv[2])
#data=np.delete(X,[4],axis=1)
a = find_centers(X,int(sys.argv[3]))
print len(a[1]), num_iter
for i in range(0, len(a[1])):
	print len(a[1][i]),
#print a
#print len(a[1][0]), len(a[1][1]), len(a[1][2]), len(a[1][3]), len(a[1][4])
