package org.ftccommunity.ftcxtensible;

import org.ftccommunity.ftcxtensible.robot.ExtensibleOpMode;

import java.util.LinkedList;

public interface FullOpMode extends OpModeLoop {
    void start(final RobotContext ctx,
               final LinkedList<Object> out) throws Exception;

    void stop(final RobotContext ctx, LinkedList<Object> out) throws Exception;

    void onSuccess(final RobotContext ctx, Object event, Object in);

    int onFailure(final RobotContext ctx,
                  ExtensibleOpMode.Type eventType, Object event, Object in);
}
