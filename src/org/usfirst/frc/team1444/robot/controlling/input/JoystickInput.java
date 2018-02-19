package org.usfirst.frc.team1444.robot.controlling.input;

public interface JoystickInput {

	double joystickX();
	double joystickY();

	double rotation();

	/**
	 * @return a number from -1 to 1 where positive is up on the slider (closer to the plus)
	 */
	double slider();

	int pov();

	boolean trigger();
	boolean thumbButton();

	boolean thumbTopLeft();
	boolean thumbTopRight();
	boolean thumbBottomLeft();
	boolean thumbBottomRight();

	boolean gridTopLeft();
	boolean gridTopRight();
	boolean gridMiddleLeft();
	boolean gridMiddleRight();
	boolean gridBottomLeft();
	boolean gridBottomRight();


}
