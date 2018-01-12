package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.Joystick;

public class SwerveController implements RobotController{
	private Joystick m_MainController;

	public SwerveController(){
		m_MainController = new Joystick(0);
	}

	@Override
	public void update(Robot robot) {

	}
}
