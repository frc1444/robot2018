package org.usfirst.frc.team1444.robot.controlling;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.Constants;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;

import java.awt.geom.Point2D;

public class SwerveController implements RobotController {
	private static final Double DEFAULT_DIRECTION = 90.0; // the default direction to go to when joystick isn't touched
	private static final Point2D ZERO = new Point2D.Double(0, 0);

	private static final String VECTOR_CONTROL = "Vector Control";
	private static final String POINT_CONTROL = "Point Control";
	private static SendableChooser<String> controlChooser;

	private ControllerInput controller;

	public SwerveController(ControllerInput controller){
		this.controller = controller;

		initControlChooser();
	}
	private static void initControlChooser(){
		if(controlChooser != null){
			return;
		}
		controlChooser = new SendableChooser<>();
		controlChooser.addDefault(VECTOR_CONTROL, VECTOR_CONTROL);
		controlChooser.addObject(POINT_CONTROL, POINT_CONTROL);
		SmartDashboard.putData("Control Type", controlChooser);
	}

	@Override
	public void update(Robot robot, double gyro) {
		SwerveDrive swerveDrive = robot.getDrive();
		//this.drive(robot.getDrive(), gyro);
		this.onDrive(swerveDrive);
		String mode = controlChooser.getSelected();
		switch (mode){
			case VECTOR_CONTROL:
//				System.out.println("Using vector");
				this.vectorDrive(swerveDrive, gyro);
				break;
			case POINT_CONTROL:
//				System.out.println("Using point");
				this.pointDrive(swerveDrive, gyro);
				break;
			default:
				throw new RuntimeException("Unknown control mode: " + mode);
		}
	}

	/** Will be called when using either driving mode */
	private void onDrive(SwerveDrive drive){
		if (controller.rightThumbLeft())
		{
			drive.switchToQuad();
		}

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
	private void pointDrive(SwerveDrive drive, double gyro) {

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
		// Direction is determined by the vector produced by the left joystick TODO this comment is wrong
		double x = leftStickX(); // these values don't have a deadband applied yet
		double y = leftStickY();

		Double direction = DEFAULT_DIRECTION;
		if(shouldKeepPosition()){
			direction = null; // Do we want to lock the position that is currently requested
		} else if (Math.hypot(x, y) > Constants.DirectionDeadband) {
			direction = Math.toDegrees(Math.atan2(y, x)); // even if negative, fixed at lower level
		}
		if(direction != null) {
			direction += gyro;
		}


		// Rotation rate is based fully on right joystick
		double turnAmount = rightStickHorizontal();
		SmartDashboard.putNumber("turnAmount", turnAmount);

		Point2D centerWhileStill;
		int pov = controller.dPad();
		SmartDashboard.putNumber("pov:", pov);
		if(pov != -1) {
			Point2D point1, point2 = null; // point2 may not always be used

			// this switch statement is used to get the most accurate results event if points are changed in a module
			switch(pov / 45){
				case 0:
					point1 = drive.getFrontRight().getLocation();
					point2 = drive.getRearRight().getLocation();
					break;
				case 1:
					point1 = drive.getFrontRight().getLocation();
					break;
				case 2: // 90 degrees
					point1 = drive.getFrontLeft().getLocation();
					point2 = drive.getFrontRight().getLocation();
					break;
				case 3:
					point1 = drive.getFrontLeft().getLocation();
					break;
				case 4: // 180 (left)
					point1 = drive.getFrontLeft().getLocation();
					point2 = drive.getRearLeft().getLocation();
					break;
				case 5:
					point1 = drive.getRearLeft().getLocation();
					break;
				case 6:
					point1 = drive.getRearLeft().getLocation();
					point2 = drive.getRearRight().getLocation();
					break;
				case 7:
					point1 = drive.getRearRight().getLocation();
					break;
				default:
					throw new RuntimeException("Unexpected pov number: " + pov + " make sure controller class is correct");
			}
			if(point2 == null){
				centerWhileStill = point1;
			} else {
				centerWhileStill = new Point2D.Double((point1.getX() + point2.getX()) / 2,
						(point1.getY() + point2.getY()) / 2);
			}
		} else {
			centerWhileStill = ZERO;
		}


		// Update the drive
		drive.update(speed, direction, turnAmount, centerWhileStill);
	}

	private void vectorDrive(SwerveDrive drive, double gyro)
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
		double gyroR = Math.toRadians(gyro);
		double temp = FWD * Math.cos(gyroR) + STR * Math.sin(gyroR);
		STR = -FWD * Math.sin(gyroR) + STR * Math.cos(gyroR);
		FWD = temp;
		
		double ROT = 0.5;
		
		// Run in "moon" mode
		if (rightBumper()) {
			STR = -rightStickHorizontal() * ROT;
			// TODO Add radius
		}
		
		// Run in "rotary mode"
		else if (leftBumper()) {
			FWD = rightStickHorizontal() * ROT;
			// TODO Add radius
		}
		
		else {
			ROT = rightStickHorizontal();
		}
		
		// TODO Use trigger or no?
		double right = rightTrigger(); // forward speed (will be added to backwards)
		double left = leftTrigger(); // backwards speed
		double speed = Math.pow(right, 2) - Math.pow(left, 2);
		
		drive.vectorControl(FWD, STR, ROT, speed, gyro);

		
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
//		if (Math.abs(value) < Constants.DirectionDeadband)
//		{
//			value = 0;
//		}
		return value;
	}
	private double leftStickY() {
		double value = controller.leftStickY();
//		if (Math.abs(value) < Constants.DirectionDeadband)
//		{
//			value = 0;
//		}
		return value;
	}

	private double rightStickHorizontal() {
		double value = controller.rightStickX();

		if (Math.abs(value) < Constants.RotationRateDeadband) {
			value = 0;
		}

		return value;
	}
	
	private boolean rightThumbLeft() {
		return controller.rightThumbLeft();
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
