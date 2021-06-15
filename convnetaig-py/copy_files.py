import argparse
from pathlib import Path
import os
import shutil

parser = argparse.ArgumentParser()
parser.add_argument("source", type=Path)
parser.add_argument("dest", type=Path)
args = parser.parse_args()

SELECTED_CLASSES = ['n02093754', 'n02089973', 'n02128385', 'n09193705', 'n04285008', 'n03937543', 'n03908714', 'n02106030', 'n01871265', 'n02978881']

#args.dest.mkdir()

for i in args.source.iterdir():
    if i.name in SELECTED_CLASSES:
        shutil.copytree(i, args.dest / i.name)
