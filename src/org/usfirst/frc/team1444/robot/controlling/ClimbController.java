package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;

/**
 * Since we probably need climbing to control differently, we can use this class to control it while climbing
 *
 * This class is not definite, just an idea. Another idea would be to send a message to CubeController and
 * SwerveController in TeleopController telling them what mode they're in
 */
public class ClimbController implements RobotController {

	public ClimbController(){
	}

	@Override
	public void update(Robot robot) {

	}
}
