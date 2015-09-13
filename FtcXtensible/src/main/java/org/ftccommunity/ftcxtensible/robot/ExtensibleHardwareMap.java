package org.ftccommunity.ftcxtensible.robot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.qualcomm.robotcore.hardware.AccelerationSensor;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogOutput;
import com.qualcomm.robotcore.hardware.CompassSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.HashMap;
import java.util.Set;

/**
 * The new version of the hardware map that provides a safer use for the hardware map.
 * This prevents client code from unintentionally modifying the devices avialable for use
 *
 * @author David Sargent
 * @since 0.1
 */
public class ExtensibleHardwareMap extends HardwareMap {
    // Take care of the inherited variables, and restrict access
    private final DeviceMapping<DcMotorController> dcMotorController;
    private final DeviceMapping<DcMotor> dcMotor;
    private final DeviceMapping<ServoController> servoController;
    private final DeviceMapping<Servo> servo;
    private final DeviceMapping<LegacyModule> legacyModule;
    private final DeviceMapping<DeviceInterfaceModule> deviceInterfaceModule;
    private final DeviceMapping<AnalogInput> analogInput;
    private final DeviceMapping<DigitalChannel> digitalChannel;
    private final DeviceMapping<OpticalDistanceSensor> opticalDistanceSensor;
    private final DeviceMapping<TouchSensor> touchSensor;
    private final DeviceMapping<PWMOutput> pwmOutput;
    private final DeviceMapping<I2cDevice> i2cDevice;
    private final DeviceMapping<AnalogOutput> analogOutput;
    private final DeviceMapping<AccelerationSensor> accelerationSensor;
    private final DeviceMapping<CompassSensor> compassSensor;
    private final DeviceMapping<GyroSensor> gyroSensor;
    private final DeviceMapping<IrSeekerSensor> irSeekerSensor;
    private final DeviceMapping<LightSensor> lightSensor;
    private final DeviceMapping<UltrasonicSensor> ultrasonicSensor;
    private final DeviceMapping<VoltageSensor> voltageSensor;

    // Conversions to our DeviceMap
    private DeviceMap<String, DcMotorController> dcMotorControllers;
    private DeviceMap<String, DcMotor> dcMotors;
    private DeviceMap<String, ServoController> servoControllers;
    private DeviceMap<String, Servo> servos;
    private DeviceMap<String, LegacyModule> legacyModules;
    private DeviceMap<String, DeviceInterfaceModule> deviceInterfaceModules;
    private DeviceMap<String, AnalogInput> analogInputs;
    private DeviceMap<String, DigitalChannel> digitalChannels;
    private DeviceMap<String, OpticalDistanceSensor> opticalDistanceSensors;
    private DeviceMap<String, TouchSensor> touchSensors;
    private DeviceMap<String, PWMOutput> pwmOutputs;
    private DeviceMap<String, I2cDevice> i2cDevices;
    private DeviceMap<String, AnalogOutput> analogOutputs;
    private DeviceMap<String, AccelerationSensor> accelerationSensors;
    private DeviceMap<String, CompassSensor> compassSensors;
    private DeviceMap<String, GyroSensor> gyroSensors;
    private DeviceMap<String, IrSeekerSensor> irSeekerSensors;
    private DeviceMap<String, LightSensor> lightSensors;
    private DeviceMap<String, UltrasonicSensor> ultrasonicSensors;
    private DeviceMap<String, VoltageSensor> voltageSensors;

    private LinkedHashMultimap<String, DeviceMap> cacheMap;

    /**
     * Builds the base ExtensibleHardwareMap; need to complete setup afterwards, specifically calling
     * {@link ExtensibleHardwareMap#createDeviceMaps()}
     */
    private ExtensibleHardwareMap() {
        dcMotorController = super.dcMotorController;
        dcMotor = super.dcMotor;
        servoController = super.servoController;
        servo = super.servo;
        legacyModule = super.legacyModule;
        deviceInterfaceModule = super.deviceInterfaceModule;
        analogInput = super.analogInput;
        digitalChannel = super.digitalChannel;
        opticalDistanceSensor = super.opticalDistanceSensor;
        touchSensor = super.touchSensor;
        pwmOutput = super.pwmOutput;
        i2cDevice = super.i2cDevice;
        analogOutput = super.analogOutput;
        accelerationSensor = super.accelerationSensor;
        compassSensor = super.compassSensor;
        this.gyroSensor = super.gyroSensor;
        irSeekerSensor = super.irSeekerSensor;
        lightSensor = super.lightSensor;
        ultrasonicSensor = super.ultrasonicSensor;
        voltageSensor = super.voltageSensor;

        cacheMap = LinkedHashMultimap.create();

        createDeviceMaps();
    }

    /**
     * Builds a new {@code ExtensibleHardwareMap} for use
     *
     * @param hwMap the HardwareMap to base off of
     */
    public ExtensibleHardwareMap(HardwareMap hwMap) {
        this();

        super.dcMotorController = hwMap.dcMotorController;
        super.dcMotor = hwMap.dcMotor;
        super.servoController = hwMap.servoController;
        super.servo = hwMap.servo;
        super.legacyModule = hwMap.legacyModule;
        super.deviceInterfaceModule = hwMap.deviceInterfaceModule;
        super.analogInput = hwMap.analogInput;
        super.digitalChannel = hwMap.digitalChannel;
        super.opticalDistanceSensor = hwMap.opticalDistanceSensor;
        super.touchSensor = hwMap.touchSensor;
        super.pwmOutput = hwMap.pwmOutput;
        super.i2cDevice = hwMap.i2cDevice;
        super.analogOutput = hwMap.analogOutput;
        super.accelerationSensor = hwMap.accelerationSensor;
        super.compassSensor = hwMap.compassSensor;
        super.gyroSensor = hwMap.gyroSensor;
        super.irSeekerSensor = hwMap.irSeekerSensor;
        super.lightSensor = hwMap.lightSensor;
        super.ultrasonicSensor = hwMap.ultrasonicSensor;
        super.voltageSensor = hwMap.voltageSensor;

        createDeviceMaps();
    }

