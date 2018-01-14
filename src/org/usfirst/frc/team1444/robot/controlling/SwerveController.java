package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.SwerveDrive;
import org.usfirst.frc.team1444.robot.SwerveModule;

public class SwerveController implements RobotController {
	private static final double ROTATE_DEAD_ZONE = .1;  // used to make sure the user really wants to turn while still
	private static final double ROTATE_MULTIPLIER = .4; // The speed that is applied while rotating
	private static final double SPEED_DEAD_ZONE = .05;  // used to check if the user really wants to go forward

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
		speed = Math.pow(speed, 2);  // Because speed is a value from 0 to 1, we need pow not sqrt

		// Calculate direction of Swerve wheels to allow agility
		double vertical = controller.leftStickVertical();  // y
		double horizontal = controller.leftStickHorizontal();  // x
		double magnitude = Math.hypot(vertical, horizontal); // always positive
		if(magnitude > .3){  // make sure that the person inputting really wants to change steer position.
			this.rotation = (float) Math.toDegrees(Math.atan2(vertical, horizontal));
		}

		double turnAmount = controller.rightStickHorizontal(); // -1 is left, 1 is right
		if(Math.abs(speed) >= SPEED_DEAD_ZONE || Math.abs(turnAmount) < ROTATE_DEAD_ZONE){
			// if statement used for: if we want to go forward/backwards, or if we don't want to turn
			// basically, this code is run if the user probably doesn't want to turn while still

			drive.rotateAll(this.rotation);  // since we aren't turning, we can turn to where we want to
			// both if statements, set the speed
			if(turnAmount == 0) {
				// I think, if we just use the else statement, it should do the same thing as this, but we'll keep this
				// just to be safe
				drive.setSpeed(speed);
			} else { // if the user wants to turn, then we will do it like a tank drive, adjust the speeds
				// complex algorithm to basically make a tank drive, but in whatever direction you want
				final double angle = Math.toRadians(-this.rotation);
				final SwerveModule[] modules = drive.getModules();
				final int length = modules.length;

				double[] speeds = new double[length];  // array of speeds all with a positive sign or 0
				double maxSpeed = 1;  // can be divided by to scale speeds down if some speeds are > 1.0
				for(int i = 0; i < length; i++){
					SwerveModule module = modules[i];
//					double x = module.getX();
//					double y = module.getY();
					double x, y;  // until we have getters for x and y, use simple case statement
					// I didn't want to change constructors because merging THAT didn't sound fun
					switch(i){
						case 0:
							x = -1; y =  1; break;
						case 1:
							x =  1; y =  1; break;
						case 2:
							x = -1; y = -1; break;
						case 3:
							x =  1; y = -1; break;
						default:
							throw new RuntimeException("The length shouldn't be more than 4.");
					}
					double distanceFromCenter = Math.hypot(x, y); // distance from the center of the robot
					double newY = (x * Math.sin(angle)) + (y * Math.cos(angle)); // around values between -1 and 1
					newY *= -1;  // after multiplying, -1 means module on left, 1 means module on right
					newY /= distanceFromCenter; // Now that this is here, I don't think we need to calc maxSpeed
					// so if dividing my distanceFromCenter, fixes maxSpeed, remove it later

					double absSpeed = Math.abs(speed) - (newY * turnAmount);
					// speed - left(-1) * left(-1)
					speeds[i] = absSpeed;
					if(absSpeed > maxSpeed){
						maxSpeed = absSpeed;
					}
				}
				for(int i = 0; i < length; i++){ // simple for loop to set speeds from variable 'speeds'
					SwerveModule module = modules[i];
					double moduleSpeed = Math.signum(speed) * speeds[i];
					module.setSpeed(moduleSpeed / maxSpeed);
				}
			}
		} else{  // only fires if the user definitely wants to turn while still
			float targetFL;
			float targetFR;
			float targetRL;
			float targetRR;

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

	}
}
