package org.ftccommunity.ftcxtensible;

import android.content.Context;

import com.google.common.collect.ImmutableList;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import org.ftccommunity.ftcxtensible.networking.ServerSettings;
import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;
import org.ftccommunity.ftcxtensible.robot.ExtensibleHardwareMap;
import org.ftccommunity.ftcxtensible.robot.RobotStatus;

import java.util.Collection;
import java.util.LinkedList;

import io.netty.handler.codec.http.multipart.InterfaceHttpData;

public class RobotContext {
    private ExtensibleHardwareMap hardwareMap;
    private Context appContext;
    private Telemetry telemetry;

    private Gamepad gamepad1;
    private ExtensibleGamepad extensibleGamepad1;
    private Gamepad gamepad2;
    private ExtensibleGamepad extensibleGamepad2;

    private ServerSettings serverSettings;
    private RobotStatus status;

    private NetworkedOpMode networkedOpMode;
    private boolean networkingEnabled;

    private LinkedList<InterfaceHttpData> postedData;

    private RobotContext() {
        serverSettings = ServerSettings.createServerSettings();
        status = new RobotStatus(this, RobotStatus.LogTypes.HTML);
        postedData = new LinkedList<>();

        extensibleGamepad1 = new ExtensibleGamepad();
        extensibleGamepad2 = new ExtensibleGamepad();
    }

    /**
     * Generates OpMode Robot Context item bonded to the normal items in an OpMode
     *
     * @param gp1   opMode reference to Gamepad 1
     * @param gp2   opMode reference to Gamepad 2
     * @param hwMap opMode reference to the Hardware Map
     */
    public RobotContext(Gamepad gp1, Gamepad gp2, HardwareMap hwMap, Telemetry tlmtry) {
        this();
        if (gp1 == null || gp2 == null || hwMap == null) {
            throw new NullPointerException();
        }

        gamepad1 = gp1;
        gamepad2 = gp2;
        hardwareMap = new ExtensibleHardwareMap(hwMap);
        appContext = hwMap.appContext;
        telemetry = tlmtry;
    }

    public RobotContext enableNetworking() {
        if (networkedOpMode == null) {
            networkedOpMode = new NetworkedOpMode(this);
        }
        return this;
    }

    public RobotContext disableNetworking() {
        if (!networkingEnabled || networkedOpMode == null) {
            throw new IllegalStateException("Networking is already disabled!");
        }

        networkedOpMode.stopServer();
        networkedOpMode = null;

        return this;
    }

    public RobotContext startNetworking() {
        if (networkingEnabled && networkedOpMode == null) {
            throw new IllegalStateException("Networking is disabled!");
        }

        networkedOpMode.startServer();
        return this;
    }

    public RobotContext stopNetworking() {
        if (networkingEnabled && networkedOpMode == null) {
            throw new IllegalStateException("Networking is disabled!");
        }

        networkedOpMode.stopServer();
        return this;
    }

    /**
     * Returns the Hardware Map     *
     *
     * @return the <code>ExtensibleHardwareMap</code> currently in use
     */
    public HardwareMap getHardwareMap() {
        return hardwareMap;
    }

    /**
     * Returns an App Context
     *
     * @return the Android Context
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * Returns opMode reference to the first gamepad
     *
     * @return the first gamepad
     * @deprecated Please use the xGamepad1 instead
     */
    @Deprecated
    public Gamepad getGamepad1() {
        return gamepad1;
    }

    /**
     * Returns opMode reference to the second gampepad
     *
     * @return the second gamepad
     * @deprecated Please use the xGamepad2 instead
     */
    @Deprecated
    public Gamepad getGamepad2() {
        return gamepad2;
    }

    /**
     * The current HTTP server settings
     *
     * @return <code>ServerSettings</code>
     */
    public ServerSettings getServerSettings() {
        return serverSettings;
    }

    /**
     * The Robot Status Object
     *
     * @return the current status of the robot
     */
    public RobotStatus getStatus() {
        return status;
    }

    /**
     * Add a collection of data received for further processing by user code
     *
     * @param datas a collection of type <code>InterfaceHttpData</code> to be added
     */
    public void addPostData(Collection<InterfaceHttpData> datas) {
        postedData.addAll(datas);
    }

    /**
     * Get a copy of the received data for use
     *
     * @return an <code>ImmutableList</code> that is a copy of the data received via networking
     */
    public ImmutableList<InterfaceHttpData> getPostedData() {
        return ImmutableList.copyOf(postedData);
    }

    /**
     * The telemetry object to be used
     *
     * @return the telemetry as provide by the FTC SDK
     */
    public Telemetry telemetry() {
        return telemetry;
    }

    /**
     * The first Gamepad in the form of an ExtensibleGamepad.
     *
     * @return the first Gamepad (gamepad1) cast to an ExtensibleGamepad
     */
    public ExtensibleGamepad xGamepad1() {
        return extensibleGamepad1;
    }

    /**
     * The second Gamepad in the form of an ExtensibleGamepad
     *
     * @return the second gamepad (gampad2) cast to an ExtensibleGamepad
     */
    public ExtensibleGamepad xGamepad2() {
        return extensibleGamepad2;
    }

   /* public LinkedList upstreamPipeline(HardwareDevice device) {
        if (!deviceUpstreamPipeline.containsKey(device)) {
            deviceUpstreamPipeline.put(device, new LinkedList<UpstreamHardwareUpdate>());
        }
        return deviceUpstreamPipeline.get(device);
    }

    public LinkedList upstreamPipeline(Collection<HardwareDevice> devices) {
        if (!genericUpstreamPipeline.containsKey(devices)) {
            genericUpstreamPipeline.put(devices, new LinkedList<UpstreamHardwareUpdate>());
        }
        return genericUpstreamPipeline.get(devices);
    }

    public void processDevice(HardwareDevice device) {
        if (deviceUpstreamPipeline.containsKey(device)) {
            for (UpstreamHardwareUpdate<HardwareDevice> upstreamHardwareUpdate : deviceUpstreamPipeline.get(device)) {
                upstreamHardwareUpdate.processRead(device, );
            }
        }
    }*/
}
