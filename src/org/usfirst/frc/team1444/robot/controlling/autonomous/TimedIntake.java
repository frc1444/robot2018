package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Intake;
import org.usfirst.frc.team1444.robot.Robot;

public class TimedIntake extends WaitProcess {
	private final double intakeSpeed;

	private boolean reallyDone = false;

	public TimedIntake(long timeMillis, double intakeSpeed){
		super(timeMillis, null);
		this.intakeSpeed = intakeSpeed;
	}

	@Override
	public void update(Robot robot) {
		super.update(robot);
		if(super.isDone()){
			System.out.println("really done now");
			robot.getIntake().setSpeed(0);
			reallyDone = true;
			return;
		}

		Intake intake = robot.getIntake();
		intake.setSpeed(intakeSpeed);
	}

	@Override
	protected boolean isDone() {
		return reallyDone;
	}
}
