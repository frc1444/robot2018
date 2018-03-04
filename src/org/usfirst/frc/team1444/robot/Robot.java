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
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.BNO055.IMUMode;
import org.usfirst.frc.team1444.robot.LEDHandler.LEDMode;
import org.usfirst.frc.team1444.robot.controlling.*;
import org.usfirst.frc.team1444.robot.controlling.autonomous.AutonomousController;
import org.usfirst.frc.team1444.robot.controlling.autonomous.ResetEncoderController;
import org.usfirst.frc.team1444.robot.controlling.input.ControllerInput;
import org.usfirst.frc.team1444.robot.controlling.input.JoystickInput;
import org.usfirst.frc.team1444.robot.controlling.input.LogitechExtremeJoystickInput;
import org.usfirst.frc.team1444.robot.controlling.input.PS4Controller;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	// if false, then whenever we enable, we will reset the gyro. if true, gyro is good until we disable
	private static final String IS_GYRO_RESET_KEY = "Is Gyro Reset"; // will appear on SmartDashboard

	private final double frameWidth = 28.0;
	private final double frameDepth = 33.0;
	private final double intakeExtendsDistance = 10; // compared to frame (not to bumper) // TODO measure this
	private final double bumperExtendsDistance = 3.5;

	private PidHandler pidHandler;

	private SwerveDrive drive;
	private Intake intake;
	private Lift lift;
	private BNO055 IMU;
	private LEDHandler ledHandler;
	
	private ControllerInput driveInput;
	private JoystickInput manipulatorInput;

	private GameData gameData; // Should only be used after match has started (Shouldn't be used in disabled mode)

	private RobotController robotController;  // use ***Init to change this to something that fits that mode

