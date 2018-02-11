package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Holds game data to help in autonomous mode for things like which scale is which
 * More info: http://wpilib.screenstepslive.com/s/currentCS/m/getting_started/l/826278-2018-game-data-details
 */
public class GameData {

	private DriverStation station;

	public GameData(DriverStation station){
		this.station = station;
	}


	public DriverStation.Alliance getAlliance(){
		return station.getAlliance();
	}

	/**
	 * This method is probably what will be used in autonomous mode
	 *
	 * @return Whether or not the switch closest to us is on the left (From our perspective)
	 */
	public boolean isHomeSwitchLeft(){
		return getMessage().charAt(0) == 'L';
	}
	/** @return Whether or not the scale is on the left (From our perspective) */
	public boolean isScaleLeft(){
		return getMessage().charAt(1) == 'L';
	}
	/** @return Whether or not the switch farthest to us is on the left (From our perspective) */
	public boolean isEnemySwitchLeft(){
		return getMessage().charAt(2) == 'L';
	}

	private String getMessage(){
		return station.getGameSpecificMessage();
	}


}
