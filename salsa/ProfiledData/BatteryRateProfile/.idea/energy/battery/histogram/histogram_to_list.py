import sys
import json
import os

def convertToList(histogram_in_json):
    list = []
    for key in histogram_in_json:
        freq = histogram_in_json[key]
        i = 0
        while i < freq:
            list.append(key)
            i += 1
    return list