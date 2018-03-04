package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Random;

/**
 * Holds game data to help in autonomous mode for things like which scale is which
 * More info: http://wpilib.screenstepslive.com/s/currentCS/m/getting_started/l/826278-2018-game-data-details
 */
public class GameData {
	private static final Random randomizer = new Random();

	private DriverStation station;
	private String randomizedMessage;

	public GameData(DriverStation station){
		this.station = station;

		this.randomizedMessage = createRandomizedMessage();
	}

	/**
	 * @return whether or not the data will be accurate. return station.isFMSAttached() && !station.isDisabled()
	 */
	public boolean isAccurate(){
		return station.isFMSAttached() && !station.isDisabled();
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
		final String dashString = "Game Specific Message";
		String r = station.getGameSpecificMessage();
		String endString;
		if(r.length() < 3){
			endString = " randomized";
			if(station.isFMSAttached()){
				endString += "(FMS connected not ready)";
			} else {
				endString += "(No FMS)";
			}
			r = this.randomizedMessage;
		} else { // do stuff for endString so we know what's happening with it
			if(station.isFMSAttached()){
				if(station.isDisabled()){
					endString = "(from FMS while disabled (Bad and inaccurate))";
					System.err.println("We are trying to use the game specific message while disabled. It works but might be inaccurate.");
				} else {
					endString = "(from FMS)";
				}
			} else {
				endString = "(from DriverStation GameData)";
			}
		}
		SmartDashboard.putString(dashString, r + " " + endString);
		return r;
	}

	private static String createRandomizedMessage(){
		StringBuilder builder = new StringBuilder(); // because this is faster in java when adding to strings in loops
		for(int i = 0; i < 3; i++){
			builder.append(randomizer.nextBoolean() ? 'L' : 'R');
		}
		return builder.toString();
	}

}
