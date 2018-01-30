package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {
	private static final double ROTATE_MULTIPLIER = .4; // The speed that is applied while rotating while still
	private static final double MAX_TURN_MAGNITUDE = 100; // in inches


	private final SwerveModule[] moduleArray;
	private double rotation;

	private final Point2D origin;

	private double length, width;

	/**
	 * Initializes SwerveDrive and creates SwerveModules. Even though each parameter is a BaseMotorController,
	 * we can still pass it a TalonSRX
	 */
	public SwerveDrive(BaseMotorController flDrive, BaseMotorController flSteer,
	                   BaseMotorController frDrive, BaseMotorController frSteer,
	                   BaseMotorController rlDrive, BaseMotorController rlSteer,
	                   BaseMotorController rrDrive, BaseMotorController rrSteer,
	                   PidParameters drivePid, PidParameters steerPid,
	                   int flOffset, int frOffset, int rlOffset, int rrOffset,
	                   double length, double width) {

		moduleArray = new SwerveModule[]{
				new SwerveModule(flDrive, flSteer, drivePid, steerPid, new Point2D.Double(-width / 2, length / 2), 0, flOffset),
				new SwerveModule(frDrive, frSteer, drivePid, steerPid, new Point2D.Double(width / 2, length / 2), 1, frOffset),
				new SwerveModule(rlDrive, rlSteer, drivePid, steerPid, new Point2D.Double(-width / 2,-length / 2), 2, rlOffset),
				new SwerveModule(rrDrive, rrSteer, drivePid, steerPid, new Point2D.Double(width / 2, -length / 2), 3, rrOffset)
		};

		this.origin = new Point2D.Double(0, 0);
		this.length = length;
		this.width = width;

	}
	
	// TODO: Are these accessors really necessary - do we want other classes to have access to this array?
	// Yeah the point of this was so we could loop through all of them in SwerveController, but that code was moved here
	// If we want to, we can re-add frontLeft etc but we still need a module array so I figured we could just use
	// these accessors

	public SwerveModule getFrontLeft() {
		return moduleArray[0];
	}

	public SwerveModule getFrontRight() {
		return moduleArray[1];
	}

	public SwerveModule getRearLeft() { 
		return moduleArray[2];
	}

	public SwerveModule getRearRight() {
		return moduleArray[3];
	}

	public SwerveModule[] getModules() {
		return moduleArray;
	}

	public double getLength(){ return length; }
	public double getWidth(){ return width; }

	/**
	 * 
	 * @param speed Desired speed of the motors (-1 to 1) that will eventually be converted to ft/s
	 * @param direction Desired direction of robot in degrees or null if you don't want to change direction. Even if
	 *                  this isn't between 0 and 360, it will take the most efficient path.
	 * @param turnAmount a number from -1 to 1 representing how much you want to turn.
	 * @param centerWhileStill The center of rotation. This is only applied when speed is 0
	 */
	public void update(double speed, Double direction, double turnAmount, Point2D centerWhileStill) {
		// TODO, if we need/want to we could try to change turnAmount to degrees/s but that'd make this more complex

		if(direction != null){
			this.rotation = direction;
		}
//		SmartDashboard.putNumber("SwerveDrive speed", speed);
//		SmartDashboard.putNumber("SwerveDrive this.rotation", this.rotation);
		SmartDashboard.putNumber("SwerveDrive turnAmount", turnAmount);

		regularDrive(speed, turnAmount, centerWhileStill);

		for(SwerveModule module : this.getModules()){
			module.debug();  // Since I moved all the frontLeft etc variables, this for loop will do the trick.
			// If we don't need to debug this much, remove this loop later
		}
	}

	/**
	 * Uses the provided parameters and this.rotation to set the motor speeds
	 * @param speed A value from -1 to 1 representing the percent speed
	 * @param turnAmount A value from -1 to 1 representing how much the robot should rotate. -1 is full left
	 */
	private void regularDrive(final double speed, final double turnAmount, Point2D centerWhileStill){


		final double absSpeed = Math.abs(speed); // should be used below instead of typing Math.abs(speed)
		final double rotationInRadians = Math.toRadians(this.rotation);
		final SwerveModule[] modules = this.getModules();
		final int length = modules.length;

		double[] speeds = new double[length];  // array of speeds all with a positive sign or 0
		double maxSpeed = 1;  // can be divided by to scale speeds down if some speeds are > 1.0 (set in for loop)

		// Variables for angles
		Point2D centerOfRotation = null; // if we aren't turning, this will be null
		double centerDistanceToOrigin = 0; // should only be used if turnAmount != 0 or if centerOfRotation != null
		if(turnAmount != 0){
			if(speed == 0){
				centerOfRotation = centerWhileStill;
			} else {
				double centerMagnitude; // = (Math.abs(speed) * -5 * Math.signum(turnAmount));
				centerMagnitude = MAX_TURN_MAGNITUDE * (1 - Math.abs(turnAmount)) * -Math.signum(turnAmount);
				if (absSpeed < .1) {
					centerMagnitude *= (absSpeed / .1);
				}

				centerOfRotation = new Point2D.Double(0, centerMagnitude);
				AffineTransform centerTransform = new AffineTransform();
				centerTransform.rotate(rotationInRadians);
				centerOfRotation = centerTransform.transform(centerOfRotation, null);
			}
			centerDistanceToOrigin = centerOfRotation.distance(this.origin);

		}
		SmartDashboard.putString("centerOfRotation", centerOfRotation != null ? pointToString(centerOfRotation) : "null");


		// For loop to calculate speeds and set rotation angles
		for(int i = 0; i < length; i++){ // not a for each loop because we need to use i to set value in speeds array
			SwerveModule module = modules[i];

			double newAbsSpeed; // variable for speed, used below if else clause
			if(centerOfRotation != null) { // same thing as turnAmount != 0
				Point2D location = module.getLocation();

				// ========== Calculate speed  ==========
				newAbsSpeed = absSpeed * (location.distance(centerOfRotation) / centerDistanceToOrigin);
				if(newAbsSpeed == 0){
					newAbsSpeed = Math.abs(turnAmount);
				}


				// ========== Calculate angles ==========
				this.rotateModule(module, centerOfRotation, turnAmount < 0);

			} else { // if we aren't turning, then we can't use centerOfRotation because it's null
				newAbsSpeed = absSpeed;
				module.setPosition(this.rotation);
			}

			// Put speed in speeds array
			if(newAbsSpeed > maxSpeed){
				maxSpeed = newAbsSpeed;
			}
			speeds[i] = newAbsSpeed;

		}
		// For loop to set calculated speeds
		for(int i = 0; i < length; i++){
			SwerveModule module = modules[i];
			int sig = (int) Math.signum(speed);
			if(sig == 0) sig = 1;

			double moduleSpeed = sig * speeds[i];
			moduleSpeed /= maxSpeed;  // scale the speed down if the max speed is high. maxSpeed is 1 most of the time
			module.setSpeed(moduleSpeed);
//			SmartDashboard.putNumber("regularDrive module speed " + module.getID(), moduleSpeed);
		}
	}

	/**
	 * Used by the regular drive method
	 *
	 * @param module The module to rotate
	 * @param centerOfRotation the that the module should rotate around
	 * @param isLeft true if the robot is turning left, false otherwise
	 */
	private void rotateModule(SwerveModule module, Point2D centerOfRotation, boolean isLeft){
		Point2D location = module.getLocation();
		double angle = Math.atan2(location.getY() - centerOfRotation.getY(),
			location.getX() - centerOfRotation.getX());
		angle = Math.toDegrees(angle); // remember to convert lol

		if(isLeft){
			angle += 90;
		} else {
			angle -= 90;
		}

		module.setPosition(angle);
		SmartDashboard.putNumber("module " + module.getID() + " position", angle);
	}

	/**
	 * Called when the user wants to stay still and rotate the robot. The speed is not taken because it should be 0
	 *
	 * This may be the only place in the code where degrees/s might make sense. Although, I think if we did that,
	 * we should overload a method.
	 *
	 * @param turnAmount A number from -1 to 1 representing how much we should turn.
	 */
	private void rotateDrive(double turnAmount){

		SwerveModule frontLeft = this.getFrontLeft(),
				frontRight = this.getFrontRight(),
				rearLeft = this.getRearLeft(),
				rearRight = this.getRearRight();

		double targetFL = 45;
		double targetFR = 270 + 45; // -45
		double targetRL = 90 + 45;
		double targetRR = 180 + 45; // -(90 + 45)
		// the target rotations for all the modules

		// rotate all motors
		frontLeft.setPosition(targetFL);
		frontRight.setPosition(targetFR);
		rearLeft.setPosition(targetRL);
		rearRight.setPosition(targetRR);

		// set speeds of all motors
		this.setAllSpeeds(turnAmount * ROTATE_MULTIPLIER);
	}

	/**
	 * Sets the same speed to each SwerveModule
	 *
	 * @param speed A number between -1 and 1
	 */
	public void setAllSpeeds(double speed) {
		for (SwerveModule module : moduleArray) {
			module.setSpeed(speed);
		}
	}

	/**
	 * Rotates all the SwerveModules to position
	 *
	 * @param position the position to rotate to in degrees
	 */
	public void rotateAll(double position) {
		for(SwerveModule module : moduleArray){
			module.setPosition(position);
		}
	}

	/**
	 *
	 * @param pointA makes up the line AB that intersects BC
	 * @param pointB the point in which the angle will be returned
	 * @param pointC makes up the line BC that intersects AB
	 * @return The angle in degrees that the points make where pointA and pointC intersect at PointB
	 */
	private static double getAngleFromPoints(Point2D pointA, Point2D pointB, Point2D pointC){
		double b = pointA.distance(pointC);
		double a = pointB.distance(pointC);
		double c = pointB.distance(pointA);

		return Math.toDegrees(Math.acos(
				(b*b - a*a - c*c) / (-2 * a * c)
		));
	}
	private static String pointToString(Point2D point){
		return String.format("(%s,%s)", point.getX(), point.getY());
	}

}
