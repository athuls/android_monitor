import tensorflow as tf
from google.protobuf import text_format


from tensorflow.python.framework import dtypes
import numpy as np

def outputgraph(frozen_graph_filename):
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

    tf.train.write_graph(graph, "", "mod.pbtxt")


def outputgraph_as_obj(frozen_graph_filename):
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
    return graph

def read_ops_graph(filename):
    graph = outputgraph_as_obj(filename)
    for op in graph.get_operations():
	print(op.name)

def realfreezed(filename):
    with open(filename) as f:
        txt = f.read()
        gdef = text_format.Parse(txt, tf.GraphDef())
        tf.train.write_graph(gdef, "", "fixed_freeze.pb",as_text=False)


def minigraph():
    x = tf.placeholder(tf.float64, shape=(None,24))
    tf.train.write_graph(x.graph, "", "mini.pbtxt")

# minigraph()
# outputgraph("frozen_model.pb")
# realfreezed("mini.pbtxt")
# realfreezed("mod.pbtxt")
read_ops_graph("savedmodel/1542825634/saved_model.pb")

