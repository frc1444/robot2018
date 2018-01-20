package org.usfirst.frc.team1444.robot;

// Constants used throughtout the robotprogram
public final class Constants {
	
	// CAN IDs
	
	public static final int FrontLeftDriveId = 1;
	public static final int FrontRightDriveId = 2;
	public static final int RearLeftDriveId = 3;
	public static final int RearRightDriveId = 4;
	public static final int FrontLeftSteerId = 5;
	public static final int FrontRightSteerId = 6;
	public static final int RearLeftSteerId = 7;
	public static final int RearRightSteerId = 8;
	
	public static final int PdbId = 9;
	
	// TODO: some of these may become PWM outputs
	public static final int BoxElevatorId = 10;
	public static final int LeftIntakeId = 11;
	public static final int RightIntakeId = 12;
	public static final int BoxGrabberId = 13;
	public static final int BoxEjectId = 14; 
	public static final int ClimbId = 15;
	
	// Digital Outputs
	
	// Digital Inputs
	
	// Analog Inputs
	
	// Misc Motor Controller Constants
	public static final int PidIdx = 0;
	public static final int TimeoutMs = 10;
	
	// Drive Motor Constants
	public static final int CtreUnitConversion = 600; // Conversion to CTRE units of 100 units/ms
	public static final int MaxCimRpm = 5000;
	public static final int CimCoderCountsPerRev = 20;
	
	// Controls
	public static final double DriveDeadBand = 0.1; 
	public static final double SteerDeadBand = 0.1;

}
