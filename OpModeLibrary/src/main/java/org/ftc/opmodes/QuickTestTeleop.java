package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/**
 * Created by akh06977 on 9/18/2015.
 */
@TeleOp
public class QuickTestTeleop extends OpMode{
    DcMotor l;
    DcMotor r;
    @Override
    public void init() {

        l = hardwareMap.dcMotor.get("lm");
        r = hardwareMap.dcMotor.get("rm");
        r.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        l.setPower(this.gamepad1.left_stick_y);
        r.setPower(this.gamepad1.right_stick_y);
    }
}
