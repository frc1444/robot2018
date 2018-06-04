package org.usfirst.frc.team1444.robot.controlling.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

public class DistanceDrive extends RobotControllerProcess {

	private final double distance; // in inches
	private final double heading; // in degrees
	private final boolean makeRelativeToGyro;
	private final double percentSpeed;
	private final Double desiredRobotRotation;

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
	public DistanceDrive(double distanceInInches, double headingInDegrees, boolean makeRelativeToGyro, double percentSpeed, Double desiredRobotRotation){
		this.distance = distanceInInches;
		this.heading = headingInDegrees;
		this.makeRelativeToGyro = makeRelativeToGyro;
		this.percentSpeed = percentSpeed;

		if(desiredRobotRotation != null) {
			desiredRobotRotation %= 360;
			desiredRobotRotation = desiredRobotRotation < 0 ? desiredRobotRotation + 360 : desiredRobotRotation;
		}
		this.desiredRobotRotation = desiredRobotRotation;
	}
	public DistanceDrive(double distanceInInches, double headingInDegrees, boolean makeRelativeToGyro, double percentSpeed){
		this(distanceInInches, headingInDegrees, makeRelativeToGyro, percentSpeed, null);
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
			double turnAmount = 0;
			if(desiredRobotRotation != null){
				double degreesAway = desiredRobotRotation - robot.getRobotHeadingDegrees(); // if we want to turn left, this will be positive
				if(degreesAway > 180){
					degreesAway -= 360;
				} else if (degreesAway < -180){
					degreesAway += 360;
				}
				turnAmount = degreesAway / 360.0; // can only be -.5 to .5
				turnAmount *= -1; // now if we want to turn left, this will be negative
				turnAmount = Math.max(-1, Math.min(1, turnAmount)); // if we ever decide to change the "/ 360", we need to stay in range
			}
			drive.update(percentSpeed, heading, turnAmount, null);
			SmartDashboard.putNumber("turnAmount", turnAmount);
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
