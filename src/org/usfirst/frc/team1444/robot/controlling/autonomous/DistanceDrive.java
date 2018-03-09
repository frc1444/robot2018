package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class DistanceDrive extends RobotControllerProcess {

	private final double distance; // in inches
	private final double heading; // in degrees
	private final boolean makeRelativeToGyro;
	private final double percentSpeed;

	private Double startingDistance = null; // initialized on first call to update
	private boolean done = false;

	/**
	 *
	 * @param distanceInInches distance in inches to move the robot
	 * @param headingInDegrees rotation in degrees relative to the gyro. 90 is forward, 0 is right, 180 is left
	 * @param makeRelativeToGyro Should headingInDegrees be relative to the gyro. (If false, headingInDegrees will be left
	 *                           alone when passed to SwerveDrive)
	 * @param percentSpeed The percent speed of the wheels
	 */
	public DistanceDrive(double distanceInInches, double headingInDegrees, boolean makeRelativeToGyro, double percentSpeed){
		this.distance = distanceInInches;
		this.heading = headingInDegrees;
		this.makeRelativeToGyro = makeRelativeToGyro;
		this.percentSpeed = percentSpeed;
	}


	@Override
	public void update(Robot robot) {
		SwerveDrive drive = robot.getDrive();
		SwerveModule referenceModule = drive.getFrontLeft();
		double currentDistance = referenceModule.getTotalDistanceGone();
		if(startingDistance == null){
			startingDistance = currentDistance;
		}
		// use abs because if quick reverse is enabled, it might possibly go backwards. TODO maybe change quick reverse
		if(Math.abs(currentDistance - startingDistance) > distance){
			done = true;
			drive.update(0, null, 0, null);
		} else {
			double heading = this.heading;
			if(makeRelativeToGyro){
				heading += robot.getGyroAngle();
			}
			drive.update(percentSpeed, heading, 0, null);
		}

	}

	@Override
	protected boolean isDone() {
		return done;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + String.format("{distance:%s,heading:%s,relGyro:%s,speed:%s}", distance, heading, makeRelativeToGyro, percentSpeed);
	}
}
