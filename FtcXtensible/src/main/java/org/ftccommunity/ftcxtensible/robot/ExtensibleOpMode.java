package org.ftccommunity.ftcxtensible.robot;

import android.util.Log;

import com.google.common.collect.ImmutableMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftccommunity.ftcxtensible.FullOpMode;
import org.ftccommunity.ftcxtensible.OpModeLoop;
import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.RunAssistant;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author David Sargent - FTC5395
 * @since 0.1
 */
public abstract class ExtensibleOpMode extends OpMode implements FullOpMode {
    public static final String TAG = "XTENSTIBLE_OP_MODE::";
    private transient static ExtensibleOpMode parent;

    private final RobotContext robotContext;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final HardwareMap hardwareMap;
    private final Telemetry telemetry;

    private TreeMap<Integer, LinkedList<RunAssistant>> beforeEveryXLoop;
    private LinkedHashMap<Integer, LinkedList<RunAssistant>> beforeXLoop;

    private TreeMap<Integer, OpModeLoop> loops;
    private TreeMap<Integer, LinkedList<RunAssistant>> afterEveryXLoop;
    private LinkedHashMap<Integer, LinkedList<RunAssistant>> afterXLoop;

    private int loopCount;
    private volatile int skipNextLoop;

    protected ExtensibleOpMode() {
        this.gamepad1 = super.gamepad1;
        this.gamepad2 = super.gamepad2;
        this.hardwareMap = super.hardwareMap;
        this.telemetry = super.telemetry;
        loopCount = 0;
        skipNextLoop = 0;

        if (parent == null) {
            robotContext = new RobotContext(gamepad1, gamepad2, hardwareMap, telemetry);
            parent = this;
        } else {
            robotContext = parent.robotContext;
        }

        loops = new TreeMap<>();
        beforeXLoop = new LinkedHashMap<>();
        beforeEveryXLoop = new TreeMap<>();
        afterEveryXLoop = new TreeMap<>();
        afterXLoop = new LinkedHashMap<>();
    }

    protected ExtensibleOpMode(ExtensibleOpMode prt) {
        this();
        parent = prt;
    }

    public abstract void init(final RobotContext ctx, final LinkedList<Object> out) throws Exception;

