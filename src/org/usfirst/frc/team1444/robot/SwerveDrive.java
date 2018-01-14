package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {

	private SwerveModule[] moduleArray;


	/**
	 * Initializes SwerveDrive and creates SwerveModules. Even though each parameter is a BaseMotorController,
	 * we can still pass it a TalonSRX
	 */
	public SwerveDrive(BaseMotorController flDrive, BaseMotorController flSteer,
	                   BaseMotorController frDrive, BaseMotorController frSteer,
	                   BaseMotorController rlDrive, BaseMotorController rlSteer,
	                   BaseMotorController rrDrive, BaseMotorController rrSteer) {

		moduleArray = new SwerveModule[]{
				new SwerveModule(flDrive, flSteer),
				new SwerveModule(frDrive, frSteer),
				new SwerveModule(rlDrive, rlSteer),
				new SwerveModule(rrDrive, rrSteer)
		};

	}
	
	// TODO: Are these accessors really necessary - do we want other classes to have access to this array?

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
	
	/**
	 * 
	 * @param velocity Desired linear velocity of robot in ft/s
	 * @param direction Desired direction of robot in degrees
	 * @param rotationRate Desired rotation rate of robot in degrees/s
	 */
	public void update(double velocity, double direction, double rotationRate) {
		
		// TODO: Set individual module velocity and position based on inputs
		// TODO: lots of math
	}

	public SwerveModule[] getModules() {
		return moduleArray;
	}

	/**
	 * Sets the same speed to each SwerveModule
	 *
	 * @param speed A number between -1 and 1
	 */
	public void setSpeed(double speed) {
		for (SwerveModule module : moduleArray) {
			module.setSpeedOpenLoop(speed);
		}
	}

	/**
	 * Rotates all the SwerveModules to position
	 *
	 * @param position the position to rotate to
	 */
	public void rotateAll(float position) {
		for(SwerveModule module : moduleArray){
			module.setPositionOpenLoop(position);
		}
	}

	// TODO put methods here to rotate all motors to a position and set speed here once finished in SwerveModule

}
