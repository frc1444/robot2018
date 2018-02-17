package org.usfirst.frc.team1444.robot;

// Constants used throughtout the robotprogram
public final class Constants {
	
	// CAN IDs
	
	public static final int FrontLeftDriveId = 4;
	public static final int FrontRightDriveId = 3;
	public static final int RearLeftDriveId = 2;
	public static final int RearRightDriveId = 1;
	public static final int FrontLeftSteerId = 8;
	public static final int FrontRightSteerId = 7;
	public static final int RearLeftSteerId = 6;
	public static final int RearRightSteerId = 5;
	
	public static final int PdbId = 9;
	
	public static final int MainBoomMasterId = 10;
	public static final int MainBoomSlaveId = 11;
	public static final int SecondaryBoomId = 12;
	public static final int IntakeLeftId = 13;
	public static final int IntakeRightId = 14;
	
	// Digital Outputs
	
	// Digital Inputs
		// Boom down limit
		// Boom up limit
	
	// Analog Inputs
	
	// Misc Motor Controller Constants
	public static final int PidIdx = 0;
	public static final int TimeoutMs = 10;
	
	// Drive Motor Constants
	public static final int CtreUnitConversion = 600; // Conversion to CTRE units of 100 units/ms
	public static final int MaxCimRpm = 5300;
	public static final int CimCoderCountsPerRev = 80;	// Talon SRX counts every edge of the quadrature encoder, so 4 * 20
	
	// Steer Motor Constants
	public static final int QuadSteerCountsPerRev = 1657;	// Determined from PG71 gearmotor with 7 CPR encoder
	public static final int AnalogSteerCountsPerRev = 1024; // Number of counts while using absolute encoder
	

	// Controls
	public static final int JoystickPortNumber = 1;
	public static final int CubeJoystickPortNumber = 2;

	public static final double TriggerDeadband = 0.05; // Deadband for main drive velocity input
	public static final double DirectionDeadband = 0.05; // big because only change direction if hyp big enough
	public static final double RotationRateDeadband = 0.05;

	public static final double FineScaleAmount = .3;
}
