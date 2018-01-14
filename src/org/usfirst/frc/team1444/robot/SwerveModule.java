package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	// checks whether the speed is within this from 0, if so, set speed 0
	private static final double DEAD_ZONE = 0.04;

	private BaseMotorController drive;  // basically the same as SpeedController in c++
	private BaseMotorController steer;
	
	public SwerveModule(BaseMotorController drive, BaseMotorController steer) {
		// if we need to, add parameter SwerveDrive if that has state that we need to take into account
		this.drive = drive;
		this.steer = steer;
		
		// Set the Drive motor to use an incremental encoder
		// TODO: set correct PID id and timeout
		this.drive.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		
		// Set the Drive motor to use an absolute encoder
		// TODO: set correct PID id and timeout
		this.steer.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
	}

	// These methods are just ideas on how this class might turn out.

	/**
	 * @param speed A number between -1 and 1 where 1 is full speed and -1 is full speed in reverse
	 */
	public void setSpeed(double speed){
		// http://www.ctr-electronics.com/downloads/api/java/html/com/ctre/phoenix/motorcontrol/can/BaseMotorController.html#set-com.ctre.phoenix.motorcontrol.ControlMode-double-
		// useful docs on the set method

		if(Math.abs(speed) <= DEAD_ZONE){
			speed = 0;
		}
		drive.set(ControlMode.PercentOutput, speed);
		// TODO add if statements to create a dead zone, and make sure that the motor isn't about to explode
	}

	/**
	 * Unfinished method
	 * @param position A float in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void steerTo(float position){
//		steer.set(ControlMode.Position, position)
// "In Position mode, output value is in encoder ticks or an analog value, depending on the sensor."

	}

	public void update(){
		// TODO use this to stop the motors for the steerTo method.
	}

}
