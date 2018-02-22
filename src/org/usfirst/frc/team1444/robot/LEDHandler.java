package org.usfirst.frc.team1444.robot;

import com.mach.LightDrive.LightDrive2812;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.SwerveController;
import org.usfirst.frc.team1444.robot.controlling.TeleopController;

import java.awt.*;

public class LEDHandler implements RobotController { // even though it implements this, we'll put it in the base package


	private Color lastRainbow = null;

	private int timer = 0;
	private int timer2 = 0;
	private int count = 0;

	private LEDMode mode;
	private LightDrive2812 LEDs;

	public LEDHandler(LightDrive2812 lightDrive){
		this.LEDs = lightDrive;
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

	@Override
	public void update(Robot robot) {
		double temp = 0.0;
		final int outOf = 63;
		if(timer++ > 9) {
		switch(mode){
			case TEAM_COLOR:
				if(robot.getGameData().getAlliance() == Alliance.Blue) {
					LEDs.SetRange(Color.BLUE, 0, outOf);
				} else if(robot.getGameData().getAlliance() == Alliance.Red) {
					LEDs.SetRange(Color.RED, 0, outOf);
				} else {
					LEDs.SetRange(Color.ORANGE, 0, outOf);
				}
				break;
			case MOVE_WITH_LIFT:
				Lift lift = robot.getLift();
				temp = lift.getMainStagePosition() * outOf;
				LEDs.SetRangeandClear(temp<40?Color.GREEN:temp<50?Color.YELLOW:Color.RED, 0, (int)temp, outOf);
				break;
			case DRIVE_SPEED:
				RobotController controller = robot.getController();
				if(controller instanceof TeleopController){
					SwerveController swerve = ((TeleopController) controller).getSwerveController();
					temp = swerve.getControllerInput().rightTrigger() * 63.0; // TODO this may not be accurate
					LEDs.SetRangeandClear(temp<0?Color.RED:Color.GREEN, 0, (int)Math.abs(temp), outOf);
				} else {
					// maybe we're in autonomous mode. Maybe we should get data from the talons/modules instead of the joystick
				}
				break;
			case RAINBOW:
			default:
				long time = System.currentTimeMillis();
				time *= .3; // now time isn't accurate but we will use it
				// start with (0, 255, 0) -> add red (255, 255, 0) -> remove green (255, 0, 0) -> add blue (255, 0, 255)
				// -> remove red (0, 0, 255) -> add green (0, 255, 255) -> remove blue (0, 255, 0)
				final int max = 256 * 6;
				int total = (int) ((time) % max);
				int part = total / 256; // 0 - 5
				int currentColor = total % 256;
				SmartDashboard.putNumber("total", total);
				SmartDashboard.putNumber("part", part);
				SmartDashboard.putNumber("currentColor", currentColor);

				int r, g, b;
				switch(part){
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
						b = 0;
						break;
					case 5: // remove blue
						r = 0;
						g = 255;
						b = 255 - currentColor;
						break;
					default:
//						System.err.println("part is: " + part);
						r = 0;
						g = 0;
						b = 0;
				}
				Color color = new Color(r, g, b);
				if(!color.equals(lastRainbow)) {
					SmartDashboard.putString("color:", color.toString());
					LEDs.SetRangeandClear(color, 0, outOf, outOf); // set all to color
					lastRainbow = color;
				}

				break;
		}
		timer = 0;
		LEDs.Update();
		}
	}

	public enum LEDMode{
		TEAM_COLOR,
		MOVE_WITH_LIFT,
		DRIVE_SPEED,
		RAINBOW
	}

}
