package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.GameData;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class AutonomousController extends RobotControllerProcess {
	private static final double SPEED = .3;
	private static SendableChooser<AutoMode> modeChooser = null;

	private boolean isInitialized = false;
	private RobotController currentController = null; // will be null when we have got to the right place

	public AutonomousController(){
		super();
		initModeChooser();
	}
	private static void initModeChooser(){
		if(modeChooser != null){
			return;
		}
		modeChooser = new SendableChooser<>();
		boolean first = true;
		for(AutoMode mode : AutoMode.values()){
			if(first){
				modeChooser.addDefault(mode.name, mode);
				first = false;
				continue;
			}
			modeChooser.addObject(mode.name, mode);
		}
	}
	private void initDesiredAuto(Robot robot){
		GameData data = robot.getGameData();
		SmartDashboard.putBoolean("Is data accurate:", data.isAccurate());

		AutoMode mode = modeChooser.getSelected();
		switch(mode){
			case LEFT_AUTO: case RIGHT_AUTO:
				boolean left = mode == AutoMode.LEFT_AUTO;
				this.initSideAutonomous(robot, data, left);
			case CENTER_ORIGINAL:
				this.initOriginalMiddleAutonomous(robot, data);
			case CENTER_IMPROVED:
				this.initImprovedMiddleAutonomous(robot, data);
			default:
				System.err.println("Unknown auto mode: " + mode);
		}
		// 185
	}

	/**
	 * Original autonomous code by Josh which works well except for the robot turns a little bit
	 * @param robot The robot
	 */
	private void initOriginalMiddleAutonomous(Robot robot, GameData data){
			final double depth = robot.getFrameDepth() + robot.getIntakeExtendsDistance();
			DistanceDrive driveToPowerCubeZone = new DistanceDrive(98 - depth, 90, true, SPEED);

			DistanceDrive driveToOurSide = new DistanceDrive(65, data.isHomeSwitchLeft() ? 180 : 0, true, SPEED);
			LiftController raiseLift = new LiftController(0.0, 1.0);
			MultiController raiseAndDrive = new MultiController(driveToOurSide, raiseLift);

			final double distanceToSwitch = 4.8 * 12;
			DistanceDrive halfToSwitch = new DistanceDrive(distanceToSwitch * (2.5 / 3.0), 90, true, SPEED);

			IntakeDrive moveAndSpit = new IntakeDrive(distanceToSwitch * (.5 / 3.0), SPEED, 1);


			this.currentController = driveToPowerCubeZone;
			driveToPowerCubeZone.setNextController(raiseAndDrive);
			raiseAndDrive.setNextController(halfToSwitch);
			halfToSwitch.setNextController(moveAndSpit);
			moveAndSpit.setNextController(null);
	}

	private void initImprovedMiddleAutonomous(Robot robot, GameData data){
		final double depth = robot.getFrameDepth() + robot.getIntakeExtendsDistance();
		final double angle = 63.0;
		DistanceDrive longDrive = new DistanceDrive(125, data.isHomeSwitchLeft() ? 180 - angle : angle, true, SPEED);
		LiftController raiseLift = new LiftController(.3, .8);
		MultiController driveAndLift = new MultiController(longDrive, raiseLift);

		DistanceDrive shortDrive = new DistanceDrive(30, 90, true, SPEED);

		TimedIntake spitOut = new TimedIntake(500, 1);

		this.currentController = longDrive;
		longDrive.setNextController(driveAndLift);
		driveAndLift.setNextController(shortDrive);
		shortDrive.setNextController(spitOut);
	}

	private void initSideAutonomous(Robot robot, GameData data, boolean startingLeft){
		/*
		useful: https://firstfrc.blob.core.windows.net/frc2018/Manual/2018FRCGameSeasonManual.pdf
		  * pg 23
		 */
	}

	/**
	 * This can be used if we are starting with the cube with the intake up (folded in)
	 * <p>
	 * Sets this.currentController to a controller that will make the robotStart doing something to make sure the
	 * intake comes down the right way and to make sure it stays in
	 * <p>
	 * After calling this method, it is recommended that you call setNextController on the returned value, and pass a
	 * IntakeDrive just to make sure it is in
	 * @return The controller that you should call setNextController on
	 */
	private RobotControllerProcess initStartingIntakeController(){
		LiftController up = new LiftController(0.0, .3);

		LiftController down = new LiftController(0.0, 0.0);

		TimedIntake shortIntake = new TimedIntake(200, -.5);

		up.setNextController(down);
		down.setNextController(shortIntake);
		return shortIntake;
	}

	@Override
	public void update(Robot robot) {
		if(!isInitialized){
			initDesiredAuto(robot);
			isInitialized = true;
		}
		if(currentController != null) {
			currentController.update(robot);
			if(currentController instanceof RobotControllerProcess){
				this.currentController = ((RobotControllerProcess) currentController).getNext();
			}
		}

	}

	@Override
	protected boolean isDone() {
		return this.getNextController() != null;
	}

	private enum AutoMode{
		CENTER_ORIGINAL("CENTER original auto"),
		CENTER_IMPROVED("CENTER improved auto"),
		LEFT_AUTO("LEFT auto"), RIGHT_AUTO("RIGHT auto");

		private final String name;
		AutoMode(String name){
			this.name = name;
		}
	}
}
