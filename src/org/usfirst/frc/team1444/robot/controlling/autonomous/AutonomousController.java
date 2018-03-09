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
	public static void initModeChooser(){
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
		SmartDashboard.putData("auto modes:", modeChooser);
	}


	private void initDesiredAuto(Robot robot){
//		GameData data = robot.getGameData();
//		SmartDashboard.putBoolean("Is data accurate:", data.isAccurate());

		SmartDashboard.putString("for auto:", robot.getGameData().toString());
		AutoMode mode = modeChooser.getSelected();
		switch(mode){
//			case SMART_LEFT: case SMART_RIGHT:
//				this.initSideAutonomous(robot, mode);
//				break;
			case CENTER_ORIGINAL:
				this.initOriginalMiddleAutonomous(robot);
				break;
			case CENTER_IMPROVED:
				this.initImprovedMiddleAutonomous(robot);
				break;
			case DRIVE_TO_LINE: // we don't need a whole method for this
				this.currentController = new DistanceDrive(140 - robot.getDepth(true), 90, true, SPEED);
				break;
			default:
				if(mode.position != null){ // starting on the side
					initSideAutonomous(robot, mode);
				} else {
					System.err.println("We won't do anything in auto mode: " + mode);
				}
				break;
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
//			this.initStartingIntakeController()
//				.setNextController(driveToPowerCubeZone)
			this.currentController = driveToPowerCubeZone;
			driveToPowerCubeZone
					.setNextController(raiseAndDrive)
					.setNextController(halfToSwitch)
					.setNextController(moveAndSpit);
	}

	private void initImprovedMiddleAutonomous(Robot robot){
		System.out.println("improved auton.");
		final GameData data = robot.getGameData();
		final double angle = 63.0;
		DistanceDrive longDrive = new DistanceDrive(125, data.isHomeSwitchLeft() ? 180 - angle : angle, true, SPEED);
		LiftController raiseLift = new LiftController(Lift.Position.SWITCH);
//		MultiController driveAndLift = new MultiController(longDrive, raiseLift);

		DistanceDrive shortDrive = new DistanceDrive(30, 90, true, SPEED);

		// link controllers
//		this.initStartingIntakeController()
//				.setNextController(longDrive)
		this.currentController = longDrive;
		longDrive
//				.setNextController(new WaitProcess(300, null)) // nextController will be below
				.setNextController(raiseLift)
				.setNextController(shortDrive)
				.setNextController(new TimedIntake(1000, 1));
	}

	private void initSideAutonomous(Robot robot, AutoMode mode){
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
		assert mode.position != null;
		boolean startingLeft = mode.position;

		final GameData data = robot.getGameData();

		RobotControllerProcess.Builder builder = new RobotControllerProcess.Builder();

		final double IN_ANGLE = startingLeft ? 0 : 180; // the angle to go closer to center
		final double OUT_ANGLE = startingLeft ? 180 : 0; // the angle to go closer to wall
		final TimedIntake spitOut = new TimedIntake(800, 1);

		final double driveSwitchAngle = 80; // estimated
		DistanceDrive driveSideSwitch = new DistanceDrive(185 - robot.getDepth(false), // measured
				startingLeft ? 180 - driveSwitchAngle : driveSwitchAngle, true, SPEED);
		builder.append(driveSideSwitch);

		// now the robot is between the wall and the side of switch. It's bumper should not be past the back of the home switch

		if((startingLeft == data.isHomeSwitchLeft() &&
				(startingLeft != data.isScaleLeft() || mode.onlySwitch) &&
				!mode.onlyScale)) { // put stuff on this switch if scale to far
			// take control over the switch quickly (the switch is close to use)
			builder.append(new MultiController( // raise and turn
					new TurnToHeading(IN_ANGLE),
					new LiftController(Lift.Position.SWITCH)
			));

			final double distanceToSwitch = 30;
			builder.append(new DistanceDrive(distanceToSwitch + 15, IN_ANGLE, true, SPEED)); // drive to switch

			builder.append(spitOut); // spit out

			final double distanceToMoveAway = 15;
			builder.append(new DistanceDrive(distanceToMoveAway, OUT_ANGLE, true, SPEED)); // get away from side of switch
		} else {
			builder.append(new DistanceDrive(65, 90, true, .7)); // move past switch so we're in between the scale and the back of the switch

			if(mode.onlySwitch){
				// go for far switch
				builder.append(new DistanceDrive(227 - robot.getWidth(), IN_ANGLE, true, SPEED));
				// TODO because we came off drive station wall at angle, may need to go more than 227 - width
				builder.append(new MultiController(
						new TurnToHeading(-90),
						new LiftController(Lift.Position.SWITCH)
				));
				builder.append(new DistanceDrive(15, -90, true, SPEED * (2.0 / 3.0))); // estimated
				builder.append(spitOut);
				builder.append(new DistanceDrive(15, 90, true, .3));
//				builder.append(new LiftController(Lift.Position.MIN));
			} else {
				// go for the scale
				final boolean isScaleLeft = data.isScaleLeft();

				if (startingLeft != isScaleLeft) { // is scale on the other side?
					// drive between scale and home switch to get to our side of scale
					builder.append(new DistanceDrive(200 - robot.getWidth() + 20, IN_ANGLE, true, .7)); // estimated
					builder.append(new DistanceDrive(64, IN_ANGLE, true, SPEED)); // don't do full speed the whole time
					// TODO because we came off the driver station wall at an angle, we may need to go more than 264 - width
					builder.append(new DistanceDrive(10, OUT_ANGLE, true, SPEED)); // we're probably on the wall, so move in a little
				}
				// Our position is now isScaleLeft NOT startingPosition
				builder.append(new DistanceDrive(45 + robot.getDepth(false), 90, true, SPEED)); // measured
				// now we are between the wall and the scale

				final double scaleAngle = isScaleLeft ? 0 : 180;
//				builder.append(new MultiController(
//						new LiftController(Lift.Position.SCALE_MAX), // raise
//						new TurnToHeading(scaleAngle) // turn
//				));
				builder.append(new TurnToHeading(scaleAngle));
				builder.append(new LiftController(Lift.Position.SCALE_MAX));

				// drive a little bit closer to the scale
				builder.append(new DistanceDrive(10, scaleAngle, true, .2)); // estimated
				builder.append(spitOut); // spit out
				// once we have got the cube in the scale, we can tell what side we are on based on data.isScaleLeft()
				builder.append(new DistanceDrive(15, scaleAngle + 180, true, .2));
//				builder.append(new LiftController(Lift.Position.MIN));
			}
		}
		// note that after afterPossibleScore, the robot may be turned different depending on which clause above executed
		//  so use the gyro (obviously) and use TurnToHeading to change heading if needed

//		builder.attachTo(this.initStartingIntakeController());
		this.currentController = builder.build();
	}

//	/**
//	 * This can be used if we are starting with the cube with the intake up (folded in)
//	 * <p>
//	 * Sets this.currentController to a controller that will make the robotStart doing something to make sure the
//	 * intake comes down the right way and to make sure it stays in
//	 * <p>
//	 * After calling this method, it is recommended that you call setNextController on the returned value, and pass a
//	 * IntakeDrive just to make sure it is in
//	 * @return The controller that you should call setNextController on
//	 */
//	@Deprecated
//	private RobotControllerProcess initStartingIntakeController(){
//		LiftController up = new LiftController(0.0, .3);
//
//		LiftController down = new LiftController(0.0, 0.0);
//
//		TimedIntake shortIntake = new TimedIntake(350, -.5);
//
//		this.currentController = up;
//		return up.setNextController(down)
//				.setNextController(shortIntake);
//	}

	// endregion ========= End Auto Modes =========

	@Override
	public void update(Robot robot) {
		if(!isInitialized){
//			robot.getGyro().reset(); do in Robot
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
	public String toString() {
		if(currentController == null){
			return getClass().getSimpleName() + "{done}";
		}
		return getClass().getSimpleName() + "{currentController: " + currentController.toString() + " }";
	}

	private enum AutoMode{
		DRIVE_TO_LINE("drive to line"),
		CENTER_IMPROVED("CENTER improved auto", null),
		CENTER_ORIGINAL("CENTER original auto", null),

		// modes where we start on the left
		SMART_LEFT("smart LEFT auto", true),
		SWITCH_LEFT("switch LEFT auto", true, true, false),
		SCALE_LEFT("scale LEFT auto", true, false, true),

		// modes where we start on the right
		SMART_RIGHT("smart RIGHT auto", false),
		SWITCH_RIGHT("switch RIGHT auto", false, true, false),
		SCALE_RIGHT("scale RIGHT auto", false, false, true),

		NOTHING("nothing");

		private final String name;
		/** true is left, false is right, null is middle */
		private final Boolean position;

		private final boolean onlySwitch;
		private final boolean onlyScale;

		AutoMode(String name){
			this(name, null);
		}
		AutoMode(String name, Boolean isLeft){
			this(name, isLeft, false, false);
		}
		AutoMode(String name, Boolean isLeft, boolean onlySwitch, boolean onlyScale){
			this.name = name;
			this.position = isLeft;
			this.onlySwitch = onlySwitch;
			this.onlyScale = onlyScale;
		}

	}
}
