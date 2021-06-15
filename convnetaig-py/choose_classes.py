import os
import random

CLASSES_DIR = "/home/victor/urap/val_blurred"
NUM_CLASSES = 10
VIEWER = "xdg-open"

classes = random.sample(os.listdir(CLASSES_DIR), NUM_CLASSES)
print(classes)
for c in classes:
    class_dir = f"{CLASSES_DIR}/{c}"
    os.system(f"{VIEWER} {class_dir}/{os.listdir(class_dir)[0]}")
