package remixlab.util;

public abstract class AbstractTimerJob implements Taskable {
	protected Timable tmr;
	
	public Timable timer() {
		return tmr;
	}
	
	public void setTimer(Timable t) {
		tmr = t;
	}
}
