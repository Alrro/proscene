package remixlab.proscene;

import processing.core.*;

/**
 * The InteractiveAvatarFrame class represents an InteractiveDrivableFrame that can be
 * tracked by a Camera, i.e., it implements the Trackable interface.
 * <p> 
 * The {@link #cameraPosition()} of the camera that is to be tracking the frame (see the documentation of
 * the Trackable interface) is defined in spherical coordinates ({@link #azimuth()}, {@link #inclination()}
 * and {@link #trackingDistance()}) respect to the {@link #position()} (which defines its {@link #target()})
 * of the InteractiveAvatarFrame.   
 */
public class InteractiveAvatarFrame extends InteractiveDrivableFrame implements Trackable {
	private float trackingDist;
	private float azmth;
	private float inc;
	private PVector camRelPos;
	
	/**
	 * Constructs an InteractiveAvatarFrame and sets its {@link #trackingDistance()} to 0, {@link #azimuth()}
	 * to 0, and {@link #inclination()} to PI/2.
	 * <p>
	 * <b>Attention: </b> The {@link #trackingDistance()} is modified by the Scene when
	 * {@link remixlab.proscene.Scene#setAvatar(Trackable)} is called on an instance of this class.
	 * 
	 * @see remixlab.proscene.Scene#setAvatar(Trackable)
	 */
	public InteractiveAvatarFrame(Scene scn) {
		super(scn);
		camRelPos = new PVector();
		setTrackingDistance(0);
		setAzimuth(0);
		setInclination(PApplet.PI/2);
	}	
	
	/**
	 * Returns the distance between the frame and the tracking camera.
	 */
	public float trackingDistance() {
    	return trackingDist;
    }
    
	/**
	 * Sets the distance between the frame and the tracking camera.
	 */
    public void setTrackingDistance(float d) {
    	trackingDist = d;
    	computeCameraPosition();
    }
    
    /**
     * Returns the azimuth of the tracking camera respect to the frame's {@link #position()}.
     */
    public float azimuth() {
    	return azmth;
    }
    
    /**
     * Sets the {@link #azimuth()} of the tracking camera.
     */
    public void setAzimuth(float a) {
    	azmth = a;
    	computeCameraPosition();
    }
    
    /**
     * Returns the inclination of the tracking camera respect to the frame's {@link #yAxis()}.
     */
    public float inclination() {
    	return inc;
    }
    
    /**
     * Sets the {@link #inclination()} of the tracking camera.
     */
    public void setInclination(float i) {
    	inc = i;
    	computeCameraPosition();
    }
    
    //Interface implementation
    
    /**
     * Overloading of {@link remixlab.proscene.Trackable#cameraPosition()}. Returns
     * the world coordinates of the camera position computed in {@link #computeCameraPosition()}.
     */
    public PVector cameraPosition() {
		return inverseCoordinatesOf(camRelPos);
	}
    
    /**
     * Overloading of {@link remixlab.proscene.Trackable#upVector()}. Simply returns the 
     * frame {@link #yAxis()}.
     */
    public PVector upVector() {
    	return yAxis();
    }
	
    /**
     * Overloading of {@link remixlab.proscene.Trackable#target()}. Simply returns the
     * frame {@link #position()}.
     */
	public PVector target() {
		return position();		
	}
    
	/**
     * Overloading of {@link remixlab.proscene.Trackable#computeCameraPosition()}.
     * <p> 
     * The {@link #cameraPosition()} of the camera that is to be tracking the frame (see the documentation of
     * the Trackable interface) is defined in spherical coordinates by means of the {@link #azimuth()}, the
     * {@link #inclination()} and {@link #trackingDistance()}) respect to the {@link #position()}.
     */
    public void computeCameraPosition() {
    	camRelPos.x = trackingDistance() * PApplet.sin(inclination()) * PApplet.sin(azimuth());
    	camRelPos.y = trackingDistance() * PApplet.cos(inclination());
    	camRelPos.z = trackingDistance() * PApplet.sin(inclination()) * PApplet.cos(azimuth());    	
    }
}
