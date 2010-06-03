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
import java.awt.Rectangle;
import java.awt.Point;
import javax.swing.Timer;

/**
 * The InteractiveCameraFrame class represents a InteractiveFrame with Camera
 * specific mouse bindings.
 * <p>
 * A InteractiveCameraFrame is a specialization of a InteractiveFrame, designed to be set as the
 * {@link Camera#frame()}. Mouse motions are basically interpreted in a negated way: when the
 * mouse goes to the right, the InteractiveFrame translation goes to the right, while the
 * InteractiveCameraFrame has to go to the <i>left</i>, so that the <i>scene</i> seems
 * to move to the right.
 * <p>
 * A InteractiveCameraFrame rotates around its {@link #revolveAroundPoint()}, which corresponds
 * to the associated {@link Camera#revolveAroundPoint()}.
 * <p>
 * A InteractiveCameraFrame can also "fly" in the scene. It basically moves forward, and turns
 * according to the mouse motion. See {@link #flySpeed()}, {@link #flyUpVector()} and the
 * {@link Scene.MouseAction#MOVE_FORWARD} and {@link Scene.MouseAction#MOVE_BACKWARD}.
 */
public class InteractiveCameraFrame extends InteractiveFrame {
	
	static PVector flyDisp = new PVector(0.0f, 0.0f, 0.0f);
	
	private float flySpd;
    private float drvSpd;
    private Timer flyTimer;
    private ActionListener taskFlyPerformer;
    private PVector flyUpVec;    
	private PVector revAroundPnt;

