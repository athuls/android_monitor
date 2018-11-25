import tensorflow as tf
from google.protobuf import text_format
import argparse

from tensorflow.python.framework import dtypes
import numpy as np

def toPBTXT(frozen_graph_filename, output_file):
    # We load the protobuf file from the disk and parse it to retrieve the
    # unserialized graph_def
    with tf.gfile.GFile(frozen_graph_filename, "rb") as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())

    # Then, we import the graph_def into a new Graph and returns it
    with tf.Graph().as_default() as graph:
        # The name var will prefix every op/nodes in your graph
        # Since we load everything in a new graph, this is not needed
        tf.import_graph_def(graph_def, name="")

    tf.train.write_graph(graph, "", output_file)


def toPB(filename, output_file):
    with open(filename) as f:
        txt = f.read()
        gdef = text_format.Parse(txt, tf.GraphDef())
        tf.train.write_graph(gdef, "", output_file, as_text=False)


def placeholder():
    x = tf.placeholder(tf.float64, shape=(None,24))
    tf.train.write_graph(x.graph, "", "placeholder.pbtxt")


"""
Steps after calling freeze.py: 
1. run "python3 convert.py --toPBTXT --input model_foldr/frozen_model
                                 --output <pbtxt_file_name>.pbtxt
2. run "python3 convert.py --placeholder"
3. From the output file "pbtxt_file_name.pbtxt", delete several nodes from the beginning of the
text file. Remove nodes until you see one first node with a name like this:
"dnn/input_from_feature_columns/input_layer/<actor_name>/ToFloat""
4. Copy the json for the single node in placeholder.pb file, add it to 
    "pbtxt_file_name.pbtxt" at the very beginning of the file
5. Change the input attribute of first input node to "Placeholder"
   - If there are multiple inputs where Placeholder should go (e.g. when there are more than 24 dimensions due to multiple actor types), create input nodes Placeholder1, Placeholder2, etc. for each input
6. run "python3 convert.py --toPB --input pbtxt_file_name.txt
                                  --output <pb_file.pb>
"""


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--toPB", action='store_true')
    parser.add_argument("--toPBTXT", action='store_true')
    parser.add_argument('--placeholder', action='store_true')
    parser.add_argument('--input', type=str, default='input', help="filename input")
    parser.add_argument('--output', type=str, default='output', help="filename output")

    args = parser.parse_args()

    if args.placeholder:
        placeholder()
    if args.toPB:
        toPB(args.input, args.output)
    if args.toPBTXT:
        toPBTXT(args.input, args.output)

