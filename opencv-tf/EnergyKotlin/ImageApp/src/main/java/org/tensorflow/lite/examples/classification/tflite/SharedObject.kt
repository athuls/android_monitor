package org.tensorflow.lite.examples.classification.tflite

object Fractions{
    init {

    }
    var fraction = 0.5
    fun setVal(fract:Double){
        fraction = fract
    }
    fun getVal():Double{
        return fraction
    }
}
