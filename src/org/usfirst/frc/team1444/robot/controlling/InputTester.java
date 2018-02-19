package org.usfirst.frc.team1444.robot.controlling;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.input.ControllerInput;

public class InputTester implements RobotController {
	private ControllerInput controller;

	public InputTester(ControllerInput controller){
		this.controller = controller;
	}

	@Override
	public void update(Robot robot) {
		SmartDashboard.putString("rthumb top, bottom, left, right", String.format("%s, %s, %s, %s",
				controller.rightThumbTop(), controller.rightThumbBottom(),
				controller.rightThumbLeft(), controller.rightThumbRight()));
		SmartDashboard.putString("lstick (x, y)", String.format("x: %s y: %s",
				controller.leftStickX(),
				controller.leftStickY()));
		SmartDashboard.putString("rstick (x, y)", String.format("x: %s y: %s",
				controller.rightStickX(),
				controller.rightStickY()));
		SmartDashboard.putString("lb, rb", String.format("%s, %s", controller.leftBumper(), controller.rightBumper()));
	}
}
