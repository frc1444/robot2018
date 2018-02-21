package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.GameData;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class AutonomousController extends RobotControllerProcess {

	private boolean isInitialized = false;
	private RobotController currentController = null; // will be null when we have got to the right place

	public AutonomousController(){
		super();
	}
	private void initCurrent(Robot robot){
		GameData data = robot.getGameData();
		SmartDashboard.putBoolean("Is data accurate:", data.isAccurate());

		GameData.StartingPosition position = data.getStartingPosition();
		if(position == GameData.StartingPosition.MIDDLE || true){
			/*
			Note that we can't be inside the exchange zone meaning that we can't be directly in the middle, we'll be
			a little to the right of the center
			 */
			final double speed = .3; // change speed here
			final double depth = robot.getFrameDepth() + robot.getIntakeExtendsDistance();
			DistanceDrive driveToPowerCubeZone = new DistanceDrive(98 - depth, 90, true, speed);

			DistanceDrive driveToOurSide = new DistanceDrive(65, data.isHomeSwitchLeft() ? 180 : 0, true, speed);
			LiftController raiseLift = new LiftController(0.0, 1.0);
			MultiController raiseAndDrive = new MultiController(driveToOurSide, raiseLift);

			final double distanceToSwitch = 4.8 * 12;
			DistanceDrive halfToSwitch = new DistanceDrive(distanceToSwitch * (2.5 / 3.0), 90, true, speed);

			IntakeDrive moveAndSpit = new IntakeDrive(distanceToSwitch * (.5 / 3.0), speed, 1);


			this.currentController = driveToPowerCubeZone;
			driveToPowerCubeZone.setNextController(raiseAndDrive);
			raiseAndDrive.setNextController(halfToSwitch);
			halfToSwitch.setNextController(moveAndSpit);
			moveAndSpit.setNextController(null);
		} else {
			// just some random comments that Josh needs:
			// 185

			// 125 at V
			// 63 degrees
			this.setNextController(new DistanceDrive(30, 90, true, .3));
		}
	}

	@Override
	public void update(Robot robot) {
		if(!isInitialized){
			initCurrent(robot);
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
}
