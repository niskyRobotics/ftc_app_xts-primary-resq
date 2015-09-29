package com.qualcomm.ftcrobotcontroller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvLegacyActivity;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;

import java.io.FileDescriptor;
import java.io.IOException;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.cvHoughCircles;


public class ViewProcessedImageActivity extends Activity {

    private FrameLayout layout;
    private Preview mPreview;
    private int div;
    private MatCallback mc;

    Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = new FrameLayout(this);
        mPreview = new Preview(this);
        Uri uri = getIntent().getParcelableExtra("file_uri");
        mc = new MatCallback() {
            opencv_core.CvMemStorage str = opencv_core.cvCreateMemStorage();
            opencv_core.Mat gray = new opencv_core.Mat();
            opencv_core.Mat grayHalf = new opencv_core.Mat();
            double canny = getIntent().getDoubleExtra("canny", 200.0);
            double ctr = getIntent().getDoubleExtra("centre", 100.0);
            int div = getIntent().getIntExtra("div", 2);
            opencv_core.CvSeq circles;

            @Override
            public synchronized void handleMat(opencv_core.Mat mat) {
                Log.e("MAT", "PROCESSING");
                if (gray == null || gray.arrayWidth() != mat.arrayWidth() || gray.arrayHeight() != mat.arrayHeight()) {
                    Log.i("PREPROC", "Remaking gray");
                    gray.create(mat.arrayHeight(), mat.arrayWidth(), CV_8UC1);
                }
                if (grayHalf == null || grayHalf.arrayWidth() != mat.arrayWidth() / div || grayHalf.arrayHeight() != mat.arrayHeight() / div) {
                    Log.i("PREPROC", "Remaking grayhalf");
                    grayHalf.create(mat.arrayHeight() / div, mat.arrayWidth() / div, CV_8UC1);
                }
                cvtColor(mat, gray, COLOR_RGB2GRAY);
                cvResize(gray.asIplImage(), grayHalf.asIplImage());
                cvSmooth(grayHalf.asIplImage(), grayHalf.asIplImage());

                circles = cvHoughCircles(grayHalf.asIplImage(), str, opencv_imgproc.CV_HOUGH_GRADIENT, 1, grayHalf.rows() / 8, canny, ctr, 20, 400);
            }

            @Override
            public synchronized void draw(Canvas canvas) {
                if (circles == null) return;
                Paint p = new Paint();
                p.setStrokeWidth(4);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.GREEN);
                Log.i("CIRCLE", new opencv_core.CvPoint3D32f(cvGetSeqElem(circles, 0)).toString());

                for (int i = 0; (i < circles.total()) && (i < 20); i++) {
                    opencv_core.CvPoint3D32f pt = new opencv_core.CvPoint3D32f(cvGetSeqElem(circles, i));
                    canvas.drawCircle(pt.x() * div, pt.y() * div, pt.z() * div, p);
                }
            }
        };
        Mat img = opencv_highgui.imread(getRealPathFromURI(this, uri));
        Mat mat = new Mat();
        mat.create(1080,1920,CV_8UC3);
        cvResize(img.asIplImage(), mat.asIplImage());
        mc.handleMat(mat);
        bmp = uriToBitmap(uri);
        setContentView(layout);
    }

    class Preview extends View {
        public Preview(Context context) {
            super(context);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(bmp, new Rect(0,0,bmp.getWidth(),bmp.getHeight()), new Rect(0,0,1920,1080), new Paint());
            mc.draw(canvas);
        }


    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private Bitmap uriToBitmap(Uri selectedFileUri) {
        Bitmap image;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





}
