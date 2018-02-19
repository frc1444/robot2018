package org.usfirst.frc.team1444.robot;

import com.mach.LightDrive.LightDrive2812;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.controlling.RobotController;

import java.awt.*;

public class LEDHandler implements RobotController { // even though it implements this, we'll put it in the base package
	private static final Color[] colorwheel = new Color[] {
			Color.RED, Color.GREEN, Color.CYAN, Color.ORANGE, Color.PINK, Color.WHITE
	};

	private int timer = 0;
	private int timer2 = 0;
	private int count = 0;

	private LEDMode mode;
	private LightDrive2812 LEDs;

	public LEDHandler(LightDrive2812 lightDrive){
		this.LEDs = lightDrive;

//		colorwheel = new Color[6];
//		colorwheel[0] = Color.RED;
//		colorwheel[1] = Color.GREEN;
//		colorwheel[2] = Color.CYAN;
//		colorwheel[3] = Color.ORANGE;
//		colorwheel[4] = Color.PINK;
//		colorwheel[5] = Color.WHITE;

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
//		BNO055.EulerData ed = robot.getGyro().GetEulerData();
		//System.out.format("Heading: %1.3f. Raw: %d\r\n", ed.heading, LEDs.GetRaw()[3]);

		if(timer++ == 0) {
			double angle = robot.getGyro().getAngle();
			count = (int) (angle / 6.0) + 1;
			timer2 = 61 - count;
			//SmartDashboard.putNumber("count: ", count);
			//LEDs.ClearLEDs();
			LEDs.SetRange(Color.BLUE, 0, count);
		} else {
			LEDs.SetRange(Color.BLACK, count, timer2);
			timer = 0;
		}
//		long time = System.currentTimeMillis(); // time is more accurate than a timer variable
//
//		if(timer++ > 5) { // TODO put this code in switch statement and use time variable
//			System.out.println("working");
//			LEDs.SetRange(colorwheel[timer2%6], timer2%50, 50 - (timer2%50));
//			timer = 0;
//			if(timer2++ > 100)
//				timer2 = 0;
//		}

		switch(mode){
			case TEAM_COLOR:

				break;
			case MOVE_WITH_LIFT:
				Lift lift = robot.getLift();

				break;
			case DRIVE_SPEED:

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
