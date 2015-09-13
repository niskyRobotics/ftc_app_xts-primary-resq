package org.ftc.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;


public class BasicGyro extends OpMode {
    private static final double MOTOR_POWER = .5;
    private DcMotor motorLeft;
    private DcMotor motorRight;
    private GyroSensor gyro;

    // Keep track of the readings
    // private LinkedList<Double> gyroReadings;
    private long start;

    @Override
    public void init() {
        motorRight = hardwareMap.dcMotor.get("motor_2");
        motorLeft = hardwareMap.dcMotor.get("motor_1");
        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        gyro = hardwareMap.gyroSensor.get("gyro");
        start = System.currentTimeMillis();

        //Range.clip();
        // gyroReadings = new LinkedList<>();
    }

    @Override
    public void loop() {
        final int degreesToTurn = 90;
        //gyroReadings.add(gyro.getRotation());

        // Build the average of the readings
        double avg = gyro.getRotation();
        /*for (double gyroReading : gyroReadings) {
            avg += gyroReading;
        }
        avg /= gyroReadings.size();*/

        // If we suspect the degrees we have turned (based on the degrees per second) is
        // greater than 90, stop.
        double left;
        double right;
        double degreesWeHaveTurned = (avg / 1000) * (System.currentTimeMillis() - start);
        if (degreesWeHaveTurned >= degreesToTurn) {
            left = -MOTOR_POWER;
            right = MOTOR_POWER;
        } else {
            left = 0.0;
            right = 0.0;
        }

        motorLeft.setPower(left);
        motorRight.setPower(right);

        Servo foo = new Servo(hardwareMap.servoController.get(""), 0);

        telemetry.addData("GYRO", "deg/second: " + avg);
        telemetry.addData("TURN_AMOUNT", "We have possibly turned " + degreesWeHaveTurned);
    }
}
