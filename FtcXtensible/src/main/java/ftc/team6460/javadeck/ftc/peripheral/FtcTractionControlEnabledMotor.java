package ftc.team6460.javadeck.ftc.peripheral;


import ftc.team6460.javadeck.api.motion.EncoderedMotor;
import ftc.team6460.javadeck.api.peripheral.PeripheralCommunicationException;
import ftc.team6460.javadeck.api.peripheral.PeripheralInoperableException;
import ftc.team6460.javadeck.api.safety.SafetyGroup;

import java.util.LinkedList;

/**
 * Created by hexafraction on 9/12/15.
 */
public class FtcTractionControlEnabledMotor implements EncoderedMotor {
    FtcEncoderedMotor delegate;

    LinkedList<Double> velocityMovingAvg = new LinkedList<>();

    long nanosLastEncReading;
    double lastEncReading;
    int invalidCountdown = 30; // 30 samples seems fine with current coefs
    double velocityAvg;
    int[] coefs = new int[]{1, 2, 3, 4, 5, 4, 3, 2, 1};
    double commandedPower;
    double dropFactorOverspeed;
    double overspeedFactor;
    int coefTotal = 0;
    int powerDropThreshold;
    int dropSamples;

    public FtcTractionControlEnabledMotor(FtcEncoderedMotor delegate, double dropFactorOverspeed, int dropSamples, int overspeedFactor) {
        if (delegate.inner.getController().getClass().getName().equals("d") && delegate.inner.getController().getClass().getPackage().equals("com.qualcomm.modernrobotics")) {
            throw new RuntimeException("CANNOT RUN TRACTION CONTROL ON LEGACY CONTROLLERS FOR THE TIME BEING!");
        }
        this.delegate = delegate;
        this.dropSamples = dropSamples;
        this.overspeedFactor = overspeedFactor;
        this.dropFactorOverspeed = dropFactorOverspeed;
        for (int i = 0; i < coefs.length; i++) {
            coefTotal += coefs[i];
        }
        delegate.opMode.accept(this);
    }

    @Override
    public void write(Double input) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        if(Math.abs(commandedPower-input)>Math.abs(commandedPower*overspeedFactor/10)){

            invalidCountdown = 30;
        }
        commandedPower = input;

    }

    @Override
    public void writeFast(Double input) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        if(Math.abs(commandedPower-input)>Math.abs(commandedPower*overspeedFactor/10)){

            invalidCountdown = 30;
        }
        commandedPower = input;
    }

    @Override
    public void loop() {
        double cPow = commandedPower;
        if (powerDropThreshold > 0) {
            cPow *= dropFactorOverspeed;

            delegate.telemetry.addData("trac-ctrl-status: ", "TRIG");
            powerDropThreshold--;
        }
        if (velocityMovingAvg.size() >= coefs.length) {
            velocityMovingAvg.removeFirst();

        }
        try {
            double encNow = delegate.read(null);
            long nanosNow = System.nanoTime();
            double valNow = encNow / nanosNow * 1_000_000_000; // enc counts per second
            nanosLastEncReading = nanosNow;
            lastEncReading = encNow;
            velocityMovingAvg.addLast(valNow);
            if ((Math.abs(valNow) > Math.abs(velocityAvg * overspeedFactor))|| (Math.signum(valNow)!= Math.abs(velocityAvg))){
                powerDropThreshold = dropSamples;
            }
        } catch (Exception e) {

            delegate.telemetry.addData("trac-ctrl-status: ", "ERR-READ");
            return;
        }

        if (invalidCountdown > 0) {
            invalidCountdown--;
            return;
        } else {
            double velTotal = 0;
            int i = 0; // hacky as frack
            for (double val : velocityMovingAvg) {
                velTotal += val * coefs[i];
                i++;
            }
            velTotal = velTotal / coefTotal;

            if(velTotal<100){ // 10 enc counts per second, STALL!
                cPow = 0; // Shut down motor
                delegate.telemetry.addData("trac-ctrl-status: ", "STALL");
                powerDropThreshold = dropSamples;
            }
        }

        try {
            delegate.write(cPow);
        } catch (Exception e) {
            delegate.telemetry.addData("trac-ctrl-status: ", "ERR-WRITE");
        }


    }

    @Override
    public void setup() {
    }

    @Override
    public void safetyShutdown(long nanos) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        delegate.safetyShutdown(nanos);
    }

    @Override
    public void addSafetyGroup(SafetyGroup grp) {
        delegate.addSafetyGroup(grp);
    }

    @Override
    public void doWrite(double val) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        if(Math.abs(commandedPower-val)>Math.abs(commandedPower*overspeedFactor/10)){

            invalidCountdown = 30;
        }
        commandedPower = val;
    }

    @Override
    public boolean checkSafety() {
        return delegate.checkSafety();
    }

    @Override
    public Double read(Void params) throws InterruptedException, PeripheralCommunicationException, PeripheralInoperableException {
        return velocityAvg;
    }

    @Override
    public void calibrate(Double val, Void params) throws InterruptedException, UnsupportedOperationException, PeripheralInoperableException, PeripheralCommunicationException {
        delegate.calibrate(val, params);
    }
}
