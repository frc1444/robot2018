package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Constants;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;

public class SwerveController implements RobotController {
	private static final Double DEFAULT_DIRECTION = 90.0; // the default direction to go to when joystick isn't touched

	private ControllerInput controller;

	public SwerveController(ControllerInput controller){
		this.controller = controller;
	}

	@Override
	public void update(Robot robot) {
		//this.drive(robot.getDrive());
		this.drive(robot.getDrive(), 0);
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

		// ========== Calculate speed ==========
		boolean isFineMovement = isFineMovement(); // are we going to move really slow?
		final double powAmount = isFineMovement ? 1 : 2; // if fine, make acceleration linear

		double right = rightTrigger(); // forward speed (will be added to backwards)
		double left = leftTrigger(); // backwards speed
		double speed = Math.pow(right, powAmount) - Math.pow(left, powAmount);
		if(isFineMovement){
			speed *= Constants.FineScaleAmount;
		}


		// ========== Calculate direction of wheels ==========
		// Direction is determined by the vector produced by the left joystick
		double x = leftStickX(); // these values don't have a deadband applied yet
		double y = leftStickY();

		Double direction = DEFAULT_DIRECTION;
		if(shouldKeepPosition()){
			direction = null; // Do we want to lock the position that is currently requested
		} else if (Math.hypot(x, y) > Constants.DirectionDeadband) {
			direction = Math.toDegrees(Math.atan2(y, x)); // even if negative, fixed at lower level
		}


		// Rotation rate is based fully on right joystick
		double turnAmount = rightStickHorizontal();

		// Update the drive
		drive.update(speed, direction, turnAmount);  // also, the values are debugged in here
	}

	private void drive(SwerveDrive drive, double gyro)
	{		
		// TODO Add Scaling
		double STR = leftStickX();
		double FWD = leftStickY();	
		
		if (Math.hypot(STR, FWD) < Constants.DirectionDeadband)
		{
			FWD = 0;
			STR = 0;
		}
		
		// Apply gyro angle to gain field-centric command
		// TODO Test this!
		if (false)
		{
			double gyroR = Math.toRadians(gyro);
			double temp = FWD * Math.cos(gyroR) + STR * Math.sin(gyroR);
			STR = -FWD * Math.sin(gyroR) + STR * Math.cos(gyroR);
			FWD = temp;
		}
		
		double ROT = 0.5;
		
		// Run in "moon" mode
		if (rightBumper()) {
			STR = -rightStickHorizontal() * ROT;
			// TODO Add radius
		}
		
		// Run in "rotary mode"
//		else if (leftBumper()) {
//			FWD = rightStickHorizontal() * ROT;
//			// TODO Add radius
//		}
		
		else {
			ROT = rightStickHorizontal();
		}
		
		// TODO Use trigger or no?
		double right = rightTrigger(); // forward speed (will be added to backwards)
		double left = leftTrigger(); // backwards speed
		double speed = Math.pow(right, 2) - Math.pow(left, 2);
		
		drive.vectorControl(FWD, STR, ROT, speed, gyro);
	
		if (controller.leftBumper())
		{
			drive.switchToQuad();
		}
		
	}
	
	// simple methods to get values for controller. Each should use this.controller
	private double rightTrigger() {
		double value = controller.rightTrigger();

		if (Math.abs(value) < Constants.TriggerDeadband) {
			value = 0;
		}

		return value;
	}
	private double leftTrigger() {
		double value = controller.leftTrigger();

		if (Math.abs(value) < Constants.TriggerDeadband) {
			value = 0;
		}

		return value;
	}

	// Even though these methods don't use a deadband, we'll still keep them to keep our nice abstractions
	private double leftStickX() {
		double value = controller.leftStickX();
		if (Math.abs(value) < Constants.DirectionDeadband)
		{
			value = 0;
		}
		return value;
	}
	private double leftStickY() {
		double value = controller.leftStickY();
		if (Math.abs(value) < Constants.DirectionDeadband)
		{
			value = 0;
		}
		return value;
	}

	private double rightStickHorizontal() {
		double value = controller.rightStickX();

		if (Math.abs(value) < Constants.RotationRateDeadband) {
			value = 0;
		}

		return value;
	}

	private boolean isFineMovement(){
		return controller.leftBumper();
	}
	
	private boolean leftBumper() {
		return controller.leftBumper();
	}
	
	private boolean rightBumper() {
		return controller.rightBumper();
	}

	/**
	 * @return whether or not the direction should be locked.
	 */
	private boolean shouldKeepPosition(){
		return controller.rightThumbBottom();
	}
}
