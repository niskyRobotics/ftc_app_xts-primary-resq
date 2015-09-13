package ftc.team6460.javadeck.ftc.peripheral;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import ftc.team6460.javadeck.ftc.opmode.FtcBaseOpMode;
import ftc.team6460.javadeck.ftc.peripheral.Ftc3WireServo;
import ftc.team6460.javadeck.ftc.peripheral.FtcEncoderedMotor;
import ftc.team6460.javadeck.ftc.peripheral.FtcRunToPositionMotor;
import ftc.team6460.javadeck.ftc.peripheral.FtcUnencoderedMotor;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by hexafraction on 6/11/15.
 */
public class FtcPeripheralsFactory {

    public static class DevKey {
        public final String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DevKey devKey = (DevKey) o;

            if (port != devKey.port) return false;
            return name.equals(devKey.name);

        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + port;
            return result;
        }

        public DevKey(String name, int port) {

            this.name = name;
            this.port = port;
        }

        public final int port;
    }

    private final FtcBaseOpMode op;

    public FtcPeripheralsFactory(HardwareMap map, FtcBaseOpMode op) {
        this.map = map;
        this.op = op;
    }

    private final HashMap<String, Servo> servoMap = new HashMap<>();
    private final HashMap<String, DcMotor> motorMap = new HashMap<>();

    public Ftc3WireServo getPhysicalServo(String key) {
        Servo s;
        if ((s = servoMap.get(key)) == null) {
            s = map.servo.get(key);
            if (s == null) throw new NoSuchElementException("The specified servo with key " + key + " was not found.");
            servoMap.put(key, s);
        }
        return new Ftc3WireServo(s, op);
    }

    public FtcEncoderedMotor getEncoderedMotor(String key) {
        DcMotor m;
        if ((m = motorMap.get(key)) == null) {
            m = map.dcMotor.get(key);
            if (m == null) throw new NoSuchElementException("The specified motor with key " + key + " was not found.");
            motorMap.put(key, m);
        }
        return new FtcEncoderedMotor(m, op);
    }

    public FtcUnencoderedMotor getUnncoderedMotor(String key) {
        DcMotor m;
        if ((m = motorMap.get(key)) == null) {
            m = map.dcMotor.get(key);
            if (m == null) throw new NoSuchElementException("The specified motor with key " + key + " was not found.");
            motorMap.put(key, m);
        }
        return new FtcUnencoderedMotor(m, op);
    }
    public FtcRunToPositionMotor getPositionBasedMotor(String key) {
        DcMotor m;
        if ((m = motorMap.get(key)) == null) {
            m = map.dcMotor.get(key);
            if (m == null) throw new NoSuchElementException("The specified motor with key " + key + " was not found.");
            motorMap.put(key, m);
        }
        return new FtcRunToPositionMotor(m, op);
    }

    private final HardwareMap map;
}
