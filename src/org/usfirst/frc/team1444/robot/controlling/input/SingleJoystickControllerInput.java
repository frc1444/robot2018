package org.usfirst.frc.team1444.robot.controlling.input;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.input.ControllerInput;

/**
 * @deprecated this class tries to map a single joystick to a ps4 or xbox style controller. This is not recommended for use
 */
@Deprecated
public class SingleJoystickControllerInput implements ControllerInput {

	// Axis constants
	private static final int X_AXIS = 0;
	private static final int Y_AXIS = 1; // inverted in method below
	private static final int ROTATE_AXIS = 2; // z axis
	private static final int SLIDER_AXIS = 3;

	// Button constants
	private static final int TRIGGER = 1;
	private static final int THUMB_BUTTON = 2; // weird thumb button thingy on the left side you press with thumb


	private Joystick stick;

	public SingleJoystickControllerInput(int port){
		this.stick = new Joystick(port);
	}

	@Override
	public boolean rightThumbBottom() {
		return stick.getRawButton(THUMB_BUTTON);
	}

	@Override
	public boolean rightThumbTop() {
		return false;
	}

	@Override
	public boolean rightThumbLeft() {
		return false;
	}

	@Override
	public boolean rightThumbRight() {
		return false;
	}

	@Override
	public boolean leftBumper() {
		return stick.getRawButton(TRIGGER);
	}

	@Override
	public boolean rightBumper() {
		return stick.getRawButton(TRIGGER);
	}

	@Override
	public int dPad() {
		return RobotController.calculatePov(stick.getPOV());
	}

	@Override
	public double leftStickY() {
		return -stick.getRawAxis(Y_AXIS); // inverted
	}

	@Override
	public double leftStickX() {
		return stick.getRawAxis(X_AXIS);
	}

	@Override
	public double rightStickY() {
		return 0; // should we have this as stick.getRawAxis(SLIDER_AXIS); ?
	}

	@Override
	public double rightStickX() {
		return stick.getRawAxis(ROTATE_AXIS);
	}

	@Override
	public double leftTrigger() {
		return 0;
	}

	@Override
	public double rightTrigger() {
		double r = Math.hypot(this.leftStickX(), this.leftStickY());
		if(r > 1){
			r = 1;
		}
		return r;
	}
}
