package remixlab.util.protimer;

import remixlab.proscene.*;
import remixlab.util.*;

public class Timer extends SingleThreadedTimer {
	Taskable caller;
	
	public Timer(Scene scn, Taskable t) {
		super(scn);
		caller = t;
	}
	
	public Taskable timerJob() {
		return caller;
	}
	
	public void cancel() {
		scene.unregisterFromTimerPool(this);
	}
	
	public boolean execute() {
		boolean result = isTrigggered();
		
		if(result) {
			caller.execute();
			if(runOnlyOnce)
				inactivate();		
		}
		
		return result;
	}	
}
