package com.qualcomm.ftcrobotcontroller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import ftc.team6460.javadeck.ftc.vision.OpenCvLegacyActivity;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import resq.MatColorSpreadCallback;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class OpenCvHelperTestActivity extends Activity {
    protected double getCanny() {
        return Double.parseDouble(((EditText) findViewById(R.id.valCanny)).getText().toString());
    }

    protected double getCentre() {
        return Double.parseDouble(((EditText) findViewById(R.id.valCanny)).getText().toString());
    }

    protected int getDiv() {
        return Integer.parseInt(((EditText) findViewById(R.id.divFactor)).getText().toString());
    }
    String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv_helper_test);

        findViewById(R.id.filePickBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFileAndStartViewer();
            }
        });

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                Log.e("A", "CLICKED");
                OpenCvLegacyActivity.addCallback(new MatColorSpreadCallback(OpenCvHelperTestActivity.this,null));


                ftc.team6460.javadeck.ftc.vision.OpenCvLegacyActivity.startActivity(OpenCvHelperTestActivity.this);
            }
        });

    }

    private static final int BROWSE_IMAGE_REQUEST_CODE = -74414833;

    private void pickFileAndStartViewer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, BROWSE_IMAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode &&
                BROWSE_IMAGE_REQUEST_CODE == requestCode) {
            Uri uri = data.getData();
            Intent intent = new Intent(this, ViewProcessedImageActivity.class);
            intent.putExtra("file_uri", uri);
            intent.putExtra("div", getDiv());
            intent.putExtra("canny", getCanny());
            intent.putExtra("centre", getCentre());

            this.startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
