package remixlab.proscene;

import processing.core.*;

/**
 * The InteractiveAvatarFrame class represents an InteractiveDrivableFrame that can be
 * tracked by a Camera.
 * <p>
 * An InteractiveAvatarFrame handles a {@link #cameraPosition()} that can be used to defined
 * a tracking Camera. That {@link #cameraPosition()} is defined in spherical coordinates
 * ({@link #azimuth()}, {@link #inclination()} and {@link #trackingDistance()}) respect to
 * the origin of the InteractiveAvatarFrame.   
 */
public class InteractiveAvatarFrame extends InteractiveDrivableFrame implements Trackable {
	private float trackingDist;
	private float azmth;
	private float inc;
	private PVector camRelPos;
	
	public InteractiveAvatarFrame() {
		camRelPos = new PVector();
		setTrackingDistance(0);
		setAzimuth(0);
		setInclination(PApplet.PI/2);
	}	
	
	public float trackingDistance() {
    	return trackingDist;
    }
    
    public void setTrackingDistance(float d) {
    	trackingDist = d;
    	computeCameraPosition();
    }
    
    public float azimuth() {
    	return azmth;
    }
    
    public void setAzimuth(float a) {
    	azmth = a;
    	computeCameraPosition();
    }
    
    public float inclination() {
    	return inc;
    }
    
    public void setInclination(float i) {
    	inc = i;
    	computeCameraPosition();
    }
    
    //Interface implementation
    public PVector cameraPosition() {
		return inverseCoordinatesOf(camRelPos);
	}
    
    public void computeCameraPosition() {
    	camRelPos.x = trackingDistance() * PApplet.sin(inclination()) * PApplet.sin(azimuth());
    	camRelPos.y = trackingDistance() * PApplet.cos(inclination());
    	camRelPos.z = trackingDistance() * PApplet.sin(inclination()) * PApplet.cos(azimuth());    	
    }
}
