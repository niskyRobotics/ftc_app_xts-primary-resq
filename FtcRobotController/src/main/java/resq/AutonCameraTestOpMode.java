package resq;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import ftc.team6460.javadeck.ftc.Utils;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;

import java.util.Arrays;

/**
 * Created by akh06977 on 9/18/2015.
 */
@Autonomous
public class AutonCameraTestOpMode extends OpMode {


    double scaledPower;
    volatile String state;
    private TextView tv;

    @Override
    public void init() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);
        final OpenCvActivityHelper ocvh = new OpenCvActivityHelper((FtcRobotControllerActivity) hardwareMap.appContext);
        ocvh.addCallback(new MatCallback() { // rdepend callback
            @Override
            public void handleMat(opencv_core.Mat mat) { //called on every frame
                UByteBufferIndexer bi = mat.createIndexer(); // JNI call to get access to the image pixels
                int row = mat.rows() / 2; // find middle row
                int cols = mat.cols();
                int rT = 0, gT = 0, bT = 0;

                int mTotal = 0;
                float[] hsv = new float[3];
                for (int i = 0; i < cols / 2; i += 8) { // for each pixel in left: Find if more red, green, or blue
                    int mul = Math.min(i, cols / 2 - i);
                    rT += bi.get(row, i, 0)*mul;
                    gT += bi.get(row, i, 1)*mul;
                    bT += bi.get(row, i, 2)*mul;
                    mTotal += mul;

                }

                String lS;
                Color.RGBToHSV(rT / mTotal, gT / mTotal, bT / mTotal, hsv);
                if(hsv[0]<(60) || hsv[0]>(300)) lS = "R";
                else if(hsv[0]<(180)) lS = "G";
                else lS = "B";
                Log.v("CLRAW", String.format("%d,%d,%d",rT / mTotal, gT / mTotal, bT / mTotal));
                Log.v("CL", String.format(Arrays.toString(hsv)));


                int rT2 = 0, gT2 = 0, bT2 = 0;
                int mTotal2 = 0;
                for (int i = cols/2; i < cols; i += 8) { // for each pixel in left: Find if more red, green, or blue
                    int mul = Math.min(i - cols / 2, cols - i);
                    rT2 += bi.get(row, i, 0)*mul;
                    gT2 += bi.get(row, i, 1)*mul;
                    bT2 += bi.get(row, i, 2)*mul;
                    mTotal2 += mul;

                }

                String rS;
                float[] hsv2 = new float[3];
                Color.RGBToHSV(rT2 / mTotal2, gT2 / mTotal2, bT2 / mTotal2, hsv2);
                if(hsv2[0]<(60) || hsv2[0]>(300)) rS = "R";
                else if(hsv2[0]<(180)) rS = "G";
                else rS = "B";
                Log.v("CRRAW", String.format("%d,%d,%d",rT2 / mTotal2, gT2 / mTotal2, bT2 / mTotal2));
                Log.v("CR", String.format(Arrays.toString(hsv)));
                state = lS + rS;
                ((Activity) AutonCameraTestOpMode.this.hardwareMap.appContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(state);
                    }
                });
                Log.i("STATE", state);
            }

            @Override
            public void draw(Canvas canvas) {
                Paint p = new Paint();
                p.setColor(Color.GREEN);
                canvas.drawCircle(10, 10, 4, p);
            }
        });
        tv = new TextView(hardwareMap.appContext);
        ((Activity) this.hardwareMap.appContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ocvh.attach();
                ((FrameLayout) ((Activity) AutonCameraTestOpMode.this.hardwareMap.appContext).findViewById(R.id.previewLayout)).addView(tv);
            }
        });
    }

    @Override
    public void loop() {

    }
}
