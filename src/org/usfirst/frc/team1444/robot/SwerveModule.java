package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	private IMotorController m_Drive;  // basically the same as SpeedController in c++
	private IMotorController m_Steer;
	
	public SwerveModule(IMotorController drive, IMotorController steer) {
		// if we need to, add parameter SwerveDrive if that has state that we need to take into account
		m_Drive = drive;
		m_Steer = steer;
	}

	// These methods are just ideas on how this class might turn out.

	/**
	 * @param speed A number between -1 and 1 where 1 is full speed and -1 is full speed in reverse
	 */
	public void setSpeed(double speed){
		m_Drive.set(ControlMode.PercentOutput, speed);
		// TODO add if statements to create a dead zone, and make sure that the motor isn't about to explode
	}

	/**
	 * Unfinished method
	 * @param position A float in degrees where 90 degrees is straight forward and 0 is to the right 180 left etc.
	 */
	public void rotateTo(float position){
//		m_Steer.set(ControlMode.Position, position)
// "In Position mode, output value is in encoder ticks or an analog value, depending on the sensor."

	}

	public void update(){
		// TODO use this to stop the motors for the rotateTo method.
	}

}
