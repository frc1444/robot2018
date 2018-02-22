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

	/**
	 * You are able to do something like x.setNextController(b).setNextController(go) etc
	 *
	 * Sets the next robot controller and returns the passed controller
	 * @param controller The next controller to set
	 * @param <T> Something that extends a RobotController
	 * @return The passed controller
	 */
	public <T extends RobotController> T setNextController(T controller){
		this.nextController = controller;
		return controller;
	}

	protected abstract boolean isDone();

}
