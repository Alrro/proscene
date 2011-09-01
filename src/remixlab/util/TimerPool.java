package remixlab.util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import remixlab.proscene.Scene;

public class TimerPool {
	protected Scene scene;
	public HashMap<Object, List<AbstractTimerJob>> timerPool;
	
	public TimerPool(Scene scn) {
		scene = scn;
		timerPool = new HashMap<Object, List<AbstractTimerJob>>();
	}	
	
	public HashMap<Object, List<AbstractTimerJob>> timerPool() {
		return timerPool;
	}
	
	public void register(Object o, AbstractTimerJob t) {
		if( !timerPool.containsKey(o) ) {
			timerPool.put(o, new ArrayList<AbstractTimerJob>());
		} 
		if( !timerPool.get(o).contains(t) )
			timerPool.get(o).add(t);
	}
	
	public void unregister(Object t) {
		timerPool.remove(t);
	}
	
	public void unregister(Object o, AbstractTimerJob t) {
		if ( timerPool.get(o).contains(t) )
			timerPool.get(o).remove(t);
	}
	
	public void unregister(AbstractTimerJob t) {
		for (List<AbstractTimerJob> list : timerPool.values()) {
			if ( list.contains(t) ) {
				list.remove(t);
				return;
			}
		}
	}
	
	public void unregister(Timable t) {
		for (List<AbstractTimerJob> list : timerPool.values()) {
			for( AbstractTimerJob job : list ) {
				if( job.timer().equals(t) ) {
					list.remove(job);
					return;
				}
			}
		}
	}
	
	public boolean isRegistered(Object o, AbstractTimerJob t) {
		if ( timerPool.get(o).contains(t) )
			return true;
		return false;
	}
	
	public boolean isRegistered(AbstractTimerJob t) {
		//search the whole hash
		for (List<AbstractTimerJob> list : timerPool.values())
			if ( list.contains(t) )
				return true;
		return false;
	}
	
	public void clear() {
		timerPool().clear();
	}
}
