package org.ftc.opmodes;

import org.ftccommunity.ftcxtensible.RobotContext;
import org.ftccommunity.ftcxtensible.opmodes.Autonomous;
import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;

@Autonomous(name = "Hello World Example")
public class HelloWorld extends ExtensibleOpMode {
    @Override
    public void init(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.enableNetworking().startNetworking();
    }

    @Override
    public void start(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.telemetry().addData("TIME", "Start Date: " +
                (new Date(System.nanoTime() / 1000)).toString());
    }

    @Override
    public void loop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.getStatus().log(Level.INFO, "LOOP", "Current loop count: " + getLoopCount());
        ctx.telemetry().addData("MESS", "Hello, World!");
        ctx.telemetry().addData("MESS", "How are you doing?");
    }

    @Override
    public void stop(RobotContext ctx, LinkedList<Object> out) throws Exception {
        ctx.getStatus().log(Level.WARNING, "TIME", "End Date: " +
                (new Date(System.nanoTime() / 1000)).toString() + "This ran for " + getRuntime());
    }

    @Override
    public void onSuccess(RobotContext ctx, Object event, Object in) {
        // Don't worry about this; it is used for the advanced stuff
    }

    @Override
    public int onFailure(RobotContext ctx, Type eventType, Object event, Object in) {
        // Return the default value; for when things go south
        return -1;
    }
}
