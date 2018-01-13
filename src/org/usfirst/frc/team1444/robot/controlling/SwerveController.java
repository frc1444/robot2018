package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;

public class SwerveController implements RobotController {
	private static final double ROTATE_DEAD_ZONE = .1;
	private static final double ROTATE_MULTIPLIER = .4;

	private float rotation = 90;
	private ControllerInput controller;

	public SwerveController(ControllerInput controller){
		this.controller = controller;
	}

	@Override
	public void update(Robot robot) {
		this.drive(robot.getDrive());
	}

	/**
	 * Controls: left trigger - forwards, right trigger - backwards
	 * lstick - direction of all wheels, rstick(x axis only) - turn robot
	 *
	 * If the speed of the robot is not 0 and the robot is turning because of rstick -> if positive, set back wheels
	 * straight, if negative, set front wheels straight. Each time, it won't be completely straight and it resets
	 * rotation to 90
	 *
	 * @param drive the SwerveDrive object
	 */
	private void drive(SwerveDrive drive){
		SwerveModule frontLeft = drive.getFrontLeft(),
				frontRight = drive.getFrontRight(),
				rearLeft = drive.getRearLeft(),
				rearRight = drive.getRearRight();
		// calculate motor speeds
		double speed = controller.rightTrigger() - controller.leftTrigger();

		// Calculate direction of Swerve wheels to allow agility
		double vertical = controller.leftStickVertical();  // y
		double horizontal = controller.leftStickHorizontal();  // x
		double magnitude = Math.hypot(vertical, horizontal); // always positive
		if(magnitude > .3){  // make sure that the person inputting really wants to change steer position.
			this.rotation = (float) Math.toDegrees(Math.atan2(vertical, horizontal));
		}

		double turnAmount = controller.rightStickHorizontal(); // -1 is left, 1 is right
		if(Math.abs(turnAmount) > ROTATE_DEAD_ZONE){ // if true, the user wants to turn, otherwise go to else
			// In here, you basically steer with lstick, and accelerate with the bumpers

			// target steer positions for each module in degrees // used by both ifs, different values set in each
			float targetFL;
			float targetFR;
			float targetRL;
			float targetRR;

			// Note this if statement won't fire until we remove && false and we finish it
			if(Math.abs(speed) > .1 && false){  // Happens when the user wants to go into car driving mode

				final double speedMultiplier = 1 - (Math.abs(turnAmount) / 2);
				// ^ stop user from going fast while turning

//				double speedFL = turnAmount, speedFR = turnAmount, speedRL = turnAmount, speedRR = turnAmount;
				// TODO use above values and adjust depending on how much you are turning (if needed for smoothness)

				if(speed > 0){  // going forward
					targetFL = 90f + ((float) turnAmount * -60f);
					targetFR = targetFL;

					targetRL = 90;
					targetRR = 90;

				} else {
					targetFL = 90;
					targetFR = 90;

					targetRL = 90f + ((float) turnAmount * 60f);
					targetRR = targetRL;
				}

				// TODO finish this if statement with code here

			} else {  // more likely to fire than above. Used to rotate the robot while staying still.

				targetFL = 45;
				targetFR = 270 + 45; // -45
				targetRL = 90 + 45;
				targetRR = 180 + 45; // -(90 + 45)
				// the target rotations for all the modules

				// rotate all motors
				frontLeft.steerTo(targetFL);
				frontRight.steerTo(targetFR);
				rearLeft.steerTo(targetRL);
				rearRight.steerTo(targetRR);

				// set speeds of rotated motors
				drive.setSpeed(turnAmount * ROTATE_MULTIPLIER);
			}
		} else {
			drive.setSpeed(speed);
			drive.rotateAll(this.rotation);  // since we aren't turning, we can turn to where we want to
		}
	}
}
