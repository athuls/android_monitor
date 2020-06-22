package com.example.imageloader

import android.content.ContentResolver.SCHEME_FILE
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import coil.ImageLoader
import coil.api.load
import coil.bitmappool.BitmapPool
import coil.decode.Options
import coil.extension.videoFrameMillis
import coil.fetch.VideoFrameFetcher.Companion.VIDEO_FRAME_MICROS_KEY
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.request.CachePolicy
import coil.request.LoadRequest
import coil.request.Parameters
import coil.size.OriginalSize
import coil.size.Scale
import kotlinx.coroutines.TheatreMap
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import java.io.File


class MainActivity : AppCompatActivity() {

    fun createOptions(
        config: Bitmap.Config = Bitmap.Config.ARGB_8888,
//        colorSpace: ColorSpace? = null,
        scale: Scale = Scale.FILL,
        allowInexactSize: Boolean = false,
        allowRgb565: Boolean = false,
        headers: Headers = Headers.Builder().build(),
        parameters: Parameters = Parameters.Builder().build(),
        memoryCachePolicy: CachePolicy = CachePolicy.ENABLED,
        diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        networkCachePolicy: CachePolicy = CachePolicy.ENABLED
    ): Options {
        return Options(
            config,
            null,
            scale,
            allowInexactSize,
            allowRgb565,
            headers,
            parameters,
            memoryCachePolicy,
            diskCachePolicy,
            networkCachePolicy
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
////        setSupportActionBar(toolbar)
//
////        fab.setOnClickListener { view ->
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                .setAction("Action", null).show()
////        }
//        val constraintLayout = findViewById(R.id.constraintLayout) as ConstraintLayout
//        val imageView = ImageView(this)
//        imageView.setImageResource(R.drawable.android_logo)

        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)
        val constraintLayout = findViewById(R.id.constraintLayout) as ConstraintLayout
//        val constraintLayout = findViewById(R.id.list_container) as ConstraintLayout
        val imageView = ImageView(applicationContext)
        val imageLoader = ImageLoader.Builder(imageView.context)
            .componentRegistry {
                add(VideoFrameFileFetcher(imageView.context))
                add(VideoFrameUriFetcher(imageView.context))
            }
            .build()

//        imageView.load(
//            File("/home/athuls89/Desktop/OSL/osl/Papers/Conferences/CODES+ISSS2019/coil/ImageLoader/app/src/main/res/drawable/android_logo_1_1.png"))
//        imageView.setImageResource(R.drawable.android_logo)
//        imageView.load(R.drawable.android_logo)
        val dir: File = Environment.getExternalStorageDirectory()
        val path = dir.absolutePath
        val videoPath = path+"/WhatsApp/Media/WhatsApp Video/VID-20200621-WA0005.mp4"
        println("path 1 is "+videoPath)
//        imageView.load(File(path+"/WhatsApp/Media/WhatsApp Video/VID-20200621-WA0005.mp4")) {
//            videoFrameMillis(1000)
//        }

        try {
            val request = LoadRequest.Builder(imageView.context)
                .data(videoPath.toUri())
                .target(imageView)
                .build()
            val disposable = imageLoader.execute(request)
        } catch (e:Exception) {
            println("Exception is: " + e)
        }


        var fetcher = VideoFrameUriFetcher(applicationContext)
        var pool = BitmapPool(0)
        val ASSET_FILE_PATH_ROOT = "android_asset"
        var filePath = "$SCHEME_FILE:///$ASSET_FILE_PATH_ROOT/video.mp4".toUri()
        println("File path is : " + filePath)

//        var customVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.testvideocoil
//        val result = runBlocking {
//            fetcher.fetch(
//                pool = pool,
////                data = filePath,
//                data = customVideoPath.toUri(),
////                data = videoPath.toUri(),
//                size = OriginalSize,
//                options = createOptions(
//                    parameters = Parameters.Builder()
//                        .set(VIDEO_FRAME_MICROS_KEY, 32600000L)
//                        .build()
//                )
//            )
//        }


        constraintLayout.addView(imageView)
        println("[getSegment Output]" + TheatreMap.getSegment())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
