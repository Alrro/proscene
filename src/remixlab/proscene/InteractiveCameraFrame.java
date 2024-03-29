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

import java.util.Timer;
import java.util.TimerTask;
import processing.core.*;

/**
 * The InteractiveCameraFrame class represents an InteractiveFrame with Camera
 * specific mouse bindings.
 * <p>
 * An InteractiveCameraFrame is a specialization of an InteractiveDrivableFrame
 * (hence it can "fly" in the Scene), designed to be set as the
 * {@link Camera#frame()}. Mouse motions are basically interpreted in a negated
 * way: when the mouse goes to the right, the InteractiveFrame (and also the
 * InteractiveDrivableFrame and the InteractiveAvatarFrame) translation goes to
 * the right, while the InteractiveCameraFrame has to go to the <i>left</i>, so
 * that the <i>scene</i> seems to move to the right.
 * <p>
 * An InteractiveCameraFrame rotates around its {@link #arcballReferencePoint()}
 * , which corresponds to the associated {@link Camera#arcballReferencePoint()}.
 * <p>
 * <b>Note:</b> The InteractiveCameraFrame is not added to the
 * {@link remixlab.proscene.Scene#mouseGrabberPool()} upon creation.
 */
public class InteractiveCameraFrame extends InteractiveDrivableFrame {
	protected Camera camera;
	protected PVector arcballRefPnt;
	protected PVector worldAxis;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #flyUpVector()} is (0,1,0).
	 * The {@link #arcballReferencePoint()} is set to (0,0,0).
	 * <p>
	 * <b>Attention:</b> Created object is {@link #removeFromMouseGrabberPool()}.
	 */
	public InteractiveCameraFrame(Camera cam) {
		super(cam.scene);
		camera = cam;
		removeFromMouseGrabberPool();
		arcballRefPnt = new PVector(0.0f, 0.0f, 0.0f);
		worldAxis = new PVector(0, 0, 1);
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
		clonedICamFrame.arcballRefPnt = new PVector(arcballRefPnt.x, arcballRefPnt.y, arcballRefPnt.z);
		clonedICamFrame.worldAxis = new PVector(worldAxis.x, worldAxis.y, worldAxis.z);
		return clonedICamFrame;
	}

	/**
	 * Updates the {@link remixlab.proscene.Camera#lastFrameUpdate} variable when
	 * the frame changes and then calls {@code super.modified()}.
	 */
	@Override
	protected void modified() {		
		camera.lastFrameUpdate = scene.parent.frameCount;
		super.modified();
	}

	/**
	 * Overloading of {@link remixlab.proscene.InteractiveFrame#spin()}.
	 * <p>
	 * Rotates the InteractiveCameraFrame around its #arcballReferencePoint()
	 * instead of its origin.
	 */
	@Override
	public void spin() {
		if(spinningFriction > 0) {
			if (mouseSpeed == 0) {
				stopSpinning();
				return;
			}
			rotateAroundPoint(spinningQuaternion(), arcballReferencePoint());
			recomputeSpinningQuaternion();						
		}
		else
			rotateAroundPoint(spinningQuaternion(), arcballReferencePoint());
	}

	/**
	 * Returns the point the InteractiveCameraFrame revolves around when rotated.
	 * <p>
	 * It is defined in the world coordinate system. Default value is (0,0,0).
	 * <p>
	 * When the InteractiveCameraFrame is associated to a Camera,
	 * {@link remixlab.proscene.Camera#arcballReferencePoint()} also returns this
	 * value.
	 */
	public PVector arcballReferencePoint() {
		return arcballRefPnt;
	}

	/**
	 * Sets the {@link #arcballReferencePoint()}, defined in the world coordinate
	 * system.
	 */
	public void setArcballReferencePoint(PVector refP) {
		arcballRefPnt = refP;
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveDrivableFrame#mouseDragged(Point, Camera)}
	 * .
	 * <p>
	 * Motion depends on mouse binding. The resulting displacements are basically
	 * inverted from those of an InteractiveFrame.
	 */
	public void mouseDragged(Point eventPoint, Camera camera) {
		if ((action == Scene.MouseAction.MOVE_FORWARD)
				|| (action == Scene.MouseAction.MOVE_BACKWARD)
				|| (action == Scene.MouseAction.DRIVE)
				|| (action == Scene.MouseAction.LOOK_AROUND)
				|| (action == Scene.MouseAction.ROLL)
				|| (action == Scene.MouseAction.ZOOM_ON_REGION)
				|| (action == Scene.MouseAction.NO_MOUSE_ACTION))
			super.mouseDragged(eventPoint, camera);
		else {			
			int deltaY;
			if( scene.isRightHanded() )
				deltaY = (int) (prevPos.y - eventPoint.y);
			else
				deltaY = (int) (eventPoint.y - prevPos.y);
			switch (action) {
			case TRANSLATE: {
				Point delta = new Point(prevPos.x - eventPoint.x, deltaY);
				PVector trans = new PVector((int) delta.x, (int) -delta.y, 0.0f);
				// Scale to fit the screen mouse displacement
				switch (camera.type()) {
				case PERSPECTIVE:
					trans.mult(2.0f
							* PApplet.tan(camera.fieldOfView() / 2.0f)
							* PApplet.abs((camera.frame()
									.coordinatesOf(arcballReferencePoint())).z)
							/ camera.screenHeight());
					break;
				case ORTHOGRAPHIC: {
					float[] wh = camera.getOrthoWidthHeight();
					trans.x *= 2.0f * wh[0] / camera.screenWidth();
					trans.y *= 2.0f * wh[1] / camera.screenHeight();
					break;
				}
				}
							  
				computeMouseSpeed(eventPoint);
				setTossingDirection(inverseTransformOf(PVector.mult(trans, translationSensitivity())));
				toss();
				
				prevPos = eventPoint;
				break;
			}

			case ZOOM: {
				// #CONNECTION# wheelEvent() ZOOM case
				float coef = PApplet.max(PApplet.abs((camera.frame().coordinatesOf(camera.arcballReferencePoint())).z), 0.2f * camera.sceneRadius());
				// Warning: same for left and right CoordinateSystemConvention:
				PVector trans = new PVector(0.0f, 0.0f, -coef	* ((int) (eventPoint.y - prevPos.y)) / camera.screenHeight());
							  
				computeMouseSpeed(eventPoint);
				setTossingDirection(inverseTransformOf(trans));
				toss();
				
				prevPos = eventPoint;
				break;
			}

			case ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());
				Quaternion rot = deformedBallQuaternion((int) eventPoint.x,	(int) eventPoint.y, trans.x, trans.y, camera);
				// #CONNECTION# These two methods should go together (spinning detection and activation)
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				spin();
				prevPos = eventPoint;
				break;
			}
			
			case CAD_ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());				
				// the following line calls setSpinningQuaternion
				Quaternion rot = computeCADQuaternion((int) eventPoint.x, (int) eventPoint.y, trans.x, trans.y, camera);
				// #CONNECTION# These two methods should go together (spinning detection and activation)
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				spin();
				prevPos = eventPoint;
				break;
			}

			case SCREEN_ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());
				float angle = PApplet.atan2((int) eventPoint.y - trans.y,
						                        (int) eventPoint.x - trans.x)	- PApplet.atan2((int) prevPos.y - trans.y, 
						                        		                                          (int) prevPos.x - trans.x);
				
			  // left-handed coordinate system correction
				if( scene.isLeftHanded() )
					angle = -angle;

				Quaternion rot = new Quaternion(new PVector(0.0f, 0.0f, 1.0f), angle);
				// #CONNECTION# These two methods should go together (spinning detection
				// and activation)
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				spin();
				updateFlyUpVector();
				prevPos = eventPoint;
				break;
			}

