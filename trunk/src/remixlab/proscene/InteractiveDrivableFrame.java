/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.9.0
 * 
 * Copyright (c) 2010 Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. 
 */

package remixlab.proscene;

import processing.core.*;

import java.awt.event.*;
import java.awt.Point;
import javax.swing.Timer;

/**
 * The InteractiveDrivableFrame is an abstract class that represents an InteractiveFrame
 * that can "fly" in the scene. It is the base class of all objects that are drivable in 
 * the Scene: InteractiveAvatarFrame and InteractiveCameraFrame.
 * <p>
 * An InteractiveDrivableFrame basically moves forward, and turns according to the mouse motion.
 * See {@link #flySpeed()}, {@link #flyUpVector()} and the {@link Scene.MouseAction#MOVE_FORWARD}
 * and {@link Scene.MouseAction#MOVE_BACKWARD}.
 */
public abstract class InteractiveDrivableFrame extends InteractiveFrame {
	protected float flySpd;
	protected float drvSpd;
	protected Timer flyTimer;
	protected ActionListener taskFlyPerformer;
	protected PVector flyUpVec;	
	protected PVector flyDisp;

	/**
	 * Default constructor. 
	 * <p> 
	 * {@link #flySpeed()} is set to 0.0 and {@link #flyUpVector()} is (0,1,0).
	 * <p> 
	 * <b>Attention:</b> Created object is {@link #removeFromMouseGrabberPool()}.
	 */
	public InteractiveDrivableFrame() {
		drvSpd = 0.0f;
		flyUpVec = new PVector(0.0f, 1.0f, 0.0f);
				
		flyDisp = new PVector(0.0f, 0.0f, 0.0f);
		
		setFlySpeed(0.0f);
	           
        taskFlyPerformer = new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		flyUpdate();
        	}
        };
        flyTimer = new Timer(10, taskFlyPerformer);
	}
	
	/**
	 * Implementation of the clone method. 
	 * <p> 
	 * Calls {@link remixlab.proscene.InteractiveFrame#clone()} and makes a deep
	 * copy of the remaining object attributes.
	 *  
	 * @see remixlab.proscene.InteractiveFrame#clone()
	 */	
	public InteractiveDrivableFrame clone() {
		InteractiveDrivableFrame clonedIAvtrFrame = (InteractiveDrivableFrame) super.clone();
		clonedIAvtrFrame.flyUpVec = new PVector(flyUpVec.x, flyUpVec.y, flyUpVec.z);		
		clonedIAvtrFrame.flyDisp = new PVector(flyDisp.x, flyDisp.y, flyDisp.z);
		clonedIAvtrFrame.flyTimer = new Timer(10, taskFlyPerformer);
		return clonedIAvtrFrame;
	}
	
    /**
     * Returns the fly speed, expressed in processing scene units. 
     * <p> 
     * It corresponds to the incremental displacement that is periodically
     * applied to the InteractiveAvatarFrame position when a 
     * Scene.MOVE_FORWARD or Scene.MOVE_BACKWARD
     * Scene.MouseAction is proceeded.  
     * <p>   
     * <b>Attention:</b> When the InteractiveAvatarFrame is set as the {@link remixlab.proscene.Camera#frame()}
     * (which indeed is an instance of an InteractiveCameraFrame) or When the InteractiveAvatarFrame is
     * set as the {@link remixlab.proscene.Scene#interactiveFrame()}, this value is set according to the
     * {@link remixlab.proscene.Scene#radius()} by {@link remixlab.proscene.Scene#setRadius(float)}.
     */
    public float flySpeed() { 
    	return flySpd; 
    }
	
	/**
	 * Sets the {@link #flySpeed()}, defined in processing scene units. 
	 * <p> 
	 * Default value is 0.0, but it is modified according to the
	 * {@link remixlab.proscene.Scene#radius()} when he InteractiveAvatarFrame is set as the
	 * {@link remixlab.proscene.Camera#frame()} (which indeed is an instance of an
	 * InteractiveCameraFrame) or when the InteractiveAvatarFrame is set as the
	 * {@link remixlab.proscene.Scene#interactiveFrame()}.
	 */
    public void setFlySpeed(float speed) { 
    	flySpd = speed;
    }
    
    /**
     * Returns the up vector used in fly mode, expressed in the world coordinate system. 
     * <p> 
     * Fly mode corresponds to the {@link remixlab.proscene.Scene.MouseAction#MOVE_FORWARD} and 
     * {@link remixlab.proscene.Scene.MouseAction#MOVE_BACKWARD} remixlab.proscene.Scene.MouseAction
     * bindings. In these modes, horizontal displacements of the
     * mouse rotate the InteractiveAvatarFrame around this vector. Vertical
     * displacements rotate always around the Camera {@code X} axis. 
     * <p> 
     * //TODO: check this!!! 
     * Default value is (0,1,0), but it is updated by the Camera when set as its
     * {@link remixlab.proscene.Camera#frame()}. {@link remixlab.proscene.Camera#setOrientation(Quaternion)}
     * and {@link remixlab.proscene.Camera#setUpVector(PVector)} modify this value and should be used
     * instead. 
     */
    public PVector flyUpVector() {
    	return flyUpVec;
    }

    /**
     * Sets the {@link #flyUpVector()}, defined in the world coordinate system. 
     * <p>
     * //TODO: check this!!!
     * Default value is (0,1,0), but it is updated by the Camera when
     * set as its {@link remixlab.proscene.Camera#frame()}. Use
     * {@link remixlab.proscene.Camera#setUpVector(PVector)} instead in that case.
     */
    public void setFlyUpVector(PVector up) { 
    	flyUpVec = up;
    }
	
	/**
	 * Called for continuous frame motion in first person mode (see Scene.MOVE_FORWARD).
	 */	
	public void flyUpdate()	{		
		flyDisp.set(0.0f, 0.0f, 0.0f);
		switch (action) {
		case MOVE_FORWARD:
			flyDisp.z = -flySpeed();
			translate(localInverseTransformOf(flyDisp));
			break;
	    case MOVE_BACKWARD:
	    	flyDisp.z = flySpeed();
	    	translate(localInverseTransformOf(flyDisp));
	    	break;
	    case DRIVE:
	    	flyDisp.z = flySpeed() * drvSpd;
	    	translate(localInverseTransformOf(flyDisp));
	    	break;
	    default:
	    	break;
	    }	
	}
	
	/**
	 * Protected method that simply calls {@code startAction(action, true)}.
	 * 
	 * @see #startAction(Scene.MouseAction, boolean)
	 */
	protected void startAction(
			Scene.MouseAction action) {
		startAction(action, true);
	}
	
	/**
	 * Protected internal method used to handle mouse events.
	 */
	protected void startAction(Scene.MouseAction a, boolean withConstraint) {
		super.startAction(a, withConstraint);
		
		switch (action) {
		case MOVE_FORWARD:
	    case MOVE_BACKWARD:
	    case DRIVE:
	    	flyTimer.setRepeats(true);
	    	flyTimer.setDelay(10);
	    	flyTimer.start();
	    	break;
	    default:
	    	break;
	    }
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseDragged(Point, Camera)}. 
	 * <p> 
	 * Motion depends on mouse binding. The resulting displacements are basically the same
	 * of those of an InteractiveFrame, but moving forward and backward and turning actions
	 * are implemented.
	 */
	public void mouseDragged(Point eventPoint, Camera camera) {		
		int deltaY;
		if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
			deltaY = eventPoint.y - prevPos.y;
		else
			deltaY = prevPos.y - eventPoint.y;
		
		if ( (action == Scene.MouseAction.TRANSLATE) || (action == Scene.MouseAction.ZOOM) || (action == Scene.MouseAction.SCREEN_ROTATE) || 
		     (action == Scene.MouseAction.SCREEN_TRANSLATE) || (action == Scene.MouseAction.ROTATE) || (action == Scene.MouseAction.NO_MOUSE_ACTION) )
			super.mouseDragged(eventPoint, camera);
		else switch (action) {
		case MOVE_FORWARD: {
			Quaternion rot = pitchYawQuaternion(eventPoint.x, eventPoint.y, camera);
			rotate(rot);
			//#CONNECTION# wheelEvent MOVE_FORWARD case
			// actual translation is made in flyUpdate().
			//translate(inverseTransformOf(Vec(0.0, 0.0, -flySpeed())));
			prevPos = eventPoint;
			break;
			}
		
		case MOVE_BACKWARD: {
			Quaternion rot = pitchYawQuaternion(eventPoint.x, eventPoint.y, camera);
			rotate(rot);
			// actual translation is made in flyUpdate().
			//translate(inverseTransformOf(Vec(0.0, 0.0, flySpeed())));
			prevPos = eventPoint;
			break;
			}
		
		case DRIVE: {
			//TODO: perhaps needs more testing
			Quaternion rot = turnQuaternion(eventPoint.x, camera);
			rotate(rot);
			// actual translation is made in flyUpdate().
			drvSpd = 0.01f * -deltaY;
			prevPos = eventPoint;
			break;
			}		
		
		case LOOK_AROUND: {
			Quaternion rot = pitchYawQuaternion(eventPoint.x, eventPoint.y, camera);
			rotate(rot);
			prevPos = eventPoint;
			break;
			}		
		
		case ROLL: {
			float angle = Quaternion.PI * (eventPoint.x - prevPos.x) / camera.screenWidth();
			if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
				angle = -angle;
			Quaternion rot = new Quaternion(new PVector(0.0f, 0.0f, 1.0f), angle);
			rotate(rot);
			setSpinningQuaternion(rot);
			updateFlyUpVector();
			prevPos = eventPoint;
			break;
			}		
		
		case ZOOM_ON_REGION:
			break;
			
		default:
			prevPos = eventPoint;
			break;
		}
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseReleased(Point, Camera)}.
	 */	
	public void mouseReleased(Point eventPoint, Camera camera) {
		if ((action == Scene.MouseAction.MOVE_FORWARD)  || 
			(action == Scene.MouseAction.MOVE_BACKWARD) || 
			(action == Scene.MouseAction.DRIVE))
		    flyTimer.stop();
		  
		  super.mouseReleased(eventPoint, camera);
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseWheelMoved(int, Camera)}. 
	 * <p>  
	 * The wheel behavior depends on the wheel binded action. Current possible actions are ZOOM, 
	 * MOVE_FORWARD, MOVE_BACKWARD. ZOOM speed depends on #wheelSensitivity() MOVE_FORWARD and
	 * MOVE_BACKWARD depend on #flySpeed().
	 */
	public void mouseWheelMoved(int rotation, Camera camera) {
		switch (action) {
		case ZOOM: {
			float wheelSensitivityCoef = 8E-4f;
			// PVector trans(0.0, 0.0,
			// -event.delta()*wheelSensitivity()*wheelSensitivityCoef*(camera.position()-position()).norm());
			PVector trans = new PVector(0.0f, 0.0f, rotation
					* wheelSensitivity() * wheelSensitivityCoef
					* (PVector.sub(camera.position(), position())).mag());

			// #CONNECTION# Cut-pasted from the mouseMoveEvent ZOOM case
			trans = camera.frame().orientation().rotate(trans);
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
			}
		case MOVE_FORWARD:
		case MOVE_BACKWARD:
			//#CONNECTION# mouseMoveEvent() MOVE_FORWARD case
			translate(inverseTransformOf(new PVector(0.0f, 0.0f, 0.2f*flySpeed()*(-rotation))));
			break;
		default:
			break;
		}
		
		// #CONNECTION# startAction should always be called before
		if (prevConstraint != null)
			setConstraint(prevConstraint);
		
		int finalDrawAfterWheelEventDelay = 400;
		
		// Starts (or prolungates) the timer.
		flyTimer.setRepeats(false);
		flyTimer.setDelay(finalDrawAfterWheelEventDelay);
		flyTimer.start();
		
		action = Scene.MouseAction.NO_MOUSE_ACTION;
	}
	
	/**
	 * This method will be called by the Camera when its orientation is changed,
	 * so that the {@link #flyUpVector()} (private) is changed accordingly.
	 * You should not need to call this method.
	 */
	protected final void updateFlyUpVector() {
	  flyUpVec = inverseTransformOf(new PVector(0.0f, 1.0f, 0.0f));
	}
	
	/**
	 * Returns a Quaternion that is a rotation around current camera Y,
	 * proportional to the horizontal mouse position.
	 */
	protected final Quaternion turnQuaternion(int x, Camera camera)	{
	  return new Quaternion(new PVector(0.0f, 1.0f, 0.0f), rotationSensitivity()*(prevPos.x-x)/camera.screenWidth());
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred
	 * from the mouse pitch (X axis) and yaw ({@link #flyUpVector()} axis).
	 */
	protected final Quaternion pitchYawQuaternion(int x, int y, Camera camera) {
		int deltaY;
		if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
			deltaY = y-prevPos.y;
		else
			deltaY = prevPos.y-y;
		
		Quaternion rotX = new Quaternion(new PVector(1.0f, 0.0f, 0.0f), rotationSensitivity()*deltaY/camera.screenHeight());
	    Quaternion rotY = new Quaternion(transformOf(flyUpVector()), rotationSensitivity()*(prevPos.x-x)/camera.screenWidth());
	    return Quaternion.multiply(rotY, rotX);
	}
}
