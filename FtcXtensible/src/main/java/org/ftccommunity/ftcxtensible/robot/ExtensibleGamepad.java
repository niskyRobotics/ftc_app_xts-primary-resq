package org.ftccommunity.ftcxtensible.robot;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.ftccommunity.ftcxtensible.JoystickScaler;
import org.ftccommunity.ftcxtensible.RobotContext;


/**
 * The extensible Gamepad, based on <code>{@link Gamepad}</code>. This takes care of multiple issues
 * of the design of the original <code>Gamepad</code>, including code readablity, design, and
 * extending the capilabties of what the Gamepad can do.
 *
 * @author David Sargent
 * @since 0.1
 */
public class ExtensibleGamepad {
    private boolean a;
    private boolean b;
    private boolean x;
    private boolean y;

    private boolean guide;
    private boolean start;
    private boolean back;

    private boolean leftBumper;
    private boolean rightBumper;
    private float leftTrigger;
    private float rightTrigger;

    private long timestamp;

    private int userDefinedRight;
    private int userDefinedLeft;

    private Joystick leftJoystick;
    private Joystick rightJoystick;

    private JoystickScaler leftScaler;
    private JoystickScaler rightScaler;
    private Dpad dpad;

    /**
     * Setup a basic gamepad; be sure to call {@link ExtensibleGamepad#updateGamepad}
     * to make this safe to use
     */
    public ExtensibleGamepad() {
        leftJoystick = new Joystick();
        rightJoystick = new Joystick();
        dpad = new Dpad();

        leftScaler = new PlainJoystickScaler();
        rightScaler = new PlainJoystickScaler();
    }

    public ExtensibleGamepad(final RobotContext ctx, Gamepad gp) {
        this();
        updateGamepad(ctx, gp);
    }

    /**
     * Add scaler algorithms to modify the joystick input
     *
     * @param left  a <code>{@link JoystickScaler}</code> with a custom scaler to use
     *              on the left Joystick
     * @param right a <code>JoystickScaler</code> with a custom scaler to use on the
     *              right Joystick
     * @return this object to continue work on
     */
    public ExtensibleGamepad setupJoystickScalers(JoystickScaler left, JoystickScaler right) {
        if (left == null || right == null) {
            throw new NullPointerException();
        }
        leftScaler = left;
        rightScaler = right;
        return this;
    }

    /**
     * Updates this to the status of the provided Gamepad (recast this from a <code>{@link Gamepad}</code>)
     *
     * @param ctx the Robot Context (nullable, depending on the given <code>{@link JoystickScaler}</code>)
     * @param gp  the <code>Gamepad</code> to cast into
     */
    public void updateGamepad(final RobotContext ctx, Gamepad gp) {
        a = gp.a;
        b = gp.b;
        x = gp.x;
        y = gp.y;

        guide = gp.guide;
        start = gp.start;
        back = gp.back;

        leftBumper = gp.left_bumper;
        rightBumper = gp.right_bumper;

        leftTrigger = gp.left_trigger;
        rightTrigger = gp.right_trigger;

        timestamp = gp.timestamp;

        getDpad().update(gp.dpad_up, gp.dpad_down, gp.dpad_right, gp.dpad_left);

        getRightJoystick().update(gp.right_stick_x, gp.right_stick_y, gp.right_stick_button);

        userDefinedLeft = getLeftScaler().userDefinedLeft(ctx, this);
        userDefinedRight = getRightScaler().userDefinedRight(ctx, this);

        double leftX = gp.left_stick_x;
        double leftY = gp.left_stick_y;
        leftX = getLeftScaler().scaleX(this, leftX);
        leftY = getLeftScaler().scaleY(this, leftY);
        getLeftJoystick().update(leftX, leftY, gp.left_stick_button);

        double rightX = gp.right_stick_x;
        double rightY = gp.right_stick_y;
        rightX = getRightScaler().scaleX(this, rightX);
        rightY = getRightScaler().scaleY(this, rightY);
        getRightJoystick().update(rightX, rightY, gp.right_stick_button);
    }

    /**
     * Gets the status of Button A
     *
     * @return is button A pressed
     */
    public boolean isAPressed() {
        return a;
    }

    /**
     * Gets the status of Button B
     *
     * @return is button B pressed
     */
    public boolean isBPressed() {
        return b;
    }

    /**
     * Gets the status of Button X
     *
     * @return is button X pressed
     */
    public boolean isXPressed() {
        return x;
    }

    /**
     * Gets the status of Button Y
     *
     * @return is button Y pressed
     */
    public boolean isYPressed() {
        return y;
    }

    /**
     * Gets the status of the guide button
     *
     * @return is the Guide button pressed
     */
    public boolean isGuidePressed() {
        return guide;
    }

    /**
     * Gets the status of the Start button
     *
     * @return is the Start button pressed
     */
    public boolean isStartPressed() {
        return start;
    }

    /**
     * Gets the status of the back button
     *
     * @return is the Back button pressed
     */
    public boolean isBackPressed() {
        return back;
    }

    /**
     * Gets the status of the Left Bumper (on the Gamepad)
     *
     * @return is the Left Bumper pressed
     */
    public boolean isLeftBumperPressed() {
        return leftBumper;
    }

