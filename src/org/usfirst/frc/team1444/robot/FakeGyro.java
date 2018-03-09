package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.interfaces.Gyro;

public class FakeGyro implements Gyro {
	private static FakeGyro instance = null;

	private FakeGyro(){
	}

	public static FakeGyro getInstance(){
		if(instance == null){
			instance = new FakeGyro();
		}
		return instance;
	}

	@Override
	public void calibrate() {

	}

	@Override
	public void reset() {

	}

	@Override
	public double getAngle() {
		return 0;
	}

	@Override
	public double getRate() {
		return 0;
	}

	@Override
	public void free() {

	}
}
