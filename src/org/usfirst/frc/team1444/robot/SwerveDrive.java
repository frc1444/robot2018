package org.usfirst.frc.team1444.robot;


import com.ctre.phoenix.motorcontrol.IMotorController;

// SwerveDrive defines the drive for the entire robot
// This class will take control input and assign motor outputs
public class SwerveDrive {
	
	private SwerveModule m_FrontLeft;
	private SwerveModule m_FrontRight;
	private SwerveModule m_RearLeft;
	private SwerveModule m_RearRight;

	private SwerveModule[] m_ModuleArray;  // can be used in methods below getters


	/**
	 * Initializes SwerveDrive and creates SwerveModules. Even though each parameter is a IMotorController,
	 * we can still pass it a TalonSRX
	 */
	public SwerveDrive(IMotorController flDrive, IMotorController flSteer,
	                   IMotorController frDrive, IMotorController frSteer,
	                   IMotorController rlDrive, IMotorController rlSteer,
	                   IMotorController rrDrive, IMotorController rrSteer) {
		
		m_FrontLeft = new SwerveModule(flDrive, flSteer);
		m_FrontRight = new SwerveModule(frDrive, frSteer);
		m_RearLeft = new SwerveModule(rlDrive, rlSteer);
		m_RearRight = new SwerveModule(rrDrive, rrSteer);

		m_ModuleArray = new SwerveModule[] { m_FrontLeft, m_FrontRight, m_RearLeft, m_RearRight };
	}

	public SwerveModule getFrontLeft(){ return m_FrontLeft; }
	public SwerveModule getFrontRight(){ return m_FrontRight; }
	public SwerveModule getRearLeft(){ return m_RearLeft; }
	public SwerveModule getRearRight(){ return m_RearRight; }

	// TODO put methods here to rotate all motors to a position and set speed here once finished in SwerveModule

}
