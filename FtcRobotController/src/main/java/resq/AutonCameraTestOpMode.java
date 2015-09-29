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
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.bytedeco.javacpp.indexer.ByteIndexer;
import org.bytedeco.javacpp.indexer.UByteBufferIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

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
        ocvh.addCallback(new OpenCvActivityHelper.MatCallback() { // rdepend callback
            @Override
            public void handleMat(opencv_core.Mat mat) { //called on every frame
                UByteBufferIndexer bi = mat.createIndexer(); // JNI call to get access to the image pixels
                int row = mat.rows() / 2; // find middle row
                int cols = mat.cols();
                int r = 0, g = 0, b = 0;
                for (int i = 0; i < cols / 2; i += 8) { // for each pixel in left: Find if more red, green, or blue
                    int rV = bi.get(row, i, 0);
                    int gV = bi.get(row, i, 0);
                    int bV = bi.get(row, i, 0);
                    Log.d("col", String.format("%d %d %d", rV, gV, bV));
                    if (rV >= gV && rV >= bV) r++;
                    else if (gV >= bV) g++;
                    else b++;
                }
                int r2 = 0, g2 = 0, b2 = 0;
                for (int i = cols / 2; i < cols; i += 8) { // same thing for right side
                    int rV = bi.get(row, i, 0);
                    int gV = bi.get(row, i, 0);
                    int bV = bi.get(row, i, 0);
                    if (rV >= gV && rV >= bV) r2++;
                    else if (gV >= bV) g2++;
                    else b2++;
                }

                String lS;
                String rS;
                // now "vote" on each side. Simple comparisons
                if (r >= g && r >= b) lS = "R";
                else if (g >= b) lS = "G";
                else lS = "B";
                Log.i("col", String.format("%d %d %d / %d %d %d", r, g, b, r2, g2, b2));
                if (r2 >= g2 && r2 >= b2) rS = "R";
                else if (g2 >= b2) rS = "G";
                else rS = "B";
                state = lS + rS;
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
        ((Activity) this.hardwareMap.appContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(state);
            }
        });
    }
}
