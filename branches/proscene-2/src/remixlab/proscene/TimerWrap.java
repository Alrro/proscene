package remixlab.proscene;

import java.util.*;

import remixlab.remixcam.util.*;

public class TimerWrap implements Timable {
	Scene scene;
	Timer timer;
	TimerTask timerTask;
	Taskable caller;
	
	public TimerWrap(Scene scn, Taskable o) {
		scene = scn;
		caller = o;
	}	
	
	public void create() {
		stop();
		timer = new Timer();
		timerTask = new TimerTask() {
			public void run() {
				caller.execute();
			}
		};
	}
  
  public void run(long period) {
  	create();
		timer.scheduleAtFixedRate(timerTask, 0, period);
  }
  
  public void runOnce(long period) {
  	create();
		timer.schedule(timerTask, period);
  }
  
  public void cancel() {
  	stop();
  }
  
  public void stop() {
  	if(timer != null) {
			timer.cancel();
			timer.purge();
		}
  }
}
