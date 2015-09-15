package com.qualcomm.ftcrobotcontroller;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivity;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_32FC3;
import static org.bytedeco.javacpp.opencv_core.CV_8UC1;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class OpenCvHelperTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv_helper_test);
        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                Log.e("A", "CLICKED");
                OpenCvActivity.addCallback(new OpenCvActivity.MatCallback() {

                    opencv_core.Mat circles = new opencv_core.Mat(100, 1, CV_32FC3);
                    opencv_core.Mat gray = new opencv_core.Mat();
                    @Override
                    public synchronized void handleMat(opencv_core.Mat mat) {
                        Log.e("MAT", "PROCESSING");
                        if (gray == null || gray.arrayWidth() != mat.arrayWidth() || gray.arrayHeight() != mat.arrayHeight()) {
                            Log.i("PREPROC", "Remaking yuv");
                            gray.create(mat.arrayHeight(), mat.arrayWidth(), CV_8UC1);
                        }
                        cvtColor(mat, gray, COLOR_RGB2GRAY);
                        HoughCircles(gray, circles, CV_HOUGH_GRADIENT, 1, gray.rows()/8, 200, 100, 0, 0 );
                    }

                    @Override
                    public synchronized void draw(Canvas canvas) {
                        if(circles==null) return;
                        Paint p = new Paint();
                        p.setColor(Color.GREEN);
                        FloatIndexer cIdx = circles.createIndexer();
                        for(int i = 0; i < circles.rows(); i++){
                            float x = cIdx.get(i,0,0);
                            float y = cIdx.get(i,0,1);
                            float r = cIdx.get(i,0,2);
                            canvas.drawCircle(x,y,r, p);
                        }
                    }
                });
                OpenCvActivity.startActivity(OpenCvHelperTestActivity.this);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_cv_helper_test, menu);
        return true;
    }

    public void btnOnClick(View view) {
        Log.e("F", "boo!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
