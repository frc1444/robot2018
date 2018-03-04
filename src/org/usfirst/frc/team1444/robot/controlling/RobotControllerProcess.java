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

	/**
	 * @return the last linked process. Note that if there is a regular RobotController at the very end, that will not be returned
	 */
	public RobotControllerProcess getLastLinkedProcess(){
		RobotControllerProcess r = this;
		while(true){
			RobotController next = r.getNext();
			if(next != r && next instanceof RobotControllerProcess){
				// since we check instanceof, next will not be null
				r = (RobotControllerProcess) next;
			} else {
				break;
			}
		}
		return r;
	}

	protected abstract boolean isDone();

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{}";
	}

	/**
	 * It is recommended to use this class when building complex links ex: Using multiple if statements or even methods
	 */
	public static class Builder {

		private RobotControllerProcess firstProcess;

		private RobotControllerProcess currentProcess;

		/**
		 * @param processes the list of processes to append
		 */
		public Builder(RobotControllerProcess... processes){
			this.append(processes);
		}

		/**
		 * Note that if some of the passes Processes have controllers linked to them, they will not be deleted
		 * @param processes
		 * @return returns this
		 */
		public Builder append(RobotControllerProcess... processes){
			for(RobotControllerProcess p : processes){
				if(firstProcess == null){
					firstProcess = p;
					currentProcess = p;
				} else {
					currentProcess = currentProcess.getLastLinkedProcess().setNextController(p);
				}
			}
			return this;
		}

		/**
		 * You can also think about this method like addToBeginning
		 * <p>
		 * Can be used to attach the processes in this builder to the last linked process from process
		 * <p>
		 * Code:
		 * <p>
		 * process.getLastLinkedProcess().setNextController(firstProcess);
		 * firstProcess = process;
		 *
		 * @param process the process to use it's last linked process to link this builder's processes
		 */
		public void attachTo(RobotControllerProcess process){
			process.getLastLinkedProcess().setNextController(firstProcess);
			firstProcess = process;
		}

		/**
		 * Note the returned value will have controllers linked after it so you can't add more controllers to the returned
		 * value but you can still use this instance if you'd like.
		 *
		 * If you lose this instance, you can create a new instance with the returned value and it will still work without
		 * losing any of the appended RobotControllerProcesses from the previous instance
		 *
		 * @return the controller that should go into action first.
		 */
		public RobotControllerProcess build(){
			return firstProcess;
		}

	}
}
