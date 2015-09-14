package com.qualcomm.ftcrobotcontroller;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ftc.team6460.javadeck.ftc.vision.OpenCvImageCaptureHelper;

public class OpenCvHelperTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(org.ftc.opmodes.R.layout.activity_open_cv_helper_test);

    }

    @Override
    protected void onStart() {
        Log.e("A", "CLICKED");
        new OpenCvImageCaptureHelper(OpenCvHelperTestActivity.this, null, new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.i("F", "frame!");
            }
        }).start();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(org.ftc.opmodes.R.menu.menu_open_cv_helper_test, menu);
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
        if (id == org.ftc.opmodes.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
