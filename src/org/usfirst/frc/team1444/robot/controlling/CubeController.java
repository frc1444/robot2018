package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Intake;
import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;

public class CubeController implements RobotController{

	private ControllerInput controller;

	public CubeController(ControllerInput controller){
		this.controller = controller;
		// TODO since we haven't used this class much yet, controller might be null (Something to fix in Robot.java)
	}


	@Override
	public void update(Robot robot) {
		Intake intake = robot.getIntake();
		Lift lift = robot.getLift();
		// Do stuff with these here
		// If we are going to do the crazy two wheeled thing, make sure we separate this stuff into another method at some point
	}
}