			case SCREEN_TRANSLATE: {
				PVector trans = new PVector();
				int dir = mouseOriginalDirection(eventPoint);
				if (dir == 1)
					trans.set(((int) prevPos.x - (int) eventPoint.x), 0.0f, 0.0f);
				else if (dir == -1)
					trans.set(0.0f, -deltaY, 0.0f);
				switch (camera.type()) {
				case PERSPECTIVE:
					trans.mult(2.0f
							* PApplet.tan(camera.fieldOfView() / 2.0f)
							* PApplet.abs((camera.frame()
									.coordinatesOf(arcballReferencePoint())).z)
							/ camera.screenHeight());
					break;
				case ORTHOGRAPHIC: {
					float[] wh = camera.getOrthoWidthHeight();
					trans.x *= 2.0f * wh[0] / camera.screenWidth();
					trans.y *= 2.0f * wh[1] / camera.screenHeight();
					break;
				}
				}
							  
				computeMouseSpeed(eventPoint);
				setTossingDirection(inverseTransformOf(PVector.mult(trans, translationSensitivity())));
				toss();
				
				prevPos = eventPoint;
				break;
			}

			default:
				prevPos = eventPoint;
				break;
			}
		}
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveFrame#mouseReleased(Point, Camera)}.
	 */
	public void mouseReleased(Point eventPoint, Camera camera) {
		// Added by pierre: #CONNECTION# seems that startAction should always be
		// called before :)
		if (action == Scene.MouseAction.ZOOM_ON_REGION) {
			// the rectangle needs to be normalized!
			int w = PApplet.abs((int) eventPoint.x - (int) pressPos.x);
			int tlX = (int) pressPos.x < (int) eventPoint.x ? (int) pressPos.x
					: (int) eventPoint.x;
			int h = PApplet.abs((int) eventPoint.y - (int) pressPos.y);
			int tlY = (int) pressPos.y < (int) eventPoint.y ? (int) pressPos.y
					: (int) eventPoint.y;

			// overkill:
			// if (event.getButton() == MouseEvent.BUTTON3)//right button
			// camera.fitScreenRegion( new Rectangle (tlX, tlY, w, h) );
			// else
			camera.interpolateToZoomOnRegion(new Rectangle(tlX, tlY, w, h));
		}

		super.mouseReleased(eventPoint, camera);
	}

	/**
	 * Overloading of
	 * {@link remixlab.proscene.InteractiveDrivableFrame#mouseWheelMoved(float, Camera)}.
	 * <p>
	 * The wheel behavior depends on the wheel binded action. Current possible
	 * actions are {@link remixlab.proscene.Scene.MouseAction#ZOOM},
	 * {@link remixlab.proscene.Scene.MouseAction#MOVE_FORWARD} and
	 * {@link remixlab.proscene.Scene.MouseAction#MOVE_BACKWARD}.
	 * {@link remixlab.proscene.Scene.MouseAction#ZOOM} speed depends on
	 * #wheelSensitivity() the other two depend on #flySpeed().
	 */
	public void mouseWheelMoved(float rotation, Camera camera) {
		switch (action) {
		case ZOOM: {
			float wheelSensitivityCoef = 8E-4f;
			// #CONNECTION# mouseMoveEvent() ZOOM case
			float coef = PApplet.max(PApplet.abs((camera.frame().coordinatesOf(camera.arcballReferencePoint())).z), 0.2f * camera.sceneRadius());
			PVector trans = new PVector(0.0f, 0.0f, coef * -rotation * wheelSensitivity() * wheelSensitivityCoef);						
			translate(inverseTransformOf(trans));
			break;
		}
		case MOVE_FORWARD:
		case MOVE_BACKWARD:
			// #CONNECTION# mouseMoveEvent() MOVE_FORWARD case
			translate(inverseTransformOf(new PVector(0.0f, 0.0f, 0.2f * flySpeed() * (-rotation))));
			break;
		default:
			break;
		}

		// #CONNECTION# startAction should always be called before
		if (prevConstraint != null)
			setConstraint(prevConstraint);

		int finalDrawAfterWheelEventDelay = 400;

		// Starts (or prolungates) the timer.
		if (flyTimer != null) {
			flyTimer.cancel();
			flyTimer.purge();
		}
		flyTimer = new Timer();
		TimerTask timerTask = new TimerTask() {
			public void run() {
				flyUpdate();
			}
		};
		flyTimer.schedule(timerTask, finalDrawAfterWheelEventDelay);

		action = Scene.MouseAction.NO_MOUSE_ACTION;
	}
	
	/**
	 * Returns a Quaternion computed according to mouse motion. The Quaternion
	 * is computed as composition of two rotations (quaternions): 1. Mouse motion along
	 * the screen X Axis rotates the camera along the {@link #getCADAxis()}. 2.
	 * Mouse motion along the screen Y axis rotates the camera along its X axis.
	 * 
	 * @see #getCADAxis()
	 */
	protected Quaternion computeCADQuaternion(int x, int y, float cx,	float cy, Camera camera) {
		// Points on the deformed ball
		float px = rotationSensitivity() * ((int) prevPos.x - cx)	/ camera.screenWidth();
		float py = rotationSensitivity() * (scene.isLeftHanded() ? ((int) prevPos.y - cy) : ((cy - (int) prevPos.y))) / camera.screenHeight();
		float dx = rotationSensitivity() * (x - cx) / camera.screenWidth();
		float dy = rotationSensitivity() * (scene.isLeftHanded() ? (y - cy) : (cy - y)) / camera.screenHeight();
		
		//1,0,0 is given in the camera frame
		PVector axisX = new PVector(1, 0, 0);
		//0,0,1 is given in the world and then transform to the camera frame
		PVector world2camAxis = camera.frame().transformOf(worldAxis);
		
		float angleWorldAxis = rotationSensitivity() * (scene.isLeftHanded() ? (dx - px) : (px - dx));
		float angleX = rotationSensitivity() * (dy - py);		

		Quaternion quatWorld = new Quaternion(world2camAxis, angleWorldAxis);
		Quaternion quatX = new Quaternion(axisX, angleX);
		
		return Quaternion.multiply(quatWorld, quatX);
	}
	
	/**
	 * Set axis (defined in the world coordinate system) as the main
	 * rotation axis used in CAD rotation.
	 */
	public void setCADAxis(PVector axis) {
		//non-zero
		if( axis.mag() < 1E-8 )
			return;
		else
			worldAxis = axis.get();
		worldAxis.normalize();
	}
	
	/**
	 * Returns the main CAD rotation axis ((defined in the world coordinate system).
	 */
	public PVector getCADAxis() {
		return worldAxis;
	}
}
