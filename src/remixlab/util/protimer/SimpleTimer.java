package remixlab.util.protimer;

import processing.core.PApplet;
import remixlab.proscene.*;
import remixlab.util.*;

public class SimpleTimer implements Timable {
	Scene scene;
	boolean active;
	boolean runOnlyOnce;
	private long counter;
	private long period;
	private long startTime;
	private long startFrame;
	
	public SimpleTimer(Scene scn) {
		scene = scn;
		create();
	}

	public void cancel() {
		scene.timerPool().unregister(this);		
	}

	public void create() {
		inactivate();		
	}
	
	public void run(long period) {
		run(period, false);
	}	

	public void runOnce(long period) {
		run(period, true);
	}
	
	protected void run(long p, boolean rOnce) {
		if(p <= 0)
  		return;
  	
  	inactivate(); 	
  	
  	period = p;
  	counter = 1;
  	
  	active = true;
  	runOnlyOnce = rOnce;
  	
  	startTime = System.currentTimeMillis();
  	startFrame = scene.parent.frameCount;
	}

	public void stop() {
		inactivate();
	}
	
	// others
	
	public void inactivate() {  	
  	active = false;
  }
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isTrigggered() {
  	if(!active)
  		return false;	 	 
  	  	
  	long elapsedTime = System.currentTimeMillis() - startTime;  	
  	
  	float timePerFrame = (1 / scene.frameRate) * 1000;  	
  	long threshold = counter * period;  	
  	
  	boolean result = false;
  	if( threshold >= elapsedTime) {
  		long diff = elapsedTime + (long)timePerFrame - threshold;
  		if( diff >= 0) {
  			if( ( threshold - elapsedTime) <  diff ) {		
  				result = true;
  			}
  		}
  	}
  	else {  		
  		result = true;
  	}
  	
  	if(result) {
  		counter++;
  		if( period < timePerFrame )
  		PApplet.println("Your current frame rate (~" + scene.parent.frameRate + " fps) is not high enough " +
          "to run the timer and reach the specified " + period + " ms period, " + timePerFrame
          + " ms period will be used instead. If you want to sustain a lower timer " +
          "period, define a higher frame rate (minimum of " + 1000f/period + " fps) " +
          "before running the timer (you may need to simplify your drawing to achieve it.");
  	}
  	
  	return result;  	
	}
}