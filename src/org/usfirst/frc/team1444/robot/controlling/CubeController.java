package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Intake;
import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;

/**
 * Class that handles the intake and lift
 */
public class CubeController implements RobotController{

	private ControllerInput controller; // later, we might want multiple controllers here

	public CubeController(ControllerInput controller){
		this.controller = controller;
		// TODO since we haven't used this class much yet, controller might be null (Something to fix in Robot.java)
	}


	@Override
	public void update(Robot robot) {
		Intake intake = robot.getIntake();
		Lift lift = robot.getLift();

		intakeUpdate(intake);
		liftUpdate(lift);
	}
	private void intakeUpdate(Intake intake){

	}
	private void liftUpdate(Lift lift){
		double liftSpeed = controller.leftStickY(); // if single joystick, just joystick y
		liftSpeed *= 1.;
		lift.setMainStageSpeed(liftSpeed);
		lift.debug();
	}
}
