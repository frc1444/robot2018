package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * Holds game data to help in autonomous mode for things like which scale is which
 * More info: http://wpilib.screenstepslive.com/s/currentCS/m/getting_started/l/826278-2018-game-data-details
 */
public class GameData {

	private static SendableChooser<StartingPosition> startingChooser = null;

	private DriverStation station;

	public GameData(DriverStation station){
		this.station = station;
		initStartingChooser();
	}

	private static void initStartingChooser(){
		if(startingChooser != null){
			return;
		}
		startingChooser = new SendableChooser<>();
		String text = "Starting ";
		startingChooser.addDefault(text + StartingPosition.LEFT.name, StartingPosition.LEFT);
		startingChooser.addObject(text + StartingPosition.MIDDLE.name, StartingPosition.MIDDLE);
		startingChooser.addObject(text + StartingPosition.RIGHT.name, StartingPosition.RIGHT);
	}
	public StartingPosition getStartingPosition(){
		return startingChooser.getSelected();
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
		String r = station.getGameSpecificMessage();
		if(r.length() < 3){
			r = "LLL";
		}
		return r;
	}

	public enum StartingPosition{
		LEFT("left"), MIDDLE("middle"), RIGHT("right");

		final String name;
		StartingPosition(String name){
			this.name = name;
		}

	}

}
