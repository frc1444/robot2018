package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;


// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {
	private static final double MAX_TURN_MAGNITUDE = 100; // in inches. Used in Josh's method (regularDrive())


	private final SwerveModule[] moduleArray;
	private double rotation;

	private final Point2D origin;
	
	private double cosA;			// Sine and cosine used in swerve equations - constants based on robot dimensions
	private double sinA;

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
	                   double wheelBase, double trackWidth) {

		double x = trackWidth / 2;
		double y = wheelBase / 2;
		moduleArray = new SwerveModule[]{
				new SwerveModule(flDrive, flSteer, drivePid, steerPid, new Point2D.Double(-x, y), 0, flOffset),
				new SwerveModule(frDrive, frSteer, drivePid, steerPid, new Point2D.Double(x, y), 1, frOffset),
				new SwerveModule(rlDrive, rlSteer, drivePid, steerPid, new Point2D.Double(-x,-y), 2, rlOffset),
				new SwerveModule(rrDrive, rrSteer, drivePid, steerPid, new Point2D.Double(x, -y), 3, rrOffset)
		};
		// note, changing IDs will break code in regularDrive below

		this.origin = new Point2D.Double(0, 0);
		
		// Calculate sine and cosine for swerve equations from robot dimensions
		double robotDiagonal = Math.hypot(wheelBase, trackWidth);
		this.cosA = wheelBase / robotDiagonal;
		this.sinA = trackWidth / robotDiagonal;
		
	}

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

	/**
	 * 
	 * @param speed Desired speed of the motors (-1 to 1) that will eventually be converted to ft/s
	 * @param direction Desired direction of robot in degrees or null if you don't want to change direction. Even if
	 *                  this isn't between 0 and 360, it will take the most efficient path.
	 * @param turnAmount a number from -1 to 1 representing how much you want to turn.
	 * @param centerWhileStill The center of rotation. This is only applied when speed is 0 and is usually calculated
	 *                         using the d-pad and the locations of the modules
	 */
	public void update(double speed, Double direction, double turnAmount, Point2D centerWhileStill) {

		if(direction != null){
			this.rotation = direction;
		}
		regularDrive(speed, turnAmount, centerWhileStill);

		for(SwerveModule module : this.getModules()){
			module.debug();  // Since I moved all the frontLeft etc variables, this for loop will do the trick.
		}
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

	// region Josh's Method
	/**
	 * Uses the provided parameters and this.rotation to set the motor speeds
	 * @param speed A value from -1 to 1 representing the percent speed
	 * @param turnAmount A value from -1 to 1 representing how much the robot should rotate. -1 is full left
	 * @param centerWhileStill The point that will be used to rotate around only if speed is 0
	 */
	private void regularDrive(final double speed, final double turnAmount, Point2D centerWhileStill){
		if(turnAmount == 0){
			this.straightSubDrive(speed); // the only place where straightSubDrive is called
			return;
		} else if(speed == 0){
			this.rotateAroundSubDrive(turnAmount / 3d, centerWhileStill, false);
			return;
		}
		final double rotationInRadians = Math.toRadians(this.rotation);

		Point2D centerOfRotation;

		// Calculate centerOfRotation
		double centerMagnitude = MAX_TURN_MAGNITUDE * (1 - Math.abs(turnAmount)) * -Math.signum(turnAmount);
		centerOfRotation = new Point2D.Double(0, centerMagnitude);
		AffineTransform centerTransform = new AffineTransform(); // let nice little object do rotating for us
		centerTransform.rotate(rotationInRadians);
		centerTransform.transform(centerOfRotation, centerOfRotation); // rotate centerOfRotation

		SmartDashboard.putString("centerOfRotation", pointToString(centerOfRotation));

		this.rotateAroundSubDrive(speed, centerOfRotation, turnAmount < 0);
	}

	/**
	 * Uses the rotateModuleSub method and bases rotation around the point centerOfRotation
	 * <p>
	 * Note: isLeft is used to make sure that when changing direction, wheels won't do a 180
	 *
	 * @param speed The desired speed that will be scaled depending on where centerOfRotation is
	 * @param centerOfRotation The point that you want to rotate around relative to origin (the middle of robot)
	 * @param isLeft true if the robot is turning left, false otherwise
	 */
	private void rotateAroundSubDrive(double speed, Point2D centerOfRotation, boolean isLeft){
		assert speed != 0 && centerOfRotation != null;

		final double absSpeed = Math.abs(speed); // should be used below instead of typing Math.abs(speed)
		final double centerDistanceToOrigin = centerOfRotation.distance(this.origin);

		double[] absSpeedArray = new double[this.moduleArray.length];
		double maxSpeed = 1;  // can be divided by to scale speeds down if some speeds are > 1.0 (set in for loop)

		// For loop to calculate speeds and set rotation angles
		for(SwerveModule module : this.moduleArray){
			Point2D location = module.getLocation();

			// Rotate module
			this.rotateModuleSub(module, centerOfRotation, isLeft);

			// Calc speed
			double newAbsSpeed = absSpeed;
			if(centerDistanceToOrigin != 0){
				newAbsSpeed *= location.distance(centerOfRotation) / centerDistanceToOrigin;
			}
			if(newAbsSpeed > maxSpeed) maxSpeed = newAbsSpeed;

			absSpeedArray[module.getID()] = newAbsSpeed;
		}
		// Set calculated speeds
		for(SwerveModule module : moduleArray){
			int sig = (int) Math.signum(speed);

			double moduleSpeed = sig * absSpeedArray[module.getID()];
			moduleSpeed /= maxSpeed;
			module.setSpeed(moduleSpeed);
		}
	}
	/**
	 * Used by the regularDrive method
	 *
	 * @param module The module to rotate
	 * @param centerOfRotation the that the module should rotate around
	 * @param isLeft true if the robot is turning left, false otherwise
	 */
	private void rotateModuleSub(SwerveModule module, Point2D centerOfRotation, boolean isLeft){
		Point2D location = module.getLocation();
		// use slope of line with points location and centerOfRotation to get angle
		double angle = Math.atan2(location.getY() - centerOfRotation.getY(),
			location.getX() - centerOfRotation.getX());
		angle = Math.toDegrees(angle);

		// so the wheels don't rotate 180 when turnAmount switches from + or -
		if(isLeft){
			angle += 90;
		} else {
			angle -= 90;
		}

		module.setPosition(angle);
	}
	/**
	 * Used by the regularDrive method
	 *
	 * Drives the robot in a straight line with the desired speed
	 * @param speed desired speed
	 */
	private void straightSubDrive(double speed){
		this.rotateAll(this.rotation);
		this.setAllSpeeds(speed);
	}
	/** Simple method to convert a Point2D to a String usually used for debugging purposes */
	private static String pointToString(Point2D point){
		return String.format("(%s,%s)", point.getX(), point.getY());
	}
	// endregion
	
	public void vectorControl(double FWD, double STR, double ROT, double speed, double gyro)
	{		
	
		// Calculate the necessary components separately to make the math cleaner		
		double A = FWD - ROT * this.sinA;
		double B = FWD + ROT * this.sinA;
		double C = STR - ROT * this.cosA;
		double D = STR + ROT * this.cosA;
		
		// Calculate the linear speeds for each wheel
		double frSpeed = Math.hypot(A, D);
		double flSpeed = Math.hypot(B, D);
		double rlSpeed = Math.hypot(B, C);
		double rrSpeed = Math.hypot(A, C);
		
		// Scale the outputs if the max is over 1
		double max = flSpeed;
		
		if (frSpeed > max)
		{
			max = frSpeed;
		}
		
		if (rlSpeed > max)
		{
			max = rlSpeed;
		}
		
		if (rrSpeed > max)
		{
			max = rrSpeed;
		}
		
		if (max > 1)
		{
			flSpeed /= max;
			frSpeed /= max;
			rlSpeed /= max;
			rrSpeed /= max;
		}
		
		flSpeed *= speed;
		frSpeed *= speed;
		rlSpeed *= speed;
		rrSpeed *= speed;
		
		// Calculate the angle for each wheel, make sure angles are 0-360		
		double frAngle = Math.toDegrees(Math.atan2(A, D));
		double flAngle = Math.toDegrees(Math.atan2(B, D));
		double rlAngle = Math.toDegrees(Math.atan2(B, C));
		double rrAngle = Math.toDegrees(Math.atan2(A, C));

		/* We don't really need these lines because we already do this at a lower level.
		If you think we do need these, remove comment, otherwise remove entirely.
		if (frAngle < 0)
		{
			frAngle += 360;
		}
		
		if (flAngle < 0)
		{
			flAngle += 360;
		}
		
		if (rlAngle < 0)
		{
			rlAngle += 360;
		}
		
		if (rrAngle < 0)
		{
			rrAngle += 360;
		}
		*/
		
		// Update the drive modules
		moduleArray[0].update(flSpeed, flAngle);
		moduleArray[1].update(frSpeed, frAngle);
		moduleArray[2].update(rlSpeed, rlAngle);
		moduleArray[3].update(rrSpeed, rrAngle);
		
		for (int i = 0; i < moduleArray.length; ++i) {
			moduleArray[i].debug();
		}
		 
	}
	
	public void switchToQuad() {
		for (int i = 0; i < moduleArray.length; ++i) {
			moduleArray[i].switchToQuad();
		}
	}
	
	public void switchToAnalog() {
		for (int i = 0; i < moduleArray.length; ++i) {
			moduleArray[i].switchToAnalog();
		}
	}

}
