package remixlab.util.protimer;

import java.util.List;

import remixlab.proscene.Scene;
import remixlab.util.*;

public class TimerPool extends AbstractTimerPool {
	/**
	 * Instantiates all null timers.
	 */
	@Override
	public void init(Scene scn) {
		for (List<AbstractTimerJob> list : timerPool.values())
			for ( AbstractTimerJob e : list )
				if ( e.timer() == null )
					e.setTimer(new Timer(scn, e));
		needInit = false;
	}	
}
