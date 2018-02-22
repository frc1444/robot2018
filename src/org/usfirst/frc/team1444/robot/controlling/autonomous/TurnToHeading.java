package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class TurnToHeading extends RobotControllerProcess {

	private static final double ALLOWED_DEADBAND_FOR_SUCCESS = 5; // if 3, then 6 degrees total allowed
	private static final double MAX_SPEED = .3;

	private boolean done = false;
	private double rotation; // desired rotation of robot in degrees (0 - 360)

	/**
	 * Allows you to make the robot face a certain direction
	 * @param rotationInDegrees The rotation in degrees you want to turn to where 90 is straight forward and 0 is to the right
	 */
	public TurnToHeading(double rotationInDegrees){
		rotationInDegrees %= 360;
		rotationInDegrees = rotationInDegrees < 0 ? rotationInDegrees + 360 : rotationInDegrees;
		this.rotation = rotationInDegrees;
	}

	@Override
	public void update(Robot robot) {
		SwerveDrive drive = robot.getDrive();
		double currentRotation = robot.getGyro().getAngle() + 90; // 90 - 450
		currentRotation %= 360; // 0 - 360


		double degreesAway = rotation - currentRotation; // if we want to turn left, this will be positive
		if(Math.abs(degreesAway) >= 180){
			degreesAway = currentRotation - rotation;
		}
		degreesAway %= 360;
		degreesAway = degreesAway < 0 ? degreesAway + 360 : degreesAway;
		// now calculating degreesAway is complete

		if(Math.abs(degreesAway) <= ALLOWED_DEADBAND_FOR_SUCCESS){
			this.done = true;
//			drive.update(0, null, 0, null);
			drive.setAllSpeeds(0);
			return;
		}
		double speed = MAX_SPEED;
		final double absDegreesAway = Math.abs(degreesAway);
		if(absDegreesAway < 70){
			speed *= Math.pow(absDegreesAway / 70, .75);
		}
//		drive.update(0, null, speed, null);
		drive.rotateAroundSubDrive(speed, null, false);
	}

	@Override
	protected boolean isDone() {
		return done;
	}
}
