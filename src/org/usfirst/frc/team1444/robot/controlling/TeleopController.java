package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;

public class TeleopController implements RobotController {

	private TeleopMode mode = TeleopMode.DRIVE_NORMAL;

	private SwerveController swerveController;
	private CubeController cubeController;

	public TeleopController(ControllerInput driveInput, ControllerInput manipulatorInput){
		this(new SwerveController(driveInput), new CubeController(manipulatorInput));
	}
	TeleopController(SwerveController swerveController, CubeController cubeController){
		this.swerveController = swerveController;
		this.cubeController = cubeController;
	}


	@Override
	public void update(Robot robot) {
		switch(mode) {
			case DRIVE_NORMAL:
				swerveController.update(robot);
				cubeController.update(robot);
				break;
			case CLIMB:
			default:
				throw new UnsupportedOperationException("We haven't implemented mode: " + mode);
		}
	}


	public enum TeleopMode {
		DRIVE_NORMAL,
		CLIMB;
	}
}