    @Override
    public final void init() {
        LinkedList<Object> list = new LinkedList<>();
        try {
            init(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            Log.e(TAG,
                    "An exception occurred running the OpMode " + getCallerClassName(e), e);
            if (onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, e) >= 0) {
                for (Object o : list) {
                    onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, o);
                }
            } else {
                RobotLog.setGlobalErrorMsg(e.toString());
            }
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, MainStates.STOP, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, MainStates.STOP, o);
            }
        }
    }

    @Override
    public final void start() {
        loopCount++;
        LinkedList<Object> list = new LinkedList<>();
        try {
            start(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            Log.e(TAG,
                    "An exception occurred running the OpMode " + getCallerClassName(e), e);
            if (onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, e) >= 0) {
                for (Object o : list) {
                    onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, o);
                }
            } else {
                RobotLog.setGlobalErrorMsg(e.toString());
            }
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, MainStates.STOP, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, MainStates.STOP, o);
            }
        }
    }

    @Override
    public final void loop() {
        loopCount++;
        if (skipNextLoop > 0) {
            skipNextLoop--;
            Log.i(TAG, "Skipping Loop #" + getLoopCount());
            return;
        }

        long startTime = System.nanoTime();

        final Integer loopNum = loopCount;
        if (beforeXLoop.containsKey(loopNum)) {
            for (RunAssistant assistant : beforeXLoop.get(loopNum)) {
                runAssistant(assistant);
            }
        }

        if (beforeEveryXLoop.containsKey(loopNum)) {
            for (RunAssistant assistant : beforeEveryXLoop.get(loopNum)) {
                runAssistant(assistant);
            }
        }

        LinkedList<Object> list = new LinkedList<>();
        try {
            loop(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            Log.e(TAG,
                    "An exception occurred running the OpMode " + getCallerClassName(e), e);
            if (onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, e) >= 0) {
                for (Object o : list) {
                    onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, o);
                }
            } else {
                RobotLog.setGlobalErrorMsg(e.toString());
            }
        }

        if (afterEveryXLoop.containsKey(loopNum)) {
            for (RunAssistant assistant : afterEveryXLoop.get(loopNum)) {
                runAssistant(assistant);
            }
        }

        if (afterXLoop.containsKey(loopNum)) {
            for (RunAssistant assistant : afterXLoop.get(loopNum)) {
                runAssistant(assistant);
            }
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, MainStates.STOP, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, MainStates.STOP, o);
            }
        }

        long endTime = System.nanoTime();
        if ((endTime - startTime) - (1000000 * 50) > 0) {
            Log.w(TAG, "User code took long than " + 50 + "ms. Time: " +
                    ((endTime - startTime) - (1000000 * 50)) + "ms");
        }
    }

    @Override
    public final void stop() {
        LinkedList<Object> list = new LinkedList<>();
        try {
            stop(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            Log.e(TAG,
                    "An exception occurred running the OpMode " + getCallerClassName(e), e);
            if (onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, e) >= 0) {
                for (Object o : list) {
                    onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, o);
                }
            } else {
                RobotLog.setGlobalErrorMsg(e.toString());
            }
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, MainStates.STOP, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, MainStates.STOP, o);
            }
        }

        parent = null;
    }

    protected final int getLoopCount() {
        return loopCount;
    }

    protected final void skipNextLoop() {
        skipNextLoop++;
    }

    protected ExtensibleOpMode registerNewLoopOnEveryX(int loopX, OpModeLoop loop) {
        if (loops.containsKey(loopX)) {
            Log.w(TAG, "Loop already exists; replacing");
            loops.remove(loopX);
        }

        loops.put(loopX, loop);
        return this;
    }

    protected ExtensibleOpMode unregisterNewLoopOnEveryX(int loopX, OpModeLoop loop)
            throws IllegalStateException {
        if (loops.containsKey(loopX)) {
            loops.remove(loopX);
        } else {
            Log.e(TAG, "There is no registered loop replacement for " + loopX);
            throw new IllegalStateException("Loop has not registered replacement");
        }

        return this;
    }

    protected ExtensibleOpMode registerBeforeXLoop(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (beforeXLoop.containsKey(loop)) {
            beforeXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeX(int loopNumber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, false)) {
            unregisterAfterEveryX(loopNumber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeX(int loopCount, int pos) {
        if (pos < 0 || pos > beforeXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastBeforeX(int loopNumber, String name) {
        List<Integer> candidate = getPossibleCandidatesForBeforeX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, true)) {
            unregisterAfterEveryX(loopNumber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeX(int loopNum, String name) {
        return getCandidates(loopNum, name, beforeXLoop);
    }

    protected ExtensibleOpMode registerBeforeEveryX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (beforeEveryXLoop.containsKey(loop)) {
            beforeEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, beforeEveryXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode requestChangeOfRegisterBeforeX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleOpMode unregisterBeforeEveryX(int loopNumber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, false)) {
            unregisterAfterEveryX(loopNumber, candidate.get(0));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterBeforeEveryX(int loopCount, int pos) {
        if (pos < 0 || pos > beforeEveryXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        beforeEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastBeforeEveryX(int loopNumber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, true)) {
            unregisterAfterEveryX(loopNumber, candidate.get(candidate.size() - 1));
        }
        return this;
    }

    protected List<Integer> getPossibleCandidatesForBeforeEveryX(int loopNum, String name) {
        return getCandidates(loopNum, name, beforeEveryXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterBeforeEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        beforeEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterBeforeEveryX() {
        return ImmutableMap.copyOf(beforeEveryXLoop);
    }

    protected ExtensibleOpMode registerAfterEveryX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (afterEveryXLoop.containsKey(loop)) {
            afterEveryXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, afterEveryXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterEveryX(int loopNumber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, false)) {
            unregisterAfterX(loopNumber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterEveryX(int loopCount, int pos) {
        if (pos < 0 || pos > afterEveryXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterEveryXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastEveryX(int loopNumber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, true)) {
            unregisterAfterEveryX(loopNumber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterEveryX(int loopNum, String name) {
        return getCandidates(loopNum, name, afterEveryXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterAfterEveryX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterEveryXLoop = new TreeMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterEveryX() {
        return ImmutableMap.copyOf(afterEveryXLoop);
    }

    protected ExtensibleOpMode registerAfterX(int loop, RunAssistant assistant) {
        if (assistant == null) {
            throw new NullPointerException();
        }

        if (afterXLoop.containsKey(loop)) {
            afterXLoop.get(loop).add(assistant);
        } else {
            createNewRunAssistantKey(loop, assistant, afterXLoop);
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterX(int loopNumber, String name) throws IllegalStateException {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, false)) {
            unregisterAfterX(loopNumber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected ExtensibleOpMode unregisterAfterX(int loopCount, int pos) {
        if (pos < 0 || pos > afterXLoop.get(loopCount).size() - 1) {
            throw new IllegalArgumentException();
        }
        afterXLoop.get(loopCount).remove(pos);
        return this;
    }

    protected ExtensibleOpMode unregisterLastAfterX(int loopNumber, String name) {
        List<Integer> candidate = getPossibleCandidatesForAfterEveryX(loopNumber, name);
        if (checkRunAssistantRemoval(loopNumber, candidate, true)) {
            unregisterAfterX(loopNumber, candidate.get(candidate.size() - 1));
        }

        return this;
    }

    protected List<Integer> getPossibleCandidatesForAfterX(int loopNum, String name) {
        return getCandidates(loopNum, name, afterXLoop);
    }

    protected ExtensibleOpMode requestChangeOfRegisterAfterX(Map<Integer, LinkedList<RunAssistant>> map) {
        if (map == null) {
            throw new NullPointerException();
        }

        checkRunAssistantMap(map);
        afterXLoop = new LinkedHashMap<>(map);
        return this;
    }

    protected ImmutableMap<Integer, LinkedList<RunAssistant>> getRegisterAfterX() {
        return ImmutableMap.copyOf(afterXLoop);
    }

    protected final RobotContext getContext() {
        return robotContext;
    }

    private boolean checkRunAssistantRemoval(int loopNumber, List<Integer> candidate, boolean isLast) throws IllegalStateException {
        if (candidate.size() < 1) {
            throw new IllegalStateException("Cannot remove something, if there is nothing");
        } else {
            if (candidate.size() != 1) {
                Log.w(TAG, "There are multiple removal candidates, removing the " +
                        (isLast ? "last" : "first") + ".");
                logRunAssitant(loopNumber, candidate);
            }
            return true;
        }
    }

    private boolean checkRunAssistantMap(Map<Integer, LinkedList<RunAssistant>> map) {
        for (Integer i : map.keySet()) {
            for (RunAssistant runAssistant : map.get(i)) {
                if (runAssistant == null) {
                    throw new NullPointerException();
                }
            }
        }
        return true;
    }

    private void createNewRunAssistantKey(int loop, RunAssistant assistant, Map<Integer, LinkedList<RunAssistant>> RunAssistantMap) {
        LinkedList<RunAssistant> assistants = new LinkedList<>();
        assistants.add(assistant);
        RunAssistantMap.put(loop, assistants);
    }

    private LinkedList<Integer> getCandidates(int loopNum, String name, Map<Integer, LinkedList<RunAssistant>> runAssistantMap) {
        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < runAssistantMap.get(loopNum).size(); i++) {
            RunAssistant secondItem = runAssistantMap.get(loopNum).get(i);
            if (secondItem.getClass().getSimpleName().indexOf(name) == 0) {
                list.add(i);
            }
        }
        return list;
    }

    private void logRunAssitant(int loopNumber, List<Integer> candidate) {
        for (int i : candidate) {
            Log.i(TAG, i + " " +
                    afterEveryXLoop.get(loopNumber).get(i).getClass().getSimpleName());
        }
    }

    private void runAssistant(RunAssistant assistant) {
        LinkedList<Object> list = new LinkedList<>();
        try {
            assistant.onExecute(robotContext, list);
        } catch (InterruptedException ex) {
            return;
        } catch (Exception e) {
            Log.e(TAG,
                    "An exception occurred running the OpMode " + getCallerClassName(e), e);
            if (onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, e) >= 0) {
                for (Object o : list) {
                    onFailure(robotContext, Type.FAILURE, MainStates.EXCEPTION, o);
                }
            } else {
                RobotLog.setGlobalErrorMsg(e.toString());
            }
        }

        if (list.isEmpty()) {
            onSuccess(robotContext, MainStates.STOP, null);
        } else {
            for (Object o : list) {
                onSuccess(robotContext, MainStates.STOP, o);
            }
        }
    }

    private String getCallerClassName(Exception e) {
        StackTraceElement[] stElements = e.getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ExtensibleOpMode.class.getName()) &&
                    ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName();
            }
        }
        return "";
    }

    public enum MainStates {
        CONSTRUCT, ARM, INIT, EXEC, STOP, EXCEPTION
    }

    public enum Type {
        SUCCESS, FAILURE, IDK
    }
}
