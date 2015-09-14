package ftc.team6460.javadeck.ftc.vision;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.IOException;

/**
 * Created by hexafraction on 9/13/15.
 */
public class OpenCvImageCaptureHelper {

    static Camera.PreviewCallback processFrame; // ugly singleton
    PreviewActivity pa;
    Context cx;
    static View adView;
    public OpenCvImageCaptureHelper(Context cx, View additionalView, Camera.PreviewCallback callback) {
        this.cx = cx;
        processFrame = callback;
        adView = additionalView;
    }

    public void start() {
        cx.startActivity(new Intent(cx, PreviewActivity.class));
    }

    public void stop() {
        pa.preview.stop();
    }

    Camera cam;

    public static class PreviewActivity extends Activity {
        View addView;
        Preview preview;


        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.addView = adView;
            this.preview = new Preview(this);
            // creating LinearLayout
            LinearLayout linLayout = new LinearLayout(this);
            // specifying vertical orientation
            linLayout.setOrientation(LinearLayout.VERTICAL);
            // creating LayoutParams
            ViewGroup.LayoutParams linLayoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (addView != null)
                linLayout.addView(addView);
            //linLayout.addView(preview);
            // set LinearLayout as a root element of the screen
            //setContentView(linLayout, linLayoutParam);
            setContentView(preview, linLayoutParam);
            preview.start(this );
        }


    }

    public static class Preview extends ViewGroup implements SurfaceHolder.Callback {

        SurfaceView mSurfaceView;
        SurfaceHolder mHolder;
        Camera cam;

        protected void start(Context cx) {
            try {
                this.cam = Camera.open();
                mSurfaceView = new SurfaceView(cx);
                addView(mSurfaceView);

                // Install a SurfaceHolder.Callback so we get notified when the
                // underlying surface is created and destroyed.
                mHolder = mSurfaceView.getHolder();
                mHolder.addCallback(this);
                cam.setPreviewDisplay(mHolder);
                cam.setPreviewCallback(processFrame);
                cam.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Preview(Context context) {
            super(context);


        }

        @Override
        protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
            //pass
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //pass
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            //pass
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (cam != null) {
                // Call stopPreview() to stop updating the preview surface.
                cam.stopPreview();
            }

        }

        public void stop() {
            if (cam != null) {
                // Call stopPreview() to stop updating the preview surface.
                cam.stopPreview();
            }

        }
    }
}
