package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;

public class TeleopController implements RobotController {

	private SwerveController swerveController;
	private CubeController cubeController;

	public TeleopController(ControllerInput driveInput, ControllerInput manipulatorInput){
		this(new SwerveController(driveInput), new CubeController(manipulatorInput));
	}
	protected TeleopController(SwerveController swerveController, CubeController cubeController){
		this.swerveController = swerveController;
		this.cubeController = cubeController;
	}


	@Override
	public void update(Robot robot) {
		swerveController.update(robot);
		cubeController.update(robot);
	}
}
