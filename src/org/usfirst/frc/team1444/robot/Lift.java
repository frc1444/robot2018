package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lift {
	private static final int MAIN_STAGE_ENCODER_COUNTS = 27000;
	private static final int SECOND_STAGE_ENCODER_COUNTS = 999;

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
		mainStageMaster.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
		mainStageMaster.configForwardSoftLimitThreshold(MAIN_STAGE_ENCODER_COUNTS, Constants.TimeoutMs);
		mainStageMaster.configForwardSoftLimitEnable(true, Constants.TimeoutMs);
		mainStageMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.PidIdx, Constants.TimeoutMs);
		mainStageMaster.setInverted(true); // Needs to be inverted
		mainStagePid.apply(mainStageMaster); // apply pid values

		mainStageSlave.follow(mainStageMaster);
		mainStageSlave.setInverted(true); // Inverted relative to master - tested and works


		// second stage
		secondStageMotor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
		secondStageMotor.configForwardSoftLimitThreshold(SECOND_STAGE_ENCODER_COUNTS, Constants.TimeoutMs);
		secondStageMotor.configForwardSoftLimitEnable(true, Constants.TimeoutMs);
		secondStageMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.PidIdx, Constants.TimeoutMs);
		secondStageMotor.setInverted(false); // needs to be tested
		secondStagePid.apply(secondStageMotor); // apply pid values


//		try { Mike's code for the lift. Was his tested?
//			mainStageMaster.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
//			mainStageMaster.setInverted(false);
//
//			mainStageSlave.follow(mainStageMainMotor);
//			mainStageSlave.setInverted(true);
//		} catch (NullPointerException e) { also, would we really just want to print this out? We should make this crash our program
//			System.err.println("ERROR in Lift.java: Invalid Motor Controller Instance given!");
//		}

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

	public double getMainStagePosition(){
		return mainStageMaster.getSelectedSensorPosition(mainPid.pidIdx) / (double) MAIN_STAGE_ENCODER_COUNTS;
	}
	public double getSecondStagePosition(){
		return secondStageMotor.getSelectedSensorPosition(secondPid.pidIdx) / (double) SECOND_STAGE_ENCODER_COUNTS;
	}
	// endregion

	/**
	 * @param speed Speed of the main stage. (-1 to 1) positive raises lift
	 */
	public void setMainStageSpeed(double speed){
		this.mainStageMaster.set(ControlMode.PercentOutput, speed);
//		SmartDashboard.putNumber("main stage speed", speed);
//		SmartDashboard.putNumber("main stage id", this.mainStageMaster.getDeviceID());
	}
	public void setSecondStageSpeed(double speed){
		this.secondStageMotor.set(ControlMode.PercentOutput, speed);
	}

	public void debug(){
		SmartDashboard.putNumber("Main stage motor encoder counts", mainStageMaster.getSelectedSensorPosition(Constants.PidIdx));
		SmartDashboard.putNumber("Second stage motor encoder counts", secondStageMotor.getSelectedSensorPosition(Constants.PidIdx));
	}


}
