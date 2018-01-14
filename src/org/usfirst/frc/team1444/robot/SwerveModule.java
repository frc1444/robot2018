package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	private BaseMotorController drive; 
	private BaseMotorController steer;
	private PidParameters drivePid;
	private PidParameters steerPid;
	
	public SwerveModule(BaseMotorController drive, BaseMotorController steer, PidParameters drivePid, PidParameters steerPid) {
		
		this.drive = drive;
		this.steer = steer;
		
		// Set the Drive motor to use an incremental encoder
		this.drive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Drive encoder phase
		this.drive.setSensorPhase(false);
		
		// Set the Drive motor to use an absolute encoder
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Steer encoder phase
		this.steer.setSensorPhase(false);
		
		// Set the drive PID parameters
		this.UpdateDrivePid(drivePid);
		
		// Set the steer PID parameters
		this.UpdateSteerPid(steerPid);
	}


	/**
	 * @param velocity Desired drive velocity of module in ft/s
	 */
	public void setVelocity(double velocity) {
		
		// TODO: Add calculation to convert ft/s in units/100ms?
		double targetVelocity = 0;
		
		// Set the motor controller to the target velocity
		drive.set(ControlMode.Velocity, targetVelocity);
	}
	
	/**
	 * Test method to allow drive motors to be set open loop
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeedOpenLoop(double speed) {
		
		// Set the motor speed directly
		drive.set(ControlMode.PercentOutput, speed);
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

	/**
	 * Test method to allow steer motor to run open loop
	 * @param position Desired steer motor speed (NOT ACTUALLY POSITION)
	 */
	public void setPositionOpenLoop(double position) {
		
		// Set the steer motor to move at the desired positon (this really just sets to motor to run at a certain speed)
		steer.set(ControlMode.PercentOutput, position);
	}

	/**
	 * 
	 * @param velocity Desired velocity of module in ft/s
	 * @param position Desired position of module in degrees: 0 - 360
	 */
	public void update(double velocity, double position){
		
		this.setVelocity(velocity);
		this.setPosition(position);
	}
	
	/**
	 * 
	 * @param velocity Desired open-loop drive velocity: -1 to 1
	 * @param position Desired open-loop steer "position": -1 to 1 (NOT ACTUALLY POSITION)
	 */
	public void updateOpenLoop(double velocity, double position) {
		
		this.setSpeedOpenLoop(velocity);
		this.setPositionOpenLoop(position);
	}
	
	// TODO: remove
	public void steerTo(double x) {
		
	}
	
	public void setSpeed(double x) {
		
	}
	
	public void UpdateDrivePid(PidParameters pid) {
		this.drivePid = pid;
		
		drive.config_kP(drivePid.pidIdx, drivePid.KP, Constants.TimeoutMs);
		drive.config_kI(drivePid.pidIdx, drivePid.KI, Constants.TimeoutMs);
		drive.config_kD(drivePid.pidIdx, drivePid.KD, Constants.TimeoutMs);
		drive.config_kF(drivePid.pidIdx, drivePid.KF, Constants.TimeoutMs);
	}
	
	public void UpdateSteerPid(PidParameters pid) {
		this.steerPid = pid;
		
		steer.config_kP(steerPid.pidIdx, steerPid.KP, Constants.TimeoutMs);
		steer.config_kI(steerPid.pidIdx, steerPid.KI, Constants.TimeoutMs);
		steer.config_kD(steerPid.pidIdx, steerPid.KD, Constants.TimeoutMs);
		steer.config_kF(steerPid.pidIdx, steerPid.KF, Constants.TimeoutMs);
	}

}
