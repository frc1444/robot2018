package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	private BaseMotorController drive; 
	private BaseMotorController steer;

	private double x, y;
	
	public SwerveModule(BaseMotorController drive, BaseMotorController steer, double x, double y) {
		
		this.drive = drive;
		this.steer = steer;
		this.x = x;
		this.y = y;
		
		// Set the Drive motor to use an incremental encoder
		this.drive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Drive encoder phase
		this.drive.setSensorPhase(false);
		
		// Set the Drive motor to use an absolute encoder
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Steer encoder phase
		this.steer.setSensorPhase(false);
	}

	public double getX(){ return x; }
	public double getY(){ return y; }


	
	/**
	 * Test method to allow drive motors to be set open loop
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeedOpenLoop(double speed) {
		
		// Set the motor speed directly
		drive.set(ControlMode.PercentOutput, speed); // TODO convert speed to velocity and change ControlMode
	}

	/**
	 * Unfinished method
	 * @param position A double in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void setPosition(double position){

		// TODO: convert degrees to encoder counts
		double targetPosition = 0;
		
		// Set the steer motor controller to the desired position
		steer.set(ControlMode.Position, targetPosition);

	}


//	/** Unless we are going to store the values passed to this method, just call setSpeedOpenLoop and setPosition
//	 *
//	 * @param speed Desired speed in a percent (-1 to 1) Will eventually be converted to ft/s
//	 * @param position Desired position of module in degrees: 0 - 360
//	 */
//	public void update(double speed, double position){
//
//		this.setSpeedOpenLoop(speed);
//		this.setPosition(position);
//	}
//

}
