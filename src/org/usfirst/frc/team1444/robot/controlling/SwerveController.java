package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;

public class SwerveController implements RobotController {
	private ControllerInput m_MainController;

	public SwerveController(ControllerInput controller){
		m_MainController = controller;
	}

	@Override
	public void update(Robot robot) {
		drive(robot.getDrive());
	}
	private void drive(SwerveDrive drive){
		// set motor speeds // may need to change later since we will adjust speed when turning using right stick
		double speed = m_MainController.rightTrigger() - m_MainController.leftTrigger();
		drive.setSpeed(speed);

		// Set direction of Swerve wheels to allow agility
		double vertical = m_MainController.leftStickVertical();  // y
		double horizontal = m_MainController.leftStickHorizontal();  // x
		double magnitude = Math.hypot(vertical, horizontal); // always positive
		if(magnitude > .3){
			float rotation = (float) Math.toDegrees(Math.atan2(vertical, horizontal));

		}
	}
}
