package org.usfirst.frc.team1444.robot;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1444.robot.PidParameters;

import java.util.ArrayList;
import java.util.List;

public class PidHandler {

	private List<PidDashObject> objects = new ArrayList<>();

	public void addPid(PidDashObject pidDashObject){
		objects.add(pidDashObject);
	}
	public void update(){
		for(PidDashObject obj : objects){
			obj.check();
		}
	}

	public static class PidDashObject{
		private PidParameters pid;
		private List<IMotorController> motors;
		private String name;

		public PidDashObject(PidParameters pid, List<IMotorController> motors, String name){
			this.pid = pid;
			this.motors = motors;
			this.name = name;

			SmartDashboard.putNumber(name + " KD", pid.KD);
			SmartDashboard.putNumber(name + " KP", pid.KP);
			SmartDashboard.putNumber(name + " KF", pid.KF);
			SmartDashboard.putNumber(name + " KI", pid.KI);
			SmartDashboard.putNumber(name + " rampRate", pid.closedRampRate);

		}

		private void check(){

			double KD = SmartDashboard.getNumber(name + " KD", pid.KD);
			double KP = SmartDashboard.getNumber(name + " KP", pid.KP);
			double KF = SmartDashboard.getNumber(name + " KF", pid.KF);
			double KI = SmartDashboard.getNumber(name + " KI", pid.KI);
			double rampRate = SmartDashboard.getNumber(name + " rampRate", pid.closedRampRate);

			if(KD != pid.KD || KP != pid.KP || KF != pid.KF || KI != pid.KI || rampRate != pid.closedRampRate){
				PidParameters newPid = new PidParameters();
				newPid.KD = KD;
				newPid.KP = KP;
				newPid.KF = KF;
				newPid.KI = KI;
				newPid.closedRampRate = rampRate;

				for(IMotorController controller : motors){
					newPid.apply(controller);
				}
			}
		}
	}
}
