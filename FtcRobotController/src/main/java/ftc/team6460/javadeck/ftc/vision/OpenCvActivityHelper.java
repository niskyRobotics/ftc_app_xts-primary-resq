package ftc.team6460.javadeck.ftc.vision;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.R;
import org.bytedeco.javacpp.*;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by hexafraction on 9/14/15.
 */
public class OpenCvActivityHelper {
    private FrameLayout layout;
    protected FaceView faceView;
    private Preview mPreview;
    private FtcRobotControllerActivity cx;
    CopyOnWriteArraySet<MatCallback> callbacks = new CopyOnWriteArraySet<>();

static volatile boolean running;

    public synchronized void addCallback(MatCallback cb){
        callbacks.add(cb);
    }

    public synchronized void removeCallback(MatCallback cb){
        callbacks.remove(cb);
    }

//    static {
//        OpenCVLoader.initDebug();
//        //Loader.load();
//    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }






    protected void onPause() {
        running=false;

    }

    public OpenCvActivityHelper(FtcRobotControllerActivity cx) {
        this.cx = cx;
    }

    public synchronized void attach() {

//        // Hide the window title.

        // Create our Preview view and set it as the content of our activity.
        try {

            layout = new FrameLayout(cx);
            faceView = new FaceView(cx);
            mPreview = new Preview(cx, faceView);

            layout.addView(mPreview);

            layout.addView(faceView);
            ((FrameLayout)cx.findViewById(R.id.previewLayout)).addView(layout);
        } catch (IOException e) {
            e.printStackTrace();
            new AlertDialog.Builder(cx).setMessage(e.getMessage()).create().show();
        }
    }


    public static interface MatCallback {

        public void handleMat(Mat mat);

        public void draw(Canvas canvas);
    }
    // ----------------------------------------------------------------------

    class FaceView extends View implements Camera.PreviewCallback {
        private opencv_core.Mat yuvImage = new opencv_core.Mat();
        private Mat rgbImage = new Mat();
        private opencv_core.CvMemStorage storage;
        private volatile boolean needAnotherFrame = true;
        Camera.Size size;
        public class RunProcess implements Runnable {

            @Override
            public void run() {
                while (run) {
                    if (arrPending != null) {
                        if (arrData == null || arrData.length != arrPending.length) arrData = new byte[arrPending.length];
                        System.arraycopy(arrPending, 0, arrData, 0, arrPending.length);
                        if(size!=null) processImage(arrData, size.width, size.height);
                        needAnotherFrame = true;
                    }
                }
            }
        }

        public FaceView(Activity context) throws IOException {
            super(context);


            storage = opencv_core.CvMemStorage.create();
        }

        public void onPreviewFrame(final byte[] data, final Camera camera) {


            try

            {
                if (needAnotherFrame) {
                    needAnotherFrame = false;
                    if (arrPending == null || arrPending.length != data.length) arrPending = new byte[data.length];
                    size = camera.getParameters().getPreviewSize();
                    System.arraycopy(data, 0, arrPending, 0, data.length);
                }
                camera.addCallbackBuffer(data);
            } catch (RuntimeException e)

            {
                // The camera has probably just been released, ignore.
                Log.w("KP", e.getClass().getName() + ":" + e.getMessage());
                for (StackTraceElement el : e.getStackTrace()) {
                    Log.w("KP:ST", el.toString());
                }
            }

        }

        private byte[] arrData, arrPending;
        volatile boolean run = true;
        Thread imgProcessor;

        protected void processImage(byte[] data, int width, int height) {
            Log.i("KPP", ("data = [" + data + "], width = [" + width + "], height = [" + height + "]"));
            Log.i("KP", "Entering process");
            // First, downsample our image and convert it into a grayscale IplImage
            int bytesPerPixel = data.length / (width * height);
            //1620 for YUV NV21
            if (yuvImage == null || yuvImage.arrayWidth() != width || yuvImage.arrayHeight() != height + (height/2)) {
                Log.i("PREPROC", "Remaking yuv");
                yuvImage.create(height + (height/2), width, CV_8UC3);
            }
            if (rgbImage == null || rgbImage.arrayWidth() != width || rgbImage.arrayHeight() != height) {
                Log.i("PREPROC", "Remaking rgbImage: Currently " + rgbImage.arrayWidth() + "*" + rgbImage.arrayHeight());
                rgbImage.create(height, width, CV_8UC3);
            }


            Log.i("OPENCV", "Processing " + Thread.currentThread().getName());

            ByteBuffer imageBuffer = yuvImage.createBuffer();
            for (int i = 0; i < data.length; i++) {
                imageBuffer.put(i, data[i]);

            }
            opencv_imgproc.cvtColor(yuvImage, rgbImage, opencv_imgproc.COLOR_YUV2RGB_NV21);

            for(OpenCvActivityHelper.MatCallback cb : OpenCvActivityHelper.this.callbacks){
                cb.handleMat(rgbImage);
            }

            cvClearMemStorage(storage);
            postInvalidate();
        }

        AtomicReference<opencv_features2d.KeyPoint> kpRef = new AtomicReference<>(null);
        AtomicReference<Point2f> lineRef = new AtomicReference<>(null);
        public String status = "";

        @Override
        protected void onDraw(Canvas canvas) {
            for(OpenCvActivityHelper.MatCallback cb : OpenCvActivityHelper.this.callbacks){
                cb.draw(canvas);
            }
            super.onDraw(canvas);
        }
    }

// ----------------------------------------------------------------------

    class Preview extends SurfaceView implements SurfaceHolder.Callback {
        SurfaceHolder mHolder;
        Camera mCamera;
        Camera.PreviewCallback previewCallback;

        Preview(Context context, final Camera.PreviewCallback previewCallback) {
            super(context);
            this.previewCallback = previewCallback;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                            if (previewCallback instanceof FaceView) {
                                ((FaceView) previewCallback).status = ("Autofocus " + (b ? "succeeded" : "failed"));
                            }
                        }
                    });
                }
            });
        }

        int mCID = 0;


        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d("DBG", "Camera found");
                    mCID = i;
                    break;
                }
            }

            mCamera = Camera.open(mCID);
            OpenCvActivityHelper.setCameraDisplayOrientation((Activity) this.getContext(), mCID, mCamera);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
            }
            OpenCvActivityHelper.this.faceView.run = true;
            OpenCvActivityHelper.this.faceView.imgProcessor = new Thread(OpenCvActivityHelper.this.faceView.new RunProcess(), "openCvHelperThread");
            OpenCvActivityHelper.this.faceView.imgProcessor.start();

        }

        int rt = 0;

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            mCamera.stopPreview();

            mCamera.release();

            mCamera = null;
            OpenCvActivityHelper.this.faceView.run = false;
        }


        private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
            final double ASPECT_TOLERANCE = 0.05;
            double targetRatio = (double) w / h;
            if (sizes == null) return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            // Try to find an size match aspect ratio and size
            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            // Cannot find the one match the aspect ratio, ignore the requirement
            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }
            return optimalSize;
        }


        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, w, h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setRotation(rt);
            Log.w("RT", "setting rt: " + rt);
            mCamera.setParameters(parameters);
            OpenCvActivityHelper.setCameraDisplayOrientation((Activity) this.getContext(), mCID, mCamera);
            if (previewCallback != null) {
                mCamera.setPreviewCallbackWithBuffer(previewCallback);
                Camera.Size size = parameters.getPreviewSize();
                byte[] data = new byte[size.width * size.height *
                        ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8];
                mCamera.addCallbackBuffer(data);
            }
            mCamera.startPreview();
        }

    }
}

