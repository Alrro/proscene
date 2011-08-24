package remixlab.util.protimer;

import processing.core.PApplet;
import remixlab.proscene.*;
import remixlab.util.*;

public class SimpleTimer implements Timable {
	Scene scene;
	boolean active;
	boolean runOnlyOnce;
	
	private boolean forced;
	private float targetFrameRate;
	private long magicFrame;
	private long currentFrame;
	private float timerToFrameRateRatio;	
	
	public SimpleTimer(Scene scn) {
		scene = scn;
		create();
	}
	
	/**
	 * activates the timer
	 */
	public void run(long period) {
		this.run(period, false);
	}
	
	/**
	 * activates the timer
	 */
  public void run(long period, boolean force) {
  	if(period <= 0)
  		return;
  	
  	inactivate();  	
  	forced = force;
  	active = true;
  	runOnlyOnce = false;
  	
		float timerPeriod = period;
		float timerFrameRate = 1000f/timerPeriod;
		
		if(forced) {			
			if( (timerFrameRate > scene.parent.frameRate) ) {
				PApplet.println("Your frame rate (~" + scene.parent.frameRate + " fps) will be modified by " +
						            "the timer since the 'force' option was passed to the timer's run method. " +
						            "Attempting to reach a new frame rate of " +  timerFrameRate + " fps.");
				targetFrameRate = scene.parent.frameRate;
				scene.parent.frameRate( timerFrameRate );
			}			
		}
		else {
			if( (timerFrameRate > scene.parent.frameRate) ) {
				timerFrameRate = scene.parent.frameRate;
				timerPeriod = 1000f/timerFrameRate;		
				PApplet.println("Your current frame rate (~" + scene.parent.frameRate + " fps) is not enough " +
						            "to run the timer and reach the specified " + period + " ms period, " + timerPeriod
						            + " ms period will be used instead. If you want to sustain a higher " +
						            "period, either define a higher frame rate (minimum of " + timerFrameRate + " fps) " +
						            "before running the timer or turn on the 'force' option of the timer's run method.");
				return;
			}
		}
		
		if( timerFrameRate == scene.parent.frameRate )
			currentFrame = -1;
		else {
			currentFrame = 0;
			magicFrame = scene.parent.frameCount;			
			timerToFrameRateRatio = timerFrameRate/scene.parent.frameRate;
		}
  }
  
  public void runOnce(long period) {
  	runOnce(period, false);
  }
  
  /**
	 * activates the timer
	 */  
  public void runOnce(long period, boolean force) {
  	if(period <= 0)
  		return;
  	
  	inactivate();
  	forced =  force;
  	active = true;
  	runOnlyOnce = true;  	
  	
  	float framePeriod = 1000f / scene.parent.frameRate;
  	magicFrame = PApplet.round( (float)period / framePeriod );
  	
  	if(magicFrame == 0)
  		magicFrame++;
    
  	if(forced) {
  		targetFrameRate = scene.parent.frameRate;
  		framePeriod = (float)period / magicFrame;  		
  		scene.parent.frameRate(1000f/framePeriod);
  	} 	
  	
  	magicFrame = magicFrame + scene.parent.frameCount;
  }
  
  public void create() {
  	forced = false;
		inactivate();
		/**
		currentFrame = -1; // dummy
		runOnlyOnce = false; //dummy value
		*/
	}
  
  public void stop() {  	
  	inactivate();
  }
  
  /**
   * needs to call run to activate
   */
  public void inactivate() {
  	if(forced)
  		scene.parent.frameRate(targetFrameRate);
  	active = false;
  }
  
  public boolean isActive() {
  	return active;
  }
  
  public void cancel() {
  	scene.timerPool().unregister(this);
  }
  
  public boolean isTrigggered() {
  	if(!active)
  		return false;
  	
  	if( runOnlyOnce ) {
  		if( scene.parent.frameCount == magicFrame )
  			return true;
  		else
  			return false;
  	}
  	
		if( currentFrame >= 0 ) {
			long previousFrame = currentFrame;
			currentFrame = PApplet.round( (scene.parent.frameCount - magicFrame) * timerToFrameRateRatio );
			if(currentFrame == previousFrame) {
				return false;
			}				
		}
		
		return true;
	}		
}
