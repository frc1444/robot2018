package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.GameData;
import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class AutonomousController implements RobotController {
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
				this.initSideAutonomous(robot, left);
			case CENTER_ORIGINAL:
				this.initOriginalMiddleAutonomous(robot);
			case CENTER_IMPROVED:
				this.initImprovedMiddleAutonomous(robot);
			default:
				System.err.println("Unknown auto mode: " + mode);
		}
		// 185
	}

	/**
	 * Original autonomous code by Josh which works well except for the robot turns a little bit
	 * @param robot The robot
	 */
	private void initOriginalMiddleAutonomous(Robot robot){
			final GameData data = robot.getGameData();
			final double depth = robot.getTotalDepth();
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
		useful: https://firstfrc.blob.core.windows.net/frc2018/Manual/2018FRCGameSeasonManual.pdf
		  * pg 23
		  * pg 27 for alliance wall
		 */
		final GameData data = robot.getGameData();

		final double IN_ANGLE = startingLeft ? 0 : 180; // the angle to go closer to center
		final double OUT_ANGLE = startingLeft ? 180 : 0; // the angle to go closer to wall

		final double driveSwitchAngle = 86.5; // estimated
		DistanceDrive driveSideSwitch = new DistanceDrive(185 - robot.getDepth(), // measured
				startingLeft ? 180 - driveSwitchAngle : driveSwitchAngle, true, SPEED);

		// now the robot is between the wall and the switch. It's bumper should not be past the back of the home switch
		RobotControllerProcess afterPossibleScore;
		if(startingLeft == data.isHomeSwitchLeft() && startingLeft != data.isScaleLeft()){ // put stuff on this switch if scale to far
			MultiController raiseAndTurn = new MultiController(
					new TurnToHeading(IN_ANGLE),
					new LiftController(Lift.Position.SWITCH)
			);

			final double distanceToSwitch = 30;
			DistanceDrive driveToSwitch = new DistanceDrive(distanceToSwitch + 5, IN_ANGLE, true, SPEED);

			TimedIntake spitOut = new TimedIntake(800, 1);

			final double distanceToMoveAway = 5;
			DistanceDrive getAwayFromBumpers = new DistanceDrive(distanceToMoveAway, OUT_ANGLE, true, SPEED);

			// link controllers
			afterPossibleScore = driveSideSwitch.setNextController(raiseAndTurn)
					.setNextController(driveToSwitch)
					.setNextController(spitOut)
					.setNextController(getAwayFromBumpers);
		} else { // go for the farther side of the scale (which is our side)
			RobotControllerProcess afterScaleApproachAndRaise;

			final double approachScaleAngle = 80;
			final DistanceDrive approach = new DistanceDrive(64, startingLeft ? approachScaleAngle : 180 - approachScaleAngle, true, SPEED); // measured
			final LiftController raise = new LiftController(Lift.Position.SCALE_MIN);
			if(startingLeft == data.isScaleLeft()){
				afterScaleApproachAndRaise = new MultiController(approach, raise); // raise and get near scale
			} else {
				// drive between scale and home switch to get to our side of scale then raise while approaching
				afterScaleApproachAndRaise = new DistanceDrive(190, IN_ANGLE, true, SPEED) // estimated
						.setNextController(new MultiController(approach, raise));
			}

			// drive right up to scale
			DistanceDrive driveToScale = new DistanceDrive(38, 90, true, SPEED / 2.0); // measured

			TimedIntake spitOut = new TimedIntake(800, 1);

			afterPossibleScore = afterScaleApproachAndRaise.setNextController(driveToScale)
					.setNextController(spitOut);
		}
		// note that after afterPossibleScore, the robot may be turned different depending on which clause above executed
		//  so use the gyro (obviously) and use TurnToHeading to change heading if needed

		this.initStartingIntakeController().setNextController(driveSideSwitch)
				.setNextController(afterPossibleScore);
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
//
//	@Override
//	protected boolean isDone() {
//		return this.getNextController() != null;
//	}

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