	/**
	 * Default constructor. 
	 * <p> 
	 * {@link #flySpeed()} is set to 0.0 and {@link #flyUpVector()} is (0,1,0).
	 * The {@link #revolveAroundPoint()} is set to (0,0,0). 
	 * <p> 
	 * <b>Attention:</b> Created object is {@link #removeFromMouseGrabberPool()}.
	 */
	public InteractiveCameraFrame() {
		drvSpd = 0.0f;
		flyUpVec = new PVector(0.0f, 1.0f, 0.0f);
		revAroundPnt = new PVector(0.0f, 0.0f, 0.0f);
		
		setFlySpeed(0.0f);	    
	    removeFromMouseGrabberPool();
	           
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
	public InteractiveCameraFrame clone() {
		InteractiveCameraFrame clonedICamFrame = (InteractiveCameraFrame) super.clone();
		clonedICamFrame.flyUpVec = new PVector(flyUpVec.x, flyUpVec.y, flyUpVec.z);
		clonedICamFrame.revAroundPnt = new PVector(revAroundPnt.x, revAroundPnt.y, revAroundPnt.z);
		clonedICamFrame.flyTimer = new Timer(10, taskFlyPerformer);
		return clonedICamFrame;
	}
	
	/**
	 * Overloading of {@link remixlab.proscene.InteractiveFrame#spin()}.
	 * <p> 
	 * Rotates the InteractiveCameraFrame around its #revolveAroundPoint()
	 * instead of its origin.
	 */
	public void spin() {
		rotateAroundPoint(spinningQuaternion(), revolveAroundPoint());
	}
	
    /**
     * Returns the fly speed, expressed in processing scene units. 
     * <p> 
     * It corresponds to the incremental displacement that is periodically
     * applied to the InteractiveCameraFrame position when a 
     * Scene.MOVE_FORWARD or Scene.MOVE_BACKWARD
     * Scene.MouseAction is proceeded.  
     * <p>   
     * <b>Attention:</b> When the InteractiveCameraFrame is set as the
     * {@link remixlab.proscene.Camera#frame()}, this value is set
     * according to the {@link remixlab.proscene.Scene#radius()} by
     * {@link remixlab.proscene.Scene#setRadius(float)}.
     */
    float flySpeed() { 
    	return flySpd; 
    }
	
	/**
	 * Sets the flySpeed(), defined in processing scene units. 
	 * <p> 
	 * Default value is 0.0, but it is modified according to the
	 * {@link remixlab.proscene.Scene#radius()} when the InteractiveCameraFrame
	 * is set as the {@link remixlab.proscene.Camera#frame()}.
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
     * mouse rotate the InteractiveCameraFrame around this vector. Vertical
     * displacements rotate always around the Camera {@code X} axis. 
     * <p> 
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
     * Default value is (0,1,0), but it is updated by the Camera when
     * set as its {@link remixlab.proscene.Camera#frame()}. Use
     * {@link remixlab.proscene.Camera#setUpVector(PVector)} instead in that case.
     */
    public void setFlyUpVector(PVector up) { 
    	flyUpVec = up;
    }
	
	/**
	 * Returns the point the InteractiveCameraFrame revolves around when rotated. 
	 * <p> 
	 * It is defined in the world coordinate system. Default value is (0,0,0). 
	 * <p> 
	 * When the InteractiveCameraFrame is associated to a Camera,
	 * {@link remixlab.proscene.Camera#revolveAroundPoint()}
	 * also returns this value.
	 */	
	public PVector revolveAroundPoint() {
		return revAroundPnt;
	}
	
	/**
	 * Called for continuous frame motion in fly mode (see Scene.MOVE_FORWARD).
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
	 * Sets the {@link #revolveAroundPoint()}, defined in the
	 * world coordinate system.
	 */
	public void setRevolveAroundPoint(PVector revolveAroundPoint) {
		revAroundPnt = revolveAroundPoint;
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseMoveEvent(MouseEvent, Camera)}.
	 * 
	 * <p>
	 * 
	 * Motion depends on mouse binding. The resulting displacements are basically inverted
	 * from those of an InteractiveFrame.
	 */
	public void mouseMoveEvent(MouseEvent event, Camera camera) {		
		int deltaY;
		if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
			deltaY = event.getY() - prevPos.y;
		else
			deltaY = prevPos.y - event.getY();
		
		switch (action) {
		case TRANSLATE: {			
			Point delta = new Point(prevPos.x - event.getX(), deltaY);
			PVector trans = new PVector((float)delta.x, (float)-delta.y, 0.0f);
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {			
			case PERSPECTIVE :
				trans.mult(2.0f * PApplet.tan(camera.fieldOfView()/2.0f) *
						PApplet.abs((camera.frame().coordinatesOf(revolveAroundPoint())).z) / camera.screenHeight());
			    break;
			case ORTHOGRAPHIC : {
				float [] wh = camera.getOrthoWidthHeight();				
				trans.x *= 2.0f * wh[0] / camera.screenWidth();
				trans.y *= 2.0f * wh[1] / camera.screenHeight();
				break;
				}
			}			
			translate(inverseTransformOf(PVector.mult(trans, translationSensitivity())));
			break;
			}
		
		case MOVE_FORWARD: {
			Quaternion rot = pitchYawQuaternion(event.getX(), event.getY(), camera);
			rotate(rot);
			//#CONNECTION# wheelEvent MOVE_FORWARD case
			// actual translation is made in flyUpdate().
			//translate(inverseTransformOf(Vec(0.0, 0.0, -flySpeed())));
			break;
			}
		
		case MOVE_BACKWARD: {
			Quaternion rot = pitchYawQuaternion(event.getX(), event.getY(), camera);
			rotate(rot);
			// actual translation is made in flyUpdate().
			//translate(inverseTransformOf(Vec(0.0, 0.0, flySpeed())));
			break;
			}
		
		case DRIVE: {
			//TODO: perhaps needs more testing
			Quaternion rot = turnQuaternion(event.getX(), camera);
			rotate(rot);
			// actual translation is made in flyUpdate().
			drvSpd = 0.01f * -deltaY;
			break;
			}
		
		case ZOOM: {
			//#CONNECTION# wheelEvent() ZOOM case
			float coef = PApplet.max(PApplet.abs((camera.frame().coordinatesOf(camera.revolveAroundPoint())).z),
					                 0.2f*camera.sceneRadius());
			PVector trans = new PVector(0.0f, 0.0f, -coef * -deltaY / camera.screenHeight());
			translate(inverseTransformOf(trans));
			break;
			}
		
		case LOOK_AROUND: {
			Quaternion rot = pitchYawQuaternion(event.getX(), event.getY(), camera);
			rotate(rot);
			break;
			}
		
		case ROTATE: {
			PVector trans = camera.projectedCoordinatesOf(revolveAroundPoint());
			Quaternion rot = deformedBallQuaternion(event.getX(), event.getY(), trans.x, trans.y, camera);
			//#CONNECTION# These two methods should go together (spinning detection and activation)
			computeMouseSpeed(event);
			setSpinningQuaternion(rot);
			spin();
			break;
			}
		
		case SCREEN_ROTATE: {
			PVector trans = camera.projectedCoordinatesOf(revolveAroundPoint());
			float angle = PApplet.atan2(event.getY() - trans.y, event.getX() - trans.x) - PApplet.atan2(prevPos.y-trans.y, prevPos.x-trans.x);
			if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
				angle = -angle;
			Quaternion rot = new Quaternion(new PVector(0.0f, 0.0f, 1.0f), angle);
			//#CONNECTION# These two methods should go together (spinning detection and activation)
			computeMouseSpeed(event);
			setSpinningQuaternion(rot);
			spin();
			updateFlyUpVector();
			break;
			}
		
		case ROLL: {
			float angle = Quaternion.PI * (event.getX() - prevPos.x) / camera.screenWidth();
			if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
				angle = -angle;
			Quaternion rot = new Quaternion(new PVector(0.0f, 0.0f, 1.0f), angle);
			rotate(rot);
			setSpinningQuaternion(rot);
			updateFlyUpVector();
			break;
			}
		
		case SCREEN_TRANSLATE: {
			PVector trans = new PVector();			
			int dir = mouseOriginalDirection(event);
			if (dir == 1)
				trans.set((prevPos.x - event.getX()), 0.0f, 0.0f);
			else if (dir == -1)
				trans.set(0.0f, -deltaY, 0.0f);			
			switch (camera.type()) {
			case PERSPECTIVE :
				trans.mult(2.0f * PApplet.tan(camera.fieldOfView()/2.0f) *
					      PApplet.abs((camera.frame().coordinatesOf(revolveAroundPoint())).z) / camera.screenHeight());
				break;
			case ORTHOGRAPHIC : {
				float [] wh = camera.getOrthoWidthHeight();
				trans.x *= 2.0f * wh[0] / camera.screenWidth();
				trans.y *= 2.0f * wh[1] / camera.screenHeight();
				break;
				}
			}		
			
			translate(inverseTransformOf(PVector.mult(trans, translationSensitivity())));
			break;
			}
		
		case ZOOM_ON_REGION:
			
		case NO_MOUSE_ACTION:
			break;		
		}
		
		if (action != Scene.MouseAction.NO_MOUSE_ACTION) {
			prevPos = event.getPoint();
		}
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseReleaseEvent(MouseEvent, Camera)}.
	 */	
	public void mouseReleaseEvent(MouseEvent event, Camera camera) {		
		if ((action == Scene.MouseAction.MOVE_FORWARD)  || 
			(action == Scene.MouseAction.MOVE_BACKWARD) || 
			(action == Scene.MouseAction.DRIVE))
		    flyTimer.stop();

		  if (action == Scene.MouseAction.ZOOM_ON_REGION) {
			  //the rectangle needs to be normalized!
			  int w = PApplet.abs(event.getX() - pressPos.x);			  
			  int tlX = pressPos.x < event.getX() ? pressPos.x : event.getX();
			  int h = PApplet.abs(event.getY() - pressPos.y);
			  int tlY  = pressPos.y < event.getY() ? pressPos.y : event.getY();
			  
			  if (event.getButton() == MouseEvent.BUTTON3)
				  camera.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			  else
				  camera.interpolateToZoomOnRegion(new Rectangle (tlX, tlY, w, h));
		  }
		  super.mouseReleaseEvent(event, camera);
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseWheelEvent(MouseWheelEvent, Camera)}. 
	 * <p>  
	 * The wheel behavior depends on the wheel binded action. Current possible actions are ZOOM, 
	 * MOVE_FORWARD, MOVE_BACKWARD. ZOOM speed depends on #wheelSensitivity() MOVE_FORWARD and
	 * MOVE_BACKWARD depend on #flySpeed().
	 */
	public void mouseWheelEvent(MouseWheelEvent event, Camera camera) {
		switch (action) {
		case ZOOM: {
			float wheelSensitivityCoef = 8E-4f;
			//#CONNECTION# mouseMoveEvent() ZOOM case
			float coef = PApplet.max(
					PApplet.abs((camera.frame().coordinatesOf(camera.revolveAroundPoint())).z),
					             0.2f*camera.sceneRadius());  
			PVector trans = new PVector(0.0f, 0.0f,
					coef * (-event.getWheelRotation()) * wheelSensitivity() * wheelSensitivityCoef);
			translate(inverseTransformOf(trans));
			break;
		      }
		    case MOVE_FORWARD:
		    case MOVE_BACKWARD:
		      //#CONNECTION# mouseMoveEvent() MOVE_FORWARD case
		      translate(inverseTransformOf(new PVector(0.0f, 0.0f, 
		    		  0.2f*flySpeed()*(-event.getWheelRotation()))));
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
	private final Quaternion turnQuaternion(int x, Camera camera)	{
	  return new Quaternion(new PVector(0.0f, 1.0f, 0.0f), rotationSensitivity()*(prevPos.x-x)/camera.screenWidth());
	}

	/**
	 * Returns a Quaternion that is the composition of two rotations, inferred
	 * from the mouse pitch (X axis) and yaw ({@link #flyUpVector()} axis).
	 */
	private final Quaternion pitchYawQuaternion(int x, int y, Camera camera) {
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
