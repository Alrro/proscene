/**
 *                     ProScene (version 1.2.0)      
 *    Copyright (c) 2010-2013 by National University of Colombia
 *                 @author Jean Pierre Charalambos      
 *           http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
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

import java.util.*;

/**
 * A InteractiveFrame is a Frame that can be rotated and translated using the
 * mouse.
 * <p>
 * It converts the mouse motion into a translation and an orientation updates. A
 * InteractiveFrame is used to move an object in the scene. Combined with object
 * selection, its MouseGrabber properties and a dynamic update of the scene, the
 * InteractiveFrame introduces a great reactivity in your processing
 * applications.
 * <p>
 * <b>Note:</b> Once created, the InteractiveFrame is automatically added to
 * the {@link remixlab.proscene.Scene#mouseGrabberPool()}.
 */

public class InteractiveFrame extends Frame implements MouseGrabbable, Cloneable {
	private boolean horiz;// Two simultaneous InteractiveFrame require two mice!
	private int grabsMouseThreshold;
	private float rotSensitivity;
	private float transSensitivity;	
	private float wheelSensitivity;

	// Mouse speed:
	protected float mouseSpeed;
	protected int delay;
	protected int startedTime;
	
	// spinning stuff:
	private float spngSensitivity;
	private boolean isSpng;
	private Timer spngTimer;	
	private Quaternion spngQuat;
	protected float spinningFriction;	
	private float sFriction;
	
	// tossing stuff:
	protected static final float MIN_TOSSING_FRICTION = 0.01f;
	private float tossingSensitivity;
	private boolean isTossed;
	private Timer tossingTimer;
	private PVector tossingDirection;
	protected float tossingFriction;
	private float tFriction;

	// Whether the SCREEN_TRANS direction (horizontal or vertical) is fixed or not.
	private boolean dirIsFixed;

	// MouseGrabber
	protected boolean keepsGrabbingMouse;

	protected Scene.MouseAction action;
	protected Constraint prevConstraint; // When manipulation is without
	// Constraint.
	// Previous mouse position (used for incremental updates) and mouse press
	// position.
	protected Point prevPos, pressPos;

	protected boolean grbsMouse;

	protected boolean isInCamPath;

	// P R O S C E N E A N D P R O C E S S I N G A P P L E T A N D O B J E C T S
	public Scene scene;

	/**
	 * Default constructor.
	 * <p>
	 * The {@link #translation()} is set to (0,0,0), with an identity
	 * {@link #rotation()} (0,0,0,1) (see Frame constructor for details). The
	 * different sensitivities are set to their default values (see
	 * {@link #rotationSensitivity()} , {@link #translationSensitivity()},
	 * {@link #spinningSensitivity()} and {@link #wheelSensitivity()}).
	 * <p>
	 * <b>Note:</b> the InteractiveFrame is automatically added to
	 * the {@link remixlab.proscene.Scene#mouseGrabberPool()}.
	 */
	public InteractiveFrame(Scene scn) {
		scene = scn;

		action = Scene.MouseAction.NO_MOUSE_ACTION;
		horiz = true;

		addInMouseGrabberPool();
		isInCamPath = false;
		grbsMouse = false;

		setGrabsMouseThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);
		
		keepsGrabbingMouse = false;
		
		startedTime = 0;
		
		isSpng = false;
		setSpinningSensitivity(0.3f);
		setSpinningFriction(0.0f);
		
		isTossed = false;
		setTossingSensitivity(0.3f);
		setTossingFriction(1.0f);
		
