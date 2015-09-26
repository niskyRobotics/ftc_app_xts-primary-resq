package ftc.team6460.resq;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import ftc.team6460.javadeck.ftc.Utils;
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
