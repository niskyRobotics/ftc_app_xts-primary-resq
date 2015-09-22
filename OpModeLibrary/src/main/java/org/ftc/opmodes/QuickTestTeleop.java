package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by akh06977 on 9/18/2015.
 */
@TeleOp
public class QuickTestTeleop extends OpMode{
    DcMotor l0, l1, l2;
    DcMotor r0, r1, r2;
    DcMotor w;
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
    }

    @Override
    public void loop() {
        l0.setPower(this.gamepad1.left_stick_y);
        r0.setPower(this.gamepad1.right_stick_y);

        l1.setPower(this.gamepad1.left_stick_y);
        r1.setPower(this.gamepad1.right_stick_y);

        l2.setPower(this.gamepad1.left_stick_y);
        r2.setPower(this.gamepad1.right_stick_y);

        if(this.gamepad1.left_bumper) {
            w.setPower(0.9);
            telemetry.addData("w", "1");
        }
        else if(this.gamepad1.right_bumper) {
            w.setPower(-0.9);
            telemetry.addData("w", "-1");
        }
        else {
            w.setPower(0);
            telemetry.addData("w", "0");
        }
    }
}