		prevConstraint = null;		
	}

	/**
	 * Ad-hoc constructor needed to make editable a Camera path defined by
	 * KeyFrameInterpolator.
	 * <p>
	 * Constructs a Frame from the the {@code iFrame} {@link #translation()} and
	 * {@link #orientation()} and immediately adds it to the
	 * {@link remixlab.proscene.Scene#mouseGrabberPool()}.
	 * <p>
	 * A call on {@link #isInCameraPath()} on this Frame will return {@code true}.
	 * 
	 * <b>Attention:</b> Internal use. You should not call this constructor in your
	 * own applications.
	 * 
	 * @see remixlab.proscene.Camera#addKeyFrameToPath(int)
	 */
	public InteractiveFrame(Scene scn, InteractiveCameraFrame iFrame) {
		super(iFrame.translation(), iFrame.rotation());
		scene = scn;
		action = Scene.MouseAction.NO_MOUSE_ACTION;
		horiz = true;

		addInMouseGrabberPool();
		isInCamPath = true;
		grbsMouse = false;

		setGrabsMouseThreshold(10);
		setRotationSensitivity(1.0f);
		setTranslationSensitivity(1.0f);
		setWheelSensitivity(20.0f);
		
		keepsGrabbingMouse = false;
		
		startedTime = 0;
		
		isSpng = false;
		setSpinningSensitivity(0.3f);
		setSpinningFriction(0.0f);
		
		isTossed = false;
		setTossingSensitivity(0.3f);
		setTossingFriction(1.0f);
		
		prevConstraint = null;		

		list = new ArrayList<KeyFrameInterpolator>();
		Iterator<KeyFrameInterpolator> it = iFrame.listeners().iterator();
		while (it.hasNext())
			list.add(it.next());
	}

	/**
	 * Convenience function that simply calls {@code 
	 * applyTransformation(scene)}
	 * 
	 * @see remixlab.proscene.Frame#applyTransformation(Scene)
	 */
	public void applyTransformation() {
		applyTransformation(scene);
	}
	
	/**
	 * Convenience function that simply calls {@code applyWorldTransformation(scene)}
	 * 
	 * @see remixlab.proscene.Frame#applyWorldTransformation(Scene)
	 */
	public void applyWorldTransformation() {
		applyWorldTransformation(scene);
	}

	/**
	 * Returns {@code true} if the InteractiveFrame forms part of a Camera path
	 * and {@code false} otherwise.
	 * 
	 */
	public boolean isInCameraPath() {
		return isInCamPath;
	}

	/**
	 * Implementation of the clone method.
	 * <p>
	 * Calls {@link remixlab.proscene.Frame#clone()} and makes a deep copy of the
	 * remaining object attributes except for {@link #prevConstraint} (which is
	 * shallow copied).
	 * 
	 * @see remixlab.proscene.Frame#clone()
	 */
	public InteractiveFrame clone() {
		InteractiveFrame clonedIFrame = (InteractiveFrame) super.clone();
		return clonedIFrame;
	}

	/**
	 * Returns the grabs mouse threshold which is used by this interactive frame to
	 * {@link #checkIfGrabsMouse(int, int, Camera)}.
	 * 
	 * @see #setGrabsMouseThreshold(int)
	 */
	public int grabsMouseThreshold() {
		return grabsMouseThreshold;
	}
	
	/**
	 * Sets the number of pixels that defined the {@link #checkIfGrabsMouse(int, int, Camera)}
	 * condition.
	 * 
	 * @param threshold number of pixels that defined the {@link #checkIfGrabsMouse(int, int, Camera)}
	 * condition. Default value is 10 pixels (which is set in the constructor). Negative values are
	 * silently ignored.
	 * 
	 * @see #grabsMouseThreshold()
	 * @see #checkIfGrabsMouse(int, int, Camera)
	 */
	public void setGrabsMouseThreshold( int threshold ) {
		if(threshold >= 0)
			grabsMouseThreshold = threshold; 
	}

	/**
	 * Implementation of the MouseGrabber main method.
	 * <p>
	 * The InteractiveFrame {@link #grabsMouse()} when the mouse is within a {@link #grabsMouseThreshold()}
	 * pixels region around its
	 * {@link remixlab.proscene.Camera#projectedCoordinatesOf(PVector)}
	 * {@link #position()}.
	 */
	public void checkIfGrabsMouse(int x, int y, Camera camera) {
		PVector proj = camera.projectedCoordinatesOf(position());
		setGrabsMouse(keepsGrabbingMouse || ((PApplet.abs(x - proj.x) < grabsMouseThreshold()) && (PApplet.abs(y - proj.y) < grabsMouseThreshold())));
	}

	/**
	 * Returns {@code true} when the MouseGrabber grabs the Scene's mouse events.
	 * <p>
	 * This flag is set with {@link #setGrabsMouse(boolean)} by the
	 * {@link #checkIfGrabsMouse(int, int, Camera)} method.
	 */
	public boolean grabsMouse() {
		return grbsMouse;
	}

	/**
	 * Sets the {@link #grabsMouse()} flag. Normally used by
	 * {@link #checkIfGrabsMouse(int, int, Camera)}.
	 */
	public void setGrabsMouse(boolean grabs) {
		grbsMouse = grabs;
	}

	/**
	 * Convenience wrapper function that simply returns {@code scene.isInMouseGrabberPool(this)}.
	 * 
	 * @see remixlab.proscene.Scene#isInMouseGrabberPool(MouseGrabbable)
	 */
	public boolean isInMouseGrabberPool() {
		return scene.isInMouseGrabberPool(this);
	}	

	/**
	 * Convenience wrapper function that simply calls {@code scene.addInMouseGrabberPool(this)}.
	 * 
	 * @see remixlab.proscene.Scene#addInMouseGrabberPool(MouseGrabbable)
	 */
	public void addInMouseGrabberPool() {
		scene.addInMouseGrabberPool(this);
	}

	/**
	 * Convenience wrapper function that simply calls {@code scene.removeFromMouseGrabberPool(this)}.
	 * 
	 * @see remixlab.proscene.Scene#removeFromMouseGrabberPool(MouseGrabbable)
	 */
	public void removeFromMouseGrabberPool() {
		scene.removeFromMouseGrabberPool(this);
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
	 * Defines the {@link #tossingSensitivity()}.
	 */
	public final void setTossingSensitivity(float sensitivity) {
		tossingSensitivity = sensitivity;
	}

	/**
	 * Defines the {@link #wheelSensitivity()}.
	 */
	public final void setWheelSensitivity(float sensitivity) {
		wheelSensitivity = sensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the InteractiveFrame
	 * rotation.
	 * <p>
	 * Default value is 1.0. With an identical mouse displacement, a higher value
	 * will generate a larger rotation (and inversely for lower values). A 0.0
	 * value will forbid InteractiveFrame mouse rotation (see also
	 * {@link #constraint()}).
	 * 
	 * @see #setRotationSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 * @see #tossingSensitivity()
	 */
	public final float rotationSensitivity() {
		return rotSensitivity;
	}

	/**
	 * Returns the influence of a mouse displacement on the InteractiveFrame
	 * translation.
	 * <p>
	 * Default value is 1.0. You should not have to modify this value, since with
	 * 1.0 the InteractiveFrame precisely stays under the mouse cursor.
	 * <p>
	 * With an identical mouse displacement, a higher value will generate a larger
	 * translation (and inversely for lower values). A 0.0 value will forbid
	 * InteractiveFrame mouse translation (see also {@link #constraint()}).
	 * <p>
	 * <b>Note:</b> When the InteractiveFrame is used to move a <i>Camera</i> (see
	 * the InteractiveCameraFrame class documentation), after zooming on a small
	 * region of your scene, the camera may translate too fast. For a camera, it
	 * is the Camera.arcballReferencePoint() that exactly matches the mouse
	 * displacement. Hence, instead of changing the
	 * {@link #translationSensitivity()}, solve the problem by (temporarily)
	 * setting the {@link remixlab.proscene.Camera#arcballReferencePoint()} to a
	 * point on the zoomed region).
	 * 
	 * @see #setTranslationSensitivity(float)
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #wheelSensitivity()
	 * @see #tossingSensitivity()
	 */
	public final float translationSensitivity() {
		return transSensitivity;
	}

	/**
	 * Returns the minimum mouse speed required (at button release) to make the
	 * InteractiveFrame {@link #spin()}.
	 * <p>
	 * See {@link #spin()}, {@link #spinningQuaternion()} and
	 * {@link #startSpinning(long)} for details.
	 * <p>
	 * Mouse speed is expressed in pixels per milliseconds. Default value is 0.3
	 * (300 pixels per second). Use {@link #setSpinningSensitivity(float)} to tune
	 * this value. A higher value will make spinning more difficult (a value of
	 * 100.0 forbids spinning in practice).
	 * 
	 * @see #setSpinningSensitivity(float)
	 * @see #setTossingSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 * @see #tossingSensitivity()
	 */
	public final float spinningSensitivity() {
		return spngSensitivity;
	}
	
	/**
	 * Returns the minimum mouse speed required (at button release) to make the
	 * InteractiveFrame {@link #toss()}.
	 * <p>
	 * See {@link #toss()}, {@link #tossingDirection()} and
	 * {@link #startTossing(long)} for details.
	 * <p>
	 * Mouse speed is expressed in pixels per milliseconds. Default value is 0.3
	 * (300 pixels per second). Use {@link #setTossingSensitivity(float)} to tune
	 * this value. A higher value will make tossing more difficult (a value of
	 * 100.0 forbids tossing in practice).
	 * 
	 * @see #setTossingSensitivity(float)
	 * @see #setSpinningSensitivity(float) 
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #wheelSensitivity()
	 * @see #spinningSensitivity()
	 */
	public final float tossingSensitivity() {
		return tossingSensitivity;
	}

	/**
	 * Returns the mouse wheel sensitivity.
	 * <p>
	 * Default value is 20.0. A higher value will make the wheel action more
	 * efficient (usually meaning a faster zoom). Use a negative value to invert
	 * the zoom in and out directions.
	 * 
	 * @see #setWheelSensitivity(float)
	 * @see #translationSensitivity()
	 * @see #rotationSensitivity()
	 * @see #spinningSensitivity()
	 * @see #tossingSensitivity()
	 */
	public float wheelSensitivity() {
		return wheelSensitivity;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is spinning.
	 * <p>
	 * During spinning, {@link #spin()} rotates the InteractiveFrame by its
	 * {@link #spinningQuaternion()} at a frequency defined when the
	 * InteractiveFrame {@link #startSpinning(long)}.
	 * <p>
	 * Use {@link #startSpinning(long)} and {@link #stopSpinning()} to change this
	 * state. Default value is {@code false}.
	 * 
	 * @see #isTossing()
	 */
	public final boolean isSpinning() {
		return isSpng;
	}

	/**
	 * Returns the incremental rotation that is applied by {@link #spin()} to the
	 * InteractiveFrame orientation when it {@link #isSpinning()}.
	 * <p>
	 * Default value is a {@code null} rotation (identity Quaternion). Use
	 * {@link #setSpinningQuaternion(Quaternion)} to change this value.
	 * <p>
	 * The {@link #spinningQuaternion()} axis is defined in the InteractiveFrame
	 * coordinate system. You can use
	 * {@link remixlab.proscene.Frame#transformOfFrom(PVector, Frame)} to convert
	 * this axis from another Frame coordinate system.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #spinningFriction()}
	 * till it stops completely.
	 * 
	 * @see #tossingDirection()
	 */
	public final Quaternion spinningQuaternion() {
		return spngQuat;
	}

	/**
	 * Defines the {@link #spinningQuaternion()}. Its axis is defined in the
	 * InteractiveFrame coordinate system.
	 * 
	 * @see #setTossingDirection(PVector)
	 */
	public final void setSpinningQuaternion(Quaternion spinningQuaternion) {
		spngQuat = spinningQuaternion;
	}
	
	/**
	 * Returns {@code true} when the InteractiveFrame is tossing.
	 * <p>
	 * During tossing, {@link #toss()} translates the InteractiveFrame by its
	 * {@link #tossingDirection()} at a frequency defined when the
	 * InteractiveFrame {@link #startTossing(long)}.
	 * <p>
	 * Use {@link #startTossing(long)} and {@link #stopTossing()} to change this
	 * state. Default value is {@code false}.
	 * 
	 * @see #isSpinning()
	 */
	public final boolean isTossing() {
		return isTossed;
	}
	
	/**
	 * Returns the incremental translation that is applied by {@link #toss()} to the
	 * InteractiveFrame translation when it {@link #isTossing()}.
	 * <p>
	 * Default value is a {@code null} translation. Use {@link #setTossingDirection(PVector)}
	 * to change this value.
	 * <p>
	 * The direction is defined in the InteractiveFrame coordinate system. You can use
	 * {@link remixlab.proscene.Frame#transformOfFrom(PVector, Frame)} to convert
	 * this direction from another Frame coordinate system.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #tossingFriction()}
	 * till it stops completely.
	 * 
	 * @see #spinningQuaternion()
	 */
	public final PVector tossingDirection() {
		return tossingDirection;
	}
	
	/**
	 * Defines the {@link #tossingDirection()} in the InteractiveFrame coordinate system.
	 * 
	 * @see #setSpinningQuaternion(Quaternion)
	 */
	public final void setTossingDirection(PVector dir) {
		tossingDirection = dir;
	}

	/**
	 * Returns {@code true} when the InteractiveFrame is being manipulated with
	 * the mouse. Can be used to change the display of the manipulated object
	 * during manipulation.
	 */
	public boolean isInInteraction() {
		return action != Scene.MouseAction.NO_MOUSE_ACTION;
	}

	/**
	 * Stops the spinning motion started using {@link #startSpinning(long)}.
	 * {@link #isSpinning()} will return {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #spin()}, since spinning may
	 * be decelerated according to {@link #spinningFriction()} till it stops completely.
	 * 
	 * @see #spinningFriction()
	 * @see #toss()
	 */
	public final void stopSpinning() {
		if(spngTimer!=null) {
			spngTimer.cancel();
			spngTimer.purge();
		}
		isSpng = false;
	}

	/**
	 * Starts the spinning of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #spin()} every {@code
	 * updateInterval} milliseconds. The InteractiveFrame {@link #isSpinning()}
	 * until you call {@link #stopSpinning()}.
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to {@link #spinningFriction()}
	 * till it stops completely.
	 * 
	 * @see #spinningFriction()
	 * @see #toss()
	 */
	public void startSpinning(long updateInterval) {
		isSpng = true;		
		if(updateInterval>0) {
			if(spngTimer!=null) {
				spngTimer.cancel();
				spngTimer.purge();
			}
			spngTimer=new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run() {
					spin();
				}
			};
			spngTimer.scheduleAtFixedRate(timerTask, 0, updateInterval);
		}
	}
	
	/**
	 * Rotates the InteractiveFrame by its {@link #spinningQuaternion()}. Called
	 * by a timer when the InteractiveFrame {@link #isSpinning()}. 
	 * <p>
	 * <b>Attention: </b>Spinning may be decelerated according to
	 * {@link #spinningFriction()} till it stops completely.
	 * 
	 * @see #spinningFriction()
	 * @see #toss()
	 */
	public void spin() {		
		if(spinningFriction() > 0) {
			if (mouseSpeed == 0) {
				stopSpinning();
				return;
			}
			rotate(spinningQuaternion());
			recomputeSpinningQuaternion();						
		}
		else
			rotate(spinningQuaternion());
	}
	
	/**
	 * Defines the {@link #spinningFriction()}. Values must be
	 * in the range [0..1].
	 */
	public void setSpinningFriction(float f) {
		if(f < 0 || f > 1)
			return;
		spinningFriction = f;
		setSpinningFrictionFx(spinningFriction);
	} 
	
	/**
	 * Defines the spinning deceleration.
	 * <p>
	 * Default value is 0.0, i.e., no spinning deceleration. Use
	 * {@link #setSpinningFriction(float)} to tune this value.
	 * A higher value will make spinning more difficult (a value of
	 * 1.0 forbids spinning).
	 * 
	 * @see #tossingFriction()
	 */
	public float spinningFriction() {
		return spinningFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the spinning friction used in
	 * {@link #recomputeSpinningQuaternion()}.
	 */
	protected void setSpinningFrictionFx(float spinningFriction) {
		sFriction = spinningFriction*spinningFriction*spinningFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the spinning friction used in
	 * {@link #recomputeSpinningQuaternion()}.
	 */
	protected float spinningFrictionFx() {
		return sFriction;
	}
	
	/**
	 * Internal method. Recomputes the {@link #spinningQuaternion()}
	 * according to {@link #spinningFriction()}.
	 * 
	 * @see #recomputeTossingDirection()
	 */
	protected void recomputeSpinningQuaternion() {
		float prevSpeed = mouseSpeed;
		float damping = 1.0f - spinningFrictionFx();
		mouseSpeed *= damping;
		if (Math.abs(mouseSpeed) < .001f)
			mouseSpeed = 0;
		float currSpeed = mouseSpeed;
		spinningQuaternion().fromAxisAngle(spinningQuaternion().axis(), spinningQuaternion().angle() * (currSpeed / prevSpeed) );
	}
	
	/**
	 * Stops the tossing motion started using {@link #startTossing(long)}.
	 * {@link #isTossing()} will return {@code false} after this call.
	 * <p>
	 * <b>Attention: </b>This method may be called by {@link #toss()}, since tossing is
	 * decelerated according to {@link #tossingFriction()} till it stops completely.
	 * 
	 * @see #tossingFriction()
	 * @see #spin()
	 */
	public final void stopTossing() {
		if(tossingTimer!=null) {
			tossingTimer.cancel();
			tossingTimer.purge();
		}
		isTossed = false;
	}
	
	/**
	 * Starts the tossing of the InteractiveFrame.
	 * <p>
	 * This method starts a timer that will call {@link #toss()} every {@code
	 * updateInterval} milliseconds. The InteractiveFrame {@link #isTossing()}
	 * until you call {@link #stopTossing()}.
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to {@link #tossingFriction()}
	 * till it stops completely.
	 * 
	 * @see #tossingFriction()
	 * @see #spin()
	 */
	public void startTossing(long updateInterval) {
		isTossed = true;		
		if(updateInterval>0) {
			if(tossingTimer!=null) {
				tossingTimer.cancel();
				tossingTimer.purge();
			}
			tossingTimer=new Timer();
			TimerTask timerTask = new TimerTask() {
				public void run() {
					toss();
				}
			};
			tossingTimer.scheduleAtFixedRate(timerTask, 0, updateInterval);
		}
	}
	
	/**
	 * Translates the InteractiveFrame along its {@link #tossingDirection()}. Called
	 * by a timer when the InteractiveFrame {@link #isTossing()}. 
	 * <p>
	 * <b>Attention: </b>Tossing may be decelerated according to
	 * {@link #tossingFriction()} till it stops completely.
	 * 
	 * @see #tossingFriction()
	 * @see #spin()
	 */
	public void toss() {		
		if(tossingFriction() > 0) {
			if (mouseSpeed == 0) {
				stopTossing();
				return;
			}
			translate(tossingDirection());
			recomputeTossingDirection();						
		}		
		else
			translate(tossingDirection());
	}
		
	/**
	 * Defines the {@link #tossingFriction()}. Values must be
	 * in the range [{@link #MIN_TOSSING_FRICTION}..1].
	 * {@link #MIN_TOSSING_FRICTION} is currently set to 0.01f.
	 */
	public void setTossingFriction(float f) {
		if(f < 0 || f > 1)
			return;
		if(f < MIN_TOSSING_FRICTION) {
			tossingFriction = MIN_TOSSING_FRICTION;
			PApplet.println("Setting tossing friction to " + MIN_TOSSING_FRICTION + " which is its minimum value");
		}
		tossingFriction = f;
		tFriction = f*f*f;
	}
	
	/**
	 * Defines the tossing deceleration.
	 * <p>
	 * Default value is 1.0, i.e., forbids tossing. Use
	 * {@link #setTossingFriction(float)} to tune this value.
	 * A lower value will make tossing easier.
	 * 
	 * @see #spinningFriction()
	 */
	public float tossingFriction() {
		return tossingFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Computes and caches the value of the tossing friction used in
	 * {@link #recomputeTossingDirection()}.
	 */
	protected void setTossingFrictionFx(float tossingFriction) {
		tFriction = tossingFriction*tossingFriction*tossingFriction;
	}
	
	/**
	 * Internal use.
	 * <p>
	 * Returns the cached value of the tossing friction used in
	 * {@link #recomputeTossingDirection()}.
	 */
	protected float tossingFrictionFx() {
		return tFriction;
	} 
	
	/**
	 * Internal method. Recomputes the {@link #tossingDirection()}
	 * according to {@link #tossingFriction()}.
	 * 
	 * @see #recomputeSpinningQuaternion()
	 */
	protected void recomputeTossingDirection() {
		float prevSpeed = mouseSpeed;
		float damping = 1.0f - tossingFrictionFx();
		mouseSpeed *= damping;
		if (Math.abs(mouseSpeed) < .001f)
			mouseSpeed = 0;
		float currSpeed = mouseSpeed;
		setTossingDirection(PVector.mult(this.tossingDirection(), currSpeed / prevSpeed));
	}
	
	/**
	 * Overloading of
	 * {@link remixlab.proscene.MouseGrabbable#mouseClicked(Integer, int, Camera)}.
	 * <p>
	 * Left button double click aligns the InteractiveFrame with the camera axis (see {@link #alignWithFrame(Frame)}
	 * and {@link remixlab.proscene.Scene.ClickAction#ALIGN_FRAME}). Right button projects the InteractiveFrame on
	 * the camera view direction.
	 */
	public void mouseClicked(/**Point eventPoint,*/ Integer button, int numberOfClicks, Camera camera) {
		if(numberOfClicks != 2)
			return;
		switch (button) {
		case PApplet.LEFT:  alignWithFrame(camera.frame()); break;
    case PApplet.RIGHT: projectOnLine(camera.position(), camera.viewDirection()); break;
    default: break;
    }
	}

	/**
	 * Initiates the InteractiveFrame mouse manipulation. Overloading of
	 * {@link remixlab.proscene.MouseGrabbable#mousePressed(Point, Camera)}.
	 * 
	 * The mouse behavior depends on which button is pressed.
	 * 
	 * @see #mouseDragged(Point, Camera)
	 * @see #mouseReleased(Point, Camera)
	 */
	public void mousePressed(Point eventPoint, Camera camera) {
		if (grabsMouse())
			keepsGrabbingMouse = true;

		prevPos = pressPos = eventPoint;
	}	

	/**
	 * Modifies the InteractiveFrame according to the mouse motion.
	 * <p>
	 * Actual behavior depends on mouse bindings. See the Scene documentation for
	 * details.
	 * <p>
	 * The {@code camera} is used to fit the mouse motion with the display
	 * parameters.
	 * 
	 * @see remixlab.proscene.Camera#screenWidth()
	 * @see remixlab.proscene.Camera#screenHeight()
	 * @see remixlab.proscene.Camera#fieldOfView()
	 */
	public void mouseDragged(Point eventPoint, Camera camera) {
		int deltaY = 0;
		if(action != Scene.MouseAction.NO_MOUSE_ACTION)
			if( scene.isRightHanded() )
				deltaY = (int) (eventPoint.y - prevPos.y);
			else
				deltaY = (int) (prevPos.y - eventPoint.y);

		switch (action) {
		case TRANSLATE: {
			Point delta = new Point((eventPoint.x - prevPos.x), deltaY);
			PVector trans = new PVector((int) delta.getX(), (int) -delta.getY(), 0.0f);
			// Scale to fit the screen mouse displacement
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.mult(2.0f * PApplet.tan(camera.fieldOfView() / 2.0f)
						* PApplet.abs((camera.frame().coordinatesOf(position())).z)
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC: {
				float[] wh = camera.getOrthoWidthHeight();
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
						
			computeMouseSpeed(eventPoint);
			setTossingDirection(trans);			
			toss();
			
			prevPos = eventPoint;
			break;
		}

		case ZOOM: {
			// #CONNECTION# wheelEvent ZOOM case
		  //Warning: same for left and right CoordinateSystemConvention:
			PVector trans = new PVector(0.0f, 0.0f, (PVector.sub(camera.position(), position())).mag() * ((int) (eventPoint.y - prevPos.y)) / camera.screenHeight());
			trans = camera.frame().orientation().rotate(trans);
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
			
		  //TODO: experimental
		  //translate(trans);
			computeMouseSpeed(eventPoint);
			setTossingDirection(trans);			
			toss();
			
			prevPos = eventPoint;
			break;
		}

		case SCREEN_ROTATE: {
			// TODO: needs testing to see if it works correctly when left-handed is set
			PVector trans = camera.projectedCoordinatesOf(position());
			float prev_angle = PApplet.atan2((int)prevPos.y - trans.y, (int)prevPos.x - trans.x);
			float angle = PApplet.atan2((int)eventPoint.y - trans.y, (int)eventPoint.x - trans.x);
			PVector axis = transformOf(camera.frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f)));
			 
			Quaternion rot;
			if( scene.isRightHanded() )
				rot = new Quaternion(axis, angle - prev_angle);
			else
				rot = new Quaternion(axis, prev_angle - angle);
			
			// #CONNECTION# These two methods should go together (spinning detection and activation)
			computeMouseSpeed(eventPoint);
			setSpinningQuaternion(rot);
			spin();
			prevPos = eventPoint;
			break;
		}

		case SCREEN_TRANSLATE: {
			// TODO: needs testing to see if it works correctly when left-handed is set
			PVector trans = new PVector();
			int dir = mouseOriginalDirection(eventPoint);
			if (dir == 1)
				trans.set(((int)eventPoint.x - (int)prevPos.x), 0.0f, 0.0f);
			else if (dir == -1)
				trans.set(0.0f, -deltaY, 0.0f);
			switch (camera.type()) {
			case PERSPECTIVE:
				trans.mult(PApplet.tan(camera.fieldOfView() / 2.0f)
						* PApplet.abs((camera.frame().coordinatesOf(position())).z)
						/ camera.screenHeight());
				break;
			case ORTHOGRAPHIC: {
				float[] wh = camera.getOrthoWidthHeight();
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
					  
			computeMouseSpeed(eventPoint);
			setTossingDirection(trans);			
			toss();
			
			prevPos = eventPoint;
			break;
		}

		case ROTATE: {
			PVector trans = camera.projectedCoordinatesOf(position());
			Quaternion rot = deformedBallQuaternion((int)eventPoint.x, (int)eventPoint.y,	trans.x, trans.y, camera);
			trans.set(-rot.x, -rot.y, -rot.z);
			trans = camera.frame().orientation().rotate(trans);
			trans = transformOf(trans);
			rot.x = trans.x;
			rot.y = trans.y;
			rot.z = trans.z;
			// #CONNECTION# These two methods should go together (spinning detection
			// and activation)
			computeMouseSpeed(eventPoint);
			setSpinningQuaternion(rot);
			spin();
			prevPos = eventPoint;
			break;
		}

		case NO_MOUSE_ACTION:
			// Possible when the InteractiveFrame is a MouseGrabber. This method is
			// then called without startAction
			// because of mouseTracking.
			break;

		default:
			prevPos = eventPoint;
			break;
		}
	}

	/**
	 * Stops the InteractiveFrame mouse manipulation.
	 * <p>
	 * Overloading of
	 * {@link remixlab.proscene.MouseGrabbable#mouseReleased(Point, Camera)}.
	 * <p>
	 * If the action was ROTATE MouseAction, a continuous spinning is possible if
	 * the speed of the mouse cursor is larger than {@link #spinningSensitivity()}
	 * when the button is released. Press the rotate button again to stop
	 * spinning.
	 * 
	 * @see #startSpinning(long)
	 * @see #isSpinning()
	 */
	public void mouseReleased(Point event, Camera camera) {
		keepsGrabbingMouse = false;		

		if (prevConstraint != null)
			setConstraint(prevConstraint);

		if (((action == Scene.MouseAction.ROTATE) || (action == Scene.MouseAction.SCREEN_ROTATE) || (action == Scene.MouseAction.CAD_ROTATE) )	&& (mouseSpeed >= spinningSensitivity()))
			startSpinning(delay);
		
		if (((action == Scene.MouseAction.TRANSLATE) || (action == Scene.MouseAction.ZOOM) || (action == Scene.MouseAction.SCREEN_TRANSLATE) ) && (mouseSpeed >= tossingSensitivity()) )
			startTossing(delay);

		action = Scene.MouseAction.NO_MOUSE_ACTION;
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.MouseGrabbable#mouseWheelMoved(float, Camera)}.
	 * <p>
	 * Using the wheel is equivalent to a {@link remixlab.proscene.Scene.MouseAction#ZOOM}.
	 * 
	 * @see #setWheelSensitivity(float)
	 */
	public void mouseWheelMoved(float rotation, Camera camera) {
		if (action == Scene.MouseAction.ZOOM) {
			float wheelSensitivityCoef = 8E-4f;			
			PVector trans = new PVector(0.0f, 0.0f, rotation * wheelSensitivity() * wheelSensitivityCoef * (PVector.sub(camera.position(), position())).mag());
			// #CONNECTION# Cut-pasted from the mouseMoveEvent ZOOM case
			trans = camera.frame().orientation().rotate(trans);
			if (referenceFrame() != null)
				trans = referenceFrame().transformOf(trans);
								  
		  translate(trans);
		}

		// #CONNECTION# startAction should always be called before
		if (prevConstraint != null)
			setConstraint(prevConstraint);

		action = Scene.MouseAction.NO_MOUSE_ACTION;
	}

	/**
	 * Protected method that simply calls {@code startAction(action, true)}.
	 * 
	 * @see #startAction(Scene.MouseAction, boolean)
	 */
	protected void startAction(Scene.MouseAction action) {
		startAction(action, true);
	}
	
	/**
	 * Protected internal method used to handle mouse actions.
	 */
	protected void startAction(Scene.MouseAction act, boolean withConstraint) {
		action = act;

		if (withConstraint)
			prevConstraint = null;
		else {
			prevConstraint = constraint();
			setConstraint(null);
		}

		switch (action) {		
		// /**
		case SCREEN_TRANSLATE:
			dirIsFixed = false;
		case ZOOM:		
		case TRANSLATE:
			mouseSpeed = 0.0f;
			stopTossing();
			break;
		// */
		case ROTATE:
		case CAD_ROTATE:
		case SCREEN_ROTATE:
			mouseSpeed = 0.0f;
			stopSpinning();
			break;

			/**
		case SCREEN_TRANSLATE:
			dirIsFixed = false;
			break;
			*/

		default:
			break;
		}
	}

	/**
	 * Updates mouse speed, measured in pixels/milliseconds. Should be called by
	 * any method which wants to use mouse speed. Currently used to trigger
	 * spinning in {@link #mouseReleased(Point, Camera)}.
	 */
	protected void computeMouseSpeed(Point eventPoint) {
		float dist = (float) Point.distance(eventPoint.x, eventPoint.y, prevPos.getX(), prevPos.getY());

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
	protected int mouseOriginalDirection(Point eventPoint) {
		if (!dirIsFixed) {
			Point delta = new Point((eventPoint.x - pressPos.x),
					(eventPoint.y - pressPos.y));
			dirIsFixed = PApplet.abs((int)delta.x) != PApplet.abs((int)delta.y);
			horiz = PApplet.abs((int)delta.x) > PApplet.abs((int)delta.y);
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
	 * Returns a Quaternion computed according to the mouse motion. Mouse
	 * positions are projected on a deformed ball, centered on ({@code cx},
	 * {@code cy}).
	 */
	protected Quaternion deformedBallQuaternion(int x, int y, float cx, float cy,	Camera camera) {
		// Points on the deformed ball
		float px = rotationSensitivity() *                         ((int)prevPos.x - cx)                           / camera.screenWidth();
		float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int)prevPos.y - cy) : ( cy - (int)prevPos.y)) / camera.screenHeight();
		float dx = rotationSensitivity() *                         (x - cx)             / camera.screenWidth();
		float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();
		
		PVector p1 = new PVector(px, py, projectOnBall(px, py));
		PVector p2 = new PVector(dx, dy, projectOnBall(dx, dy));
		// Approximation of rotation angle
		// Should be divided by the projectOnBall size, but it is 1.0
		PVector axis = p2.cross(p1);
		float angle = 2.0f * PApplet.asin(PApplet.sqrt(MathUtils.squaredNorm(axis) / MathUtils.squaredNorm(p1) / MathUtils.squaredNorm(p2)));
		return new Quaternion(axis, angle);
	}	

	/**
	 * Returns "pseudo-distance" from (x,y) to ball of radius size. For a point
	 * inside the ball, it is proportional to the euclidean distance to the ball.
	 * For a point outside the ball, it is proportional to the inverse of this
	 * distance (tends to zero) on the ball, the function is continuous.
	 */
	protected static float projectOnBall(float x, float y) {
		// If you change the size value, change angle computation in
		// deformedBallQuaternion().
		float size = 1.0f;
		float size2 = size * size;
		float size_limit = size2 * 0.5f;

		float d = x * x + y * y;
		return d < size_limit ? PApplet.sqrt(size2 - d) : size_limit	/ PApplet.sqrt(d);
	}
}
