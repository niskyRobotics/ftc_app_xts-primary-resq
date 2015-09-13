package ftc.team6460.javadeck.ftc.peripheral;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.robocol.Telemetry;
import ftc.team6460.javadeck.api.Maintainer;
import ftc.team6460.javadeck.api.motion.EncoderedMotor;
import ftc.team6460.javadeck.api.peripheral.PeripheralCommunicationException;
import ftc.team6460.javadeck.api.peripheral.PeripheralInoperableException;
import ftc.team6460.javadeck.api.safety.SafetyGroup;

/**
 * Created by hexafraction on 6/6/15.
 */
public class FtcEncoderedMotor implements EncoderedMotor {

    private static final double ENCODER_TICKS_PER_REVOLUTION = 1440.0;
    /*package-private*/ DcMotor inner;
    final Telemetry telemetry;
    private double valToWrite;
    private long shutdownUntil = 0;
    /*package-private*/ Maintainer opMode;

    public FtcEncoderedMotor(DcMotor inner, Telemetry telemetry, Maintainer opMode) {
        this.inner = inner;
        this.telemetry = telemetry;
        this.opMode = opMode;
        opMode.accept(this);
    }

    public FtcEncoderedMotor(DcMotor inner, Maintainer opMode) {
        this.inner = inner;
        this.opMode = opMode;
        this.telemetry = null;
        opMode.accept(this);
    }

    @Override
    public void write(Double input) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        writeFast(input);
    }

    @Override
    public void writeFast(Double input) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        doWrite(input);
    }

    @Override
    public synchronized void loop() {
        if (System.currentTimeMillis() > shutdownUntil)
            inner.setPowerFloat();

        else inner.setPower(valToWrite);
    }

    @Override
    public void setup() {
        // this is an encodered motor, and we assume PID control in other classes
        this.inner.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    @Override
    public synchronized void safetyShutdown(long nanos) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        shutdownUntil = System.currentTimeMillis() + (nanos / 1000);
    }

    @Override
    public void addSafetyGroup(SafetyGroup grp) {
        //pass
    }

    @Override
    public synchronized void doWrite(double val) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        valToWrite = val;
    }

    @Override
    public boolean checkSafety() {
        // no safety check on this implementation
        return true;
    }

    @Override
    public Double read(Void params) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        //todo tweak for Neverest motors
        return inner.getCurrentPosition() / ENCODER_TICKS_PER_REVOLUTION;
    }

    @Override
    public void calibrate(Double val, Void params) throws InterruptedException, UnsupportedOperationException, PeripheralInoperableException, PeripheralCommunicationException {
        if (val != 0) {
            if (telemetry != null)
                telemetry.addData("javadeck-last-warning", "Attempted encoder RST nonzero");
        }

        inner.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }
}
