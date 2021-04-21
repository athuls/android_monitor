package com.example.tfbenchmark;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int IMAGE_SIZE = 224;
    private static final int BATCH_SIZE = 1;
    private static final int FRAME_RATE = 5;

    private int[][] images;
    private TextView currentEpochView;
    private int currentEpoch = 0;
    private int currentImage = 0;
    private Inferer inferer;
    int[][] batch = new int[BATCH_SIZE][];

    private final Runnable updateDisplay = new Runnable() {
        @Override
        public void run() {
            currentEpochView.setText("Current Epoch: " + currentEpoch);
        }
    };

    private final TimerTask createInferer = new TimerTask() {
        @Override
        public void run() {
            inferer = new Inferer(getApplicationContext(), getString(R.string.model_file), IMAGE_SIZE, BATCH_SIZE);
        }
    };

    private final TimerTask runNextInference = new TimerTask() {
        @Override
        public void run() {
            System.arraycopy(images, currentImage, batch, 0, BATCH_SIZE);
            inferer.infer(batch);
            currentImage += BATCH_SIZE;
            if (currentImage >= images.length) {
                currentImage = 0;
                currentEpoch++;
                runOnUiThread(updateDisplay);
                /*
                if (currentEpoch == 100) {
                    cancel(); // cancel the TimerTask
                }
                */
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentEpochView = findViewById(R.id.currentEpoch);

        // load images
        File dir = new File("/sdcard/TfBenchmark/images");
        File[] imageNames = dir.listFiles();
        int numImages = imageNames.length;
        images = new int[numImages][];
        for (int i = 0; i < numImages; i++) {
            Bitmap b = BitmapFactory.decodeFile(imageNames[i].getPath());
            b = ThumbnailUtils.extractThumbnail(b, IMAGE_SIZE, IMAGE_SIZE, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

            images[i] = new int[IMAGE_SIZE * IMAGE_SIZE];
            b.getPixels(images[i], 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
        }

        // run inference
        Timer timer = new Timer("ScheduledInference");
        timer.schedule(createInferer, 0);
        timer.scheduleAtFixedRate(runNextInference, 1000, 1000 / FRAME_RATE);
        updateDisplay.run();
    }
}
