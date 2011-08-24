package remixlab.util.protimer;

import remixlab.proscene.*;
import remixlab.util.*;

public class Timer extends SimpleTimer {
	Taskable caller;	
	
	public Timer(Scene scn, Taskable t) {
		super(scn);
		caller = t;
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
