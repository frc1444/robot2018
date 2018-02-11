package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;
import org.usfirst.frc.team1444.robot.controlling.RobotController;

/**
 * In the autonomous package because this doesn't use any ControllerInputs
 * Since this would take less than a second, we might be able to do it in the 15 seconds autonomous mode.
 */
public class ResetEncoderController implements RobotController {

	private static final double ALLOWED_DEGREES_FOR_ZERO = 2; // can be within -x and x including -x and x

	public ResetEncoderController(){

	}

	@Override
	public void update(Robot robot) {
		SwerveDrive drive = robot.getDrive();
		boolean allReady = true;
		for(SwerveModule module : drive.getModules()){
			if(module.isQuad()){
				continue;
			}
			module.setPosition(0);

			double position = module.getSensorPositionDegrees();
			position = position > 180 ? position - 360 : position;

			if(Math.abs(position) > ALLOWED_DEGREES_FOR_ZERO){
				allReady = false;
			}
		}

		if(allReady){
			// TODO add delay so modules that are able to, get closer to 0 degrees before we switch
			drive.switchToQuad();
		}

	}
}
