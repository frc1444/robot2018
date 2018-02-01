package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.awt.geom.Point2D;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {
	private static final int ENCODER_COUNTS = 1024;

	private static final boolean SCALE_COUNTS = false; // if false, will not use MAX and MIN _ENCODER_COUNTS in setPosition
	private static final int MAX_ENCODER_COUNTS = 898;
	private static final int MIN_ENCODER_COUNTS = 12;

	// if false, will use encoderOffset in the set position method. If true, will call the steer set selected position
	private static final boolean USE_SET_SELECTED = true;


	private BaseMotorController drive;
	private BaseMotorController steer;
	
	private PidParameters drivePid;
	private PidParameters steerPid;

	private Point2D location;

	private int ID;
	
	private int encoderOffset; // the offset in encoder counts in not perfect world
	private double offsetDegrees; // the offset in degrees, in our perfect world

	private SensorCollection steerSensorCollection;

	/**
	 * Creates a SwerveModule with the given parameters
	 *
	 * @param drive The drive motor that is used to drive
	 * @param steer The steer motor that is used to steer/rotate the wheel
	 * @param drivePid PidParameters for the drive motor
	 * @param steerPid PidParameters for the steer motor
	 * @param id An integer to easily identify the module while debugging. Should start at 0
	 */
	public SwerveModule(BaseMotorController drive, BaseMotorController steer,
			PidParameters drivePid, PidParameters steerPid,
			Point2D location,
			int id, int offset) {
		
		this.drive = drive;
		this.steer = steer;

		// Cartesian position of module on robot
		this.location = location;

		// Unique ID for this module - mainly used for debugging
		this.ID = id;
		
		// Manully measured encoder offset
		this.encoderOffset = offset;
		this.offsetDegrees = convertToDegrees(this.encoderOffset);
		
		// Set the Drive motor to use an incremental encoder (CIMcoder)
		this.drive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		
		// Set the Drive encoder phase
		this.drive.setSensorPhase(false);
		
		// Set steer motor to use an MA3 absolute encoder
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.Analog, Constants.PidIdx, Constants.TimeoutMs);
		this.steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, Constants.TimeoutMs);

		// Zero encoder reading by applying the premeasured offsets
		this.steerSensorCollection = new SensorCollection(this.steer);

		if(USE_SET_SELECTED) {
			this.steer.setSelectedSensorPosition(this.steerSensorCollection.getAnalogInRaw() - this.encoderOffset, Constants.PidIdx, Constants.TimeoutMs);
		}
		
		// Set the Steer encoder phase
		this.steer.setSensorPhase(false);
		
		// Set the drive PID parameters
		this.UpdateDrivePid(drivePid);
		
		// Set the steer PID parameters
		this.UpdateSteerPid(steerPid);
	}

	public Point2D getLocation(){ return this.location; }

	public int getID() { return ID; }


	
	/**
	 * Update the setpoint for the drive PID control
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeed(double speed) {
		
		// Convert input percentage to CTRE units/100ms		
		double targetSpeed = (speed * Constants.CimCoderCountsPerRev * Constants.MaxCimRpm) / Constants.CtreUnitConversion;
		
		// Update the drive PID setpoints
		drive.set(ControlMode.Velocity, targetSpeed);
	}

	/**
	 * Sets the position of the steer motor.
	 * @param position A double in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void setPosition(double position){
		if(SCALE_COUNTS && !USE_SET_SELECTED){
			position += offsetDegrees;
		}
		
		// Convert the input into 0 to 359
		double actualPosition = position % 360;
		actualPosition = actualPosition < 0 ? actualPosition + 360 : actualPosition;
		
		// Convert desired position to encoder counts
		int targetEncoderCounts;
		if(SCALE_COUNTS) { // scale targetEncoderCounts linearly using constant max and min values
			// don't remove if, just change constant SCALE_COUNTS
			targetEncoderCounts = (int) scaleFromDegrees(actualPosition);
		} else {
			actualPosition /= 360;
			targetEncoderCounts = (int)(actualPosition * ENCODER_COUNTS); // 0 to 1024
			if(!USE_SET_SELECTED) {
				targetEncoderCounts += this.encoderOffset;
			}
		}

		// Find the fastest path from the current position to the new position
		int currentEncoderCount = steer.getSelectedSensorPosition(steerPid.pidIdx);
		// Add rotation offset factoring in the number of rotations (either positive or negative)
		targetEncoderCounts += (int) Math.round((currentEncoderCount - targetEncoderCounts) / (double) ENCODER_COUNTS) * ENCODER_COUNTS;

		// Command a new steering position
		steer.set(ControlMode.Position, targetEncoderCounts);
		SmartDashboard.putNumber("setPosition " + ID, targetEncoderCounts);

	}

	/**
	 * Uses the MAX and MIN encoder counts to give you a value between them
	 * This returns a double so it doesn't lose precision when storing it. However, when setting it, cast it to an int
	 * @param degrees Number to convert to real world encoder counts
	 * @return Real world encoder counts between MIN and MAX assuming 0 <= degrees < 360
	 */
	private static double scaleFromDegrees(double degrees){
		final int allowed = MAX_ENCODER_COUNTS - MIN_ENCODER_COUNTS;

		double counts = (degrees / 360) * allowed;
		counts += MIN_ENCODER_COUNTS;
		return counts;
	}

	/**
	 *
	 * @param realWorldCounts The number of real world counts that should be between MIN and MAX encoder counts
	 * @return Number in degrees in our perfect world
	 */
	private static double convertToDegrees(double realWorldCounts){
		final int allowed = MAX_ENCODER_COUNTS - MIN_ENCODER_COUNTS;

		realWorldCounts -= MIN_ENCODER_COUNTS;
		realWorldCounts /= allowed;
		realWorldCounts *= 360;

		return realWorldCounts;
	}

	/**
	 * Calls setSpeed and setPosition with the passed speed and position
	 *
	 * @param speed Desired speed in a percent (-1 to 1) Will eventually be converted to ft/s
	 * @param position Desired position of module in degrees: 0 - 360
	 */
	public void update(double speed, double position) {

		this.setSpeed(speed);
		this.setPosition(position);
		
	}
	
	public void debug() {	
		// Get the raw analog encoder count (can be removed later once drive is sorted out)
		SmartDashboard.putNumber("Raw Analog " + ID, this.steerSensorCollection.getAnalogInRaw());
		
		// Print the current, measured encoder count
		SmartDashboard.putNumber("Encoder " + ID, steer.getSelectedSensorPosition(steerPid.pidIdx));
		
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
	}

}
