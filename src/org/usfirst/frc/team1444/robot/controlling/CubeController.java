package org.usfirst.frc.team1444.robot.controlling;

import org.usfirst.frc.team1444.robot.Intake;
import org.usfirst.frc.team1444.robot.Lift;
import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.input.JoystickInput;

/**
 * Class that handles the intake and lift
 */
public class CubeController implements RobotController{

	private JoystickInput controller; // later, we might want multiple controllers here

	private LiftMode mode = LiftMode.NONE;

	public CubeController(JoystickInput controller){
		this.controller = controller;
	}


	@Override
	public void update(Robot robot) {
		Intake intake = robot.getIntake();
		Lift lift = robot.getLift();

		intakeUpdate(intake);
		liftUpdate(lift);
	}
	private void intakeUpdate(Intake intake){
		int pov = controller.pov();
		if(pov == -1){
			intake.setSpeed(-.25);
			return;
		}
		double yPov = Math.sin(Math.toRadians(controller.pov())); // because I'm lazy.

		if(yPov != 0) {
			double slider = (controller.slider() + 1) / 2; // now between 0 and 1
			double speed =  slider * (yPov > 0 ? 1 : -1);
			double joystickX = controller.joystickX();

			double leftSpeed = speed;
			double rightSpeed = speed;

			if(joystickX != 0){ // TODO replace with deadband
				if(joystickX < 0){
					leftSpeed *= 1 - joystickX;
				} else {
					rightSpeed *= 1 - joystickX;
				}
			}
			intake.setSpeeds(leftSpeed, rightSpeed);

		} else {
			intake.setSpeed(-.25);
		}

	}
	private void liftUpdate(Lift lift){
		lift.debug();
//		double liftSpeed = controller.joystickY(); // if single joystick, just joystick y
//		liftSpeed *= 1.;
//		lift.setMainStageSpeed(liftSpeed);
//		lift.debug();
		boolean isPressed = true; // set to false in else
		if(controller.trigger()){
			mode = LiftMode.MANUAL;
		} else if (controller.thumbButton()){
			mode = LiftMode.MANUAL_SECOND_STAGE_ONLY;
		} else if (controller.gridTopLeft()){ // TODO change
			mode = LiftMode.MANUAL_MAIN_STAGE_ONLY;
		} else if(controller.thumbTopLeft()){
			mode = LiftMode.SCALE_MAX;
		} else if(controller.thumbTopRight()){
			mode = LiftMode.SWITCH;
		} else if(controller.thumbBottomLeft()){
			mode = LiftMode.MIN_11;
		} else if(controller.thumbBottomRight()){
			mode = LiftMode.MIN_13;
		} else {
			isPressed = false;
		}
		if(!isPressed && mode.needsPress){
			mode = LiftMode.NONE;  // reset mode if they go out of it
		}

		if(mode.isSpecial){
			if(mode == LiftMode.NONE || mode == LiftMode.BRAKE){
//				lift.setMainStageSpeed(0);
//				lift.setSecondStageSpeed(0);
				if(mode == LiftMode.BRAKE){
					// brake mode does same as NONE, so if we wanted, we could try to lock the position when we enter
					// otherwise, this mode does nothing
					
					// Attempt to hold position
					lift.setMainStagePosition(lift.getMainStagePosition());
					lift.setSecondStagePosition(lift.getSecondStagePosition());
					
				}
			} else if(mode == LiftMode.MANUAL || mode == LiftMode.MANUAL_MAIN_STAGE_ONLY || mode == LiftMode.MANUAL_SECOND_STAGE_ONLY){
				double speed = controller.joystickY();

				if (Math.abs(speed) < 0.1) {
					speed = 0;
				}

				if(!isPressed){
					speed = 0;
				}

				boolean both = mode == LiftMode.MANUAL;
				boolean main = mode == LiftMode.MANUAL_MAIN_STAGE_ONLY || both;
				boolean second = mode == LiftMode.MANUAL_SECOND_STAGE_ONLY || both;
				if (main) {
					lift.setMainStageSpeed(speed);
				} else {
					lift.setMainStageSpeed(0);
				}
				if (second) {
					lift.setSecondStageSpeed(speed);
				} else {
					lift.setSecondStageSpeed(0);
				}
			} else {
				throw new UnsupportedOperationException("Do not know how to deal with special mode: " + mode);
			}
		} else {
			lift.setBothPosition(mode.position);
		}
	}
	public LiftMode getLiftMode(){
		return mode;
	}

	public enum LiftMode {
		NONE(), @Deprecated BRAKE(),
		MANUAL(), MANUAL_SECOND_STAGE_ONLY(),
		MANUAL_MAIN_STAGE_ONLY(), // will be used for climbing

		SCALE_MAX(Lift.Position.SCALE_MAX), SCALE_MIN(Lift.Position.SCALE_MIN), SWITCH(Lift.Position.SWITCH),
		MIN_11(Lift.Position.MIN), MIN_13(Lift.Position.MIN_13),
		@Deprecated
		DRIVE(Lift.Position.DRIVE);

		Lift.Position position;

		boolean isSpecial;
		boolean needsPress;

		LiftMode(Lift.Position position, boolean isSpecial, boolean needsPress){
			this.position = position;
			this.isSpecial = isSpecial;
			this.needsPress = needsPress;
		}

		/**
		 * Creates normal LiftMode that doesn't need press
		 */
		LiftMode(Lift.Position position){
			this(position, false, false);
		}

		/**
		 * Creates a special LiftMode that needs a press
		 */
		LiftMode(){
			this(null, true, true);
		}
	}
}
