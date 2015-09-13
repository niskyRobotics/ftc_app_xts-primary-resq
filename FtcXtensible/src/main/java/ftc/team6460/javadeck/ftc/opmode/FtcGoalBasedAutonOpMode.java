package ftc.team6460.javadeck.ftc.opmode;

import ftc.team6460.javadeck.api.motion.impl.ParallelDrivetrain;
import ftc.team6460.javadeck.api.motion.impl.PositionBasedDrivetrain;
import ftc.team6460.javadeck.api.planner.ImmutableRobotPosition;

/**
 * Currently assumes names DRIVE_L and DRIVE_R, with 1 unit/sec/power, 1 unit width
 */
public class FtcGoalBasedAutonOpMode extends FtcBaseOpMode {

    private final ImmutableRobotPosition currentPosition;
    private final double speedFactor;
    private final double robotWidth;
    private final double accel;
    private final double maxSpeed;

    public FtcGoalBasedAutonOpMode(ImmutableRobotPosition currentPosition, double speedFactor, double robotWidth, double accel, double maxSpeed) {
        this.currentPosition = currentPosition;
        this.speedFactor = speedFactor;
        this.robotWidth = robotWidth;
        this.accel = accel;
        this.maxSpeed = maxSpeed;
    }

    @Override
    protected void doActions() {
        PositionBasedDrivetrain drv = new PositionBasedDrivetrain(
                currentPosition,
                new ParallelDrivetrain(super.peripheralFactory.getEncoderedMotor("DRIVE_L"),
                        super.peripheralFactory.getEncoderedMotor("DRIVE_L"), speedFactor)
                , robotWidth, accel, maxSpeed);
    }
}
