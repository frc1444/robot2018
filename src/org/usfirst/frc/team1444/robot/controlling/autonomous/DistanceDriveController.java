package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class DistanceDriveController extends RobotControllerProcess {

	private final double distance; // in inches
	private final double heading; // in degrees

	private Double startingDistance = null; // initialized on first call to update
	private boolean done = false;

	/**
	 *
	 * @param distanceInInches distance in inches to move the robot
	 * @param headingInDegrees rotation in degrees relative to the gyro
	 */
	public DistanceDriveController(double distanceInInches, double headingInDegrees){
		this.distance = distanceInInches;
		this.heading = headingInDegrees;
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
		} else {
			drive.update(.5, heading, 0, null);
		}

	}

	@Override
	protected boolean isDone() {
		return done;
	}

}
