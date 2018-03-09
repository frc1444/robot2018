package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Intake;
import org.usfirst.frc.team1444.robot.Robot;

public class IntakeDrive extends DistanceDrive {


	private double intakeSpeed;

	private boolean reallyDone = false;

	/**
	 * This constructs an IntakeDrive which will drive the robot forward at the desired speed and run the intake at the
	 * desired speed
	 *
	 * @param distanceInInches distance to go forward in inches
	 * @param speed The speed to drive the robot at (-1 to 1) Normally positive
	 * @param intakeSpeed Should usually be between 0 and -1 where -1 makes it go in
	 */
	public IntakeDrive(double distanceInInches, double speed, double intakeSpeed){
		super(distanceInInches, 90, false, speed);
		this.intakeSpeed = intakeSpeed;
//		if(intakeSpeed > 0){
//			System.err.println("Are you sure you want to run the motors positive while driving? - From " + this.getClass().getName());
//		}
	}

	@Override
	public void update(Robot robot) {
		super.update(robot);
		Intake intake = robot.getIntake();
		if(super.isDone()){
			intake.setSpeed(0);
			this.reallyDone = true;
			return;
		}
		intake.setSpeed(this.intakeSpeed);
	}

	@Override
	protected boolean isDone() {
		return reallyDone;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + String.format("{speed:%s}", intakeSpeed);
	}
}
