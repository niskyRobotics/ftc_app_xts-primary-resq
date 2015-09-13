package org.ftc.opmodes.examples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.opmodes.Disabled;
import org.ftccommunity.ftcxtensible.opmodes.TeleOp;

/*
 * An example linear op mode where the pushbot
 * will drive in stopMode square pattern using sleep()
 * and stopMode for loop.
 */
@Disabled
@TeleOp(pairWithAuto = "PushBot")
public class PushBotSquare extends LinearOpMode {
    DcMotor leftMotor;
    DcMotor rightMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        leftMotor.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        rightMotor.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        waitForStart();

        for(int i=0; i<4; i++) {
            leftMotor.setPower(1.0);
            rightMotor.setPower(1.0);

            sleep(1000);

            leftMotor.setPower(0.5);
            rightMotor.setPower(-0.5);

            sleep(500);
        }

        leftMotor.setPowerFloat();
        rightMotor.setPowerFloat();

    }
}
