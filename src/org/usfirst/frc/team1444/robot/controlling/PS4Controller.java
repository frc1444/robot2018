package org.usfirst.frc.team1444.robot.controlling;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A ControllerInput class made just for the PS4 Controller
 * Thanks: https://forum.unity.com/threads/playstation-4-controller-mapping.368549/
 */
public class PS4Controller implements ControllerInput {
	private Joystick stick;
	
	// Axes definitions
	private static final int kLeftStickVertical = 1;
	private static final int kLeftStickHorizontal = 0;
	private static final int kRightStickVertical = 5;
	private static final int kRightStickHorizontal = 2;
	private static final int kLeftTrigger = 3;
	private static final int kRightTrigger = 4;
	
	// Button definitions
	private static final int kCross = 2;
	private static final int kTriangle = 4;
	private static final int kSquare = 1;
	private static final int kCircle = 3;

	/**
	 * Creates a PS4 Controller object
	 *
	 * @param port The port on the Driver Station that the joystick is plugged into.
	 */
	public PS4Controller(int port){
		this.stick = new Joystick(port);
	}

	@Override
	public boolean rightThumbBottom() {
		return stick.getRawButton(kCross);
	}

	@Override
	public boolean rightThumbTop() {
		return stick.getRawButton(kTriangle);
	}

	@Override
	public boolean rightThumbLeft() {
		return stick.getRawButton(kSquare);
	}

	@Override
	public boolean rightThumbRight() {
		return stick.getRawButton(kCircle);
	}

	@Override
	public boolean leftBumper() {
		return stick.getRawButton(5);
	}

	@Override
	public boolean rightBumper() {
		return stick.getRawButton(6);
	}

	@Override
	public int dPad() {
		return stick.getPOV();
	}

	@Override
	public double leftStickVertical() {
		return stick.getRawAxis(kLeftStickVertical);
	}

	@Override
	public double leftStickHorizontal() {		
		return stick.getRawAxis(kLeftStickHorizontal);
	}

	@Override
	public double rightStickVertical() {
		return stick.getRawAxis(kRightStickVertical);
	}

	@Override
	public double rightStickHorizontal() {
		return stick.getRawAxis(kRightStickHorizontal);
	}

	@Override
	public double leftTrigger() {
		return stick.getRawAxis(kLeftTrigger);
	}

	@Override
	public double rightTrigger() {
		return stick.getRawAxis(kRightTrigger);
	}
}