    /**
     * Gets the status of the Right Bumper (on the Gamepad)
     *
     * @return is the Right Bumper pressed
     */
    public boolean isRightBumperPressed() {
        return rightBumper;
    }

    /**
     * Gets the value of the Left Trigger (no idea what this is; we are just exposing it)
     *
     * @return the value of the Left Trigger
     */
    public float getLeftTrigger() {
        return leftTrigger;
    }

    /**
     * Gets the value of the Right Trigger (again no idea what the value means; we are just exposing it)
     *
     * @return the value of the Right Trigger
     */
    public float getRightTrigger() {
        return rightTrigger;
    }

    /**
     * The time the joystick was update (in milliseconds) from the FTC SDK
     *
     * @return the time in milliseconds of last update
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the custom value of <code>{@link JoystickScaler#userDefinedRight(RobotContext, ExtensibleGamepad)}</code>
     *
     * @return the value that the code returned
     */
    public int getUserDefinedRight() {
        return userDefinedRight;
    }

    /**
     * Gets the custom value of <code>{@link JoystickScaler#userDefinedLeft(RobotContext, ExtensibleGamepad)}</code>
     *
     * @return the value that the code returned
     */
    public int getUserDefinedLeft() {
        return userDefinedLeft;
    }

    /**
     * Gets the left joystick
     *
     * @return the left <code>
     * {@link ExtensibleGamepad.Joystick}
     * </code> of this controller
     */
    public Joystick getLeftJoystick() {
        return leftJoystick;
    }

    /**
     * Gets the right joystick
     *
     * @return the right <code>Joystick</code> of this controller
     */
    public Joystick getRightJoystick() {
        return rightJoystick;
    }

    /**
     * Gets the left joystick scaler
     *
     * @return <code>{@link JoystickScaler}</code> of the left <code>Joystick</code>
     */
    public JoystickScaler getLeftScaler() {
        return leftScaler;
    }

    /**
     * Gets the right joystick scaler
     *
     * @return {@code JoystickScaler} of the right <code>Joystick</code>
     */
    public JoystickScaler getRightScaler() {
        return rightScaler;
    }

    /**
     * Gets the D-Pad of this controller
     *
     * @return {@link org.ftccommunity.networkedopmode.robot.ExtensibleGamepad.Dpad} of this controller
     */
    public Dpad getDpad() {
        return dpad;
    }


    /**
     * The Joystick for use in {@link ExtensibleGamepad}. This is an object representative of the data
     * present in the joysticks, and the data that the FTC SDK can give
     *
     * @author David Sargent
     * @since 0.1
     */
    public class Joystick {
        private double x;
        private double y;
        private boolean pressed;

        /**
         * Update the joystick to match the parameters given
         *
         * @param X         the value of X coordinates, should be between -1 and 1
         * @param Y         the value of Y coordinates, should be between -1 and 1
         * @param isPressed is the Joystick being pressed
         */
        void update(double X, double Y, boolean isPressed) {
            x = X;
            y = Y;
            pressed = isPressed;
        }

        /**
         * Gets the X (in Cartesian) of the Joystick. This should range between -1 and 1, but it is
         * not guaranteed to be so.
         *
         * @return the Cartesian X of the Joystick
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the Y (in Cartesian) of the Joystick. This should range between -1 and 1, but it is
         * not guaranteed to be so.
         *
         * @return the Cartesian Y-coordinate of the Joystick
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the button within the Joystick
         *
         * @return is the Joystick button pressed
         */
        public boolean isPressed() {
            return pressed;
        }
    }

    public class Dpad {
        private boolean up;
        private boolean down;
        private boolean right;
        private boolean left;

        /**
         * Updates the D-Pad based on the Gamepad's current status
         *
         * @param upPressed    is the Up button pressed
         * @param downPressed  is the Down button pressed
         * @param rightPressed is the right button pressed
         * @param leftPressed  is the left button pressed
         */
        void update(boolean upPressed, boolean downPressed,
                    boolean rightPressed, boolean leftPressed) {
            up = upPressed;
            down = downPressed;
            right = rightPressed;
            left = leftPressed;
        }

        /**
         * Gets the status of the D-Pad Up button
         *
         * @return is the Dpad Up button pressed
         */
        public boolean isUpPressed() {
            return up;
        }

        /**
         * Gets the status of the D-Pad Down button
         *
         * @return is the D-Pad Down button pressed
         */
        public boolean isDownPressed() {
            return down;
        }

        /**
         * Gets the status of the D-Pad Right button
         *
         * @return is the D-Pad Right pressed
         */
        public boolean isRightPressed() {
            return right;
        }

        /**
         * Gets the status of the D-Pad Left button
         *
         * @return is the D-Pad Left pressed
         */
        public boolean isLeftPressed() {
            return left;
        }
    }

    public class PlainJoystickScaler implements JoystickScaler {
        @Override
        public double scaleX(ExtensibleGamepad gamepad, double x) {
            return x;
        }

        @Override
        public double scaleY(ExtensibleGamepad gamepad, double y) {
            return y;
        }

        @Override
        public int userDefinedLeft(RobotContext ctx, ExtensibleGamepad gamepad) {
            return 0;
        }

        @Override
        public int userDefinedRight(RobotContext ctx, ExtensibleGamepad gamepad) {
            return 0;
        }
    }
}
