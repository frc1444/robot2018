package org.usfirst.frc.team1444.robot.controlling;

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
