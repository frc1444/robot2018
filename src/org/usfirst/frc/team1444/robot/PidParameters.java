package org.usfirst.frc.team1444.robot;

public class PidParameters {
	
	public double KP;
	public double KI;
	public double KD;
	public double KF;
	public int pidIdx;
	
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
}
