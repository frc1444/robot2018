package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;

/**
 * This class will have an instance stored in Robot which can change throughout the game or just when changing modes.
 *
 * When you want to change the RobotController, you can either construct a new instance of a subclass of
 * RobotController, or in that subclass we can have a static getInstance() method (singleton) and of course, set
 * the m_controller field in Robot to that instance
 *
 * This is used to wait for things like button presses in teleop or checking sensors in autonomous (if we do something
 * advanced in autonomous mode) and then calling other code that will execute like that of the SwerveDrive class
 * or other mechanisms
 */
public interface RobotController {

	/**
	 * Called whenever this class is the controller in Robot
	 *  @param robot The main Robot class that contains SwerveDrive etc.
	 *
	 */
	void update(Robot robot);
	// later we could make this return RobotController (if we wanted to change it when we press a button)
	// (return this or null to keep it the same if we choose to change it to that)

	/**
	 *
	 * @param pov the value in degrees from Joystick#getPov();
	 * @return The correct value in degrees based off of how degrees should work OR -1 if passed pov is -1
	 */
	static int calculatePov(int pov){
		if(pov == -1){
			return pov;
		}
		int r = -1 * (pov - 90);
		if(r < 0){
			r += 360;
		}
		return r;
	}
	
}
