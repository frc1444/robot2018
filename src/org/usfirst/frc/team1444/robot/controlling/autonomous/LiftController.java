package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
	public LiftController(Lift.Position position){
		if(position != null){
			mainStagePosition = position.getMainPosition();
			secondStagePosition = position.getSecondPosition();
		} else {
			mainStagePosition = null;
			secondStagePosition = null;
		}
	}

	@Override
	public void update(Robot robot) {
		Lift lift = robot.getLift();
		double mainAway = 0;
		double secondAway = 0;
		if(mainStagePosition != null){
			lift.setMainStagePosition(mainStagePosition);
			mainAway = mainStagePosition - lift.getMainStagePosition(); // positive when going up
		}
		if(secondStagePosition != null){
			lift.setSecondStagePosition(secondStagePosition);
			secondAway = secondStagePosition - lift.getSecondStagePosition();
		}
		SmartDashboard.putString("LiftController mainAway, secondAway", "" + mainAway + ", " + secondAway);
		done = Math.abs(mainAway) <= ALLOWED_DEADBAND_FOR_SUCCESS &&
				Math.abs(secondAway) <= ALLOWED_DEADBAND_FOR_SUCCESS;
	}

	@Override
	protected boolean isDone() {
		return done;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + String.format("{1st pos:%s, 2nd pos:%s, done:%s}", mainStagePosition, secondStagePosition, done);
	}
}
