package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public class Lift {
	private final int mainStageEncoderCounts = 999;
	private final int secondStageEncoderCounts = 999;

	private BaseMotorController mainStageMainMotor;

	private BaseMotorController secondStageMotor;

	public Lift(BaseMotorController mainStageMainMotor, BaseMotorController mainStageSecondMotor,
	            BaseMotorController secondStageMotor){

		// set instance variables
		this.mainStageMainMotor = mainStageMainMotor;
		this.secondStageMotor = secondStageMotor;

		// configure the heck out of passed controllers
		// main stage
		mainStageMainMotor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, Constants.TimeoutMs);
		mainStageMainMotor.setInverted(false);

		mainStageSecondMotor.follow(mainStageMainMotor);
		mainStageSecondMotor.setInverted(true);

		// second stage


	}

	// region stage positions
	/**
	 * Method that takes a position and starts to move the main stage to that position
	 * @param position wanted position of main stage (0 to 1)
	 */
	public void setMainStagePosition(double position){
		this.mainStageMainMotor.set(ControlMode.Position, position * mainStageEncoderCounts);
	}

	/**
	 * Method that takes a position and starts to move the second stage to that position
	 * @param position wanted position of second stage (0 to 1)
	 */
	public void setSecondStagePosition(double position){
		this.secondStageMotor.set(ControlMode.Position, position * secondStageEncoderCounts);
	}

	public double getMainStagePosition(){ // TODO if this method is not used, make it private or remove it
		throw new UnsupportedOperationException("Getting the position is not yet implemented"); // to make it compile
	}
	public double getSecondStagePosition(){
		throw new UnsupportedOperationException("Getting the position is not yet implemented"); // to make it compile
	}
	// endregion

	public void setMainStageSpeed(double speed){
		this.mainStageMainMotor.set(ControlMode.PercentOutput, speed);
	}
	public void setSecondStageSpeed(double speed){
		this.secondStageMotor.set(ControlMode.PercentOutput, speed);
	}

	public void debug(){

	}


}
