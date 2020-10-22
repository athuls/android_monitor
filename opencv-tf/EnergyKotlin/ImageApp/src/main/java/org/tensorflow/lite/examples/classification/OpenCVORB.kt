package org.tensorflow.lite.examples.classification

//import android.support.v4.app.ActivityCompat
//import android.support.v4.content.ContextCompat
//import android.support.v7.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.TextView
import org.opencv.android.Utils
import org.opencv.android.Utils.*
import org.opencv.core.*
import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.FeatureDetector
import org.opencv.imgproc.Imgproc
import java.io.File
import java.util.*


class OpenCVORB {
    private var w = 0
    private var h = 0
   // private var mOpenCvCameraView: CameraBridgeViewBase? = null
    var tvName: TextView? = null
    var RED = Scalar(255.00, 0.00, 0.00)
    var GREEN = Scalar(0.00, 255.00, 0.00)
    var detector: FeatureDetector? = null
    var descriptor: DescriptorExtractor? = null
    var matcher: DescriptorMatcher? = null
    var descriptors2: Mat? = null
    var descriptors1: Mat? = null
    var img1: Mat? = null
    var keypoints1: MatOfKeyPoint? = null
    var keypoints2: MatOfKeyPoint? = null
//


//    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
//        override fun onManagerConnected(status: Int) {
//            when (status) {
//                LoaderCallbackInterface.SUCCESS -> {
//                    Log.i(TAG, "OpenCV loaded successfully")
//                    mOpenCvCameraView!!.enableView()
//                    try {
//                        initializeOpenCVDependencies()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//                else -> {
//                    super.onManagerConnected(status)
//                }
//            }
//        }
//    }


     fun Basic(AInp1 : Bitmap): Boolean{
      //  mOpenCvCameraView!!.enableView()
        detector = FeatureDetector.create(FeatureDetector.ORB)
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB)
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
       // var fileName = "/sdcard/a.jpg"
        //var AInp1 = BitmapFactory.decodeFile(fileName)
        img1 = Mat()
        var aInputFrame : Mat? = null
        aInputFrame = Mat()
        bitmapToMat(AInp1,img1 )
       // Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY)
       // val assetManager = assets

       // Utils.bitmapToMat(bitmap, img1)
        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY)
        img1!!.convertTo(img1, 0) //converting the image to match with the type of the cameras image
        descriptors1 = Mat()
        keypoints1 = MatOfKeyPoint()
        detector!!.detect(img1, keypoints1)
        descriptor!!.compute(img1, keypoints1, descriptors1)
        return true
    }

    /**
     * Called when the activity is first created.
     */








     fun onCameraViewStarted(width: Int, height: Int) {
        w = width
        h = height
    }

     fun recognize(inp1: Bitmap): Boolean {
        var aInputFrame : Mat? = null
        aInputFrame = Mat()
        bitmapToMat(inp1,aInputFrame )
        Imgproc.cvtColor(aInputFrame, aInputFrame, Imgproc.COLOR_RGB2GRAY)
        descriptors2 = Mat()
        keypoints2 = MatOfKeyPoint()
        detector!!.detect(aInputFrame, keypoints2)
        descriptor!!.compute(aInputFrame, keypoints2, descriptors2)
        // Matching
        val matches = MatOfDMatch()
        if (img1!!.type() == aInputFrame.type()) {
            matcher!!.match(descriptors1, descriptors2, matches)
        } else {
            return false
        }
        val matchesList = matches.toList()
        var max_dist = 0.0
        var min_dist = 100.0
        for (i in matchesList.indices) {
            val dist = matchesList[i].distance.toDouble()
            if (dist < min_dist) min_dist = dist
            if (dist > max_dist) max_dist = dist
        }
        val good_matches = LinkedList<DMatch>()
        for (i in matchesList.indices) {
            if (matchesList[i].distance <= 1.5 * min_dist) good_matches.addLast(matchesList[i])
        }
        val goodMatches = MatOfDMatch()
        goodMatches.fromList(good_matches)
        val outputImg = Mat()
        val drawnMatches = MatOfByte()
        if (aInputFrame.empty() || aInputFrame.cols() < 1 || aInputFrame.rows() < 1) {
            return false
        }
        //Features2d.drawMatches(img1, keypoints1, aInputFrame, keypoints2, goodMatches, outputImg, GREEN, RED, drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS)
        //Imgproc.resize(outputImg, outputImg, aInputFrame.size())
        return true
    }

//     fun onCameraFrame(inputFrame: CvCameraViewFrame): Boolean {
//        return recognize(inputFrame.rgba())
//    }
//companion object {
//    initializeOpenCVDependencies()
//}

}