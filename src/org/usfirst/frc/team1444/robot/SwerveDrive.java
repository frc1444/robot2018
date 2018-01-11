package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;


// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor ouputs
public class SwerveDrive {
	
	private SwerveModule m_FrontLeft;
	private SwerveModule m_FrontRight;
	private SwerveModule m_RearLeft;
	private SwerveModule m_RearRight;
	
	public SwerveDrive(TalonSRX flDrive, TalonSRX flSteer,
			TalonSRX frDrive, TalonSRX frSteer,
			TalonSRX rlDrive, TalonSRX rlSteer,
			TalonSRX rrDrive, TalonSRX rrSteer) {
		
		m_FrontLeft = new SwerveModule(flDrive, flSteer);
		m_FrontRight = new SwerveModule(frDrive, frSteer);
		m_RearLeft = new SwerveModule(rlDrive, rlSteer);
		m_RearRight = new SwerveModule(rrDrive, rrSteer);
	}

}
