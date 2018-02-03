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
	// For autonomousChooser
	private static final String DEFAULT_AUTO = "Default";	// Boring default autonomous
	private static final String RAMPAGE_AUTO = "RAMPAGE";	// RAMPAGE

	// For controllerInputChooser
	private static final String PS4_CONTROLLER = "PS4 Controller";
	private static final String SINGLE_JOYSTICK = "Single Joystick";

	private SendableChooser<String> autonomousChooser = new SendableChooser<>();

	private SendableChooser<String> controllerInputChooser = new SendableChooser<>();

	private SwerveDrive drive;
	private RobotController robotController;  // use ***Init to change this to something that fits that mode


	private PidParameters drivePid;
	private PidParameters steerPid;

	public Robot(){ // use the constructor for specific things, otherwise, use robotInit()
		super();
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
		
		final int flOffset = 728;
		final int frOffset = 554;
		final int rlOffset = 552; 
		final int rrOffset = 135;
		
		// Initialize the drive by passing in new TalonSRXs for each drive and steer motor
		// length: 29.5 width: 20.5
		drive = new SwerveDrive(
				new TalonSRX(Constants.FrontLeftDriveId), new TalonSRX(Constants.FrontLeftSteerId),
				new TalonSRX(Constants.FrontRightDriveId), new TalonSRX(Constants.FrontRightSteerId),
				new TalonSRX(Constants.RearLeftDriveId), new TalonSRX(Constants.RearLeftSteerId),
				new TalonSRX(Constants.RearRightDriveId), new TalonSRX(Constants.RearRightSteerId),
				drivePid, steerPid, flOffset, frOffset, rlOffset, rrOffset, 29, 20.5);
		this.setRobotController(null);

		// Setup dashboard autonomousChooser
		autonomousChooser.addDefault(DEFAULT_AUTO, DEFAULT_AUTO); // since DEFAULT_AUTO is a String, use for both
		autonomousChooser.addObject(RAMPAGE_AUTO, RAMPAGE_AUTO);
		SmartDashboard.putData("Auto choices", autonomousChooser);

		controllerInputChooser.addDefault(PS4_CONTROLLER, PS4_CONTROLLER);
		controllerInputChooser.addObject(SINGLE_JOYSTICK, SINGLE_JOYSTICK);
		SmartDashboard.putData("Controller Type", controllerInputChooser);
		
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

	@Override
	public void autonomousInit() {
		setRobotController(null);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		// We probably won't but code in here. Probably do this in autonomousInit and check in here if we
		// have the right robotController
		switch (autonomousChooser.getSelected()) {
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
		robotController = new SwerveController(createControllerInput(1));
	}

	/**
	 * Uses controllerInputChooser to create the correct ControllerInput object
	 */
	private ControllerInput createControllerInput(int port){
		String selected = controllerInputChooser.getSelected();
		switch(selected){
			case PS4_CONTROLLER:
				return new PS4Controller(port);
			case SINGLE_JOYSTICK:
				return new SingleJoystickInput(port);
			default:
				break;
		}
		throw new RuntimeException("controllerInputChooser has selected: '" + selected + "'");
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
