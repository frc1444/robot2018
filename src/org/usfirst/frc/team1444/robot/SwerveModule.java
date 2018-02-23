package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.awt.geom.Point2D;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {
	private static final boolean USE_QUICK_REVERSE = false; // TODO test this today

	private final double wheelCircumference = 4 * Math.PI; // in inches

	private BaseMotorController drive;
	private BaseMotorController steer;
	
	private PidParameters drivePid;
	private PidParameters steerPid;

	private Point2D location;

	private int ID;
	
	private int encoderOffset; // offset for absolute encoder

//	private SensorCollection steerSensorCollection; use built in sensor collection instead

	// null means not initialized, true means it is a quad encoder, false means it is an analog encoder
	private Boolean setToQuad = null;
	private boolean isSpeedInReverse = false; // TODO this is not checked unless we call setSpeed
	//                                           so if we only call setSpeed once, it may give us bugs

	private double lastSetSpeed = 0;
	

	/**
	 * Creates a SwerveModule with the given parameters
	 *
	 * @param drive The drive motor that is used to drive
	 * @param steer The steer motor that is used to steer/rotate the wheel
	 * @param drivePid PidParameters for the drive motor
	 * @param steerPid PidParameters for the steer motor
	 * @param id An integer to easily identify the module while debugging. Should start at 0
	 * @param offset The offset of the analog (absolute) absolute encoder
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
		
		// Manually measured encoder offset
		this.encoderOffset = offset;

//		this.steerSensorCollection = new SensorCollection(this.steer);
		this.switchToAnalog();

		// Set the drive PID parameters
		this.UpdateDrivePid(drivePid);
		
		// Set the steer PID parameters
		this.UpdateSteerPid(steerPid);
	}

	public Point2D getLocation(){ return this.location; }

	public int getID() { return ID; }

	/**
	 * @return the total distance the drive wheel has gone in inches
	 */
	public double getTotalDistanceGone(){
		return (this.drive.getSelectedSensorPosition(drivePid.pidIdx) / (double) Constants.RealCimCountsPerRev) * wheelCircumference;
	}

	private boolean canUseQuickReverse(){
		return setToQuad != null && setToQuad && USE_QUICK_REVERSE;
	}

	/**
	 * Will be used in LED code and if needed, can be used for debugging.
	 * @return The last value passed to setSpeed (-1 to 1)
	 */
	public double getLastSetSpeed(){
		return lastSetSpeed;
	}
	
	/**
	 * Update the setpoint for the drive PID control
	 * @param speed Desired motor speed as a percentage of maximum: -1 to 1
	 */
	public void setSpeed(double speed) {
		this.lastSetSpeed = speed;
		
		// Convert input percentage to CTRE units/100ms		
		double targetSpeed = (speed * Constants.CimCoderCountsPerRev * Constants.MaxCimRpm) / Constants.CtreUnitConversion;
		if(this.isSpeedInReverse){
			targetSpeed *= -1;
		}
		
		// Update the drive PID setpoints
		drive.set(ControlMode.Velocity, targetSpeed);
	}

	/**
	 * Sets the position of the steer motor.
	 * @param position A double in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void setPosition(double position){
		// Convert the input into 0 to 359
		double actualPosition = position % 360;
		actualPosition = actualPosition < 0 ? actualPosition + 360 : actualPosition;

		actualPosition /= 360; // actualPosition is now a number between 0 and 1



		if(setToQuad == null) throw new IllegalStateException("why is setToQuad null?");

		final int encoderCounts = this.getUsedEncoderCounts();
		int targetEncoderCounts = (int) (actualPosition * encoderCounts);

		// Find the fastest path from the current position to the new position
		final int currentEncoderCount = steer.getSelectedSensorPosition(steerPid.pidIdx);
		// Add rotation offset factoring in the number of rotations (either positive or negative)
		if(this.canUseQuickReverse()){
			final double halfRotationCounts = encoderCounts / 2;
			int halfRotationsAway = (int) Math.round((currentEncoderCount - targetEncoderCounts) / halfRotationCounts);
			targetEncoderCounts += halfRotationsAway * halfRotationCounts;
			this.isSpeedInReverse = halfRotationsAway % 2 != 0; // if it's odd, then reverse speed
		} else {
			int rotationsAway = (int) Math.round((currentEncoderCount - targetEncoderCounts) / (double) encoderCounts);
			targetEncoderCounts += rotationsAway * encoderCounts;
			this.isSpeedInReverse = false;
		}

		// Command a new steering position
		steer.set(ControlMode.Position, targetEncoderCounts);

	}

	/**
	 * While in analog mode, this method won't return an accurate result unless it is near 0. (This will never get with
	 * in 4 degrees of 0 if the raw analog value is 0 when at 0 degrees)
	 *
	 * @return The position of the steering in degrees.
	 */
	public double getSensorPositionDegrees(){
		int encoderCounts = getUsedEncoderCounts();

		int position = steer.getSelectedSensorPosition(steerPid.pidIdx);
		position %= encoderCounts;
		position = position < 0 ? position + encoderCounts : position;

		return (position / (double) encoderCounts) * 360;
	}
	private int getUsedEncoderCounts(){
		int encoderCounts;
		if(!setToQuad){ // absolute encoder
			encoderCounts = Constants.AnalogSteerCountsPerRev;
		} else {
			encoderCounts = Constants.QuadSteerCountsPerRev;
		}
		return encoderCounts;
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
//		SmartDashboard.putNumber("Raw Analog " + ID, steer.getSensorCollection().getAnalogInRaw());
		
		// Print the current, measured encoder count
//		SmartDashboard.putNumber("Encoder " + ID, steer.getSelectedSensorPosition(steerPid.pidIdx));

//		SmartDashboard.putString("is quad encoder? id: " + ID, "" + this.setToQuad);
//		SmartDashboard.putNumber("distance gone id: " + ID, this.getTotalDistanceGone());
//		SmartDashboard.putNumber("drive encoder counts id: " + ID, this.drive.getSelectedSensorPosition(drivePid.pidIdx));
	}
	
	private void UpdateDrivePid(PidParameters pid) {
		this.drivePid = pid;
		
//		drive.config_kP(drivePid.pidIdx, drivePid.KP, Constants.TimeoutMs);
//		drive.config_kI(drivePid.pidIdx, drivePid.KI, Constants.TimeoutMs);
//		drive.config_kD(drivePid.pidIdx, drivePid.KD, Constants.TimeoutMs);
//		drive.config_kF(drivePid.pidIdx, drivePid.KF, Constants.TimeoutMs);
		drivePid.apply(drive);
	}
	
	private void UpdateSteerPid(PidParameters pid) {
		this.steerPid = pid;
		
//		steer.config_kP(steerPid.pidIdx, steerPid.KP, Constants.TimeoutMs);
//		steer.config_kI(steerPid.pidIdx, steerPid.KI, Constants.TimeoutMs);
//		steer.config_kD(steerPid.pidIdx, steerPid.KD, Constants.TimeoutMs);
//		steer.config_kF(steerPid.pidIdx, steerPid.KF, Constants.TimeoutMs);
		steerPid.apply(steer);
	}

	/**
	 * Switches to quad encoder and uses the current position as 0 (origin) meaning that when this is called,
	 * the steer should be in the 0 position
	 */
	public void switchToQuad() {
		if (setToQuad != null && setToQuad) {
			return;
		}
		
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, Constants.PidIdx, Constants.TimeoutMs);
		this.steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, Constants.TimeoutMs);
		this.steer.setSelectedSensorPosition(0, Constants.PidIdx, Constants.TimeoutMs);
		this.steer.setSensorPhase(true);
		
		setToQuad = true;
	}
	
	public void switchToAnalog() {
		if (setToQuad != null && !setToQuad) {
			return;
		}
		
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.Analog, Constants.PidIdx, Constants.TimeoutMs);
		this.steer.configSetParameter(ParamEnum.eFeedbackNotContinuous, 0, 0, 0, Constants.TimeoutMs);
		this.steer.setSelectedSensorPosition(steer.getSensorCollection().getAnalogInRaw() - this.encoderOffset, Constants.PidIdx, Constants.TimeoutMs);
		this.steer.setSensorPhase(false);
		
		setToQuad = false;
			
	}

	public boolean isQuad(){
		if(setToQuad == null){
			throw new IllegalStateException("This SwerveModule has not yet had a FeedbackDevice set.");
		}
		return setToQuad;
	}

}