    /**
     * Move the propriety {@link DeviceMapping} to our
     * {@link DeviceMap} for our
     * internal use
     */
    private void createDeviceMaps() {
        dcMotorControllers = new DeviceMap<>(dcMotorController);
        dcMotors = new DeviceMap<>(dcMotor);
        servoControllers = new DeviceMap<>(servoController);
        servos = new DeviceMap<>(servo);
        legacyModules = new DeviceMap<>(legacyModule);
        deviceInterfaceModules = new DeviceMap<>(deviceInterfaceModule);
        analogInputs = new DeviceMap<>(analogInput);
        digitalChannels = new DeviceMap<>(digitalChannel);
        opticalDistanceSensors = new DeviceMap<>(opticalDistanceSensor);
        touchSensors = new DeviceMap<>(touchSensor);
        pwmOutputs = new DeviceMap<>(pwmOutput);
        i2cDevices = new DeviceMap<>(i2cDevice);
        analogOutputs = new DeviceMap<>(analogOutput);
        accelerationSensors = new DeviceMap<>(accelerationSensor);
        compassSensors = new DeviceMap<>(compassSensor);
        gyroSensors = new DeviceMap<>(gyroSensor);
        irSeekerSensors = new DeviceMap<>(irSeekerSensor);
        lightSensors = new DeviceMap<>(lightSensor);
        ultrasonicSensors = new DeviceMap<>(ultrasonicSensor);
        voltageSensors = new DeviceMap<>(voltageSensor);
    }

    public ImmutableMap<String, DcMotorController> getDcMotorControllers() {
        return ImmutableMap.copyOf(dcMotorControllers);
    }

    public ImmutableMap<String, DcMotor> getDcMotors() {
        return ImmutableMap.copyOf(dcMotors);
    }

    public ImmutableMap<String, ServoController> getServoControllers() {
        return ImmutableMap.copyOf(servoControllers);
    }

    public ImmutableMap<String, Servo> getServos() {
        return ImmutableMap.copyOf(servos);
    }

    public ImmutableMap<String, LegacyModule> getLegacyModules() {
        return ImmutableMap.copyOf(legacyModules);
    }

    public ImmutableMap<String, DeviceInterfaceModule> getDeviceInterfaceModules() {
        return ImmutableMap.copyOf(deviceInterfaceModules);
    }

    public ImmutableMap<String, AnalogInput> getAnalogInputs() {
        return ImmutableMap.copyOf(analogInputs);
    }

    public ImmutableMap<String, DigitalChannel> getDigitalChannels() {
        return ImmutableMap.copyOf(digitalChannels);
    }

    public ImmutableMap<String, OpticalDistanceSensor> getOpticalDistanceSensors() {
        return ImmutableMap.copyOf(opticalDistanceSensors);
    }

    public ImmutableMap<String, TouchSensor> getTouchSensors() {
        return ImmutableMap.copyOf(touchSensors);
    }

    public ImmutableMap<String, PWMOutput> getPwmOutputs() {
        return ImmutableMap.copyOf(pwmOutputs);
    }

    public ImmutableMap<String, I2cDevice> getI2cDevices() {
        return ImmutableMap.copyOf(i2cDevices);
    }

    public ImmutableMap<String, AnalogOutput> getAnalogOutputs() {
        return ImmutableMap.copyOf(analogOutputs);
    }

    public ImmutableMap<String, AccelerationSensor> getAccelerationSensors() {
        return ImmutableMap.copyOf(accelerationSensors);
    }

    public ImmutableMap<String, CompassSensor> getCompassSensors() {
        return ImmutableMap.copyOf(compassSensors);
    }

    public ImmutableMap<String, GyroSensor> getGyroSensors() {
        return ImmutableMap.copyOf(gyroSensors);
    }

    public ImmutableMap<String, IrSeekerSensor> getIrSeekerSensors() {
        return ImmutableMap.copyOf(irSeekerSensors);
    }

    public ImmutableMap<String, LightSensor> getLightSensors() {
        return ImmutableMap.copyOf(lightSensors);
    }

    public ImmutableMap<String, UltrasonicSensor> getUltrasonicSensors() {
        return ImmutableMap.copyOf(ultrasonicSensors);
    }

    public ImmutableMap<String, VoltageSensor> getVoltageSensors() {
        return ImmutableMap.copyOf(voltageSensors);
    }


    private class DeviceMap<K, T> extends HashMap<String, T> {
        public DeviceMap(DeviceMapping<T> deviceMapping) {
            super(deviceMapping.size());
            buildFromDeviceMapping(deviceMapping);
        }

        public DeviceMap<K, T> buildFromDeviceMapping(DeviceMapping<T> deviceMapping) {
            Set<Entry<String, T>> entries = deviceMapping.entrySet();
            for (Entry<String, T> device : entries) {
                super.put(device.getKey(), device.getValue());
            }
            return this;
        }
    }

}
