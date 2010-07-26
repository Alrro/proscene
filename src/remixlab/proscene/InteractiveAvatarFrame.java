/**
 *                     ProScene (version 0.9.1)                         
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

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
	 * Constructs an InteractiveAvatarFrame and sets its {@link #trackingDistance()} to 
	 * {@link remixlab.proscene.Scene#radius()}/3, {@link #azimuth()} to 0, and
	 * {@link #inclination()} to PI/2.
	 * 
	 * @see remixlab.proscene.Scene#setAvatar(Trackable)
	 * @see remixlab.proscene.Scene#setInteractiveFrame(InteractiveFrame)
	 */
	public InteractiveAvatarFrame(Scene scn) {
		super(scn);
		camRelPos = new PVector();
		setTrackingDistance(0);
		setAzimuth( scene.radius()/3 );
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
     * Returns the azimuth of the tracking camera measured respect to the frame's {@link #zAxis()}.
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
     * Returns the inclination of the tracking camera measured respect to the frame's {@link #yAxis()}.
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
