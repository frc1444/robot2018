package org.usfirst.frc.team1444.robot.controlling.input;

/**
 * Should be used in different RobotController subclasses to check when a button is pressed
 */
public interface ControllerInput {

	// 'thumb' buttons (triangle, square, circle, X) or (A, B, X, Y on other controllers)
	boolean rightThumbBottom();
	boolean rightThumbTop();
	boolean rightThumbLeft();
	boolean rightThumbRight();

	boolean leftBumper();
	boolean rightBumper();

	/**
	 * @return always a number between 0 and 360
	 */
	int dPad();

	double leftStickY();
	double leftStickX();

	double rightStickY();
	double rightStickX();

	/**
	 * @return A number from 0 to 1
	 */
	double leftTrigger();
	/**
	 * @return A number from 0 to 1
	 */
	double rightTrigger();

}
