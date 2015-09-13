package ftc.team6460.javadeck.ftc.peripheral;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import ftc.team6460.javadeck.api.Maintainer;
import ftc.team6460.javadeck.api.SlewedDouble;
import ftc.team6460.javadeck.api.motion.SelfContainedServo;
import ftc.team6460.javadeck.api.peripheral.PeripheralCommunicationException;
import ftc.team6460.javadeck.api.peripheral.PeripheralInoperableException;
import ftc.team6460.javadeck.api.safety.SafetyGroup;


public class Ftc3WireServo extends SelfContainedServo {
    public Ftc3WireServo(Servo delegate, Maintainer opMode) {
        this.delegate = delegate;
        this.opMode = opMode;
        opMode.accept(this);
    }

    private final Servo delegate;
    private volatile double position;
    private volatile double cPos;
    private volatile double slewRate;
    private final Maintainer opMode;
    @Override
    public synchronized void writeFast(SlewedDouble input) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        Range.throwIfRangeIsInvalid(input.getValue(), 0.0D, 1.0D);
        position = input.getValue();
        slewRate = input.getSlewRate()*0.030; // Called every 30msec

    }

    @Override
    public void safetyShutdown(long nanos) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {

    }

    @Override
    public void addSafetyGroup(SafetyGroup grp) {

    }

    @Override
    public synchronized void loop() {
        double dist = Range.clip(position-cPos, -slewRate, slewRate);
        cPos = dist+cPos;
        delegate.setPosition(cPos);
    }

    @Override
    public void setup() {

    }
}
