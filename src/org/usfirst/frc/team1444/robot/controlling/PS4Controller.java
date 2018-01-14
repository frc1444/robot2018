package org.usfirst.frc.team1444.robot.controlling;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A ControllerInput class made just for the PS4 Controller
 * Thanks: https://forum.unity.com/threads/playstation-4-controller-mapping.368549/
 */
public class PS4Controller implements ControllerInput{
	private Joystick stick;

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
		return stick.getRawButton(2);
	}

	@Override
	public boolean rightThumbTop() {
		return stick.getRawButton(4);
	}

	@Override
	public boolean rightThumbLeft() {
		return stick.getRawButton(1);
	}

	@Override
	public boolean rightThumbRight() {
		return stick.getRawButton(3);
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

	//@Override
	//public double dPadHorizontal() {
	//	return stick.getRawAxis(7);
	//}

	@Override
	public double leftStickVertical() {
		return stick.getRawAxis(1);
	}

	@Override
	public double leftStickHorizontal() {
		return stick.getRawAxis(0);
	}

	@Override
	public double rightStickVertical() {
		return stick.getRawAxis(5);
	}

	@Override
	public double rightStickHorizontal() {
		return stick.getRawAxis(2);
	}

	@Override
	public double leftTrigger() {
		return stick.getRawAxis(3);
	}

	@Override
	public double rightTrigger() {
		return stick.getRawAxis(4);
	}
}