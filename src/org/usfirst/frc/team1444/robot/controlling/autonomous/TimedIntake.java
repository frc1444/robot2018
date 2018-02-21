package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class TimedIntake extends RobotControllerProcess {
	private final long timeMillis;
	private final double intakeSpeed;

	private Long startTime;

	public TimedIntake(long timeMillis, double intakeSpeed){
		this.timeMillis = timeMillis;
		this.intakeSpeed = intakeSpeed;
	}

	@Override
	public void update(Robot robot) {
		long time = System.currentTimeMillis();
		if(startTime == null){
			startTime = time;
		}
		robot.getIntake().setSpeed(intakeSpeed);
	}

	@Override
	protected boolean isDone() {
		return startTime + timeMillis < System.currentTimeMillis();
	}
}
