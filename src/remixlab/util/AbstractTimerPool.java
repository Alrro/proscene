package remixlab.util;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import remixlab.proscene.Scene;

public abstract class AbstractTimerPool {
	public HashMap<Object, List<AbstractTimerJob>> timerPool;
	protected boolean needInit;
	
	public AbstractTimerPool() {
		timerPool = new HashMap<Object, List<AbstractTimerJob>>();
		needInit = false;
	}

	public boolean needInit() {
		/**
		if(!cached)
			for (List<TimerJob> list : timerPool.values())
				for ( TimerJob e : list )
					if ( e.timer() == null )
						needIt = true;
					else
						needIt = false;
						*/
		return needInit;
	}
	
	public HashMap<Object, List<AbstractTimerJob>> timerPool() {
		return timerPool;
	}
	
	public void register(Object o, AbstractTimerJob t) {
		register(o,t,true);
	}
	
	public void register(Object o, AbstractTimerJob t, boolean nInit) {
		if( !timerPool.containsKey(o) ) {
			timerPool.put(o, new ArrayList<AbstractTimerJob>());
		} 
		if( !timerPool.get(o).contains(t) )
			timerPool.get(o).add(t);
		needInit = nInit;
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
	
	public abstract void init(Scene scn);
}
