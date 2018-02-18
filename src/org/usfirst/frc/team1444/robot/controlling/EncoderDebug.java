package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveModule;

public class EncoderDebug implements RobotController {
	
	@Override
	public void update(Robot robot) {
		for(SwerveModule module : robot.getDrive().getModules()) {
			module.debug();
		}
		robot.getLift().debug();
		
	}
	
}
