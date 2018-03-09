package org.usfirst.frc.team1444.robot.controlling.autonomous;

import org.usfirst.frc.team1444.robot.Robot;
import org.usfirst.frc.team1444.robot.controlling.RobotController;
import org.usfirst.frc.team1444.robot.controlling.RobotControllerProcess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class MultiController extends RobotControllerProcess {

	private List<RobotController> controllers;

	public MultiController(RobotController... controllers){
		this.controllers = new ArrayList<>(Arrays.asList(controllers));
	}

	@Override
	public void update(Robot robot) {
		ListIterator<RobotController> it = controllers.listIterator();
		while(it.hasNext()){
			RobotController controller = it.next();
			controller.update(robot);
			if(controller instanceof RobotControllerProcess){
				RobotControllerProcess process = (RobotControllerProcess) controller;
				RobotController next = process.getNext();
				if(next != null){
					if(next != process){ // if new controller remove
						it.remove();
						it.add(next);
					}
				} else { // if null remove
					it.remove();
				}

			}
		}

		if(controllers.size() == 0){
			System.out.println("multi should be done");
		}
	}
	@Override
	protected boolean isDone() {
		for(RobotController controller : controllers){
			if(controller instanceof RobotControllerProcess){
				return false; // we are done when we have no RobotControllerProcesses left
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getClass().getSimpleName());
		builder.append("{");
		for(RobotController c : controllers){
			builder.append(c.toString());
			builder.append(",");
		}
		builder.append("}");
		return builder.toString();
	}
}
