package org.usfirst.frc.team1444.robot.controlling.input;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team1444.robot.controlling.RobotController;

public class LogitechExtremeJoystickInput implements JoystickInput{

	private Joystick stick;

	public LogitechExtremeJoystickInput(int port){
		this.stick = new Joystick(port);
	}


	@Override
	public double joystickX() {
		return stick.getRawAxis(0);
	}

	@Override
	public double joystickY() {
		return -stick.getRawAxis(1);
	}

	@Override
	public double rotation() {
		return stick.getRawAxis(2);
	}

	@Override
	public double slider() {
		return -stick.getRawAxis(3);
	}

	@Override
	public int pov() {
		return RobotController.calculatePov(stick.getPOV());
	}

	@Override
	public boolean trigger() {
		return stick.getRawButton(1);
	}

	@Override
	public boolean thumbButton() {
		return stick.getRawButton(2);
	}

	// region thumb buttons
	@Override
	public boolean thumbTopLeft() {
		return stick.getRawButton(5);
	}

	@Override
	public boolean thumbTopRight() {
		return stick.getRawButton(6);
	}

	@Override
	public boolean thumbBottomLeft() {
		return stick.getRawButton(3);
	}

	@Override
	public boolean thumbBottomRight() {
		return stick.getRawButton(4);
	}
	// endregion

	// region grid
	@Override
	public boolean gridTopLeft() {
		return stick.getRawButton(7);
	}

	@Override
	public boolean gridTopRight() {
		return stick.getRawButton(8);
	}

	@Override
	public boolean gridMiddleLeft() {
		return stick.getRawButton(9);
	}

	@Override
	public boolean gridMiddleRight() {
		return stick.getRawButton(10);
	}

	@Override
	public boolean gridBottomLeft() {
		return stick.getRawButton(11);
	}

	@Override
	public boolean gridBottomRight() {
		return stick.getRawButton(12);
	}
	// endregion
}
