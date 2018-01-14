package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;

public class SwerveController implements RobotController {

	private ControllerInput controller;
	
	// TODO: find proper deadbands
	
	// Deadband for main drive velocity input
	private static final double kTriggerDeadband = 0.1;
	
	// Deadband for direction input
	private static final double kDirectionDeadband = 0.1;
	
	// Deadband for rotation rate input
	private static final double kRotationRateDeadband = 0.1;
	
	private double rotation = 0;

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
	 *
	 * If the speed of the robot is not 0 and the robot is turning because of rstick -> if positive, set back wheels
	 * straight, if negative, set front wheels straight. Each time, it won't be completely straight and it resets
	 * rotation to 90
	 *
	 * @param drive the SwerveDrive object
	 */
	private void drive(SwerveDrive drive) {
		
		// Linear velocity of robot is determined by the combination of the left and right triggers		
		double velocity = rightTrigger() - leftTrigger();
		
//		// Convert to ft/s // TODO do this at lower level
//		velocity *= maxLinearSpeed;
		
		// Direction is determined by the vector produced by the left joystick
		double x = leftStickVertical();
		double y = leftStickHorizontal();
		
		// If there is no valid input from the left joystick, just steer ahead which is defined as 90 degrees
		Double direction = 90.0;  // if we want, we can set this to null which won't change the direction
		
		// If controller input is valid, calculate the angle of the joystick
		if (x != 0 || y != 0) { 		
			direction = Math.toDegrees(Math.atan2(y, x));
		}
		
		// Rotation rate is based fully on right joystick
		double rotationRate = rightStickHorizontal();
		
//		// Convert to degrees/sec
//		rotationRate *= maxRotationRate;  // TODO do at lower level
		
		// Update the drive
		drive.update(velocity, direction, rotationRate);  // TODO decide if we want to pass null as a direction
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
