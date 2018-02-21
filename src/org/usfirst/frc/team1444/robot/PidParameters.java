package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.IMotorController;

public class PidParameters {
	
	public double KP;
	public double KI;
	public double KD;
	public double KF;
	public int pidIdx;

	public double closedRampRate = 0;
	
	PidParameters() {
		this.KP = 0;
		this.KI = 0;
		this.KD = 0;
		this.KF = 0;
		this.pidIdx = 0;
	}
	
	PidParameters(PidParameters pid) {
		this.KP = pid.KP;
		this.KI = pid.KI;
		this.KD = pid.KD;
		this.KF = pid.KF;
		this.pidIdx = pid.pidIdx;
	}

	/**
	 * Simple util method to apply the current PID values of this instance to passed motor
	 *
	 * @param motor The motor controller to apply the PID values to
	 */
	public void apply(IMotorController motor){
		motor.config_kP(pidIdx, KP, Constants.TimeoutMs);
		motor.config_kI(pidIdx, KI, Constants.TimeoutMs);
		motor.config_kD(pidIdx, KD, Constants.TimeoutMs);
		motor.config_kF(pidIdx, KF, Constants.TimeoutMs);
		motor.configClosedloopRamp(closedRampRate, Constants.TimeoutMs);
	}


}
