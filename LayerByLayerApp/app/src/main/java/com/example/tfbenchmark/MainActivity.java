package com.example.tfbenchmark;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ThumbnailUtils;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int IMAGE_SIZE = 224;
    private static final int BATCH_SIZE = 1;
    private static final int FRAME_RATE = 15;
    private static final long SECONDS_TO_RUN = 22 * 60;

    private TextView currentEpochView;
    private EditText modelFileView;

    private int[][] images;
    private int currentEpoch = 0;
    private int currentImage = 0;
    private Inferer inferer;
    private int[][] batch = new int[BATCH_SIZE][];

    private long startTime = -1;
    private long totalInferenceTime = 0;

    private final Runnable updateDisplay = new Runnable() {
        @Override
        public void run() {
            int numImagesSoFar = currentEpoch * images.length + currentImage;
            long avgTime = totalInferenceTime / numImagesSoFar;
            String formatString = "Current Epoch: %d\nAverage Time per Image: %d ns\n" +
                    "Total time elapsed: %d ns\nTotal images processed: %d";
            currentEpochView.setText(String.format(formatString, currentEpoch, avgTime,
                    System.nanoTime() - startTime, numImagesSoFar));
        }
    };

    private final TimerTask createInferer = new TimerTask() {
        @Override
        public void run() {
            inferer = new Inferer(modelFileView.getText().toString(), IMAGE_SIZE, BATCH_SIZE);
        }
    };

    private final TimerTask runNextInference = new TimerTask() {
        @Override
        public void run() {
            if (startTime == -1) {
                startTime = System.nanoTime();
            }

            System.arraycopy(images, currentImage, batch, 0, BATCH_SIZE);
            totalInferenceTime += inferer.infer(batch);
            currentImage += BATCH_SIZE;
            if (currentImage >= images.length) {
                currentImage = 0;
                currentEpoch++;
                runOnUiThread(updateDisplay);
            }

            if (System.nanoTime() - startTime > SECONDS_TO_RUN * 1_000_000_000) {
                cancel(); // cancel the TimerTask
                runOnUiThread(updateDisplay);
                new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME).startTone(ToneGenerator.TONE_DTMF_0, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentEpochView = findViewById(R.id.currentEpoch);
        modelFileView = findViewById(R.id.modelFileInput);
        Button runButton = findViewById(R.id.runButton);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // run inference
                Timer timer = new Timer("ScheduledInference");
                timer.schedule(createInferer, 0);
                timer.scheduleAtFixedRate(runNextInference, 1000, 1000 / FRAME_RATE);
                currentEpochView.setText("Started");
            }
        });

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

        currentEpochView.setText("Images loaded");
    }
}
