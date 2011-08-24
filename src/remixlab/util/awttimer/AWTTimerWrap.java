package remixlab.util.awttimer;

import java.util.*;

import remixlab.util.*;

public class AWTTimerWrap implements Timable {
	Timer timer;
	TimerTask timerTask;
	Taskable caller;
	
	public AWTTimerWrap(Taskable o) {
		caller = o;
		create();
	}	
	
	public void create() {
		cancel();
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
  	if(timer != null) {
			timer.cancel();
			timer.purge();
		}
  }
}
