package org.usfirst.frc.team1444.robot;

import com.mach.LightDrive.LightDrive2812;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.controlling.RobotController;

import java.awt.*;

public class LEDHandler implements RobotController { // even though it implements this, we'll put it in the base package

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
		
		switch(mode){
			case TEAM_COLOR:
				if(robot.getGameData().getAlliance() == Alliance.Blue) {
					LEDs.SetRange(Color.BLUE, 0, 63);
				} else if(robot.getGameData().getAlliance() == Alliance.Red) {
					LEDs.SetRange(Color.RED, 0, 63);
				} else {
					LEDs.SetRange(Color.ORANGE, 0, 63);
				}
				break;
			case MOVE_WITH_LIFT:
				Lift lift = robot.getLift();
				temp = lift.getMainStagePosition() * 63.0;
				LEDs.SetRangeandClear(temp<40?Color.GREEN:temp<50?Color.YELLOW:Color.RED, 0, (int)temp, 63);
				break;
			case DRIVE_SPEED:
				temp = (robot.getController().getControllerInput().leftStickY() * 63.0);
				LEDs.SetRangeandClear(temp<0?Color.RED:Color.GREEN, 0, (int)Math.abs(temp), 63);				
				break;
			case RAINBOW:
			default:

				break;
		}
	}

	public enum LEDMode{
		TEAM_COLOR,
		MOVE_WITH_LIFT,
		DRIVE_SPEED,
		RAINBOW
	}

}
