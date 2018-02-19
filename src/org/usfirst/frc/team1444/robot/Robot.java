/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.mach.LightDrive.LightDrive2812;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.BNO055.MODES;
import org.usfirst.frc.team1444.robot.controlling.EncoderDebug;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;
import org.usfirst.frc.team1444.robot.controlling.TeleopController;
import org.usfirst.frc.team1444.robot.controlling.autonomous.ResetEncoderController;
import org.usfirst.frc.team1444.robot.controlling.input.*;

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
	private Intake intake;
	private Lift lift;
	private BNO055 IMU;
	private LEDHandler ledHandler;

//	private LightDrive2812 LEDs;
//	private int timer = 0;
//	private int timer2 = 0;
//	private Color[] colorwheel;

	private GameData gameData; // Should only be used after match has started (Shouldn't be used in disabled mode)

	private RobotController robotController;  // use ***Init to change this to something that fits that mode

//	private PidParameters drivePid;
//	private PidParameters steerPid;

	public Robot(){ // use the constructor for specific things, otherwise, use robotInit()
		super();
	}
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		try{
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			camera.setFPS(15);
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("Unable to start camera server.");
		}
		//gyro = new ADXRS450_Gyro(Port.kOnboardCS0);
		//gyro.calibrate();

		IMU = new BNO055();
		IMU.SetMode(MODES.NDOF);

		this.ledHandler = new LEDHandler(new LightDrive2812());
//		LEDs = new LightDrive2812();
//
//		colorwheel = new Color[6];
//		colorwheel[0] = Color.RED;
//		colorwheel[1] = Color.GREEN;
//		colorwheel[2] = Color.CYAN;
//		colorwheel[3] = Color.ORANGE;
//		colorwheel[4] = Color.PINK;
//		colorwheel[5] = Color.WHITE;
//
		
		PidParameters drivePid = new PidParameters();
		drivePid.KF = 1;
		drivePid.KP = 1.5;

		PidParameters steerPid = new PidParameters();
		steerPid.KP = 8;
		steerPid.KI = 0.008;
		
		final int flOffset = 515;
		final int frOffset = 703;
		final int rlOffset = 559;
		final int rrOffset = 333;
		
		// Initialize the drive by passing in new TalonSRXs for each drive and steer motor
		// length: 28 width: 17.5
		drive = new SwerveDrive(
				new TalonSRX(Constants.FrontLeftDriveId), new TalonSRX(Constants.FrontLeftSteerId),
				new TalonSRX(Constants.FrontRightDriveId), new TalonSRX(Constants.FrontRightSteerId),
				new TalonSRX(Constants.RearLeftDriveId), new TalonSRX(Constants.RearLeftSteerId),
				new TalonSRX(Constants.RearRightDriveId), new TalonSRX(Constants.RearRightSteerId),
				drivePid, steerPid, flOffset, frOffset, rlOffset, rrOffset, 28, 17.5);

		this.intake = new Intake(new TalonSRX(Constants.IntakeLeftId), new TalonSRX(Constants.IntakeRightId));

		PidParameters mainPid = new PidParameters();
		PidParameters secondPid = new PidParameters();
		this.lift = new Lift(
				new TalonSRX(Constants.MainBoomMasterId), new TalonSRX(Constants.MainBoomSlaveId),
				new TalonSRX(Constants.SecondaryBoomId),
				mainPid, secondPid);

		this.gameData = new GameData(DriverStation.getInstance());

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
		this.ledHandler.getLEDs().ClearLEDs();
		this.ledHandler.setMode(LEDHandler.LEDMode.RAINBOW);
	}
	
	public void disabledPeriodic() {

		this.ledHandler.update(this); // TODO also update this in robotPeriodic below
		
	}

	public SwerveDrive getDrive() {
		return drive;
	}
	public BNO055 getGyro(){
		return this.IMU;
	}
	public Intake getIntake(){
		return intake;
	}
	public Lift getLift(){
		return lift;
	}
	public GameData getGameData(){
//		if(isDisabled()) throw new IllegalStateException("GameData may not be accurate while disabled.");
		return gameData;
	}

	public void setRobotController(RobotController robotController){
		this.robotController = robotController;
		if(this.robotController == null){
			this.robotController = new EncoderDebug();  // make the default controller an InputTester
		}
	}

	@Override
	public void robotPeriodic() {
		lift.update();
		if(robotController != null) {
			robotController.update(this);
			if(robotController instanceof RobotControllerProcess){
				robotController = ((RobotControllerProcess) robotController).getNext();
			}
		}
		
		SmartDashboard.putNumber("Gyro (IMU)", IMU.getAngle());
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
		ControllerInput driveInput = createControllerInput(Constants.JoystickPortNumber);
//		ControllerInput manipulatorInput = new SingleJoystickControllerInput(Constants.CubeJoystickPortNumber);
		JoystickInput manipulatorInput = new LogitechExtremeJoystickInput(Constants.CubeJoystickPortNumber);
//		setRobotController(new TeleopController(driveInput, manipulatorInput));
		setRobotController(new ResetEncoderController(new TeleopController(driveInput, manipulatorInput)));

		IMU.reset(); // TODO don't reset gyro here. Do it somewhere else
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
				return new SingleJoystickControllerInput(port);
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
