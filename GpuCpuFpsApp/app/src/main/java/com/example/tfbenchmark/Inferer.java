/*
Based on https://github.com/tensorflow/examples/blob/master/lite/examples/object_detection/android/lib_interpreter/src/main/java/org/tensorflow/lite/examples/detection/tflite/TFLiteObjectDetectionAPIModel.java
Adapted by Victor Szabo

Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.tfbenchmark;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class Inferer {
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;

    private int inputSize;
    private int batchSize;
    private ByteBuffer imgData;
    private float[][] output;

    private Object[] inputArray;
    private Map<Integer, Object> outputMap;

    private Interpreter tfLite;

    Inferer(Context context, String modelFilename, int inputSize, int batchSize) {
        this.inputSize = inputSize;
        this.batchSize = batchSize;
        int numBytesPerChannel = 4;
        imgData = ByteBuffer.allocateDirect(batchSize * inputSize * inputSize * 3 * numBytesPerChannel);
        imgData.order(ByteOrder.nativeOrder());
        inputArray = new Object[]{imgData};

        output = new float[batchSize][1001];
        outputMap = new HashMap<>();
        outputMap.put(0, output);

        try {
            loadModel(context.getAssets(), modelFilename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Memory-map the model file in Assets.
     */
    private void loadModel(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        MappedByteBuffer modelFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(4);
        options.setUseXNNPACK(true);
        //options.addDelegate(new GpuDelegate());
        tfLite = new Interpreter(modelFile, options);
        tfLite.resizeInput(0, new int[]{batchSize, inputSize, inputSize, 3});
    }

    public void infer(int[][] batch) {
        imgData.rewind();
        if (batch.length != batchSize) {
            throw new AssertionError("Incorrect batch size");
        }
        for (int[] intValues : batch) {
            if (intValues.length != inputSize * inputSize) {
                throw new AssertionError("Incorrect input size");
            }
            for (int i = 0; i < inputSize * inputSize; ++i) {
                int pixelValue = intValues[i];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
            }
        }

        Trace.beginSection("runInference");
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
        Trace.endSection();
    }
}
