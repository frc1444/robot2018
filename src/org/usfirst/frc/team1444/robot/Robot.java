/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.controlling.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	private static final String DEFAULT_AUTO = "Default";	// Boring default autonomous
	private static final String RAMPAGE_AUTO = "RAMPAGE";	// RAMPAGE
	private String autoSelected;
	private SendableChooser<String> chooser = new SendableChooser<>();

	private SwerveDrive drive;
	private RobotController robotController;  // use ***Init to change this to something that fits that mode

	private final ControllerInput defaultController;
	
	private PidParameters drivePid;
	private PidParameters steerPid;

	public Robot(){ // use the constructor for specific things, otherwise, use robotInit()
		super();
		defaultController = new PS4Controller(1);  // set the default controller to a PS4Controller
	}
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		drivePid = new PidParameters();
		drivePid.KF = 1;
		drivePid.KP = 1.5;
		
		steerPid = new PidParameters();
		steerPid.KP = 8;
		steerPid.KI = 0.008;
		//steerPid.KD = 10;
		
//		final int flOffset = 135;
//		final int frOffset = 552;
//		final int rlOffset = 116; 
//		final int rrOffset = 289;
		
		final int flOffset = 728;
		final int frOffset = 554;
		final int rlOffset = 552; 
		final int rrOffset = 135;
		
		// Initialize the drive by passing in new TalonSRXs for each drive and steer motor
		drive = new SwerveDrive(
				new TalonSRX(Constants.FrontLeftDriveId), new TalonSRX(Constants.FrontLeftSteerId),
				new TalonSRX(Constants.FrontRightDriveId), new TalonSRX(Constants.FrontRightSteerId),
				new TalonSRX(Constants.RearLeftDriveId), new TalonSRX(Constants.RearLeftSteerId),
				new TalonSRX(Constants.RearRightDriveId), new TalonSRX(Constants.RearRightSteerId),
				drivePid, steerPid, flOffset, frOffset, rlOffset, rrOffset);
		this.setRobotController(null);

		// Setup dashboard chooser
		chooser.addDefault("Default Auto", DEFAULT_AUTO);
		chooser.addObject("RAMPAGE", RAMPAGE_AUTO);
		SmartDashboard.putData("Auto choices", chooser);
		
	}

	@Override
	public void disabledInit() {
		setRobotController(null);
	}

	public SwerveDrive getDrive() {
		return drive;
	}
	public void setRobotController(RobotController robotController){
		this.robotController = robotController;
		if(this.robotController == null){
			this.robotController = new EncoderDebug();  // make the default controller an InputTester
		}
	}

	@Override
	public void robotPeriodic() {
		if(robotController != null) {
			robotController.update(this);
		}
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 * <p>
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		setRobotController(null);
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
			case RAMPAGE_AUTO:
				// RAMPAGE
				break;
			case DEFAULT_AUTO:
			default:
				// Boring default auto
				break;
		}
	}

	@Override
	public void teleopInit() {
		robotController = new SwerveController(defaultController);
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
	}

	@Override
	public void testInit() {
		setRobotController(null);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

}
