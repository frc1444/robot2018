package org.usfirst.frc.team1444.robot;

import com.mach.LightDrive.LightDrive2812;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.SwerveController;
import org.usfirst.frc.team1444.robot.controlling.TeleopController;

import java.awt.*;

public class LEDHandler implements RobotController { // even though it implements this, we'll put it in the base package

	private final int outOf = 63;

	private Color lastRainbow = null;

	private int timer = 0;
	private int toggle = 0;

	private LEDMode mode;
	private LightDrive2812 LEDs;

	public LEDHandler(LightDrive2812 lightDrive){
		this.LEDs = lightDrive;
		//LEDs.DebugEnable();
		System.out.println("LightDrive set. Version " + Integer.toString(LEDs.GetVersion()));
	}
	public LightDrive2812 getLEDs(){
		return LEDs;
	}

	public void setMode(LEDMode mode){
		if(this.mode != mode){
			LEDs.ClearLEDs();
		}
		this.mode = mode;
	}
	public LEDMode getMode(){
		return mode;
	}

	@Override
	public void update(Robot robot) {
		double temp = 0.0;

		if(timer++ > 2) {
			timer = 0;
			switch(mode) {
				case TEAM_COLOR:
					if(robot.getGameData().getAlliance() == Alliance.Blue) {
						LEDs.SetRange(Color.BLUE, 0, outOf);
					} else if(robot.getGameData().getAlliance() == Alliance.Red) {
						LEDs.SetRange(Color.RED, 0, outOf);
					} else {
						LEDs.SetRange(Color.ORANGE, 0, outOf);
					}
					break;
				case RSL_LIGHT:
					if(robot.isEnabled()) {
						if(toggle == 0) {
							LEDs.SetRange(Color.ORANGE, 0, outOf);
						} else if(toggle == 2){
							LEDs.SetRange(Color.BLACK, 0, outOf);
						}
					} else {
						LEDs.SetRange(Color.ORANGE, 0, outOf);
					}
					
					if(toggle++ > 2) toggle = 0;
					break;
				case COUNTDOWN:
					//Remaining time in current mode (auton or teleop) in sec
					double remaining = DriverStation.getInstance().getMatchTime();
					LEDs.SetRangeandClear(remaining>10?Color.GREEN:remaining>5?Color.YELLOW:Color.RED, 0, (int)((remaining/15.0)*outOf), outOf);
					break;
				case MOVE_WITH_LIFT:
					Lift lift = robot.getLift();
					temp = lift.getMainStagePosition() * outOf;
					LEDs.SetRangeandClear(temp<40?Color.GREEN:temp<50?Color.YELLOW:Color.RED, 0, (int)temp, outOf);
					break;
				case DRIVE_SPEED:
					SwerveModule referenceModule = robot.getDrive().getFrontLeft();
					temp = referenceModule.getLastSetSpeed();
					LEDs.SetRangeandClear(temp<0?Color.RED:Color.GREEN, 0, (int)Math.abs(temp)*outOf, outOf);
					break;
				case TEAM_RAINBOW:
					Color color;
					int startAt;
					Alliance alliance = robot.getGameData().getAlliance();
					if(alliance == Alliance.Blue){
						startAt = 4;
						color = Color.BLUE;
					} else if(alliance == Alliance.Red){
						startAt = 2;
						color = Color.RED;
					} else {
						startAt = 1; // start at yellow
						color = Color.ORANGE;
					}
					double currentTimeSeconds = System.currentTimeMillis() / 1000.0;
					final double totalTimeInSeconds = 6.0;
					double spotInRevolution = (currentTimeSeconds % totalTimeInSeconds) / totalTimeInSeconds;
					if(spotInRevolution < .5){ // for half of the time
						LEDs.SetRangeandClear(color, 0, outOf, outOf);
					} else { // other half use rainbow
						doLEDRainbow(startAt, totalTimeInSeconds / 2.0, alliance == Alliance.Blue);
					}
					break;
				case RAINBOW:
				default:
					doLEDRainbow(0, 3.0, false);
					break;
			}
			
		}
		LEDs.Update();
	}

	/**
	 * Note the LED probably won't start at startAt unless you are making sure that the current time is a multiple
	 * of secondsForFullRev (timeMillis / 1000.0) % secondsForFullRev < .1 or something
	 *
	 * @param startAt The mode to start at (0-5) 0:green 1:yellow 2:red 3:purple 4:blue 5:cyan
	 * @param secondsForFullRev The amount of seconds it takes in order for the rainbow to do a full cycle
	 * @param backwards should the rainbow go in reverse order?
	 */
	private void doLEDRainbow(int startAt, double secondsForFullRev, boolean backwards){
		assert startAt >=0 && startAt <= 5;

		long time = System.currentTimeMillis();
		final int max = 256 * 6;

		int total = (int) (((time / 1000.0) * (max / secondsForFullRev)) % max);
		if(backwards){
			total = max - total; // basically make count down to 0 and go back to max
			startAt = 5 - startAt; // make sure it still starts at desired color
		}
		int part = total / 256; // 0 - 5
		int currentColor = total % 256;
		int r, g, b;
		switch((part + startAt) % 6){
			case 0: // adding red
				r = currentColor;
				g = 255;
				b = 0;
				break;
			case 1: // remove green
				r = 255;
				g = 255 - currentColor;
				b = 0;
				break;
			case 2: // add blue
				r = 255;
				g = 0;
				b = currentColor;
				break;
			case 3: // remove red
				r = 255 - currentColor;
				g = 0;
				b = 255;
				break;
			case 4: // add green
				r = 0;
				g = currentColor;
				b = 255;
				break;
			case 5: // remove blue
				r = 0;
				g = 255;
				b = 255 - currentColor;
				break;
			default:
				System.err.println("part is: " + part);
				r = 0;
				g = 0;
				b = 0;
				break;
		}
		final double roundTo = 1;
		Color color = new Color((int) (Math.round(r / roundTo) * roundTo),
				(int) (Math.round(g / roundTo) * roundTo),
				(int) (Math.round(b / roundTo) * roundTo));
		if(!color.equals(lastRainbow)) {
			LEDs.SetRangeandClear(color, 0, outOf, outOf); // set all to color
			lastRainbow = color;
		}
	}

	public enum LEDMode{
		TEAM_COLOR,
		TEAM_RAINBOW,
		RSL_LIGHT,
		COUNTDOWN,
		MOVE_WITH_LIFT,
		DRIVE_SPEED,
		RAINBOW
	}

}
