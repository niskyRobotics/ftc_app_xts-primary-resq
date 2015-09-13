package org.ftccommunity.ftcxtensible;


import org.ftccommunity.ftcxtensible.robot.ExtensibleGamepad;

public interface JoystickScaler {
    double scaleX(ExtensibleGamepad gamepad, double x);

    double scaleY(ExtensibleGamepad gamepad, double y);

    int userDefinedLeft(final RobotContext ctx, final ExtensibleGamepad gamepad);

    int userDefinedRight(final RobotContext ctx, final ExtensibleGamepad gamepad);
}
