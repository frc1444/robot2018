package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift {
	private static final boolean USE_SPEED_SET = true;
	private static final double MAX_POSITION_INCREMENT = .075;

	private static final int MAIN_STAGE_ENCODER_COUNTS = 29600; // 29600
	private static final int SECOND_STAGE_ENCODER_COUNTS = 22600;

	private static final double SAFETY_SPEED = .2;

	private BaseMotorController mainStageMaster;

	private BaseMotorController secondStageMotor;

//	private PidParameters mainPid;
//	private PidParameters secondPid;

	public Lift(TalonSRX mainStageMaster, BaseMotorController mainStageSlave,
	            TalonSRX secondStageMotor){
//	            PidParameters mainStagePid, PidParameters secondStagePid,
//	            PidHandler pidHandler){

		// set instance variables
		this.mainStageMaster = mainStageMaster;
		this.secondStageMotor = secondStageMotor;
//		this.mainPid = mainStagePid;
//		this.secondPid = secondStagePid;

		// configure the heck out of passed controllers
		// main stage
		mainStageMaster.configReverseSoftLimitEnable(false, Constants.TimeoutMs);
		mainStageMaster.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
		mainStageMaster.configForwardSoftLimitThreshold(MAIN_STAGE_ENCODER_COUNTS, Constants.TimeoutMs);
		mainStageMaster.configForwardSoftLimitEnable(true, Constants.TimeoutMs);
		mainStageMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.PidIdx, Constants.TimeoutMs);
		mainStageMaster.setInverted(true); // Needs to be inverted
		mainStageMaster.setSensorPhase(true);
		mainStageMaster.setNeutralMode(NeutralMode.Brake);

		mainStageSlave.follow(mainStageMaster);
		mainStageSlave.setInverted(true); // Inverted relative to master - tested and works


		// second stage
		secondStageMotor.configReverseSoftLimitEnable(false, Constants.TimeoutMs);
		secondStageMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
		secondStageMotor.configForwardSoftLimitThreshold(SECOND_STAGE_ENCODER_COUNTS, Constants.TimeoutMs);
		secondStageMotor.configForwardSoftLimitEnable(true, Constants.TimeoutMs);

		secondStageMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.PidIdx, Constants.TimeoutMs);
		secondStageMotor.setInverted(false); // needs to be tested
		secondStageMotor.setSensorPhase(false);
		secondStageMotor.setNeutralMode(NeutralMode.Brake);

