package org.ftc.opmodes;

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
public class QuickTestTeleop extends OpMode{
    DcMotor l0, l1, l2;
    DcMotor r0, r1, r2;
    DcMotor w;
    double scaledPower;
    @Override
    public void init() {
        l0 = hardwareMap.dcMotor.get("l0");
        r0 = hardwareMap.dcMotor.get("r0");

        l1 = hardwareMap.dcMotor.get("l1");
        r1 = hardwareMap.dcMotor.get("r1");

        l2 = hardwareMap.dcMotor.get("l2");
        r2 = hardwareMap.dcMotor.get("r2");

        w = hardwareMap.dcMotor.get("w");

        r0.setDirection(DcMotor.Direction.REVERSE);
        r1.setDirection(DcMotor.Direction.REVERSE);
        r2.setDirection(DcMotor.Direction.REVERSE);

        l0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        l2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r0.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r1.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        r2.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        w.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
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
