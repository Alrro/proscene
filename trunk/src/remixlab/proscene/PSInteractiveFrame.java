/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.7.0
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

import java.util.*;
import java.awt.event.*;
import java.awt.Point;
import javax.swing.Timer;

/**
 * A PSInteractiveFrame is a PSFrame that can be rotated and translated using the mouse.
 * <p>
 * It converts the mouse motion into a translation and an orientation updates.
 * A PSInteractiveFrame is used to move an object in the scene. Combined with
 * object selection, its PSMouseGrabber properties and a dynamic update of the
 * scene, the PSInteractiveFrame introduces a great reactivity in your
 * processing applications. 
 */

public class PSInteractiveFrame extends PSFrame
                                implements PSMouseGrabber, Cloneable {
	
	static boolean horiz = true;//Two simultaneous PSInteractiveFrame require two mice!
	
	/**
	 * This enum defines the coordinate system convention which is defined as {@code LEFT_HANDED}
	 * by default (processing standard).
	 */
	public enum CoordinateSystemConvention {LEFT_HANDED, RIGHT_HANDED};
	protected CoordinateSystemConvention coordSysConvention;

	private float rotSensitivity;
	private float transSensitivity;
	private float spngSensitivity;
	private float wheelSensitivity;

	// Mouse speed:
	private float mouseSpeed;
	// spinning stuff:
	private boolean isSpng;
	private Timer spngTimer;
	private int startedTime;
	private int delay;
	private ActionListener taskPerformer;

	private PSQuaternion spngQuat;

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or
	// not.
	private boolean dirIsFixed;

	// PSMouseGrabber
	private boolean keepsGrabbingMouse;

	protected PScene.MouseAction action;
	protected PSConstraint prevConstraint; // When manipulation is without
	// Constraint.
	// Previous mouse position (used for incremental updates) and mouse press
	// position.
	protected Point prevPos, pressPos;

	protected boolean grbsMouse;

	/**
	 * Default constructor.
	 * <p> 
	 * The {@link #translation()} is set to (0,0,0), with an identity {@link #rotation()}
	 * (0,0,0,1) (see PSFrame constructor for details). The different sensitivities are
	 * set to their default values (see {@link #rotationSensitivity()} ,
	 * {@link #translationSensitivity()}, {@link #spinningSensitivity()} and
	 * {@link #wheelSensitivity()}).
	 */
	public PSInteractiveFrame() {
		coordSysConvention = CoordinateSystemConvention.LEFT_HANDED;
		action = PScene.MouseAction.NO_MOUSE_ACTION;
		
		addInMouseGrabberPool();
		grbsMouse = false;
		
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setSpinningSensitivity(0.3f);
		setWheelSensitivity(20.0f);
		
		keepsGrabbingMouse = false;
		isSpng = false;
		prevConstraint = null;
		startedTime = 0;
		// delay = 10;

		taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				spin();
			}
		};
		spngTimer = new Timer(10, taskPerformer);
	}

	/**
	 * Implementation of the clone method. 
	 * <p> 
	 * Calls {@link remixlab.proscene.PSFrame#clone()} and makes a deep
	 * copy of the remaining object attributes except for
	 * {@link #prevConstraint} (which is shallow copied).
	 * 
	 * @see remixlab.proscene.PSFrame#clone()
	 */
	public PSInteractiveFrame clone() {
		PSInteractiveFrame clonedPSiFrame = (PSInteractiveFrame) super.clone();
		clonedPSiFrame.prevPos = new Point(prevPos.x, prevPos.y);
		clonedPSiFrame.pressPos = new Point(pressPos.x, pressPos.y);
		clonedPSiFrame.spngTimer = new Timer(10, taskPerformer);
		return clonedPSiFrame;
	}
	
	/**
	 * Returns the coordinate system convention used by this PSInteractiveFrame.
	 * 
	 * @see #setCoordinateSystemConvention(CoordinateSystemConvention)
	 */
	public CoordinateSystemConvention coordinateSystemConvention() {
		return coordSysConvention;
	}
	
	/**
	 * Defines the coordinate system convention of this PSInteractiveFrame, i.e.,
	 * how mouse displacements are mapped into the virtual world.
	 * 
	 * @see #coordinateSystemConvention()
	 */
	public void setCoordinateSystemConvention(CoordinateSystemConvention c) {
		coordSysConvention = c;
	}
	
	/**
	 * See {@link remixlab.proscene.PSMouseGrabber#getMouseGrabberPool()}.
	 */
	public List<PSMouseGrabber> getMouseGrabberPool() {
		return PSMouseGrabber.MouseGrabberPool;		
	}
	
	/**
	 * Implementation of the PSMouseGrabber main method. 
	 * <p> 
	 * The PSInteractiveFrame {@link #grabsMouse()} when the mouse is within a
	 * 10 pixels region around its
	 * {@link remixlab.proscene.PSCamera#projectedCoordinatesOf(PVector)}
	 * {@link #position()}.
	 */
	public void checkIfGrabsMouse(int x, int y, PSCamera camera) {
		int threshold = 10;
		PVector proj = camera.projectedCoordinatesOf(position());
		
		setGrabsMouse(keepsGrabbingMouse || ((PApplet.abs(x - proj.x) < threshold) 
				                          && (PApplet.abs(y - proj.y) < threshold)));
	}

	/**
	 * Returns {@code true} when the PSMouseGrabber grabs the PScene's mouse events. 
	 * <p> 
	 * This flag is set with {@link #setGrabsMouse(boolean)} by the
	 * {@link #checkIfGrabsMouse(int, int, PSCamera)} method.
	 */
	public boolean grabsMouse() {
		return grbsMouse;
	}
	
	/**
	 * Sets the {@link #grabsMouse()} flag. Normally used by
	 * {@link #checkIfGrabsMouse(int, int, PSCamera)}.
	 */
	public void setGrabsMouse(boolean grabs) {
		grbsMouse = grabs;
	}
	
	/**
	 * Returns {@code true} if the PSMouseGrabber is currently in the
	 * {@link #mouseGrabberPool()} list. 
	 * <p> 
	 * Default value is {@code true}. When set to {@code false} using
	 * {@link #removeFromMouseGrabberPool()}, the PScene no longer
	 * {@link #checkIfGrabsMouse(int, int, PSCamera)} on this PSMouseGrabber.
	 * Use {@link #addInMouseGrabberPool()} to insert it back.
	 */
    public boolean isInMouseGrabberPool() {
		return PSMouseGrabber.MouseGrabberPool.contains(this);
    }
    
	/**
	 * Adds the PSMouseGrabber in the {@link #mouseGrabberPool()}. 
	 * <p> 
	 * All created PSMouseGrabber are automatically added in the
	 * {@link #mouseGrabberPool()} by the constructor. Trying to add a
	 * PSMouseGrabber that already {@link #isInMouseGrabberPool()} has no effect. 
	 * <p> 
	 * Use {@link #removeFromMouseGrabberPool()} to remove the PSMouseGrabber
	 * from the list, so that it is no longer tested with
	 * {@link #checkIfGrabsMouse(int, int, PSCamera)} by the PScene, and hence
	 * can no longer grab mouse focus. Use {@link #isInMouseGrabberPool()} to know
	 * the current state of the PSMouseGrabber.
	 */
    public void addInMouseGrabberPool() {    	
		if (!isInMouseGrabberPool())
			PSMouseGrabber.MouseGrabberPool.add(this);
    }
    
	/**
	 * Removes the PSMouseGrabber from the {@link #mouseGrabberPool()}. 
	 * <p> 
	 * See {@link #addInMouseGrabberPool()} for details. Removing a PSMouseGrabber
	 * that is not in {@link #mouseGrabberPool()} has no effect.
	 */
    public void removeFromMouseGrabberPool() {
    	PSMouseGrabber.MouseGrabberPool.remove(this);
    }
    
	/**
	 * Clears the {@link #mouseGrabberPool()}. 
	 * <p> 
	 * Use this method only if it is faster to clear the {@link #mouseGrabberPool()}
	 * and then to add back a few PSMouseGrabbers than to remove each one independently.
	 * Use PScene.setMouseTracking(false) instead if you want to disable mouse grabbing.
	 */
    public void clearMouseGrabberPool() {
		PSMouseGrabber.MouseGrabberPool.clear();
    }
    
	/**
	 * Defines the {@link #rotationSensitivity()}.
	 */
	public final void setRotationSensitivity(float sensitivity) {
		rotSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #translationSensitivity()}.
	 */
	public final void setTranslationSensitivity(float sensitivity) {
		transSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #spinningSensitivity()}.
	 */
	public final void setSpinningSensitivity(float sensitivity) {
		spngSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public final void setWheelSensitivity(float sensitivity) {
		wheelSensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the PSInteractiveFrame
	 * rotation. 
	 * <p> 
	 * Default value is 8.0. With an identical mouse displacement, a higher
	 * value will generate a larger rotation (and inversely for lower values). A
	 * 0.0 value will forbid PSInteractiveFrame mouse rotation (see also 
	 * {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the PSInteractiveFrame
	 * translation. 
	 * <p> 
	 * Default value is 1.0. You should not have to modify this value, since
	 * with 1.0 the PSInteractiveFrame precisely stays under the mouse cursor. 
	 * <p> 
	 * With an identical mouse displacement, a higher value will generate a
	 * larger translation (and inversely for lower values). A 0.0 value will
	 * forbid PSInteractiveFrame mouse translation (see also {@link #constraint()}). 
	 * <p> 
	 * <b>Note:</b> When the PSInteractiveFrame is used to move a <i>Camera</i>
	 * (see the PSInteractiveCameraFrame class documentation), after zooming on
	 * a small region of your scene, the camera may translate too fast. For a
	 * camera, it is the PSCamera.revolveAroundPoint() that exactly matches the
	 * mouse displacement. Hence, instead of changing the
	 * {@link #translationSensitivity()}, solve the problem by (temporarily) setting
	 * the {@link remixlab.proscene.PSCamera#revolveAroundPoint()} to a point
	 * on the zoomed region).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum mouse speed required (at button release) to make the
	 * PSInteractiveFrame {@link #spin()}.
	 * <p> 
	 * See {@link #spin()}, {@link #spinningQuaternion()} and
	 * {@link #startSpinning(int)} for details. 
	 * <p> 
	 * Mouse speed is expressed in pixels per milliseconds. Default value is 0.3
	 * (300 pixels per second). Use setSpinningSensitivity() to tune this value.
	 * A higher value will make spinning more difficult (a value of 100.0
	 * forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}

	/**
	 * Returns the mouse wheel sensitivity. 
	 * <p> 
	 * Default value is 1.0. A higher value will make the wheel action more
	 * efficient (usually meaning a faster zoom). Use a negative value to invert
	 * the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 */
	float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns {@code true} when the PSInteractiveFrame is spinning. 
	 * <p> 
	 * During spinning, {@link #spin()} rotates the PSInteractiveFrame by its
	 * {@link #spinningQuaternion()} at a frequency defined when the PSInteractiveFrame
	 * {@link #startSpinning(int)}. 
	 * <p> 
	 * Use {@link #startSpinning(int)} and {@link #stopSpinning()} to change this state.
	 * Default value is {@code false}.
	 */
	public final boolean isSpinning() {
		return isSpng;
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the
	 * PSInteractiveFrame orientation when it {@link #isSpinning()}. 
	 * <p> 
	 * Default value is a {@code null} rotation (identity PSQuaternion). Use
	 * {@link #setSpinningQuaternion(PSQuaternion)} to change this value. 
	 * <p> 
	 * The {@link #spinningQuaternion()} axis is defined in the PSInteractiveFrame
	 * coordinate system. You can use
	 * {@link remixlab.proscene.PSFrame#transformOfFrom(PVector, PSFrame)} to
	 * convert this axis from an other PSFrame coordinate system.
	 */
	public final PSQuaternion spinningQuaternion() {
		return spngQuat;
	}

	/**
	 * Defines the {@link #spinningQuaternion()}. Its axis is defined in the
	 * PSInteractiveFrame coordinate system.
	 */
	public final void setSpinningQuaternion(PSQuaternion spinningQuaternion) {
		spngQuat = spinningQuaternion;
	}
	
	/**
	 * Returns {@code true} when the PSInteractiveFrame is being manipulated with the mouse.
	 * Can be used to change the display of the manipulated object during manipulation.
	 */
	boolean isInInteraction() {
		return action != PScene.MouseAction.NO_MOUSE_ACTION;
	}

	/**
	 * Stops the spinning motion started using {@link #startSpinning(int)}.
	 * {@link #isSpinning()} will return {@code false} after this call.
	 */
	public final void stopSpinning() {
		spngTimer.stop();
		isSpng = false;
	}

	/**
	 * Starts the spinning of the PSInteractiveFrame. 
	 * <p> 
	 * This method starts a timer that will call {@link #spin()} every {@code
	 * updateInterval} milliseconds. The PSInteractiveFrame
	 * {@link #isSpinning()} until you call {@link #stopSpinning()}.
	 */
	public void startSpinning(int updateInterval) {
		isSpng = true;		
		spngTimer.setDelay(updateInterval);
		spngTimer.start();
	}

	/**
	 * Rotates the PSInteractiveFrame by its {@link #spinningQuaternion()}.
	 * Called by a timer when the PSInteractiveFrame {@link #isSpinning()}.
	 */
	public void spin() {
		rotate(spinningQuaternion());
	}
	
	/**
	 * Initiates the PSInteractiveFrame mouse manipulation. 
	 * Overloading of
	 * {@link remixlab.proscene.PSMouseGrabber#mousePressEvent(MouseEvent, PSCamera)}.
	 * 
	 * The mouse behavior depends on which button is pressed.
	 * 
	 * @see #mouseMoveEvent(MouseEvent, PSCamera)
	 * @see #mouseReleaseEvent(MouseEvent, PSCamera)
	 */	
	public void mousePressEvent(MouseEvent event, PSCamera camera) {		
		if (grabsMouse())
			keepsGrabbingMouse = true;
		
		prevPos = pressPos = event.getPoint();
	}

	/**
	 * Modifies the PSInteractiveFrame according to the mouse motion. 
	 * <p>  
	 * Actual behavior depends on mouse bindings. See the PScene documentation for details. 
	 * <p> 
	 * The {@code camera} is used to fit the mouse motion with the display parameters.
	 * 
	 * @see remixlab.proscene.PSCamera#screenWidth()
	 * @see remixlab.proscene.PSCamera#screenHeight()
	 * @see remixlab.proscene.PSCamera#fieldOfView()
	*/
	public void mouseMoveEvent(MouseEvent event, PSCamera camera) {
		int deltaY;
		if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
			deltaY = prevPos.y - event.getY();
		else
			deltaY = event.getY() - prevPos.y;
		
		switch (action) {
		case TRANSLATE:	{
			Point delta = new Point((event.getX() - prevPos.x), deltaY);				
			PVector trans = new PVector((float)delta.getX(), (float)-delta.getY(), 0.0f);
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {
			case PERSPECTIVE :
				trans.mult(2.0f * PApplet.tan(camera.fieldOfView()/2.0f) * PApplet.abs((camera.frame().coordinatesOf(position())).z) / camera.screenHeight());
				break;
			case ORTHOGRAPHIC :  {				
				float [] wh = camera.getOrthoWidthHeight();				
				trans.x *= 2.0 * wh[0] / camera.screenWidth();
				trans.y *= 2.0 * wh[1] / camera.screenHeight();
				break;
				}
			}
			// Transform to world coordinate system.
			trans = camera.frame().orientation().rotate(PVector.mult(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null) trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
			}
		
		case ZOOM: {
			//#CONNECTION# wheelEvent ZOOM case
			PVector trans = new PVector( 0.0f, 
					                     0.0f, 
					                     (PVector.sub(camera.position(), position()) ).mag() * deltaY / camera.screenHeight());
			trans = camera.frame().orientation().rotate(trans);
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
			break;
			}
		
		case SCREEN_ROTATE: {
			//TODO: needs testing to see if it works correctly when left-handed is set			
			PVector trans = camera.projectedCoordinatesOf(position());
			float prev_angle = PApplet.atan2(prevPos.y-trans.y, prevPos.x-trans.x);
			float angle = PApplet.atan2(event.getY()-trans.y, event.getX()-trans.x);
			PVector axis = transformOf(camera.frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f)));
			PSQuaternion rot;
			if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
				rot = new PSQuaternion(axis, prev_angle-angle);
			else
				rot = new PSQuaternion(axis, angle-prev_angle);
			//#CONNECTION# These two methods should go together (spinning detection and activation)
			computeMouseSpeed(event);
			setSpinningQuaternion(rot);
			spin();
			break;
			}
		
		case SCREEN_TRANSLATE: {
			//TODO: needs testing to see if it works correctly when left-handed is set
			PVector trans = new PVector();
			int dir = mouseOriginalDirection(event);
			if (dir == 1)
				trans.set((event.getX() - prevPos.x), 0.0f, 0.0f);
			else if (dir == -1)
				trans.set(0.0f, -deltaY, 0.0f);
			switch (camera.type()) {
			case PERSPECTIVE :
				trans.mult(PApplet.tan(camera.fieldOfView()/2.0f) * PApplet.abs((camera.frame().coordinatesOf(position())).z) / camera.screenHeight());
				break;
			case ORTHOGRAPHIC : {				
				float [] wh = camera.getOrthoWidthHeight();
				trans.x *= 2.0 * wh[0] / camera.screenWidth();
				trans.y *= 2.0 * wh[1] / camera.screenHeight();
				break;
				}
			}
			// Transform to world coordinate system.			
			trans = camera.frame().orientation().rotate(PVector.mult(trans, translationSensitivity()));
			// And then down to frame
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			
			translate(trans);
			break;
			}
		
		case ROTATE: {
			PVector trans = camera.projectedCoordinatesOf(position());
			PSQuaternion rot = deformedBallQuaternion(event.getX(), event.getY(), trans.x, trans.y, camera);
			trans.set(-rot.x, -rot.y, -rot.z);
			trans = camera.frame().orientation().rotate(trans);
			trans = transformOf(trans);
			rot.x = trans.x;
			rot.y = trans.y;
			rot.z = trans.z;
			//#CONNECTION# These two methods should go together (spinning detection and activation)
			computeMouseSpeed(event);
			setSpinningQuaternion(rot);
			spin();
			break;
			}
		
		case NO_MOUSE_ACTION:
			// Possible when the PSInteractiveFrame is a PSMouseGrabber. This method is then called without startAction
			// because of mouseTracking.
			break;
			}
		
		if (action != PScene.MouseAction.NO_MOUSE_ACTION) {
			prevPos = event.getPoint();
		}
	}

	/**
	 * Stops the PSInteractiveFrame mouse manipulation. 
	 * <p> 
	 * Overloading of
	 * {@link remixlab.proscene.PSMouseGrabber#mouseReleaseEvent(MouseEvent, PSCamera)}. 
	 * <p> 
	 * If the action was ROTATE MouseAction, a continuous spinning is possible if the speed
	 * of the mouse cursor is larger than {@link #spinningSensitivity()} when the button is
	 * released. Press the rotate button again to stop spinning.
	 * 
	 * @see #startSpinning(int)
	 * @see #isSpinning() 
	 */
	public void mouseReleaseEvent(MouseEvent event, PSCamera camera) {		
		keepsGrabbingMouse = false;

		if (prevConstraint != null)
			setConstraint(prevConstraint);

		if (((action == PScene.MouseAction.ROTATE) || (action == PScene.MouseAction.SCREEN_ROTATE))
				&& (mouseSpeed >= spinningSensitivity()))
			startSpinning(delay);

		action = PScene.MouseAction.NO_MOUSE_ACTION;
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.PSMouseGrabber#mouseDoubleClickEvent(MouseEvent, PSCamera)}. 
	 * <p> 
	 * Left button double click aligns the PSInteractiveFrame with the {@code camera} axis
	 * (see {@link #alignWithFrame(PSFrame)}). Right button projects the PSInteractiveFrame
	 * on the {@code camera} view direction.
	 */
	public void mouseDoubleClickEvent(MouseEvent event, PSCamera camera) {
		//TODO needs to test: event.getModifiers
		if ( !event.isAltDown() && !event.isAltGraphDown() && !event.isControlDown() && !event.isMetaDown() && !event.isShiftDown() ); 
		//if (event.getModifiers() == 0)
			switch (event.getButton()) {
			case MouseEvent.BUTTON1: alignWithFrame(camera.frame()); break;
			case MouseEvent.BUTTON2: projectOnLine(camera.position(), camera.viewDirection()); break;
			default: break;
			}		
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.PSMouseGrabber#mouseWheelEvent(MouseWheelEvent, PSCamera)}. 
	 * <p> 
	 * Using the wheel is equivalent to a ZOOM MouseAction.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public void mouseWheelEvent(MouseWheelEvent event, PSCamera camera) {
		if (action == PScene.MouseAction.ZOOM) {
			float wheelSensitivityCoef = 8E-4f;
			// PVector trans(0.0, 0.0,
			// -event.delta()*wheelSensitivity()*wheelSensitivityCoef*(camera.position()-position()).norm());
			PVector trans = new PVector(0.0f, 0.0f, event.getWheelRotation()
					* wheelSensitivity() * wheelSensitivityCoef
					* (PVector.sub(camera.position(), position())).mag());

			// #CONNECTION# Cut-pasted from the mouseMoveEvent ZOOM case
			trans = camera.frame().orientation().rotate(trans);
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			translate(trans);
		}

		// #CONNECTION# startAction should always be called before
		if (prevConstraint != null)
			setConstraint(prevConstraint);

		action = PScene.MouseAction.NO_MOUSE_ACTION;
	}

	/**
	 * Protected method that simply calls {@code startAction(action, true)}.
	 * 
	 * @see #startAction(PScene.MouseAction, boolean)
	 */
	protected void startAction(
			PScene.MouseAction action) {
		startAction(action, true);
	}

	/**
	 * Protected internal method used to handle mouse events.
	 */
	protected void startAction(
			PScene.MouseAction act,
			boolean withConstraint) {
		
		action = act;
		
		if (withConstraint)
			prevConstraint = null;
		else {
			prevConstraint = constraint();
			setConstraint(null);
		}

		switch (action) {
		case ROTATE:
		case SCREEN_ROTATE:
			mouseSpeed = 0.0f;
			stopSpinning();
			break;

		case SCREEN_TRANSLATE:
			dirIsFixed = false;
			break;

		default:
			break;
		}
	}

	/**
	 * Updates mouse speed, measured in pixels/milliseconds. Should be called by
	 * any method which wants to use mouse speed. Currently used to trigger
	 * spinning in {@link #mouseReleaseEvent(MouseEvent, PSCamera)}.
	 */
	protected void computeMouseSpeed(MouseEvent event) {		
		float dist = (float) Point.distance(event.getX(), event.getY(), prevPos.getX(), prevPos.getY());

		if (startedTime == 0) {
			delay = 0;
			startedTime = (int) System.currentTimeMillis();
		} else {
			delay = (int) System.currentTimeMillis() - startedTime;
			startedTime = (int) System.currentTimeMillis();
		}

		if (delay == 0)
			// Less than a millisecond: assume delay = 1ms
			mouseSpeed = dist;
		else
			mouseSpeed = dist / delay;
	}

	/**
	 * Return 1 if mouse motion was started horizontally and -1 if it was more
	 * vertical. Returns 0 if this could not be determined yet (perfect diagonal
	 * motion, rare).
	 */
	protected int mouseOriginalDirection(MouseEvent event) {				
		if (!dirIsFixed) {
			Point delta = new Point((event.getX() - pressPos.x), (event.getY() - pressPos.y));
			dirIsFixed = PApplet.abs(delta.x) != PApplet.abs(delta.y);
			horiz = PApplet.abs(delta.x) > PApplet.abs(delta.y);
		}
		
		if (dirIsFixed)
			if (horiz)
				return 1;
			else
				return -1;
		else
			return 0;
	}
	
	/**
	 * Returns a PSQuaternion computed according to the mouse motion.
	 * Mouse positions are projected on a deformed ball, centered on
	 * ({@code cx}, {@code cy}).
	 */
	protected PSQuaternion deformedBallQuaternion(int x, int y, float cx, float cy, PSCamera camera) {
		// Points on the deformed ball
		float px = rotationSensitivity() * (prevPos.x - cx) / camera.screenWidth();
		float py = rotationSensitivity() * (cy - prevPos.y)  / camera.screenHeight();
		float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
		float dy = rotationSensitivity() * (cy - y)	/ camera.screenHeight();
		
		PVector p1 = new PVector(px, py, projectOnBall(px, py));
		PVector p2 = new PVector(dx, dy, projectOnBall(dx, dy));
		// Approximation of rotation angle
		// Should be divided by the projectOnBall size, but it is 1.0
		PVector axis = p2.cross(p1);
		 
		float angle = 2.0f * PApplet.asin(PApplet.sqrt(PSUtility.squaredNorm(axis) / PSUtility.squaredNorm(p1) / PSUtility.squaredNorm(p2)));
		
		if ( coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED) {
			axis.y = -axis.y;
			angle=-angle;
		}
		
		return new PSQuaternion(axis, angle);
	}
	
	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point
	 * inside the ball, it is proportional to the euclidean distance to the ball.
	 * For a point outside the ball, it is proportional to the inverse of this
	 * distance (tends to zero) on the ball, the function is continuous.
	 */
	static float projectOnBall(float x, float y) {
	  // If you change the size value, change angle computation in deformedBallQuaternion().
	  float size       = 1.0f;
	  float size2      = size*size;
	  float size_limit = size2*0.5f;

	  float d = x*x + y*y;
	  return d < size_limit ? PApplet.sqrt(size2 - d) : size_limit/PApplet.sqrt(d);
	}
	
	/**
	 * Static version of {@link #getMouseGrabberPool()}. 
	 * @return {@link PSMouseGrabber#MouseGrabberPool}
	 */
	public static List<PSMouseGrabber> mouseGrabberPool() {
		return PSMouseGrabber.MouseGrabberPool;
	}
}
