package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public class Intake {

	private BaseMotorController left;
	private BaseMotorController right;

	public Intake(BaseMotorController left, BaseMotorController right){
		this.left = left;
		this.right = right;
	}

	/**
	 * Sets the motor speeds
	 * @param speed The speed for the motors to spin. 1 is full speed out (spitting), -1 is full speed intake
	 */
	public void setSpeed(double speed){
		this.setSpeeds(speed, speed);
	}

	/**
	 * Sets each motor speed individually
	 *
	 * @param leftSpeed The speed of the intake wheels on the left of the robot (nearest front left SwerveModule)
	 * @param rightSpeed The speed of the intake wheels on the right of the robot (nearest front right SwerveModule)
	 */
	public void setSpeeds(double leftSpeed, double rightSpeed){
		this.left.set(ControlMode.PercentOutput, leftSpeed);
		this.right.set(ControlMode.PercentOutput, rightSpeed);
	}


}
