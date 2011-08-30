package remixlab.util;

public abstract class AbstractTimerJob implements Taskable {
	protected Timable tmr;
	//TODO new:
	
	/**
	protected boolean rOnce = false;
	protected boolean pndng = false;
	protected long prd = 0;
	*/
	
	protected boolean rOnce;
	protected boolean pndng;
	protected long prd;
	
	public AbstractTimerJob() {
		rOnce = false;
		pndng = false;
		prd = 0;
	}
	
	public Timable timer() {
		return tmr;
	}
	
	public void setTimer(Timable t) {
		tmr = t;
	}
	
	//TODO new: wrappers
	/**
	public void execute() {
		// TODO check hot to set the rest of variables
		if(timer()!=null)
			timer().execute();
	}
	*/
	
	public void run(long period) {
		if(timer()!=null) {
			timer().run(period);
			pndng = false;
		} else {
			pndng = true;
			rOnce = false;
			prd = period;
		}
	}
	
	public void runOnce(long period) {
		if(timer()!=null) {
			timer().runOnce(period);
			pndng = false;
		} else {
			pndng = true;
			rOnce = true;
			prd = period;
		}
	}
	
	public void stop() {
		if(timer()!=null) {
			timer().stop();
		}
	}
	
	public void cancel() {
		if(timer()!=null) {
			timer().cancel();
		}
	}
	
	public void create() {
		if(timer()!=null) {
			timer().create();
		}
	}
		
	// utilities:
	public long period() {
		return prd;
	}
	
	public boolean isSchedule2RunOnce() {
		return rOnce;
	}
	
	public boolean pending() {
		return pndng;
	}
}
