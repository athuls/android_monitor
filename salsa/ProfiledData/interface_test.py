from hist_interface import *

# param 1: file name of the histogram created


filename = sys.argv[1]

hists = normalize_histos(getHistosFromFile(filename))

print hists[0]
print hists[1]

print simple_difference(hists[0], hists[1])
