package org.ftccommunity.ftcxtensible;


import java.util.LinkedList;

public interface RunAssistant {
    void onExecute(RobotContext ctx, LinkedList<Object> out) throws Exception;
}
