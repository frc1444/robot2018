package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

/**
 * In the autonomous package because this doesn't use any ControllerInputs
 * Since this would take less than a second, we might be able to do it in the 15 seconds autonomous mode.
 */
public class ResetEncoderController extends RobotControllerProcess {

	private static final double ALLOWED_DEGREES_FOR_ZERO = 4.0; // can be within -x and x including -x and x
	private static final long TIME_OUT_AFTER = 3000;
	private static final long WAIT_AFTER_LAST = 100;

	private Long timeOutAt = null; // initialized on first call to update
	private boolean done = false; // true when all set to quad and are done
	private Long resetAt = null; // initialized when waiting to be set to quad

	public ResetEncoderController(RobotController nextController){
		super(nextController);
	}

	@Override
	public void update(Robot robot) {
		if(resetAt != null){
			if(System.currentTimeMillis() >= resetAt){
				this.done = true;
				robot.getDrive().switchToQuad();
				System.out.println("Should have made all quad");
			}
			return;
		}
		if(timeOutAt == null){
			System.out.println("ResetEncoderCounts update for first time.");
			timeOutAt = System.currentTimeMillis() + TIME_OUT_AFTER;
		}
		SwerveDrive drive = robot.getDrive();
		boolean allReady = true;
		boolean allQuad = true;
		for(SwerveModule module : drive.getModules()){
			boolean quad = module.isQuad();
			if(quad){
				continue;
			}
			allQuad = false;
			module.setPosition(0);

			double position = module.getSensorPositionDegrees();
			position = position > 180 ? position - 360 : position;

			if(Math.abs(position) > ALLOWED_DEGREES_FOR_ZERO){
				allReady = false;
			}
		}

		if(allQuad){
			this.done = true;
			return;
		}

		if(allReady || System.currentTimeMillis() >= timeOutAt){
			if(resetAt == null) { // don't keep increasing
				if(allReady){
					System.out.println("ResetEncoderCounts correctly worked.");
				} else {
					System.out.println("ResetEncoderCounts timed out.");
				}
				resetAt = System.currentTimeMillis() + WAIT_AFTER_LAST;
			}
		}

	}

	@Override
	protected boolean isDone() {
		return done;
	}
}
