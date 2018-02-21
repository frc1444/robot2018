package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift {
	private static final int MAIN_STAGE_ENCODER_COUNTS = 27000;
	private static final int SECOND_STAGE_ENCODER_COUNTS = 22600;

	private static final double SAFETY_SPEED = .2;

	private BaseMotorController mainStageMaster;

	private BaseMotorController secondStageMotor;

	private PidParameters mainPid;
	private PidParameters secondPid;

	public Lift(TalonSRX mainStageMaster, BaseMotorController mainStageSlave,
	            TalonSRX secondStageMotor,
	            PidParameters mainStagePid, PidParameters secondStagePid){

		// set instance variables
		this.mainStageMaster = mainStageMaster;
		this.secondStageMotor = secondStageMotor;
		this.mainPid = mainStagePid;
		this.secondPid = secondStagePid;

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
		mainStagePid.apply(mainStageMaster); // apply pid values
//		mainStageMaster.configClosedloopRamp(.5, Constants.TimeoutMs);

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
		secondStagePid.apply(secondStageMotor); // apply pid values

	}

	/**
	 * does necessary things like check if the limit switch is pressed
	 * Should be called in the Robot class
	 */
	public void update(){
		if(mainStageMaster.getSensorCollection().isRevLimitSwitchClosed()){
			mainStageMaster.setSelectedSensorPosition(0, mainPid.pidIdx, Constants.TimeoutMs);
		}
		if(secondStageMotor.getSensorCollection().isRevLimitSwitchClosed()){
			secondStageMotor.setSelectedSensorPosition(0, secondPid.pidIdx, Constants.TimeoutMs);
		}
	}

	// region stage positions
	/**
	 * Method that takes a position and starts to move the main stage to that position
	 * @param position wanted position of main stage (0 to 1)
	 */
	public void setMainStagePosition(double position){
		this.mainStageMaster.set(ControlMode.Position, position * MAIN_STAGE_ENCODER_COUNTS);
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
		return mainStageMaster.getSelectedSensorPosition(mainPid.pidIdx) / (double) MAIN_STAGE_ENCODER_COUNTS;
	}
	/** @return A number from 0 to 1. 0 is at bottom. Note returned number could be slightly out of the range 0 to 1 */
	public double getSecondStagePosition(){
		return secondStageMotor.getSelectedSensorPosition(secondPid.pidIdx) / (double) SECOND_STAGE_ENCODER_COUNTS;
	}
	// endregion

	// region stage speeds
	/**
	 * @param speed Speed of the main stage. (-1 to 1) positive raises lift
	 */
	public void setMainStageSpeed(double speed){
		double position = getMainStagePosition(); // a value from 0 to 1
		if(position < .2 && speed < 0){
			double scale = (position + .05) / .3;
			if(scale >= 0){ // we don't want to reverse the speed
				speed *= scale;
			} else {
				System.out.println("setting to safety speed... scale: " + scale + " position " + position + "speed: " + speed);
				speed *= SAFETY_SPEED;
			}
		} else if(position > .8 && speed > 0){
			double scale = ((1 - position) + .05) / .3;
			if(scale >= 0){ // we don't want to reverse the speed
				speed *= scale;
			} else {
				speed *= SAFETY_SPEED;
			}
		}
		SmartDashboard.putNumber("main speed", speed);
		this.mainStageMaster.set(ControlMode.PercentOutput, speed);

//		double targetSpeed = (speed * Constants.LiftEncoderCountsPerRev * Constants.MaxCimRpm )
//				/ (Constants.CtreUnitConversion);

		//this.mainStageMaster.set(ControlMode.Velocity, targetSpeed);
	}
	public void setSecondStageSpeed(double speed){
		double position = getSecondStagePosition(); // update this code when above code is updated
		if(position < .2 && speed < 0){
			double scale = (position + .05) / .3;
			if(scale >= 0){ // we don't want to reverse the speed
				speed *= scale;
			} else {
				speed *= SAFETY_SPEED;
			}
		} else if(position > .8 && speed > 0){
			double scale = ((1 - position) + .05) / .3;
			if(scale >= 0){ // we don't want to reverse the speed
				speed *= scale;
			} else {
				speed *= SAFETY_SPEED;
			}
		}
		SmartDashboard.putNumber("2nd speed", speed);
		this.secondStageMotor.set(ControlMode.PercentOutput, speed);
	}
	// endregion


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


}
