import sys
from libs import histograms
#import kmeans

#def cluster_histograms(dict_hists_data, num_clusters)
	
#	return clusters

def normalize_histos(dict_hists):
	maxi = 0
	for i in range(0, len(dict_hists)):
		if(len(dict_hists[i])-1 > maxi):
			maxi = len(dict_hists[i])-1
	for i in range(0, len(dict_hists)):
		while(len(dict_hists[i]) - 1 < maxi):
			newIndex = len(dict_hists[i])
			dict_hists[i][newIndex] = 0
	return dict_hists

def convertToList(dict_hists):
	return_dict = []
	for i in range(0, len(dict_hists)):
		if(type(dict_hists[i]) != list):
			newList = []
			for j in range(0, len(dict_hists[i])):		
				newList.append(dict_hists[i][j])
			return_dict.append(newList)
		else:
			return_dict = dict_hosts
	return return_dict

fileName = sys.argv[1]
interval = int(sys.argv[2])
samples_per_sec = int(sys.argv[3])
dict_hists = histograms.generate_histograms(fileName, interval, samples_per_sec)
dict_hists_normal=normalize_histos(dict_hists)
list_hists_normal=convertToList(dict_hists_normal)
for i in range(0, len(list_hists_normal)):
	for j in range(0, len(list_hists_normal[i])):
		print list_hists_normal[i][j], 
	print

#cluster_centers = kmeans.find_centers(list_hists_normal, 3)
#print cluster_centers
