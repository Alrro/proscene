package remixlab.proscene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.Timer;

import processing.core.*;

public class KeyFrameInterpolator {
	private class KeyFrame {
		private PVector p, tgPVec;
	    private Quaternion q, tgQuat;
	    private float tm;
	    private Frame frm;	  
	      
		KeyFrame(Frame fr, float t) {
			tm = t;
			frm = fr;
			updateValues();							
		}		
		
		void updateValues() {
			p = frame().position();
		    q = frame().orientation();
		}
		
		PVector position() { return p; }
		
		Quaternion orientation() { return q; }
		
		PVector tgP() { return tgPVec; }
		
        Quaternion tgQ() { return tgQuat; }
        
        float time() { return tm; }
        
        Frame frame() { return frm; }
        
        void flipOrientationIfNeeded(Quaternion prev) {
        	if (Quaternion.dotProduct(prev, q) < 0.0f)
        	    q.negate();
        }
        
        void computeTangent(KeyFrame prev, KeyFrame next){        	
        	tgPVec = PVector.mult(PVector.sub(next.position(), prev.position()), 0.5f);
        	tgQuat = Quaternion.squadTangent(prev.orientation(), q, next.orientation());
        }
    }
	
	private List<KeyFrame> keyFrame_;
    private ListIterator<KeyFrame> currentFrame0;
    private ListIterator<KeyFrame> currentFrame1;
    private ListIterator<KeyFrame> currentFrame2;
    private ListIterator<KeyFrame> currentFrame3;
    //private List<Frame> path_;//TODO only when implementing drawPath()
    // A s s o c i a t e d   f r a m e
    private Frame frame_;

    // R h y t h m
    private Timer timer_;
    private ActionListener taskPerformer;
    private int period_;
    private float interpolationTime_;
    private float interpolationSpeed_;
    private boolean interpolationStarted_;

    // M i s c
    private boolean loopInterpolation_;

    // C a c h e d   v a l u e s   a n d   f l a g s
    //private boolean pathIsValid_;//TODO only when implementing drawPath()
    private boolean valuesAreValid_;
    private boolean currentFrameValid_;
    private boolean splineCacheIsValid_;
    private PVector v1, v2;
    