//	private boolean isGyroReset = false; // should only be used in checkGyro()


	public Robot(){ // use the constructor for specific things, otherwise, use robotInit()
		super();
	}
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		this.pidHandler = new PidHandler();
		try{
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);
			camera.setFPS(9);
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("Unable to start camera server.");
		}
		//gyro = new ADXRS450_Gyro(Port.kOnboardCS0);
		//gyro.calibrate();

		IMU = new BNO055();
		IMU.SetMode(IMUMode.NDOF);

		try {
			LightDrive2812 lightDrive = new LightDrive2812();
			this.ledHandler = new LEDHandler(lightDrive);
		} catch(Exception ex){
			System.err.println("\nStart error message for Light Drive");
			ex.printStackTrace();
		}

		PidParameters drivePid = new PidParameters();
		drivePid.KF = 1;
		drivePid.KP = 1.5;
		drivePid.closedRampRate = .25;

		PidParameters steerPid = new PidParameters();
		steerPid.KP = 8;
		steerPid.KI = 0.008;
		
		final int flOffset = 515;
		final int frOffset = 703;
		final int rlOffset = 559;
		final int rrOffset = 333;
		
		// Initialize the drive by passing in new TalonSRXs for each drive and steer motor
		drive = new SwerveDrive(
				new TalonSRX(Constants.FrontLeftDriveId), new TalonSRX(Constants.FrontLeftSteerId),
				new TalonSRX(Constants.FrontRightDriveId), new TalonSRX(Constants.FrontRightSteerId),
				new TalonSRX(Constants.RearLeftDriveId), new TalonSRX(Constants.RearLeftSteerId),
				new TalonSRX(Constants.RearRightDriveId), new TalonSRX(Constants.RearRightSteerId),
				drivePid, steerPid, flOffset, frOffset, rlOffset, rrOffset, 27.375, 22.25);

		this.intake = new Intake(new TalonSRX(Constants.IntakeLeftId), new TalonSRX(Constants.IntakeRightId));

		this.lift = new Lift(
				new TalonSRX(Constants.MainBoomMasterId), new TalonSRX(Constants.MainBoomSlaveId),
				new TalonSRX(Constants.SecondaryBoomId));

		this.gameData = new GameData(DriverStation.getInstance());

		this.setRobotController(null);
		
	}

	@Override
	public void disabledInit() {
		setRobotController(null);
	}
	
	public void disabledPeriodic() {

	}
	public PidHandler getPidHandler(){
		return pidHandler;
	}

	public double getWidth(){
		return frameWidth + bumperExtendsDistance;
	}
	public double getDepth(boolean includeIntakeExtends){
		return frameDepth + bumperExtendsDistance + (includeIntakeExtends ? intakeExtendsDistance : 0);
	}

	public SwerveDrive getDrive() {
		return drive;
	}
	
	public RobotController getController() {
		return this.robotController;
	}

	/**
	 * Note that the return value of getAngle will be 0 when facing straight (which is 90 degrees in most of the code)
	 * This means that when adding the gyro value, it has no side effects and no need to add or subtract 90. However
	 * if you want to get your current heading, you should add 90 to the return value of getAngle()
	 * <p>
	 * This is not saying that the implementation of Gyro should account for this, it's saying the opposite. The reason
	 * for this is that unlike the SwerveModules, we don't want to have to turn the robot sideways to 0 the gyro
	 *
	 * @return the gyro this robot uses.
	 */
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
		pidHandler.update();
		this.checkGyro();

		if(ledHandler != null) {
			//SmartDashboard.putBoolean("LED working:", true);
			
			if (isDisabled()) {
				//MODE WHEN DISABLED
				ledHandler.setMode(LEDMode.TEAM_RAINBOW);
				//<-- Maybe we should make a TEAM_COLOR mode that does a rainbow-like fade through blue/redish colors...
				//    say no more ^
			} else if(DriverStation.getInstance().getMatchTime() < 16.0) {
				//MODE WHEN IN FINAL COUNTDOWN (TELEOP OR AUTO)
				ledHandler.setMode(LEDMode.COUNTDOWN);
			} else if (isOperatorControl() && this.robotController instanceof TeleopController) {
				//MODES WHEN IN TELEOP
				TeleopController controller = (TeleopController) this.robotController;
				
				if (controller.getCubeController().getLiftMode() != CubeController.LiftMode.NONE) {
					ledHandler.setMode(LEDMode.MOVE_WITH_LIFT);
				} else {
					ledHandler.setMode(LEDMode.DRIVE_SPEED);
				}
			} else if(isTest()) {
				//MODE IN TEST MODE
				ledHandler.setMode(LEDMode.RSL_LIGHT);
			} else {
				//DEFAULT MODE
				ledHandler.setMode(LEDMode.TEAM_COLOR);
			}
			
			try {
				this.ledHandler.update(this);
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
		}

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
//		this.robot_state = Robot_State.AUTO;
		RobotController nextController = new AutonomousController();
//		RobotController nextController = new DistanceDrive(12 * 5, 90, false, .3);
		setRobotController(new ResetEncoderController(nextController));
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
		driveInput = new PS4Controller(Constants.JoystickPortNumber);
		manipulatorInput = new LogitechExtremeJoystickInput(Constants.CubeJoystickPortNumber);
		setRobotController(new ResetEncoderController(new TeleopController(driveInput, manipulatorInput)));
		
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
		ledHandler.setMode(LEDMode.RSL_LIGHT);
//		this.robot_state = Robot_State.TEST;
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	// region Reset Gyro Code
	private boolean isGyroReset(){
		return SmartDashboard.getBoolean(IS_GYRO_RESET_KEY, false);
	}
	private void setGyroReset(boolean b){
		SmartDashboard.putBoolean(IS_GYRO_RESET_KEY, b);
	}
	/**
	 * Resets the gyro if necessary
	 */
	private void checkGyro(){
		if(isDisabled()){
			setGyroReset(false);
			return;
		}
		if(isGyroReset()){
			return;
		}
		setGyroReset(true);
		this.IMU.reset();
	}
	// endregion

}
