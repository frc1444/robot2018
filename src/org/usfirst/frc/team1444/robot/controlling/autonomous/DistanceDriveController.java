package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class DistanceDriveController extends RobotControllerProcess {

	private double distance; // in inches
	private double heading; // in degrees

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
	}

	@Override
	protected boolean isDone() {
		return false;
	}

}
