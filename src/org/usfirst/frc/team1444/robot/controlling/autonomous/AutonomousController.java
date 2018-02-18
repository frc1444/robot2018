package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class AutonomousController extends RobotControllerProcess {

	/*
	 * This may not be used all because we can just use different RobotControllerProcesses
	 */

	public AutonomousController(){
		super();
	}

	@Override
	public void update(Robot robot) {

	}

	@Override
	protected boolean isDone() {
		return false;
	}
}
