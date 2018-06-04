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
	private static SendableChooser<Boolean> safeChooser = null; // by default false

	private static final String DELAY_KEY = "wait time (s)";

	private boolean isInitialized = false;
	private RobotController currentController = null; // will be null when we have got to the right place

	public AutonomousController(){
		super();
		initChoosers();
	}
	public static void initChoosers(){
		if(modeChooser != null && safeChooser != null){
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

		safeChooser = new SendableChooser<>();
		safeChooser.addDefault("Not safe auton (normal)", false);
		safeChooser.addObject("safe auton (no cross over)", true);
		SmartDashboard.putData("is safe auton", safeChooser);

		if(!SmartDashboard.containsKey(DELAY_KEY)){
			SmartDashboard.putNumber(DELAY_KEY, 0);
		}
	}


	private void initDesiredAuto(Robot robot){

		SmartDashboard.putString("for auto:", robot.getGameData().toString());
		AutoMode mode = modeChooser.getSelected();
		System.out.println("initting auto mode: " + mode.toString());
		switch(mode){
			case CENTER_ORIGINAL:
				this.initOriginalMiddleAutonomous(robot);
				break;
			case CENTER_IMPROVED:
				this.initImprovedMiddleAutonomous(robot);
				break;
			case DRIVE_TO_LINE: // we don't need a whole method for this
				this.currentController = new DistanceDrive(140 - robot.getDepth(true), 90, true, SPEED, 90.0);
				break;
			default:
				if(mode.position != null){ // starting on the side
					initSideAutonomous(robot, mode);
				} else {
					System.err.println("We won't do anything in auto mode: " + mode);
				}
				break;
		}
		// hyjack whatever desired auto was
		long delayMillis = (long) (SmartDashboard.getNumber(DELAY_KEY, 0) * 1000.0);
		System.out.println("Delay millis: " + delayMillis);
		if(delayMillis > 0){
			this.currentController = new WaitProcess(delayMillis, this.currentController);
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
		DistanceDrive longDrive = new DistanceDrive(125, data.isHomeSwitchLeft() ? 180 - angle : angle, true, SPEED, 90.0);
		LiftController raiseLift = new LiftController(Lift.Position.SWITCH);
//		MultiController driveAndLift = new MultiController(longDrive, raiseLift);

		DistanceDrive shortDrive = new DistanceDrive(30, 90, true, SPEED);

		// link controllers
//		this.initStartingIntakeController()
//				.setNextController(longDrive)
		MultiController driveAndLift = new MultiController(longDrive, raiseLift);
		this.currentController = driveAndLift;
		driveAndLift
//				.setNextController(new WaitProcess(300, null)) // nextController will be below
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
		final boolean isSafe = safeChooser.getSelected();

		RobotControllerProcess.Builder builder = new RobotControllerProcess.Builder();

		final double IN_ANGLE = startingLeft ? 0 : 180; // the angle to go closer to center
		final double OUT_ANGLE = startingLeft ? 180 : 0; // the angle to go closer to wall
		final TimedIntake spitOut = new TimedIntake(800, 1);

		DistanceDrive driveSideSwitch = new DistanceDrive(185 - robot.getDepth(false), // measured
				90, true, SPEED, 90.0);
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
			builder.append(new DistanceDrive(distanceToSwitch + 15, IN_ANGLE, true, SPEED, IN_ANGLE)); // drive to switch

			builder.append(spitOut); // spit out

			final double distanceToMoveAway = 15;
			builder.append(new DistanceDrive(distanceToMoveAway, OUT_ANGLE, true, SPEED)); // get away from side of switch
		} else {
			builder.append(new DistanceDrive(60, 90, true, .7)); // move past switch so we're in between the scale and the back of the switch

			if(mode.onlySwitch){
				if(!isSafe) {
					boolean isSwitchLeft = !startingLeft;
					// go for far switch
					builder.append(new DistanceDrive(264 - robot.getWidth(), IN_ANGLE, true, SPEED));

					builder.append(new MultiController(
							new DistanceDrive(75, -90, true, .4),
							new LiftController(Lift.Position.SWITCH)
					));
					builder.append(new TurnToHeading(isSwitchLeft ? 0 : 180));
					builder.append(new TimedIntake(1000, 1));
				}
			} else {
				// go for the scale
				final boolean isScaleLeft = data.isScaleLeft();

				final boolean isFar = startingLeft != isScaleLeft;

				if(!isFar || !isSafe) {
					if (isFar) { // is scale on the other side?
						// drive between scale and home switch to get to our side of scale
						builder.append(new DistanceDrive(200 - robot.getWidth(), IN_ANGLE, true, .7, 90.0)); // estimated
						builder.append(new DistanceDrive(64 + 10, IN_ANGLE, true, SPEED, 90.0)); // don't do full speed the whole time
						// + 10 above just to be save
//						builder.append(new DistanceDrive(10, OUT_ANGLE, true, SPEED)); // we're probably on the wall, so move in a little
					}
					// Our position is now isScaleLeft NOT startingPosition
					builder.append(new DistanceDrive(50 + robot.getDepth(false), 90, true, SPEED, 90.0)); // measured
//					if(!isFar){
//						builder.append(new DistanceDrive(10, OUT_ANGLE, true, .3)); // just make sure we aren't under scale
//					}
					// now we are between the wall and the scale

					final double scaleAngle = isScaleLeft ? 0 : 180;
//					builder.append(new MultiController(
//							new LiftController(Lift.Position.SCALE_MAX), // raise
//							new TurnToHeading(scaleAngle) // turn
//					));
					builder.append(new TurnToHeading(scaleAngle));
					builder.append(new LiftController(Lift.Position.SCALE_MAX));

					// drive a little bit closer to the scale
					builder.append(new DistanceDrive(10, scaleAngle, true, .2, scaleAngle)); // estimated
					builder.append(spitOut); // spit out
					// once we have got the cube in the scale, we can tell what side we are on based on data.isScaleLeft()
					builder.append(new DistanceDrive(15, scaleAngle + 180, true, .2, scaleAngle));
//				    builder.append(new LiftController(Lift.Position.MIN));
				} else {
					System.out.println("We must be using a safe auton mode. We will not cross over. isSafe: " + isSafe);
				}
			}
		}
		// note that after afterPossibleScore, the robot may be turned different depending on which clause above executed
		//  so use the gyro (obviously) and use TurnToHeading to change heading if needed

//		builder.attachTo(this.initStartingIntakeController());
		this.currentController = builder.build();
	}


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
