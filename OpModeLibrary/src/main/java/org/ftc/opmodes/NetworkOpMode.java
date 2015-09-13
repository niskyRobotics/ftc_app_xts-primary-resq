package org.ftc.opmodes;


import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.LinkedList;
import java.util.logging.Level;

@Autonomous(name = "Network Op")
public class NetworkOpMode extends ExtensibleOpMode {
    private int loopCount;

    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.enableNetworking().startNetworking();
    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {
        loopCount = 0;
    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        loopCount++;
        if (loopCount % 10 == 0) {
            ctx.getStatus().log(Level.WARNING, "FOO", "This is opMode test! " + loopCount++);
        }
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.stopNetworking().disableNetworking();
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {

    }

    @Override
    public int onFailure(RobotContext ctx, Type eventType, Object event, Object in) {
        return -1;
    }


}
