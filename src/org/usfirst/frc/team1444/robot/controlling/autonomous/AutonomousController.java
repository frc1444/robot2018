package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.GameData;
import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class AutonomousController implements RobotController {
	private static final double SPEED = .5; // TODO test this. Originally it was .3
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
//		GameData data = robot.getGameData();
//		SmartDashboard.putBoolean("Is data accurate:", data.isAccurate());

		AutoMode mode = modeChooser.getSelected();
		switch(mode){
			case LEFT_AUTO: case RIGHT_AUTO:
				boolean left = mode == AutoMode.LEFT_AUTO;
				this.initSideAutonomous(robot, left);
			case CENTER_ORIGINAL:
				this.initOriginalMiddleAutonomous(robot);
			case CENTER_IMPROVED:
				this.initImprovedMiddleAutonomous(robot);
			default:
				System.err.println("Unknown auto mode: " + mode);
		}
	}

	// region ========== Auto Modes ==========
	/**
	 * Original autonomous code by Josh which works well except for the robot turns a little bit
	 * @param robot The robot
	 */
	private void initOriginalMiddleAutonomous(Robot robot){
			final GameData data = robot.getGameData();
			final double depth = robot.getDepth(true); // with intake extends
			DistanceDrive driveToPowerCubeZone = new DistanceDrive(98 - depth, 90, true, SPEED);

			DistanceDrive driveToOurSide = new DistanceDrive(65, data.isHomeSwitchLeft() ? 180 : 0, true, SPEED);
			LiftController raiseLift = new LiftController(0.0, 1.0);
			MultiController raiseAndDrive = new MultiController(driveToOurSide, raiseLift);

			final double distanceToSwitch = 4.8 * 12;
			DistanceDrive halfToSwitch = new DistanceDrive(distanceToSwitch * (2.5 / 3.0), 90, true, SPEED);

			IntakeDrive moveAndSpit = new IntakeDrive(distanceToSwitch * (.5 / 3.0), SPEED, 1);


			// link controllers
//			this.currentController = driveToPowerCubeZone;
			this.initStartingIntakeController()
					.setNextController(driveToPowerCubeZone)
					.setNextController(raiseAndDrive)
					.setNextController(halfToSwitch)
					.setNextController(moveAndSpit);
	}

	private void initImprovedMiddleAutonomous(Robot robot){
		final GameData data = robot.getGameData();
		final double angle = 63.0;
		DistanceDrive longDrive = new DistanceDrive(125, data.isHomeSwitchLeft() ? 180 - angle : angle, true, SPEED);
		LiftController raiseLift = new LiftController(.3, .8);
		MultiController driveAndLift = new MultiController(longDrive, raiseLift);

		DistanceDrive shortDrive = new DistanceDrive(30, 90, true, SPEED);

		TimedIntake spitOut = new TimedIntake(500, 1);

		// link controllers
		this.initStartingIntakeController()
				.setNextController(longDrive)
				.setNextController(driveAndLift)
				.setNextController(shortDrive)
				.setNextController(spitOut);
	}

	private void initSideAutonomous(Robot robot, boolean startingLeft){
		/*
		 * useful: https://firstfrc.blob.core.windows.net/frc2018/Manual/2018FRCGameSeasonManual.pdf
		 * pg 23
		 * pg 27 for alliance wall
		 *
		 * Also note in the code below, since we use if statements, linking RobotControllerProcesses should be done very carefully:
		 * You cannot do this: RobotControllerProcess someProcess = new CoolProcess().setNextController(nextProcess)
		 * because the new CoolProcess() instance will basically be lost
		 * This is why we use the RobotControllerProcess.Builder
		 */
		final GameData data = robot.getGameData();

		RobotControllerProcess.Builder builder = new RobotControllerProcess.Builder();

		final double IN_ANGLE = startingLeft ? 0 : 180; // the angle to go closer to center
		final double OUT_ANGLE = startingLeft ? 180 : 0; // the angle to go closer to wall

		final double driveSwitchAngle = 86.5; // estimated
		DistanceDrive driveSideSwitch = new DistanceDrive(185 - robot.getDepth(false), // measured
				startingLeft ? 180 - driveSwitchAngle : driveSwitchAngle, true, SPEED);
		builder.append(driveSideSwitch);

		// now the robot is between the wall and the side of switch. It's bumper should not be past the back of the home switch

		if(startingLeft == data.isHomeSwitchLeft() && startingLeft != data.isScaleLeft()){ // put stuff on this switch if scale to far
			// take control over the switch quickly
			builder.append(new MultiController( // raise and turn
					new TurnToHeading(IN_ANGLE),
					new LiftController(Lift.Position.SWITCH)
			));

			final double distanceToSwitch = 30;
			builder.append(new DistanceDrive(distanceToSwitch + 5, IN_ANGLE, true, SPEED)); // drive to switch

			builder.append(new TimedIntake(800, 1)); // spit out

			final double distanceToMoveAway = 5;
			builder.append(new DistanceDrive(distanceToMoveAway, OUT_ANGLE, true, SPEED)); // get away from side of switch
		} else {
			// go for the scale

			builder.append(new DistanceDrive(65, 90, true, SPEED)); // move past switch so we're in between the scale and the back of the switch
			if(startingLeft != data.isScaleLeft()){ // is scale on the other side?
				// drive between scale and home switch to get to our side of scale then raise while approaching
				builder.append(new DistanceDrive(190, IN_ANGLE, true, SPEED)); // estimated
			}
			final double approachScaleAngle = 80;
			builder.append(new MultiController(
					new DistanceDrive(64, startingLeft ? approachScaleAngle : 180 - approachScaleAngle, true, SPEED), // raise // measured
					new LiftController(Lift.Position.SCALE_MIN) // raise
			)); // raise and get near scale

			// drive right up to scale
			builder.append(new DistanceDrive(38, 90, true, SPEED / 2.0)); // drive to scale // measured
			builder.append(new TimedIntake(800, 1)); // spit out
			// once we have got the cube in the scale, we can tell what side we are on based on data.isScaleLeft()
		}
		// note that after afterPossibleScore, the robot may be turned different depending on which clause above executed
		//  so use the gyro (obviously) and use TurnToHeading to change heading if needed

		builder.attachTo(this.initStartingIntakeController());
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

		TimedIntake shortIntake = new TimedIntake(350, -.5);

		this.currentController = up;
		return up.setNextController(down)
				.setNextController(shortIntake);
	}
	// endregion ========= End Auto Modes =========

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
