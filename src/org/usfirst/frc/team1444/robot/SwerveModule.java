package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	private BaseMotorController drive; 
	private BaseMotorController steer;
	
	private PidParameters drivePid;
	private PidParameters steerPid;

	private double x, y;
	
	private int ID;
	
	public SwerveModule(BaseMotorController drive, BaseMotorController steer, 
			PidParameters drivePid, PidParameters steerPid, 
			double x, double y,
			int id) {
		
		this.drive = drive;
		this.steer = steer;

		this.x = x;
		this.y = y;
		
		this.ID = id;
		
		// Set the Drive motor to use an incremental encoder
		this.drive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Drive encoder phase
		this.drive.setSensorPhase(false);
		
		// Set the Drive motor to use an absolute encoder
		// TODO Change to absolute encoder
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Steer encoder phase
		this.steer.setSensorPhase(true);
		
		// Set the drive PID parameters
		this.UpdateDrivePid(drivePid);
		
		// Set the steer PID parameters
		this.UpdateSteerPid(steerPid);
	}

	public double getX(){ return x; }
	public double getY(){ return y; }


	
	/**
	 * Test method to allow drive motors to be set open loop
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeed(double speed) {
		
		// Set the motor speed directly
		drive.set(ControlMode.PercentOutput, speed); // TODO convert speed to velocity and change ControlMode
	}

	/**
	 * Unfinished method
	 * @param position A double in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void setPosition(double position){
		position *= -1;
		// TODO: convert degrees to encoder counts
		double targetPosition = position % 360;
		
		targetPosition = (position / 360) * 4096;
		SmartDashboard.putNumber("Target Position " + ID, targetPosition);
		
		// Set the steer motor controller to the desired position
		steer.set(ControlMode.Position, targetPosition);

	}

	/** Unless we are going to store the values passed to this method, just call setSpeedOpenLoop and setPosition
	 *
	 * @param speed Desired speed in a percent (-1 to 1) Will eventually be converted to ft/s
	 * @param position Desired position of module in degrees: 0 - 360
	 */
	public void update(double speed, double position){

		this.setSpeed(speed);
		this.setPosition(position);
		
	}
	
	public void debug() {
		SmartDashboard.putNumber("Encoder " + ID, this.steer.getSelectedSensorPosition(0));
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
