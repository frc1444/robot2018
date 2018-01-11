package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

// SwerveModule defines one corner of a swerve drive
// Two motor controllers are defined, drive and steer
public class SwerveModule {

	private TalonSRX m_Drive;
	private TalonSRX m_Steer;
	
	public SwerveModule(TalonSRX drive, TalonSRX steer) {
		m_Drive = drive;
		m_Steer = steer;
	}
}
