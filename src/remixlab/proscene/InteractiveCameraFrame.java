/**
 *                     ProScene (version 1.0.0-beta1)      
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

import java.awt.Rectangle;
import java.awt.Point;

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
 */
public class InteractiveCameraFrame extends InteractiveDrivableFrame {
	private PVector arcballRefPnt;

	/**
	 * Default constructor.
	 * <p>
	 * {@link #flySpeed()} is set to 0.0 and {@link #flyUpVector()} is (0,1,0).
	 * The {@link #arcballReferencePoint()} is set to (0,0,0).
	 * <p>
	 * <b>Attention:</b> Created object is {@link #removeFromMouseGrabberPool()}.
	 */
	public InteractiveCameraFrame(Scene scn) {
		super(scn);
		removeFromMouseGrabberPool();
		arcballRefPnt = new PVector(0.0f, 0.0f, 0.0f);
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
		InteractiveCameraFrame clonedICamFrame = (InteractiveCameraFrame) super
				.clone();
		clonedICamFrame.arcballRefPnt = new PVector(arcballRefPnt.x,
				arcballRefPnt.y, arcballRefPnt.z);
		return clonedICamFrame;
	}

	/**
	 * Overloading of {@link remixlab.proscene.InteractiveFrame#spin()}.
	 * <p>
	 * Rotates the InteractiveCameraFrame around its #arcballReferencePoint()
	 * instead of its origin.
	 */
	public void spin() {
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
		int deltaY;
		if (coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
			deltaY = eventPoint.y - prevPos.y;
		else
			deltaY = prevPos.y - eventPoint.y;

		if ((action == Scene.MouseAction.MOVE_FORWARD)
				|| (action == Scene.MouseAction.MOVE_BACKWARD)
				|| (action == Scene.MouseAction.DRIVE)
				|| (action == Scene.MouseAction.LOOK_AROUND)
				|| (action == Scene.MouseAction.ROLL)
				|| (action == Scene.MouseAction.ZOOM_ON_REGION)
				|| (action == Scene.MouseAction.NO_MOUSE_ACTION))
			super.mouseDragged(eventPoint, camera);
		else
			switch (action) {
			case TRANSLATE: {
				Point delta = new Point(prevPos.x - eventPoint.x, deltaY);
				PVector trans = new PVector((float) delta.x, (float) -delta.y, 0.0f);
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
				translate(inverseTransformOf(PVector.mult(trans,
						translationSensitivity())));
				prevPos = eventPoint;
				break;
			}

			case ZOOM: {
				// #CONNECTION# wheelEvent() ZOOM case
				float coef = PApplet.max(PApplet.abs((camera.frame()
						.coordinatesOf(camera.arcballReferencePoint())).z), 0.2f * camera
						.sceneRadius());
				PVector trans = new PVector(0.0f, 0.0f, -coef * -deltaY
						/ camera.screenHeight());
				translate(inverseTransformOf(trans));
				prevPos = eventPoint;
				break;
			}

			case ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());
				Quaternion rot = deformedBallQuaternion(eventPoint.x, eventPoint.y,
						trans.x, trans.y, camera);
				// #CONNECTION# These two methods should go together (spinning detection
				// and activation)
				computeMouseSpeed(eventPoint);
				setSpinningQuaternion(rot);
				spin();
				prevPos = eventPoint;
				break;
			}

			case SCREEN_ROTATE: {
				PVector trans = camera.projectedCoordinatesOf(arcballReferencePoint());
				float angle = PApplet.atan2(eventPoint.y - trans.y, eventPoint.x
						- trans.x)
						- PApplet.atan2(prevPos.y - trans.y, prevPos.x - trans.x);
				if (coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
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
					trans.set((prevPos.x - eventPoint.x), 0.0f, 0.0f);
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

				translate(inverseTransformOf(PVector.mult(trans,
						translationSensitivity())));
				prevPos = eventPoint;
				break;
			}

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
		// Added by pierre: #CONNECTION# seems that startAction should always be
		// called before :)
		if (action == Scene.MouseAction.ZOOM_ON_REGION) {
			// the rectangle needs to be normalized!
			int w = PApplet.abs(eventPoint.x - pressPos.x);
			int tlX = pressPos.x < eventPoint.x ? pressPos.x : eventPoint.x;
			int h = PApplet.abs(eventPoint.y - pressPos.y);
			int tlY = pressPos.y < eventPoint.y ? pressPos.y : eventPoint.y;

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
	 * {@link remixlab.proscene.InteractiveDrivableFrame#mouseWheelMoved(int, Camera)}.
	 * <p>
	 * The wheel behavior depends on the wheel binded action. Current possible
	 * actions are {@link remixlab.proscene.Scene.MouseAction#ZOOM},
	 * {@link remixlab.proscene.Scene.MouseAction#MOVE_FORWARD} and
	 * {@link remixlab.proscene.Scene.MouseAction#MOVE_BACKWARD}.
	 * {@link remixlab.proscene.Scene.MouseAction#ZOOM} speed depends on
	 * #wheelSensitivity() the other two depend on #flySpeed().
	 */
	public void mouseWheelMoved(int rotation, Camera camera) {
		switch (action) {
		case ZOOM: {
			float wheelSensitivityCoef = 8E-4f;
			// #CONNECTION# mouseMoveEvent() ZOOM case
			float coef = PApplet.max(PApplet.abs((camera.frame().coordinatesOf(camera
					.arcballReferencePoint())).z), 0.2f * camera.sceneRadius());
			PVector trans = new PVector(0.0f, 0.0f, coef * (-rotation)
					* wheelSensitivity() * wheelSensitivityCoef);
			translate(inverseTransformOf(trans));
			break;
		}
		case MOVE_FORWARD:
		case MOVE_BACKWARD:
			// #CONNECTION# mouseMoveEvent() MOVE_FORWARD case
			translate(inverseTransformOf(new PVector(0.0f, 0.0f, 0.2f * flySpeed()
					* (-rotation))));
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
}
