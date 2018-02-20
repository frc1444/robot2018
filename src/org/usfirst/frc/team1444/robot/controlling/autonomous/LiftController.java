package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class LiftController extends RobotControllerProcess {

	private static final double ALLOWED_DEADBAND_FOR_SUCCESS = 0.07; // 7%

	private Double mainStagePosition;
	private Double secondStagePosition;

	private boolean done = false;

	public LiftController(Double mainStagePosition, Double secondStagePosition){
		super();
		this.mainStagePosition = mainStagePosition;
		this.secondStagePosition = secondStagePosition;
	}

	@Override
	public void update(Robot robot) {
		Lift lift = robot.getLift();
		if(mainStagePosition != null){
			lift.setMainStagePosition(mainStagePosition);
		}
		if(secondStagePosition != null){
			lift.setSecondStagePosition(secondStagePosition);
		}
		done = Math.abs(lift.getMainStagePosition() - mainStagePosition) <= ALLOWED_DEADBAND_FOR_SUCCESS &&
				Math.abs(lift.getSecondStagePosition() - secondStagePosition) <= ALLOWED_DEADBAND_FOR_SUCCESS;
	}

	@Override
	protected boolean isDone() {
		return done;
	}
}