    /** Creates a KeyFrameInterpolator, with \p frame as associated frame().

    The frame() can be set or changed using setFrame().

    interpolationTime(), interpolationSpeed() and interpolationPeriod() are set to their default
    values.
    */
    public KeyFrameInterpolator(Frame frame) {
    	keyFrame_ = new ArrayList<KeyFrame>();
    	frame_ = null;
    	period_ = 40;
    	interpolationTime_ = 0.0f;
    	interpolationSpeed_ = 1.0f;
    	interpolationStarted_ = false;
        loopInterpolation_ = false;
        //pathIsValid_ = false;//TODO only when implementing drawPath()
        valuesAreValid_ = true;
        currentFrameValid_ = false;
        setFrame(frame);
        
        /**
        currentFrame_ = new ListIterator[4];        
        for (int i=0; i<4; ++i)        	
        	currentFrame_[i] = keyFrame_.listIterator();
        */
        
        currentFrame0 = keyFrame_.listIterator();
        currentFrame1 = keyFrame_.listIterator();
        currentFrame2 = keyFrame_.listIterator();
        currentFrame3 = keyFrame_.listIterator();
        
        taskPerformer = new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		update();
        	}
        };
        timer_ = new Timer(interpolationPeriod(), taskPerformer);
        timer_.setRepeats(true);
    }
    
    public void setFrame(Frame f) {
    	frame_ = f;
    }
    
    Frame frame() { return frame_; }
    
    public int numberOfKeyFrames() { return keyFrame_.size(); }
    
	public float interpolationTime() { return interpolationTime_; }
	
	public float interpolationSpeed() { return interpolationSpeed_; }
	
	public int interpolationPeriod() { return period_; }
	
	public boolean loopInterpolation() { return loopInterpolation_; }
	
	public void setInterpolationTime(float time) { interpolationTime_ = time; };
    /*! Sets the interpolationSpeed(). Negative or null values are allowed. */
	public void setInterpolationSpeed(float speed) { interpolationSpeed_ = speed; }
    /*! Sets the interpolationPeriod(). */
	public void setInterpolationPeriod(int period) { period_ = period; }
    
	public void setLoopInterpolation() { setLoopInterpolation(true); }
    
    /*! Sets the loopInterpolation() value. */
	public void setLoopInterpolation(boolean loop) { loopInterpolation_ = loop; }
    
	public boolean interpolationIsStarted() { return interpolationStarted_; }
	
	public void toggleInterpolation() { if (interpolationIsStarted()) stopInterpolation(); else startInterpolation(); }
	
	public void update() {
    	interpolateAtTime(interpolationTime());
    	
    	interpolationTime_ += interpolationSpeed() * interpolationPeriod() / 1000.0f;
    	
    	if (interpolationTime() > keyFrame_.get(keyFrame_.size()-1).time()) {
    		if (loopInterpolation())
    			setInterpolationTime(keyFrame_.get(0).time() + interpolationTime_ - keyFrame_.get(keyFrame_.size()-1).time());
    		else {
    			// Make sure last KeyFrame is reached and displayed
    			interpolateAtTime(keyFrame_.get(keyFrame_.size()-1).time());
    			stopInterpolation();
    		}
    		//emit endReached();
    	}
    	else if (interpolationTime() < keyFrame_.get(0).time()) {
    		if (loopInterpolation())
    			setInterpolationTime(keyFrame_.get(keyFrame_.size()-1).time() - keyFrame_.get(0).time() + interpolationTime_);
    		else {
    			// Make sure first KeyFrame is reached and displayed
    			interpolateAtTime(keyFrame_.get(0).time());
    			stopInterpolation();
    		}
    		//emit endReached();
    	}
    }
	
	public void invalidateValues() {
		valuesAreValid_ = false;
		//pathIsValid_ = false;//TODO only when implementing drawPath()
		splineCacheIsValid_ = false;
	}
	
	public void startInterpolation() {
		startInterpolation(-1);
	}
	
	public void startInterpolation(int period) {
		if (period >= 0)
			setInterpolationPeriod(period);
		
		if (!keyFrame_.isEmpty()) {
			if ((interpolationSpeed() > 0.0) && (interpolationTime() >= keyFrame_.get(keyFrame_.size()-1).time()))
				setInterpolationTime(keyFrame_.get(0).time());
			if ((interpolationSpeed() < 0.0) && (interpolationTime() <= keyFrame_.get(0).time()))
				setInterpolationTime(keyFrame_.get(keyFrame_.size()-1).time());			
			timer_.setDelay(interpolationPeriod());
			timer_.start();
			interpolationStarted_ = true;
			update();
		}
	}
	
	public void stopInterpolation() {
	  timer_.stop();
	  interpolationStarted_ = false;
	}
	
	public void resetInterpolation() {
		stopInterpolation();
		setInterpolationTime(firstTime());
	}
	
	public void addKeyFrame(Frame frame) {
		float time;
		
		if (keyFrame_.isEmpty())
			time = 0.0f;
		else
			time = keyFrame_.get(keyFrame_.size()-1).time() + 1.0f;
		
		addKeyFrame(frame, time);
	}
	
	public void addKeyFrame(Frame frame, float time) {
		if( frame == null )
			return;
		
		if (keyFrame_.isEmpty())
			interpolationTime_ = time;
		
		if ( (!keyFrame_.isEmpty()) && (keyFrame_.get(keyFrame_.size()-1).time() > time) )
			PApplet.println("Error in KeyFrameInterpolator.addKeyFrame: time is not monotone");
		else
			keyFrame_.add(new KeyFrame(frame, time));
		
		//TODO
		// connect(frame, SIGNAL(modified()), SLOT(invalidateValues()));
		
		valuesAreValid_ = false;
		//pathIsValid_ = false;//TODO only when implementing drawPath()
		currentFrameValid_ = false;
		resetInterpolation();
	}
	
	public void deletePath() {
		stopInterpolation();
		keyFrame_.clear();
		//pathIsValid_ = false;//TODO only when implementing drawPath()
		valuesAreValid_ = false;
		currentFrameValid_ = false;
	}
	
	public void updateModifiedFrameValues() {
		Quaternion prevQ = keyFrame_.get(0).orientation();		
		
		for ( KeyFrame kf: keyFrame_ ) {
			if (kf.frame() != null)
				kf.updateValues();
			kf.flipOrientationIfNeeded(prevQ);
			prevQ = kf.orientation();			
		}
		
		KeyFrame prev = keyFrame_.get(0);
		KeyFrame kf = keyFrame_.get(0);
		
		ListIterator<KeyFrame> it = keyFrame_.listIterator(1);
		while(it.hasNext()) {
			KeyFrame next = it.next();
			kf.computeTangent(prev, next);
			prev = kf;
			kf = next;
		}
		
		kf.computeTangent(prev, kf);
		valuesAreValid_ = true;
	}
	
	public Frame keyFrame(int index) {
		KeyFrame kf = keyFrame_.get(index);
		return new Frame(kf.position(), kf.orientation());
	}

	/*! Returns the time corresponding to the \p index keyFrame.

	 See also keyFrame(). \p index has to be in the range 0..numberOfKeyFrames()-1. */
	public float keyFrameTime(int index) {
		return keyFrame_.get(index).time();
	}
	
	public float duration() {
		return lastTime() - firstTime();
	}
	
	public float firstTime() {
		if (keyFrame_.isEmpty())
			return 0.0f;
		else
			return keyFrame_.get(0).time();
	}

	/*! Returns the time corresponding to the last keyFrame, expressed in seconds.

	Returns 0.0 if the path is empty. See also firstTime(), duration() and keyFrameTime(). */
	public float lastTime() {
		if (keyFrame_.isEmpty())
			return 0.0f;
		else
			return keyFrame_.get(keyFrame_.size()-1).time();
	}
	
	public void updateCurrentKeyFrameForTime(float time) {
		  // Assertion: times are sorted in monotone order.
		  // Assertion: keyFrame_ is not empty

		  // TODO: Special case for loops when closed path is implemented !!
		if (!currentFrameValid_)
			// Recompute everything from scratch
			currentFrame1 = keyFrame_.listIterator();
		
		while (peekNext(currentFrame1).time() > time) {
			currentFrameValid_ = false;
			if (!currentFrame1.hasPrevious())
				break;
			currentFrame1.previous();
		}
		
		if (!currentFrameValid_)
			currentFrame2 = keyFrame_.listIterator( currentFrame1.nextIndex() );
		
		while (peekNext(currentFrame2).time() < time) {
			currentFrameValid_ = false;
			if (!currentFrame2.hasNext())
				break;
			currentFrame2.next();
		}
		
		if (!currentFrameValid_) {
			currentFrame1 = keyFrame_.listIterator( currentFrame2.nextIndex() );
			if ((currentFrame1.hasPrevious()) && (time < peekNext(currentFrame2).time()))
				currentFrame1.previous();
			
			currentFrame0 = keyFrame_.listIterator( currentFrame1.nextIndex() );
			if (currentFrame0.hasPrevious())
				currentFrame0.previous();
			
			currentFrame3 = keyFrame_.listIterator( currentFrame2.nextIndex() );
			if (currentFrame3.hasNext())
				currentFrame3.next();
			
			currentFrameValid_ = true;
			splineCacheIsValid_ = false;
		}
	}
	
	public void updateSplineCache() {		
		PVector delta = PVector.sub( peekNext(currentFrame2).position(), peekNext(currentFrame1).position() );
		
		v1 = PVector.sub( PVector.mult(delta, 3.0f), PVector.mult(peekNext(currentFrame1).tgP(), 2.0f) );
		v1 = PVector.sub( v1, peekNext(currentFrame2).tgP() );
		
		v2 = PVector.add( PVector.mult(delta, -2.0f), peekNext(currentFrame1).tgP());
		v2 = PVector.add(v2, peekNext(currentFrame2).tgP());
		
		splineCacheIsValid_ = true;
	}

	/*! Interpolate frame() at time \p time (expressed in seconds). interpolationTime() is set to \p
	  time and frame() is set accordingly.

	  If you simply want to change interpolationTime() but not the frame() state, use
	  setInterpolationTime() instead.

	  Emits the interpolated() signal and makes the frame() emit the Frame::interpolated() signal. */
	public void interpolateAtTime(float time) {
		setInterpolationTime(time);
		
		if ((keyFrame_.isEmpty()) || (frame() == null))
			return;
		
		if (!valuesAreValid_)
			updateModifiedFrameValues();
		
		updateCurrentKeyFrameForTime(time);
		
		if (!splineCacheIsValid_)
			updateSplineCache();
		
		float alpha;
		float dt = peekNext(currentFrame2).time() -  peekNext(currentFrame1).time();
		if (dt == 0.0)
			alpha = 0.0f;
		else
			alpha = (time - peekNext(currentFrame1).time()) / dt;
		
		// Linear interpolation - debug
		// Vec pos = alpha*(currentFrame2->peekNext()->position()) + (1.0-alpha)*(currentFrame1->peekNext()->position());
		PVector pos = PVector.add( peekNext(currentFrame1).position(), PVector.mult( peekNext(currentFrame1).tgP(), alpha ) );		
		pos = PVector.add(pos, PVector.mult(PVector.add(v1, PVector.mult(v2, alpha) ), alpha));
		Quaternion q = Quaternion.squad(peekNext(currentFrame1).orientation(), peekNext(currentFrame1).tgQ(),
				peekNext(currentFrame2).tgQ(), peekNext(currentFrame2).orientation(), alpha);
		
		frame().setPositionWithConstraint(pos);
		frame().setRotationWithConstraint(q);
		//emit interpolated();
		//debug
		//cout<< "Position: (" << kfi_.frame()->position().x << ", " << kfi_.frame()->position().y << ", " << kfi_.frame()->position().z << ") Orientation: ("
        //<< kfi_.frame()->orientation()[0] << ", " << kfi_.frame()->orientation()[1] << ", " << kfi_.frame()->orientation()[2] << ", " << kfi_.frame()->orientation()[3] << ")" << endl;
		PApplet.println( "Position: (" + frame().position().x + ", " + frame().position().y + ", " + frame().position().z + ") Orientation: (" +
				frame().orientation().x + ", " + frame().orientation().y + ", " + frame().orientation().z + ", " + frame().orientation().w + ")" );
	}
	
	private KeyFrame peekNext(ListIterator<KeyFrame> it) {
		KeyFrame kf = it.next();
		it.previous();
		return kf;
	}
}
