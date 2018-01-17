package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {
	public static final int ENCODER_COUNTS = 4096;


	private BaseMotorController drive;
	private BaseMotorController steer;
	
	private PidParameters drivePid; // TODO make these local variable since we never access these.
	private PidParameters steerPid;

	private double x, y;
	
	private int ID;

	/**
	 * Creates a SwerveModule with the given parameters
	 *
	 * @param drive The drive motor that is used to drive
	 * @param steer The steer motor that is used to steer/rotate the wheel
	 * @param drivePid PidParameters for the drive motor
	 * @param steerPid PidParameters for the steer motor
	 * @param x The x coordinate relative to the center of the robot. Normally 1 or -1
	 * @param y The y coordinate relative to the center of the robot. Normally 1 or -1
	 * @param id An integer to easily identify the module while debugging. Should start at 0
	 */
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

	public int getID() { return ID; }

	
	/**
	 * Test method to allow drive motors to be set open loop
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeed(double speed) {
		
		// Set the motor speed directly
		drive.set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets the position of the steer motor.
	 * @param position A double in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void setPosition(double position){
		position %= 360;  // returns remainder, can be -359 to 360 (excluding 360)
		position = position < 0 ? position + 360 : position;  // makes it 0 to 360
		double targetPosition = ((position) / 360) * ENCODER_COUNTS; // a number 0 to 4096 excluding 4096
		assert 0 <= targetPosition && targetPosition < ENCODER_COUNTS;  // just make sure for now.
		
		SmartDashboard.putNumber("Target Position " + ID, targetPosition);
		final int currentPosition = steer.getActiveTrajectoryPosition();
		while(Math.abs(targetPosition - currentPosition) > ENCODER_COUNTS / 2){ // finds the quickest route
			if(targetPosition > currentPosition){
				targetPosition -= ENCODER_COUNTS;
			} else { // targetPosition < currentPosition
				targetPosition += ENCODER_COUNTS;
			}
		}

		// Set the steer motor controller to the desired position
		steer.set(ControlMode.Position, targetPosition);

	}

	/**
	 * Calls setSpeed and setPosition with the passed speed and position
	 *
	 * @param speed Desired speed in a percent (-1 to 1) Will eventually be converted to ft/s
	 * @param position Desired position of module in degrees: 0 - 360
	 */
	@Deprecated  // Since we don't need this method, I think we can remove it. If not, remove deprecation
	public void update(double speed, double position){

		this.setSpeed(speed);
		this.setPosition(position);
		
	}
	
	public void debug() {
		SmartDashboard.putNumber("Encoder " + ID, this.steer.getSelectedSensorPosition(0));
	}
	
	private void UpdateDrivePid(PidParameters pid) {
		this.drivePid = pid;
		
		drive.config_kP(drivePid.pidIdx, drivePid.KP, Constants.TimeoutMs);
		drive.config_kI(drivePid.pidIdx, drivePid.KI, Constants.TimeoutMs);
		drive.config_kD(drivePid.pidIdx, drivePid.KD, Constants.TimeoutMs);
		drive.config_kF(drivePid.pidIdx, drivePid.KF, Constants.TimeoutMs);
	}
	
	private void UpdateSteerPid(PidParameters pid) {
		this.steerPid = pid;
		
		steer.config_kP(steerPid.pidIdx, steerPid.KP, Constants.TimeoutMs);
		steer.config_kI(steerPid.pidIdx, steerPid.KI, Constants.TimeoutMs);
		steer.config_kD(steerPid.pidIdx, steerPid.KD, Constants.TimeoutMs);
		steer.config_kF(steerPid.pidIdx, steerPid.KF, Constants.TimeoutMs);

//		steer.setSelectedSensorPosition((90 / 360) * 4096, steerPid.pidIdx, Constants.TimeoutMs); // will this work?
	}

}
