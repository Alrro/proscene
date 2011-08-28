package remixlab.util.awttimer;

import java.util.List;

import remixlab.proscene.Scene;
import remixlab.util.*;

public class AWTTimerPool extends AbstractTimerPool {
	/**
	 * Instantiates all null timers.
	 */
	@Override
	public void init(Scene scn) {
		for (List<AbstractTimerJob> list : timerPool.values()) {
			for ( AbstractTimerJob e : list ) {
				if ( e.timer() == null )
					e.setTimer(new AWTTimerWrap(e));
				if( e.pending() ) {
					if( e.isSchedule2RunOnce() )
						e.runOnce(e.period());
					else
						e.run(e.period());
				}
			}
		}
		needInit = false;
	}
}
