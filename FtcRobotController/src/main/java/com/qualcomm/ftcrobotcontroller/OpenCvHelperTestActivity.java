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
import org.bytedeco.javacpp.opencv_imgproc;

import static org.bytedeco.javacpp.opencv_core.*;
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
                    opencv_core.CvMemStorage str = opencv_core.cvCreateMemStorage();
                    opencv_core.Mat gray = new opencv_core.Mat();
                    opencv_core.Mat grayHalf = new opencv_core.Mat();
                    opencv_core.CvSeq circles;
                    @Override
                    public synchronized void handleMat(opencv_core.Mat mat) {
                        Log.e("MAT", "PROCESSING");
                        if (gray == null || gray.arrayWidth() != mat.arrayWidth() || gray.arrayHeight() != mat.arrayHeight()) {
                            Log.i("PREPROC", "Remaking gray");
                            gray.create(mat.arrayHeight(), mat.arrayWidth(), CV_8UC1);
                        }
                        if (grayHalf == null || grayHalf.arrayWidth() != mat.arrayWidth()/2 || grayHalf.arrayHeight() != mat.arrayHeight()/2) {
                            Log.i("PREPROC", "Remaking grayhalf");
                            grayHalf.create(mat.arrayHeight()/2, mat.arrayWidth()/2, CV_8UC1);
                        }
                        cvtColor(mat, gray, COLOR_RGB2GRAY);
                        cvResize(gray.asIplImage(), grayHalf.asIplImage());
                        cvSmooth(grayHalf.asIplImage(),grayHalf.asIplImage());

                        circles = cvHoughCircles(grayHalf.asIplImage(), str, opencv_imgproc.CV_HOUGH_GRADIENT,1,grayHalf.rows()/8, 200, 100, 20,400);
                    }

                    @Override
                    public synchronized void draw(Canvas canvas) {
                        if (circles == null) return;
                        Paint p = new Paint();
                        p.setStyle(Paint.Style.STROKE);
                        p.setColor(Color.GREEN);
                        Log.i("CIRCLE", new CvPoint3D32f(cvGetSeqElem(circles, 0)).toString());

                        for (int i = 0; (i < circles.total())&&(i<20); i++) {
                            opencv_core.CvPoint3D32f pt = new CvPoint3D32f(cvGetSeqElem(circles, i));
                            canvas.drawCircle(pt.x()*2, pt.y()*2, pt.z()*2, p);
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
