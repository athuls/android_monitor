from hist_interface import *

# param 1: file name of the histogram created


filename = sys.argv[1]

hists = getHistosFromFile(filename)

for i in range(0, len(hists)):
    hists[i] = {int(k):int(v) for k,v in hists[i].items()}
hists = normalize_histos(hists)

for i in range(0, len(hists)-1):
    print(simple_difference(hists[i], hists[i+1]))
    #print hists[0]
    #print hists[1]