//		pidHandler.addPid(new PidHandler.PidDashObject(mainPid, Arrays.asList(mainStageMaster), "main stage pid"));
//		pidHandler.addPid(new PidHandler.PidDashObject(secondStagePid, Arrays.asList(secondStageMotor), "second stage pid"));
	}

	/**
	 * does necessary things like check if the limit switch is pressed
	 * Should be called in the Robot class
	 */
	public void update(){
		if(mainStageMaster.getSensorCollection().isRevLimitSwitchClosed()){
			mainStageMaster.setSelectedSensorPosition(0, Constants.PidIdx, Constants.TimeoutMs);
		}
		if(secondStageMotor.getSensorCollection().isRevLimitSwitchClosed()){
			secondStageMotor.setSelectedSensorPosition(0, Constants.PidIdx, Constants.TimeoutMs);
		}
	}

	// region ========= stage positions ===========
	/**
	 * Move the boom to a desired position - wraps the veolicty control from below
	 * @param position desired position of main stage (0 - full down, 1 - full up)
	 */
	public void setMainStagePosition(double position){	
		
		// Set position using simple proportional control
		double currentPosition = getMainStagePosition();
		double error = position - currentPosition;
		
		// "Five sound good?" "Yeah, that should work..." - Mike and Aaron
		double KP = 5;
		
		double setPoint = error * KP;
		
		this.setMainStageSpeed(setPoint);
		
	}

	/**
	 * Method that takes a position and starts to move the second stage to that position
	 * @param position wanted position of second stage (0 to 1)
	 */
	public void setSecondStagePosition(double position){
		this.secondStageMotor.set(ControlMode.Position, position * SECOND_STAGE_ENCODER_COUNTS);
	}

	/** @return A number from 0 to 1. 0 is at bottom. Note returned number could be slightly out of the range 0 to 1 */
	public double getMainStagePosition(){
		return mainStageMaster.getSelectedSensorPosition(Constants.PidIdx) / (double) MAIN_STAGE_ENCODER_COUNTS;
	}
	/** @return A number from 0 to 1. 0 is at bottom. Note returned number could be slightly out of the range 0 to 1 */
	public double getSecondStagePosition(){
		return secondStageMotor.getSelectedSensorPosition(Constants.PidIdx) / (double) SECOND_STAGE_ENCODER_COUNTS;
	}
	public Position getBothPosition(){
		return new Position(this.getMainStagePosition(), this.getSecondStagePosition());
	}

	/**
	 * Uses setMainStagePosition and setSecondStagePosition to set a preset position
	 * @param position the preset position
	 */
	public void setBothPosition(Position position){
		this.setMainStagePosition(position.getMainPosition());
		this.setSecondStagePosition(position.getSecondPosition());
	}
	// endregion ========= end stage positions ============

	// region stage speeds
	/**
	 * Sets the speed of the main lift stage using velocity control
	 * @param speed Desired speed of the main stage. (-1 to 1) - positive raises lift
	 */
	public void setMainStageSpeed(double speed){
		double position = getMainStagePosition(); // a value from 0 to 1

		double targetSpeed = (speed * Constants.LiftMainStageEncoderCountsPerRev * Constants.MaxCimRpm)
				/ (Constants.CtreUnitConversion * Constants.LiftMainGearboxRatio);

		// Linearally scale the speed as the stage approaches the limits
		if(position > (0.8) && (speed > 0)) {
			// Add a little feed-forward to help the final approach to the upper limit
			targetSpeed *= ((1.0 - position) / 0.2) + (position >= 1.0 ? 0 : 0.1);
		} else if(position < (0.1) && (speed < 0)) {
			// No need for feed forward when lowering since gravity will bring the stage all the way home
			// The divisor is also larger here due to gravity
			targetSpeed *= (position / 0.4);
		}

		this.mainStageMaster.set(ControlMode.Velocity, targetSpeed);

		SmartDashboard.putNumber("main speed", targetSpeed);
	}
	
	/**
	 * Sets the speed of the second lift stage using velocity control
	 * @param speed Desired speed of the second stage (-1 to 1) - positive raises the stage
	 */
	public void setSecondStageSpeed(double speed){
		double position = getSecondStagePosition(); // 0 to 1		
		
		double targetSpeed = (speed * Constants.LiftSecondStageEncoderCountsPerRev * Constants.MaxBagRpm)
				/ (Constants.CtreUnitConversion * Constants.LiftSecondStageGearboxRatio);

		// Linearally scale the speed as the stage approaches the limits
		if((position > 0.8) && (speed > 0)) {
			// Add a little feed-forward to help the final approach to the upper limit
			targetSpeed *= ((1.0 - position) / 0.2) + (position >= 1.0 ? 0 : 0.1);
		} else if((position < 0.3) && (speed < 0)) {
			// No need for feed forward when lowering since gravity will bring the stage all the way home
			// The divisor is also larger here due to gravity
			targetSpeed *= (position / 0.5);
		}

		SmartDashboard.putNumber("main speed", targetSpeed);
		
		this.secondStageMotor.set(ControlMode.Velocity, targetSpeed);
	}
	// endregion End State speeds


	public void debug(){
		SmartDashboard.putNumber("Main stage motor encoder counts", mainStageMaster.getSelectedSensorPosition(Constants.PidIdx));
		SmartDashboard.putNumber("Second stage motor encoder counts", secondStageMotor.getSelectedSensorPosition(Constants.PidIdx));

		SensorCollection secondSensor = secondStageMotor.getSensorCollection();
		SmartDashboard.putBoolean("2nd rev sensor", secondSensor.isRevLimitSwitchClosed());
		SmartDashboard.putBoolean("2nd forward sensor", secondSensor.isFwdLimitSwitchClosed());

		SensorCollection mainSensor = mainStageMaster.getSensorCollection();
		SmartDashboard.putBoolean("main rev sensor", mainSensor.isRevLimitSwitchClosed());

		SmartDashboard.putNumber("main stage position", getMainStagePosition());
		SmartDashboard.putNumber("second stage position", getSecondStagePosition());
	}

	/**
	 * A bunch of preset positions
	 */
	public static class Position{
		public static final Position SCALE_MAX = new Position(1, 1);
		public static final Position SCALE_MIN = new Position(.9, 1);
		public static final Position SWITCH = new Position(0.2, .7);// TODO make sure accurate
		public static final Position MIN = new Position(0, 0);
		public static final Position MIN_13 = new Position(0, .08);

		private final double mainPosition;
		private final double secondPosition;

		public Position(double mainPosition, double secondPosition){
			this.mainPosition = mainPosition;
			this.secondPosition = secondPosition;
		}

		public double getMainPosition(){
			return mainPosition;
		}
		public double getSecondPosition(){
			return secondPosition;
		}

	}

}
