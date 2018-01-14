package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {
	private static final double ROTATE_MULTIPLIER = .4; // The speed that is applied while rotating while still


	private final SwerveModule[] moduleArray;
	private double rotation;


	/**
	 * Initializes SwerveDrive and creates SwerveModules. Even though each parameter is a BaseMotorController,
	 * we can still pass it a TalonSRX
	 */
	public SwerveDrive(BaseMotorController flDrive, BaseMotorController flSteer,
	                   BaseMotorController frDrive, BaseMotorController frSteer,
	                   BaseMotorController rlDrive, BaseMotorController rlSteer,
	                   BaseMotorController rrDrive, BaseMotorController rrSteer) {

		moduleArray = new SwerveModule[]{
				new SwerveModule(flDrive, flSteer, -1, 1),
				new SwerveModule(frDrive, frSteer, 1, 1),
				new SwerveModule(rlDrive, rlSteer, -1, -1),
				new SwerveModule(rrDrive, rrSteer, 1, -1)
		};

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

	/**
	 * 
	 * @param speed Desired speed of the motors (-1 to 1) that will eventually be converted to ft/s
	 * @param direction Desired direction of robot in degrees or null if you don't want to change direction
	 * @param turnAmount a number from -1 to 1 representing how much you want to turn.
	 */
	public void update(double speed, Double direction, double turnAmount) {
		// TODO, if we need/want to we could try to change turnAmount to degrees/s but that'd make this more complex
		
		// TODO: Set individual module velocity and position based on inputs
		// TODO: lots of math
		SwerveModule frontLeft = this.getFrontLeft(),
				frontRight = this.getFrontRight(),
				rearLeft = this.getRearLeft(),
				rearRight = this.getRearRight();
		// calculate motor speeds

		// Calculate direction of Swerve wheels to allow agility
		if(direction != null){  // make sure that the person inputting really wants to change steer position.
			this.rotation = direction;
		}

		if(speed != 0 || turnAmount != 0){
			// if statement used for: if we want to go forward/backwards, or if we don't want to turn
			// basically, this code is run if the user probably doesn't want to turn while still

			this.rotateAll(this.rotation);  // since we aren't turning, we can turn to where we want to
			// both if statements, set the speed
			if(turnAmount == 0) {
				// I think, if we just use the else statement, it should do the same thing as this, but we'll keep this
				// just to be safe
				this.setAllSpeeds(speed);
			} else { // if the user wants to turn, then we will do it like a tank drive, adjust the speeds
				// complex algorithm to basically make a tank drive, but in whatever direction you want
				final double angle = Math.toRadians(-this.rotation);
				final SwerveModule[] modules = this.getModules();
				final int length = modules.length;

				double[] speeds = new double[length];  // array of speeds all with a positive sign or 0
				double maxSpeed = 1;  // can be divided by to scale speeds down if some speeds are > 1.0
				for(int i = 0; i < length; i++){
					SwerveModule module = modules[i];
					double x = module.getX();
					double y = module.getY();

//					double distanceFromCenter = Math.hypot(x, y); // distance from the center of the robot
					double newY = (x * Math.sin(angle)) + (y * Math.cos(angle)); // around values between -1 and 1
					newY *= -1;  // after multiplying, -1 means module on left, 1 means module on right
//					newY /= distanceFromCenter;  // I don't think we need this, but if uncommented, don't need maxSpeed
					// so if dividing my distanceFromCenter, fixes maxSpeed, remove it later

					double absSpeed = Math.abs(speed) - (newY * turnAmount);
					speeds[i] = absSpeed;
					if(absSpeed > maxSpeed){
						maxSpeed = absSpeed;
					}
				}
				for(int i = 0; i < length; i++){ // simple for loop to set speeds from variable 'speeds'
					SwerveModule module = modules[i];
					double moduleSpeed = Math.signum(speed) * speeds[i];
					module.setSpeedOpenLoop(moduleSpeed / maxSpeed);
				}
			}
		} else{  // only fires if the user definitely wants to turn while still
			float targetFL;
			float targetFR;
			float targetRL;
			float targetRR;

			targetFL = 45;
			targetFR = 270 + 45; // -45
			targetRL = 90 + 45;
			targetRR = 180 + 45; // -(90 + 45)
			// the target rotations for all the modules

			// rotate all motors
			frontLeft.setPosition(targetFL);
			frontRight.setPosition(targetFR);
			rearLeft.setPosition(targetRL);
			rearRight.setPosition(targetRR);

			// set speeds of all motors
			this.setAllSpeeds(turnAmount * ROTATE_MULTIPLIER);

		}
	}

	/**
	 * Sets the same speed to each SwerveModule
	 *
	 * @param speed A number between -1 and 1
	 */
	public void setAllSpeeds(double speed) {
		for (SwerveModule module : moduleArray) {
			module.setSpeedOpenLoop(speed);
		}
	}

	/**
	 * Rotates all the SwerveModules to position
	 *
	 * @param position the position to rotate to in degrees
	 */
	public void rotateAll(double position) {
		for(SwerveModule module : moduleArray){
			module.setSpeedOpenLoop(position);
		}
	}

}
