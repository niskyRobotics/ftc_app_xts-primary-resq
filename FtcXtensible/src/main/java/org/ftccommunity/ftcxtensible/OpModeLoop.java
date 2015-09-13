package org.ftccommunity.ftcxtensible;


import java.util.LinkedList;

public interface OpModeLoop {
    void loop(final RobotContext ctx,
              final LinkedList<Object> out) throws Exception;
}
