package org.ftccommunity.ftcxtensible;

import com.qualcomm.robotcore.hardware.DcMotorController;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class ReadWriteExtensibleOpMode extends ExtensibleOpMode {
    private final DcMotorController[] controllers;
    private boolean skipReadMode;
    private boolean isInReadyMode;
    private int runEveryXTimes;
    private int readOnlyPos;
    private int writeOnlyPos;

    public ReadWriteExtensibleOpMode(int howOften, final DcMotorController[] ctrls) {
        if (howOften < 1) {
            throw new IllegalArgumentException("howOften is less than 1");
        }

        controllers = ctrls;

        modifyLoopChangeoverNumber(howOften);
    }

    protected final void skipAnyReadModeChangeOver() {
        skipReadMode = true;
    }

    public final boolean getIsInReadMode() {
        return isInReadyMode;
    }

    protected final void changeLoopChangeoverNumber(int loopCount) {
        Map<Integer, LinkedList<RunAssistant>> everyX = getRegisterAfterEveryX();

        if (everyX.get(loopCount).get(readOnlyPos) instanceof ReadOnlySwitch) {
            unregisterAfterEveryX(loopCount, readOnlyPos);
        } else { // We need to find our registration
            getPossibleCandidatesForAfterEveryX(loopCount, ReadOnlySwitch.class.getSimpleName());
        }

        if (everyX.get(loopCount + 1).get(writeOnlyPos) instanceof WriteOnlyAssistant) {
            unregisterAfterEveryX(loopCount + 1, writeOnlyPos);
        } else { // We need to find our registration
            List<Integer> canditates = getPossibleCandidatesForAfterEveryX(
                    loopCount, WriteOnlyAssistant.class.getSimpleName());
            for (int i = 0; i < canditates.size(); i++) {
                if (everyX.get(loopCount).get(canditates.get(i)) instanceof WriteOnlyAssistant) {
                    unregisterAfterEveryX(loopCount, canditates.get(i));
                }
            }
        }

        modifyLoopChangeoverNumber(loopCount);
    }

    private void modifyLoopChangeoverNumber(int loopCount) {
        int currentStatus = getRegisterAfterEveryX().size();
        writeOnlyPos = currentStatus;
        readOnlyPos = ++currentStatus;

        registerAfterEveryX(loopCount, new ReadOnlySwitch());
        registerNewLoopOnEveryX(loopCount, new ReadLoop());
        registerAfterEveryX(loopCount + 3, new WriteOnlyAssistant());

        runEveryXTimes = loopCount;
    }

    protected abstract void readLoop(final RobotContext ctx, final LinkedList<Object> out);

    protected final int getLoopChangeoverNumber() {
        return runEveryXTimes;
    }

    private class ReadLoop implements OpModeLoop {
        @Override
        public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
            if (!skipReadMode) {
                readLoop(ctx, out);
            } else {
                loop(ctx, out);
            }
        }
    }

    private class ReadOnlySwitch implements RunAssistant {
        @Override
        public void onExecute(RobotContext ctx, LinkedList<Object> out) throws Exception {
            if (!skipReadMode) {
                for (DcMotorController controller : controllers) {
                    controller.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
                    out.add(controller);
                    isInReadyMode = true;
                    skipNextLoop();
                }
            }
        }
    }

    private class WriteOnlyAssistant implements RunAssistant {
        @Override
        public void onExecute(RobotContext ctx, LinkedList<Object> out) throws Exception {
            if (!skipReadMode) {
                for (DcMotorController controller : controllers) {
                    controller.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);
                    out.add(controller);
                    isInReadyMode = false;
                    skipNextLoop();
                }
            }
        }
    }
}
