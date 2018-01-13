package org.usfirst.frc.team1444.robot;


import com.ctre.phoenix.motorcontrol.IMotorController;

// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {

//	private SwerveModule frontLeft;
//	private SwerveModule frontRight;
//	private SwerveModule rearLeft;
//	private SwerveModule rearRight;

	private SwerveModule[] moduleArray;


	/**
	 * Initializes SwerveDrive and creates SwerveModules. Even though each parameter is a IMotorController,
	 * we can still pass it a TalonSRX
	 */
	public SwerveDrive(IMotorController flDrive, IMotorController flSteer,
	                   IMotorController frDrive, IMotorController frSteer,
	                   IMotorController rlDrive, IMotorController rlSteer,
	                   IMotorController rrDrive, IMotorController rrSteer) {

//		frontLeft = new SwerveModule(flDrive, flSteer);
//		frontRight = new SwerveModule(frDrive, frSteer);
//		rearLeft = new SwerveModule(rlDrive, rlSteer);
//		rearRight = new SwerveModule(rrDrive, rrSteer);

//		moduleArray = new SwerveModule[] {frontLeft, frontRight, rearLeft, rearRight};
		moduleArray = new SwerveModule[]{
				new SwerveModule(flDrive, flSteer),
				new SwerveModule(frDrive, frSteer),
				new SwerveModule(rlDrive, rlSteer),
				new SwerveModule(rrDrive, rrSteer)
		};
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

	/**
	 * Sets the same speed to each SwerveModule
	 *
	 * @param speed A number between -1 and 1
	 */
	public void setSpeed(double speed) {
		for (SwerveModule module : moduleArray) {
			module.setSpeed(speed);
		}
	}

	/**
	 * Rotates all the SwerveModules to position
	 *
	 * @param position the position to rotate to
	 */
	public void rotateAll(float position) {
		for(SwerveModule module : moduleArray){
			module.steerTo(position);
		}
	}

	// TODO put methods here to rotate all motors to a position and set speed here once finished in SwerveModule

}
