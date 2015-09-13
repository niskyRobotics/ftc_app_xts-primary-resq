package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by David on 8/31/2015.
 */
public class DriveForwardStop extends OpMode {
    private DcMotor left;
    private DcMotor right;
    private long startTime;
    private int loopCount;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("left_motor");
        right = hardwareMap.dcMotor.get("right_motor");

        loopCount = 0;
    }

    @Override
    public void loop() {
        if (loopCount == 0) {
            startTime = System.nanoTime();
        }

        if ((startTime + (1000000000 * (long) 5)) < System.nanoTime()) {
            left.setPower(1);
            right.setPower(1);
        } else {
            left.setPower(0);
            left.setPower(0);
        }

    }
}
