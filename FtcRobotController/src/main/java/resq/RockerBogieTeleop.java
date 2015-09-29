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
import ftc.team6460.javadeck.ftc.Utils;
import ftc.team6460.javadeck.ftc.vision.MatCallback;
import ftc.team6460.javadeck.ftc.vision.OpenCvActivityHelper;
import org.bytedeco.javacpp.opencv_core;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by akh06977 on 9/18/2015.
 */
@TeleOp
public class RockerBogieTeleop extends RockerBogieCommon {


    double scaledPower;

    @Override
    public void init() {
        super.init();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.hardwareMap.appContext);
        scaledPower = Utils.getSafeDoublePref("lowspeed_power_scale", sharedPref, 0.50);
        this.gamepad1.setJoystickDeadzone(0.1f);
        final OpenCvActivityHelper ocvh = new OpenCvActivityHelper((FtcRobotControllerActivity)hardwareMap.appContext);
        ocvh.addCallback(new MatCallback() {
            @Override
            public void handleMat(opencv_core.Mat mat) {
                Log.i("FRAME", "Processed a frame");
                try {
                    Thread.sleep(100); // imitate long process
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void draw(Canvas canvas) {
                Paint p = new Paint();
                p.setColor(Color.GREEN);
                canvas.drawCircle(10, 10, 4, p);
            }
        });
        final TextView tv = new TextView(hardwareMap.appContext);
        ((Activity) this.hardwareMap.appContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ocvh.attach();
                ((FrameLayout)((Activity)RockerBogieTeleop.this.hardwareMap.appContext).findViewById(R.id.previewLayout)).addView(tv);
            }
        });
    }

    @Override
    public void loop() {
        double scaleActual = (this.gamepad1.right_trigger>0.2)?scaledPower:1.00;

        l0.setPower(this.gamepad1.left_stick_y * scaleActual);
        r0.setPower(this.gamepad1.right_stick_y * scaleActual);

        l1.setPower(this.gamepad1.left_stick_y * scaleActual);
        r1.setPower(this.gamepad1.right_stick_y * scaleActual);

        l2.setPower(this.gamepad1.left_stick_y * scaleActual);
        r2.setPower(this.gamepad1.right_stick_y * scaleActual);

        if(this.gamepad1.left_bumper) {
            w.setPower(1.0);
            telemetry.addData("w", "1");
        }
        else if(this.gamepad1.right_bumper) {
            w.setPower(-1.0);
            telemetry.addData("w", "-1");
        }
        else {
            w.setPower(0);
            telemetry.addData("w", "0");
        }
    }
}
