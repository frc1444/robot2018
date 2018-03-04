package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class TurnToHeading extends RobotControllerProcess {

	private static final double ALLOWED_DEADBAND_FOR_SUCCESS = 5; // if 3, then 6 degrees total allowed
	private static final double MAX_SPEED = .3;

	private boolean done = false;
	private double rotation; // desired rotation of robot in degrees (0 - 360)

	// variables for debug: (do not use in code)
	private Integer debugDegreesAway = null;
	private Integer debugGyroAngle = null;

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
		double currentRotation = robot.getGyro().getAngle() + 90;
		currentRotation %= 360;
		currentRotation = currentRotation < 0 ? currentRotation + 360 : currentRotation;
		debugGyroAngle = (int) currentRotation;


		double degreesAway = rotation - currentRotation; // if we want to turn left, this will be positive
		if(degreesAway > 180){
			degreesAway -= 360;
		} else if (degreesAway < -180){
			degreesAway += 360;
		}
		debugDegreesAway = (int) degreesAway;
		// now calculating degreesAway is complete. if negative, turn to right, if positive, turn to left

		final double absDegreesAway = Math.abs(degreesAway);
		if(absDegreesAway <= ALLOWED_DEADBAND_FOR_SUCCESS){
			this.done = true;
//			drive.update(0, null, 0, null);
			drive.setAllSpeeds(0);
			return;
		}
		double speed = MAX_SPEED;
		if(absDegreesAway < 70){
			speed *= Math.pow(absDegreesAway / 70, .75);
		}
		speed *= -1 * Math.signum(degreesAway);
//		drive.update(0, null, speed, null);
		drive.rotateAroundSubDrive(speed, null, false);
	}

	@Override
	protected boolean isDone() {
		return done;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + String.format("{desired:%s,done:%s | debug: degreesAway:%s, gyro:%s}", rotation, done, debugDegreesAway, debugGyroAngle);
	}
}
