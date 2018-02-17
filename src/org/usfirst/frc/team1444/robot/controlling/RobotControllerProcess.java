package org.usfirst.frc.team1444.robot.controlling;

/**
 * Great name right? This class just has a "next controller". If you don't set the next controller, when isDone()
 * returns true, nextController will be called
 */
public abstract class RobotControllerProcess implements RobotController {

	private RobotController nextController;

	public RobotControllerProcess(RobotController nextController){
		this.nextController = nextController;
	}
	public RobotControllerProcess(){
		this(null);
	}

	/**
	 * The method that should be called when you want to get the next RobotController. Normally will return this
	 * @return the next RobotController that will normally be this
	 */
	public RobotController getNext(){
		if (this.isDone()) {
			return this.getNextController();
		}
		return this;
	}

	protected RobotController getNextController(){
		return nextController;
	}
	public void setNextController(RobotController controller){
		this.nextController = controller;
	}

	protected abstract boolean isDone();

}
