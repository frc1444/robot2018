package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class WaitProcess extends RobotControllerProcess {

	private final long waitMillis;

	private Long startTime = null;

	public WaitProcess(long waitMillis, RobotController nextController){
		super(nextController);
		this.waitMillis = waitMillis;
	}
	@Override
	public void update(Robot robot) {
		if(startTime == null){
			startTime = System.currentTimeMillis();
		}
	}

	@Override
	protected boolean isDone() {
		if(startTime == null){
			return false;
		}
		return startTime + waitMillis <= System.currentTimeMillis();
	}

	@Override
	public String toString() {
		String timeLeft = "";
		if(startTime != null){
			timeLeft = "" + ((startTime + waitMillis) - System.currentTimeMillis());
		}
		return getClass().getSimpleName() + "{" + timeLeft + "}";
	}
}
