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
		if(position == GameData.StartingPosition.MIDDLE){
			/*
			Note that we can't be inside the exchange zone meaning that we can't be directly in the middle, we'll be
			a little to the right of the center
			 */
			final double speed = .3; // change speed here
			final double depth = robot.getFrameDepth() + robot.getIntakeExtendsDistance();
			DistanceDrive driveToPowerCubeZone = new DistanceDrive(30 - depth, 90, true, speed);

			DistanceDrive driveToOurSide = new DistanceDrive(50, data.isHomeSwitchLeft() ? 180 : 0, true, speed);
			// TODO raise lift
			DistanceDrive driveToSwitch = new DistanceDrive(30 - depth, 90, true, speed);
			// TODO spit out cube


			this.currentController = driveToPowerCubeZone;
			driveToPowerCubeZone.setNextController(driveToOurSide);
			driveToOurSide.setNextController(driveToSwitch);
			driveToSwitch.setNextController(null);
		} else {
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
