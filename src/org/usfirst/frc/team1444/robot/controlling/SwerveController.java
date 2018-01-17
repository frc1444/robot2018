package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;

public class SwerveController implements RobotController {

	// TODO: find proper deadbands // Also decide if we want to put these values in Constants

	// Deadband for main drive velocity input
	private static final double kTriggerDeadband = 0.05;
	// Deadband for direction input
	private static final double kDirectionDeadband = 0.05;
	// Deadband for rotation rate input
	private static final double kRotationRateDeadband = 0.1;


	private ControllerInput controller;


	public SwerveController(ControllerInput controller){
		this.controller = controller;
	}

	@Override
	public void update(Robot robot) {
		this.drive(robot.getDrive());
	}

	/**
	 * Controls: left trigger - forwards, right trigger - backwards
	 * lstick - direction of all wheels, rstick(x axis only) - turn robot
	 * <p>
	 * If the speed of the robot is not 0 and the robot is turning because of rstick -> if positive, set back wheels
	 * straight, if negative, set front wheels straight. Each time, it won't be completely straight and it resets
	 * rotation to 90
	 *
	 * @param drive the SwerveDrive object
	 */
	private void drive(SwerveDrive drive) {
		double powAmount = 3;

		// Linear velocity of robot is determined by the combination of the left and right triggers		
		double velocity = Math.pow(rightTrigger(), powAmount) - Math.pow(leftTrigger(), powAmount);

		// Direction is determined by the vector produced by the left joystick
		double x = leftStickHorizontal();
		double y = leftStickVertical();

		// If there is no valid input from the left joystick, just steer ahead which is defined as 90 degrees
		Double direction = null;  // TODO decide if we want this to be null or 90 degrees.

		// If controller input is valid, calculate the angle of the joystick
		if (Math.hypot(x, y) > kDirectionDeadband) {
			direction = Math.toDegrees(Math.atan2(y, x));
			
			if (direction < 0)
			{
				direction += 360;
			}
		}

//		System.out.println("X,Y,D" + x + "," + y + "," + direction); // if uncomment, cast direction to int
		// Rotation rate is based fully on right joystick
		double turnAmount = rightStickHorizontal();
		System.out.println("turnAmount: " + (Math.round(turnAmount * 100) / 100) +
				"direction: " + (direction != null ? Math.round(direction) + "" : "null"));

		// Update the drive
		drive.update(velocity, direction, turnAmount);
	}

	// simple methods to get values for controller. Each should use this.controller
	private double rightTrigger() {
		double value = controller.rightTrigger();

		if (Math.abs(value) < kTriggerDeadband) {
			value = 0;
		}

		return value;
	}

	private double leftTrigger() {
		double value = controller.leftTrigger();

		if (Math.abs(value) < kTriggerDeadband) {
			value = 0;
		}

		return value;
	}

	private double leftStickHorizontal() {
		double value = controller.leftStickHorizontal();

		if (Math.abs(value) < kDirectionDeadband) {
			value = 0;
		}

		return value;
	}

	private double leftStickVertical() {
		double value = controller.leftStickVertical();

		if (Math.abs(value) < kDirectionDeadband) {
			value = 0;
		}

		return value;
	}

	private double rightStickHorizontal() {
		double value = controller.rightStickHorizontal();

		if (Math.abs(value) < kRotationRateDeadband) {
			value = 0;
		}

		return 0;
	}
}
