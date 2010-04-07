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
import remixlab.proscene.InteractiveFrame.CoordinateSystemConvention;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

/**
 * A perspective or orthographic camera.
 * <p>
 * A Camera defines some intrinsic parameters ({@link #fieldOfView()},
 * {@link #position()}, {@link #viewDirection()}, {@link #upVector()}...)
 * and useful positioning tools that ease its placement
 * ({@link #showEntireScene()}, {@link #fitSphere(PVector, float)},
 * {@link #lookAt(PVector)}...). It exports its associated projection and
 * modelview matrices and can interactively be modified using the mouse.
 */
public class Camera implements Cloneable {
	public class WorldPoint {
		public WorldPoint(PVector p, boolean f) {
			point = p;
			found = f;
		}
		public PVector point;
		public boolean found;
	}
	static int viewport[] = new int [4];
	//next variables are needed for frustrum plan coefficients
	static PVector normal[] = new PVector[6];
	static float dist[] = new float[6];
	
	/**
	 * Enumerates the two possible types of Camera.
	 * <p>
	 * This type mainly defines different camera projection matrix. Many other
	 * methods take this Type into account.
	 */
	public enum Type {
		PERSPECTIVE, ORTHOGRAPHIC
	};
	
	// F r a m e
	private InteractiveCameraFrame frm;

	// C a m e r a p a r a m e t e r s
	private int scrnWidth, scrnHeight; // size of the window, in pixels
	private float fldOfView; // in radians
	private PVector scnCenter;
	private float scnRadius; // processing scene units
	private float zNearCoef;
	private float zClippingCoef;
	private float orthoCoef;
	private Type tp; // PERSPECTIVE or ORTHOGRAPHIC
	private PMatrix3D modelViewMat;
	private PMatrix3D projectionMat;

	// S t e r e o p a r a m e t e r s
	private float IODist; // inter-ocular distance, in meters
	private float focusDist; // in scene units
	private float physicalDist2Scrn; // in meters
	private float physicalScrnWidth; // in meters
	
	// P o i n t s   o f   V i e w s   a n d   K e y F r a m e s
    private Map<Integer, KeyFrameInterpolator> kfi;
    private KeyFrameInterpolator interpolationKfi;

	/**
	 * Default constructor. 
	 * <p> 
	 * {@link #sceneCenter()} is set to (0,0,0) and {@link #sceneRadius()} is
	 * set to 1.0. {@link #type()} Camera.PERSPECTIVE, with a {@code PI/4}
	 * {@link #fieldOfView()}. 
	 * <p> 
	 * See {@link #IODistance()}, {@link #physicalDistanceToScreen()},
	 * {@link #physicalScreenWidth()} and {@link #focusDistance()}
	 * documentations for default stereo parameter values.
	 */
	public Camera() {		
		fldOfView = Quaternion.PI / 4.0f;
		
		//interpolationKfi = new KeyFrameInterpolator();
		setFrame(new InteractiveCameraFrame());

		// Requires fieldOfView() to define focusDistance()
		setSceneRadius(1.0f);

		// Initial value (only scaled after this)
		orthoCoef = PApplet.tan(fieldOfView() / 2.0f);

		// Also defines the revolveAroundPoint(), which changes orthoCoef.
		setSceneCenter(new PVector(0.0f, 0.0f, 0.0f));

		// Requires fieldOfView() when called with ORTHOGRAPHIC. Attention to
		// projectionMat below.
		setType(Camera.Type.PERSPECTIVE);

		setZNearCoefficient(0.005f);
		setZClippingCoefficient(PApplet.sqrt(3.0f));

		// Dummy values
		setScreenWidthAndHeight(600, 400);

		// Stereo parameters
		setIODistance(0.062f);
		setPhysicalDistanceToScreen(0.5f);
		setPhysicalScreenWidth(0.4f);
		// focusDistance is set from setFieldOfView()
		
		modelViewMat = new PMatrix3D();
		projectionMat  = new PMatrix3D();
		
		projectionMat.set(0, 0, 0, 0,
						  0, 0, 0, 0,
						  0, 0, 0, 0,
						  0, 0, 0, 0);
		computeProjectionMatrix();
	}

	/**
	 * Implementation of the clone method. 
	 * <p> 
	 * Calls {@link remixlab.proscene.Frame#clone()} and makes a deep
	 * copy of the remaining object attributes except for
	 * {@code prevConstraint} (which is shallow copied).
	 * 
	 * @see remixlab.proscene.Frame#clone()
	 */
	public Camera clone() {
		try {
			Camera clonedCam = (Camera) super.clone();
			clonedCam.scnCenter = new PVector(scnCenter.x, scnCenter.y, scnCenter.z);			
			clonedCam.modelViewMat = new PMatrix3D(modelViewMat);
			clonedCam.projectionMat = new PMatrix3D(projectionMat);
			clonedCam.frm = frm.clone();
			return clonedCam;
		} catch (CloneNotSupportedException e) {
			throw new Error("Is too");
		}
	}

	// 1. POSITION AND ORIENTATION

	/**
	 * Returns the Camera position (the eye), defined in the world coordinate
	 * system. 
	 * <p> 
	 * Use {@link #setPosition(PVector)} to set the Camera position. Other
	 * convenient methods are showEntireScene() or fitSphere(). Actually returns
	 * {@link remixlab.proscene.Frame#position()}. 
	 * <p> 
	 * This position corresponds to the projection center of a
	 * Camera.PERSPECTIVE camera. It is not located in the image plane, which
	 * is at a zNear() distance ahead.
	 */
	public final PVector position() {
		return frame().position();
	}

	/**
	 * Sets the Camera {@link #position()} (the eye), defined in the world
	 * coordinate system.
	 */
	public void setPosition(PVector pos) {
		frame().setPosition(pos);
	}

	/**
	 * Returns the normalized up vector of the Camera, defined in the world
	 * coordinate system. 
	 * <p> 
	 * Set using {@link #setUpVector(PVector)} or {@link #setOrientation(Quaternion)}.
	 * It is orthogonal to {@link #viewDirection()} and to {@link #rightVector()}. 
	 * <p> 
	 * It corresponds to the Y axis of the associated {@link #frame()} (actually returns
	 * {@link remixlab.proscene.Frame#inverseTransformOf(PVector)})
	 */
	public PVector upVector() {
		return frame().inverseTransformOf(new PVector(0.0f, 1.0f, 0.0f));
	}

	/**
	 * Convenience function that simply calls {@code setUpVector(up, true)}.
	 * 
	 * @see #setUpVector(PVector, boolean)
	 */
	public void setUpVector(PVector up) {
		setUpVector(up, true);
	}

	/**
	 * Rotates the Camera so that its {@link #upVector()} becomes {@code up}
	 * (defined in the world coordinate system). 
	 * <p> 
	 * The Camera is rotated around an axis orthogonal to {@code up} and to
	 * the current {@link #upVector()} direction. 
	 * <p> 
	 * Use this method in order to define the Camera horizontal plane.
	 * <p> 
	 * When {@code noMove} is set to {@code false}, the orientation modification
	 * is compensated by a translation, so that the
	 * {@link #revolveAroundPoint()} stays projected at the same position on
	 * screen. This is especially useful when the Camera is an observer of the
	 * scene (default mouse binding). 
	 * <p> 
	 * When {@code noMove} is true, the Camera {@link #position()} is left
	 * unchanged, which is an intuitive behavior when the Camera is in a
	 * walkthrough fly mode.
	 * 
	 * See also setViewDirection(), lookAt() and setOrientation().
	 */
	public void setUpVector(PVector up, boolean noMove) {
		Quaternion q = new Quaternion(new PVector(0.0f, 1.0f, 0.0f),
				frame().transformOf(up));

		if (!noMove)
			frame().setPosition(
					PVector.sub(revolveAroundPoint(), (Quaternion.multiply(
							frame().orientation(), q)).rotate(frame()
							.coordinatesOf(revolveAroundPoint()))));

		frame().rotate(q);

		// Useful in fly mode to keep the horizontal direction.
		frame().updateFlyUpVector();
	}

	/**
	 * Returns the normalized view direction of the Camera, defined in the
	 * world coordinate system. 
	 * <p> 
	 * Change this value using {@link #setViewDirection(PVector)}, {@link #lookAt(PVector)}
	 * or {@link #setOrientation(Quaternion)}. It is orthogonal to {@link #upVector()} and
	 * to {@link #rightVector()}.
	 * <p> 
	 * This corresponds to the negative Z axis of the {@link #frame()} ( {@code
	 * frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f))} ).
	 */
	public PVector viewDirection() {
		return frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f));
	}

	/**
	 * Rotates the Camera so that its {@link #viewDirection()} is {@code
	 * direction} (defined in the world coordinate system). 
	 * <p> 
	 * The Camera {@link #position()} is not modified. The Camera is rotated
	 * so that the horizon (defined by its {@link #upVector()}) is preserved.
	 * 
	 * @see #lookAt(PVector)
	 * @see #setUpVector(PVector)
	 */
	public void setViewDirection(PVector direction) {
		if (Utility.squaredNorm(direction) < 1E-10)
			return;

		PVector xAxis = direction.cross(upVector());
		if (Utility.squaredNorm(xAxis) < 1E-10) {
			// target is aligned with upVector, this means a rotation around X
			// axis
			// X axis is then unchanged, let's keep it !
			xAxis = frame().inverseTransformOf(new PVector(1.0f, 0.0f, 0.0f));
		}

		Quaternion q = new Quaternion();
		q.fromRotatedBasis(xAxis, xAxis.cross(direction), PVector.mult(
				direction, -1));
		frame().setOrientationWithConstraint(q);
	}

	/**
	 * Returns the normalized right vector of the Camera, defined in the world
	 * coordinate system. 
	 * <p> 
	 * This vector lies in the Camera horizontal plane, directed along the X
	 * axis (orthogonal to {@link #upVector()} and to {@link #viewDirection()}.
	 * Set using {@link #setUpVector(PVector)}, {@link #lookAt(PVector)} or
	 * {@link #setOrientation(Quaternion)}.
	 * <p> 
	 * Simply returns {@code frame().inverseTransformOf(new PVector(1.0f, 0.0f,
	 * 0.0f))}.
	 */
	public PVector rightVector() {
		return frame().inverseTransformOf(new PVector(1.0f, 0.0f, 0.0f));
	}

	/**
	 * Returns the Camera orientation, defined in the world coordinate system.
	 * <p> 
	 * Actually returns {@code frame().orientation()}. 
	 * Use {@link #setOrientation(Quaternion)}, {@link #setUpVector(PVector)}
	 * or {@link #lookAt(PVector)} to set the Camera orientation.
	 */
	public Quaternion orientation() {
		return frame().orientation();
	}

	/**
	 * Sets the {@link #orientation()} of the Camera using polar coordinates. 
	 * <p> 
	 * {@code theta} rotates the Camera around its Y axis, and then {@code
	 * phi} rotates it around its X axis. 
	 * <p> 
	 * The polar coordinates are defined in the world coordinates system:
	 * {@code theta = phi = 0} means that the Camera is directed towards the
	 * world Z axis. Both angles are expressed in radians.
	 * <p> 
	 * The {@link #position()} of the Camera is unchanged, you may want to call
	 * {@link #showEntireScene()} after this method to move the Camera.
	 * 
	 * @see #setUpVector(PVector)
	 */
	public void setOrientation(float theta, float phi) {
		//TODO: check coordinate system convention
		PVector axis = new PVector(0.0f, 1.0f, 0.0f);
		Quaternion rot1 = new Quaternion(axis, theta);
		axis.set(-PApplet.cos(theta), 0.0f, PApplet.sin(theta));
		Quaternion rot2 = new Quaternion(axis, phi);
		setOrientation(Quaternion.multiply(rot1, rot2));
	}

	/**
	 * Sets the Camera {@link #orientation()}, defined in the world coordinate
	 * system.
	 */
	public void setOrientation(Quaternion q) {
		frame().setOrientation(q);
		frame().updateFlyUpVector();
	}
	
	/**
	 * Wrapper function that simply calls
	 * {@code frame().setCoordinateSystemConvention(CoordinateSystemConvention.LEFT_HANDED)} 
	 */
	public void setCoordinateSystemLeftHanded() {
		frm.setCoordinateSystemConvention(CoordinateSystemConvention.LEFT_HANDED);
	}
	
	/**
	 * Wrapper function that simply calls
	 * {@code frame().setCoordinateSystemConvention(CoordinateSystemConvention.RIGHT_HANDED)} 
	 */
	public void setCoordinateSystemRightHanded() {
		frm.setCoordinateSystemConvention(CoordinateSystemConvention.RIGHT_HANDED);
	}
	
	// 2. FRUSTUM

	/**
	 * Returns the Camera.Type of the Camera. 
	 * <p> 
	 * Set by {@link #setType(Type)}. 
	 * <p> 
	 * A {@link remixlab.proscene.Camera.Type#PERSPECTIVE} Camera uses a classical projection
	 * mainly defined by its {@link #fieldOfView()}. 
	 * <p> 
	 * With a {@link remixlab.proscene.Camera.Type#ORTHOGRAPHIC} {@link #type()}, the {@link #fieldOfView()} is
	 * meaningless and the width and height of the Camera frustum are inferred
	 * from the distance to the {@link #revolveAroundPoint()} using
	 * {@link #getOrthoWidthHeight()}. 
	 * <p> 
	 * Both types use {@link #zNear()} and {@link #zFar()} (to define their
	 * clipping planes) and {@link #aspectRatio()} (for frustum shape).
	 */
	public final Type type() {
		return tp;
	}

	/**
	 * Defines the Camera {@link #type()}. 
	 * <p> 
	 * Changing the Camera Type alters the viewport and the objects' size can
	 * be changed. This method guarantees that the two frustum match in a plane
	 * normal to {@link #viewDirection()}, passing through the Revolve Around
	 * Point (RAP).
	 */
	public final void setType(Type type) {
		// make ORTHOGRAPHIC frustum fit PERSPECTIVE (at least in plane normal
		// to viewDirection(), passing
		// through RAP). Done only when CHANGING type since orthoCoef may have
		// been changed with a
		// setRevolveAroundPoint() in the meantime.
		if ((type == Camera.Type.ORTHOGRAPHIC)
				&& (type() == Camera.Type.PERSPECTIVE))
			orthoCoef = PApplet.tan(fieldOfView() / 2.0f);

		this.tp = type;
	}

	/**
	 * Returns the vertical field of view of the Camera (in radians). 
	 * <p> 
	 * Value is set using {@link #setFieldOfView(float)}. Default value is pi/4
	 * radians. This value is meaningless if the Camera {@link #type()} is
	 * {@link remixlab.proscene.Camera.Type#ORTHOGRAPHIC}. 
	 * <p> 
	 * The field of view corresponds the one used in {@code gluPerspective} (see
	 * manual). It sets the Y (vertical) aperture of the Camera. The X
	 * (horizontal) angle is inferred from the window aspect ratio (see
	 * {@link #aspectRatio()} and {@link #horizontalFieldOfView()}).
	 * <p> 
	 * Use {@link #setFOVToFitScene()} to adapt the {@link #fieldOfView()}
	 * to a given scene.
	 */
	public float fieldOfView() {
		return fldOfView;
	}

	/**
	 * Sets the vertical {@link #fieldOfView()} of the Camera (in radians). 
	 * <p> 
	 * Note that {@link #focusDistance()} is set to {@link #sceneRadius()} /
	 * tan( {@link #fieldOfView()}/2) by this method.
	 */
	public void setFieldOfView(float fov) {
		fldOfView = fov;
		setFocusDistance(sceneRadius() / PApplet.tan(fov / 2.0f));
	}

	/**
	 * Changes the Camera {@link #fieldOfView()} so that the entire scene
	 * (defined by {@link remixlab.proscene.Scene#center()} and
	 * {@link remixlab.proscene.Scene#radius()} is visible from the Camera
	 * {@link #position()}. 
	 * <p> 
	 * The {@link #position()} and {@link #orientation()} of the Camera are
	 * not modified and you first have to orientate the Camera in order to
	 * actually see the scene (see {@link #lookAt(PVector)},
	 * {@link #showEntireScene()} or {@link #fitSphere(PVector, float)}). 
	 * <p> 
	 * This method is especially useful for <i>shadow maps</i> computation. Use
	 * the Camera positioning tools ({@link #setPosition(PVector)},
	 * {@link #lookAt(PVector)}) to position a Camera at the light position.
	 * Then use this method to define the {@link #fieldOfView()} so that the
	 * shadow map resolution is optimally used: 
	 * <p> 
	 * {@code // The light camera needs size hints in order to optimize its
	 * fieldOfView} <br>
	 * {@code lightCamera.setSceneRadius(sceneRadius());} <br>
	 * {@code lightCamera.setSceneCenter(sceneCenter());} <br>
	 * {@code // Place the light camera} <br>
	 * {@code lightCamera.setPosition(lightFrame.position());} <br>
	 * {@code lightCamera.lookAt(sceneCenter());} <br>
	 * {@code lightCamera.setFOVToFitScene();} <br>
	 * <p> 
	 * <b>Attention:</b> The {@link #fieldOfView()} is clamped to M_PI/2.0. This
	 * happens when the Camera is at a distance lower than sqrt(2.0) *
	 * sceneRadius() from the sceneCenter(). It optimizes the shadow map
	 * resolution, although it may miss some parts of the scene.
	 */
	public void setFOVToFitScene() {
		if (distanceToSceneCenter() > PApplet.sqrt(2.0f) * sceneRadius())
			setFieldOfView(2.0f * PApplet.asin(sceneRadius()
					/ distanceToSceneCenter()));
		else
			setFieldOfView(Quaternion.PI / 2.0f);
	}

	/**
	 * Convenience function that simply returns {@code getOrthoWidthHeight(new
	 * float[2])}.
	 */
	public float[] getOrthoWidthHeight() {		
		return getOrthoWidthHeight(new float[2]);
	}

	/**
	 * Fills in {@code target} with the {@code halfWidth} and {@code halfHeight}
	 * of the Camera orthographic frustum and returns it. 
	 * <p> 
	 * While {@code target[0]} holds {@code halfWidth}, {@code target[1]} holds
	 * {@code halfHeight}. 
	 * <p> 
	 * These values are only valid and used when the Camera is of {@link #type()}
	 * ORTHOGRAPHIC and they are expressed in processing scene units.
	 * <p> 
	 * These values are proportional to the Camera (z projected) distance to
	 * the {@link #revolveAroundPoint()}. When zooming on the object, the
	 * Camera is translated forward \e and its frustum is narrowed, making the
	 * object appear bigger on screen, as intuitively expected. 
	 * <p> 
	 * Overload this method to change this behavior if desired.
	 */
	public float[] getOrthoWidthHeight(float[] target) {
		if ((target == null) || (target.length != 2)) {
			target = new float[2];
		}
		float dist = orthoCoef
				* PApplet.abs(cameraCoordinatesOf(revolveAroundPoint()).z);
		// #CONNECTION# fitScreenRegion
		// 1. halfWidth
		target[0] = dist * ((aspectRatio() < 1.0f) ? 1.0f : aspectRatio());
		// 2. halfHeight
		target[1] = dist
				* ((aspectRatio() < 1.0f) ? 1.0f / aspectRatio() : 1.0f);

		return target;
	}

	/**
	 * Returns the horizontal field of view of the Camera (in radians). 
	 * <p> 
	 * Value is set using {@link #setHorizontalFieldOfView(float)} or
	 * {@link #setFieldOfView(float)}. These values are always linked by:
	 * {@code horizontalFieldOfView() = 2.0 * atan ( tan(fieldOfView()/2.0) *
	 * aspectRatio() )}.
	 */
	public float horizontalFieldOfView() {
		return 2.0f * PApplet.atan(PApplet.tan(fieldOfView() / 2.0f)
				* aspectRatio());
	}

	/**
	 * Sets the {@link #horizontalFieldOfView()} of the Camera (in radians). 
	 * <p> 
	 * {@link #horizontalFieldOfView()} and {@link #fieldOfView()} are linked by
	 * the {@link #aspectRatio()}. This method actually calls {@code
	 * setFieldOfView(( 2.0 * atan (tan(hfov / 2.0) / aspectRatio()) ))} so that
	 * a call to {@link #horizontalFieldOfView()} returns the expected value.
	 */
	public void setHorizontalFieldOfView(float hfov) {
		setFieldOfView(2.0f * PApplet.atan(PApplet.tan(hfov / 2.0f)
				/ aspectRatio()));
	}

	/**
	 * Returns the Camera aspect ratio defined by {@link #screenWidth()} /
	 * {@link #screenHeight()}. 
	 * <p> 
	 * When the Camera is attached to a Scene, these values and hence the
	 * aspectRatio() are automatically fitted to the viewer's window aspect
	 * ratio using setScreenWidthAndHeight().
	 */
	public float aspectRatio() {
		return (float) scrnWidth / (float) scrnHeight;
	}

	/**
	 * Defines the Camera {@link #aspectRatio()}. 
	 * <p> 
	 * This value is actually inferred from the {@link #screenWidth()} /
	 * {@link #screenHeight()} ratio. You should use
	 * {@link #setScreenWidthAndHeight(int, int)} instead. 
	 * <p> 
	 * This method might however be convenient when the Camera is not
	 * associated with a Scene. It actually sets the
	 * {@link #screenHeight()} to 100 and the {@link #screenWidth()}
	 * accordingly. See also {@link #setFOVToFitScene()}.
	 */
	public void setAspectRatio(float aspect) {
		setScreenWidthAndHeight((int) (100.0 * aspect), 100);
	}

	/**
	 * Sets Camera {@link #screenWidth()} and {@link #screenHeight()}
	 * (expressed in pixels). 
	 * <p> 
	 * You should not call this method when the Camera is associated with a
	 * Scene, since the latter automatically updates these values when it
	 * is resized (hence overwriting your values). 
	 * <p> 
	 * Non-positive dimension are silently replaced by a 1 pixel value to ensure
	 * frustrum coherence. 
	 * <p> 
	 * If your Camera is used without a Scene (offscreen rendering,
	 * shadow maps), use {@link #setAspectRatio(float)} instead to define the
	 * projection matrix.
	 */
	public void setScreenWidthAndHeight(int width, int height) {
		// Prevent negative and zero dimensions that would cause divisions by
		// zero.
		scrnWidth = width > 0 ? width : 1;
		scrnHeight = height > 0 ? height : 1;
	}

	/**
	 * Returns the width (in pixels) of the Camera screen. 
	 * <p> 
	 * Set using {@link #setScreenWidthAndHeight(int, int)}. This value is automatically fitted
	 * to the Scene's window dimensions when the Camera is attached to a Scene.
	 */
	public final int screenWidth() {
		return scrnWidth;
	}

	/**
	 * Returns the height (in pixels) of the Camera screen. 
	 * <p> 
	 * Set using {@link #setScreenWidthAndHeight(int, int)}. This value is automatically fitted
	 * to the Scene's window dimensions when the Camera is attached to a Scene.
	 */
	public final int screenHeight() {
		return scrnHeight;
	}

	/**
	 * Convenience function that simply calls {@code return}
	 * {@link #getViewport(int[])}.
	 */
	public int[] getViewport() {
		return getViewport(new int[4]);
	}

	/**
	 * Fills {@code viewport} with the Camera viewport and returns it.
	 * If viewport is null (or not the correct size), a new array will be
	 * created. 
	 * <p>
	 * This method is mainly used in conjunction with
	 * {@link #project(float, float, float, PMatrix3D, PMatrix3D, int[], float[])} , which
	 * requires such a viewport. Returned values are (0, {@link #screenHeight()},
	 * {@link #screenWidth()}, -{@link #screenHeight()}), so that the origin is located in the
	 * upper left corner of the window.
	 */
	public int[] getViewport(int[] viewport) {
		if ((viewport == null) || (viewport.length != 4)) {
			viewport = new int[4];
		}
		viewport[0] = 0;
		viewport[1] = screenHeight();
		viewport[2] = screenWidth();
		viewport[3] = -screenHeight();
		return viewport;
	}

	/**
	 * Returns the near clipping plane distance used by the Camera projection
	 * matrix. 
	 * <p> 
	 * The clipping planes' positions depend on the {@link #sceneRadius()} and
	 * {@link #sceneCenter()} rather than being fixed small-enough and
	 * large-enough values. A good scene dimension approximation will hence
	 * result in an optimal precision of the z-buffer. 
	 * <p> 
	 * The near clipping plane is positioned at a distance equal to
	 * {@link #zClippingCoefficient()} * {@link #sceneRadius()} in front of the
	 * {@link #sceneCenter()}: {@code distanceToSceneCenter() -
	 * zClippingCoefficient() * sceneRadius()} 
	 * <p> 
	 * In order to prevent negative or too small {@link #zNear()} values (which would
	 * degrade the z precision), {@link #zNearCoefficient()} is used when the
	 * Camera is inside the {@link #sceneRadius()} sphere:
	 * <p>
	 * {@code zMin = zNearCoefficient() * zClippingCoefficient() *
	 * sceneRadius();}<br>
	 * {@code zNear = zMin;}<br>
	 * {@code // With an ORTHOGRAPHIC type, the value is simply clamped to 0.0}
	 * <br> 
	 * <p> 
	 * See also the {@link #zFar()}, {@link #zClippingCoefficient()} and
	 * {@link #zNearCoefficient()} documentations. 
	 * <p> 
	 * If you need a completely different zNear computation, overload the
	 * {@link #zNear()} and {@link #zFar()} methods in a new class that publicly inherits from
	 * Camera and use {@link remixlab.proscene.Scene#setCamera(Camera)}. 
	 * <p> 
	 * <b>Attention:</b> The value is always positive although the clipping
	 * plane is positioned at a negative z value in the Camera coordinate
	 * system. This follows the {@code gluPerspective} standard.
	 */
	public float zNear() {
		float z = distanceToSceneCenter() - zClippingCoefficient()
				* sceneRadius();

		// Prevents negative or null zNear values.
		final float zMin = zNearCoefficient() * zClippingCoefficient()
				* sceneRadius();
		if (z < zMin)
			switch (type()) {
			case PERSPECTIVE:
				z = zMin;
				break;
			case ORTHOGRAPHIC:
				z = 0.0f;
				break;
			}
		return z;
	}

	/**
	 * Returns the far clipping plane distance used by the Camera projection
	 * matrix. 
	 * <p> 
	 * The far clipping plane is positioned at a distance equal to {@code
	 * zClippingCoefficient() * sceneRadius()} behind the {@link #sceneCenter()}:
	 * <p>
	 * {@code zFar = distanceToSceneCenter() +
	 * zClippingCoefficient()*sceneRadius();}<br>
	 * 
	 * @see #zNear()
	 */
	public float zFar() {
		return distanceToSceneCenter() + zClippingCoefficient() * sceneRadius();
	}

	/**
	 * Returns the coefficient which is used to set {@link #zNear()} when the
	 * Camera is inside the sphere defined by {@link #sceneCenter()} and
	 * {@link #zClippingCoefficient()} * {@link #sceneRadius()}. 
	 * <p> 
	 * In that case, the {@link #zNear()} value is set to {@code
	 * zNearCoefficient() * zClippingCoefficient() * sceneRadius()}. See the
	 * {@code zNear()} documentation for details. 
	 * <p> 
	 * Default value is 0.005, which is appropriate for most applications. In
	 * case you need a high dynamic ZBuffer precision, you can increase this
	 * value (~0.1). A lower value will prevent clipping of very close objects
	 * at the expense of a worst Z precision. 
	 * <p> 
	 * Only meaningful when Camera type is PERSPECTIVE.
	 */
	public float zNearCoefficient() {
		return zNearCoef;
	}

	/**
	 * Sets the {@link #zNearCoefficient()} value.
	 */
	public void setZNearCoefficient(float coef) {
		zNearCoef = coef;
	}

	/**
	 * Returns the coefficient used to position the near and far clipping
	 * planes. 
	 * <p> 
	 * The near (resp. far) clipping plane is positioned at a distance equal to
	 * {@code zClippingCoefficient() * sceneRadius()} in front of (resp. behind)
	 * the {@link #sceneCenter()}. This guarantees an optimal use of the
	 * z-buffer range and minimizes aliasing. See the {@link #zNear()} and
	 * {@link #zFar()} documentations. 
	 * <p> 
	 * Default value is square root of 3.0 (so that a cube of size
	 * {@link #sceneRadius()} is not clipped). 
	 * <p> 
	 * However, since the {@link #sceneRadius()} is used for other purposes (see
	 * showEntireScene(), flySpeed(), ...) and you may want to change this value
	 * to define more precisely the location of the clipping planes. See also
	 * {@link #zNearCoefficient()}.
	 */
	public float zClippingCoefficient() {
		return zClippingCoef;
	}

	/**
	 * Sets the {@link #zClippingCoefficient()} value.
	 */
	public void setZClippingCoefficient(float coef) {
		zClippingCoef = coef;
	}
	
	/**
	 * Returns the ratio between pixel and processing scene units at {@code position}. 
	 * <p> 
	 * A line of {@code n * pixelPRatio()} processing scene units, located at {@code position} in
	 * the world coordinates system, will be projected with a length of {@code n} pixels on screen. 
	 * <p> 
	 * Use this method to scale objects so that they have a constant pixel size on screen.
	 * The following code will draw a 20 pixel line, starting at {@link #sceneCenter()} and
	 * always directed along the screen vertical direction:
	 * <p>
	 * {@code beginShape(LINES);}<br>
	 * {@code vertex(sceneCenter().x, sceneCenter().y, sceneCenter().z);}<br>
	 * {@code PVector v = PVector.add(sceneCenter(), PVector.mult(upVector(), 20 * pixelPRatio(sceneCenter())));}<br>
	 * {@code vertex(v.x, v.y, v.z);}<br>
	 * {@code endShape();}<br>
	 */
	public float pixelPRatio(PVector position) {
		switch (type()) {
	    case PERSPECTIVE :
	    	return 2.0f * PApplet.abs((frame().coordinatesOf(position)).z) * PApplet.tan(fieldOfView()/2.0f) / screenHeight();
	    case ORTHOGRAPHIC : {
	    	float [] wh = getOrthoWidthHeight();
	    	return 2.0f * wh[1] / screenHeight();
	    	}
	    }		
		return 1.0f;	
	}

	// 3. SCENE RADIUS AND CENTER

	/**
	 * Returns the radius of the scene observed by the Camera. 
	 * <p> 
	 * You need to provide such an approximation of the scene dimensions so that
	 * the Camera can adapt its {@link #zNear()} and {@link #zFar()} values. See the
	 * {@link #sceneCenter()} documentation. 
	 * <p> 
	 * Note that Scene.sceneRadius() (resp. Scene.setSceneRadius()) simply call this
	 * method on its associated Camera.
	 * 
	 * @see #setSceneBoundingBox(PVector, PVector)
	 */
	public float sceneRadius() {
		return scnRadius;
	}

	/**
	 * Sets the {@link #sceneRadius()} value. Negative values are ignored. 
	 * <p> 
	 * <b>Attention:</b> This methods also sets {@link #focusDistance()} to
	 * {@code sceneRadius() / tan(fieldOfView()/2)} and {@link #flySpeed()} to
	 * 1% of {@link #sceneRadius()}.
	 */
	public void setSceneRadius(float radius) {
		if (radius <= 0.0f) {
			PApplet.println("Warning: Scene radius must be positive - Ignoring value");
			return;
		}

		scnRadius = radius;

		setFocusDistance(sceneRadius() / PApplet.tan(fieldOfView() / 2.0f));

		setFlySpeed(0.01f * sceneRadius());
	}

	/**
	 * 
	 * Returns the position of the scene center, defined in the world coordinate
	 * system. 
	 * <p> 
	 * The scene observed by the Camera should be roughly centered on this
	 * position, and included in a {@link #sceneRadius()} sphere. This approximate
	 * description of the scene permits a {@link #zNear()} and {@link #zFar()}
	 * clipping planes definition, and allows convenient positioning methods such as
	 * {@link #showEntireScene()}. 
	 * <p> 
	 * Note that {@link remixlab.proscene.Scene#center()} (resp.
	 * remixlab.proscene.Scene{@link #setSceneCenter(PVector)})
	 * simply call this method (resp. {@link #setSceneCenter(PVector)}) on its associated
	 * {@link remixlab.proscene.Scene#camera()}. Default value is (0,0,0) (world origin). Use
	 * {@link #setSceneCenter(PVector)} to change it.
	 * 
	 * @see #setSceneBoundingBox(PVector, PVector)
	 */
	public PVector sceneCenter() {
		return scnCenter;
	}

	/**
	 * Sets the {@link #sceneCenter()}. 
	 * <p> 
	 * <b>Attention:</b> This method also sets the {@link #revolveAroundPoint()}
	 * to {@link #sceneCenter()}.
	 */
	public void setSceneCenter(PVector center) {
		scnCenter = center;
		setRevolveAroundPoint(sceneCenter());
	}

	/**
	 * Returns the distance from the Camera center to {@link #sceneCenter()}, projected
	 * along the Camera Z axis. 
	 * <p> 
	 * Used by {@link #zNear()} and {@link #zFar()} to optimize the Z range.
	 */
	public float distanceToSceneCenter() {
		return PApplet.abs((frame().coordinatesOf(sceneCenter())).z);
	}

	/**
	 * Similar to {@link #setSceneRadius(float)} and
	 * {@link #setSceneCenter(PVector)}, but the scene limits are defined by a
	 * (world axis aligned) bounding box.
	 */
	public void setSceneBoundingBox(PVector min, PVector max) {
		setSceneCenter(PVector.mult(PVector.add(min, max), 1 / 2.0f));
		setSceneRadius(0.5f * (PVector.sub(max, min)).mag());
	}

	// 4. REVOLVE AROUND POINT

	/**
	 * The point the Camera revolves around its
	 * {@link remixlab.proscene.InteractiveCameraFrame#revolveAroundPoint()}. 
	 * <p> 
	 * Default value is the {@link #sceneCenter()}. 
	 * <p> 
	 * <b>Attention:</b> {@link #setSceneCenter(PVector)} changes this value.
	 */
	public final PVector revolveAroundPoint() {
		return frame().revolveAroundPoint();
	}

	/**
	 * Changes the {@link #revolveAroundPoint()} to {@code rap} (defined in the
	 * world coordinate system).
	 */
	public void setRevolveAroundPoint(PVector rap) {
		float prevDist = PApplet
				.abs(cameraCoordinatesOf(revolveAroundPoint()).z);

		frame().setRevolveAroundPoint(rap);

		// orthoCoef is used to compensate for changes of the
		// revolveAroundPoint, so that the image does
		// not change when the revolveAroundPoint is changed in ORTHOGRAPHIC
		// mode.
		float newDist = PApplet
				.abs(cameraCoordinatesOf(revolveAroundPoint()).z);
		// Prevents division by zero when rap is set to camera position
		if ((prevDist > 1E-9) && (newDist > 1E-9))
			orthoCoef *= prevDist / newDist;
	}
	
	/**
	 * The {@link #revolveAroundPoint()} is set to the point located under {@code pixel}
	 * on screen. Returns {@code true} if a point was found under {@code pixel} and
	 * {@code false} if none was found (in this case no {@link #revolveAroundPoint()}
	 * is set).
	 * <p>
	 * Override {@link #pointUnderPixel(Point)} in your jogl-based camera class.
	 * <p>
	 * Current implementation always returns {@code false}, meaning that no point was
	 * set. 
	 */	
	public boolean setRevolveAroundPointFromPixel(Point pixel) {
		WorldPoint wP = pointUnderPixel(pixel);
		if ( wP.found )
			setRevolveAroundPoint( wP.point);		
		return wP.found;
	}
	
	/**
	 * The {@link #setSceneCenter(PVector)} is set to the point located under {@code pixel}
	 * on screen. Returns {@code true} if a point was found under {@code pixel} and
	 * {@code false} if none was found (in this case no {@link #sceneCenter()} is set).
	 * <p>
	 * Override {@link #pointUnderPixel(Point)} in your jogl-based camera class.
	 * <p>
	 * Current implementation always returns {@code false}, meaning that no point was
	 * set.
	 */
	public boolean setSceneCenterFromPixel(Point pixel) {
		WorldPoint wP = pointUnderPixel(pixel);
		if ( wP.found )
			setSceneCenter( wP.point );
		return wP.found;
	}
	
	/**
	 * Returns the coordinates of the 3D point located at {@code pixel} (x,y) on screen.
	 * <p>
	 * Override this method in your jogl-based camera class.
	 * <p>
	 * Current implementation always returns {@code WorlPoint.found = false} (dummy value),
	 * meaning that no point was found under pixel. 
	 */
	protected WorldPoint pointUnderPixel(Point pixel) {
		return new WorldPoint(new PVector(0,0,0), false);
	}

	// 5. ASSOCIATED FRAME

	/**
	 * Returns the InteractiveCameraFrame attached to the Camera. 
	 * <p> 
	 * This InteractiveCameraFrame defines its {@link #position()} and
	 * {@link #orientation()} and can translate mouse events into Camera
	 * displacement. Set using {@link #setFrame(InteractiveCameraFrame)}.
	 */
	public InteractiveCameraFrame frame() {
		return frm;
	}

	/**
	 * Sets the Camera {@link #frame()}. 
	 * <p> 
	 * If you want to move the Camera, use {@link #setPosition(PVector)} and
	 * {@link #setOrientation(Quaternion)} or one of the Camera positioning
	 * methods ({@link #lookAt(PVector)}, {@link #fitSphere(PVector, float)},
	 * {@link #showEntireScene()}...) instead.
	 * <p> 
	 * This method is actually mainly useful if you derive the
	 * InteractiveCameraFrame class and want to use an instance of your new
	 * class to move the Camera. 
	 * <p> 
	 * A {@code null} {@code icf} reference will silently be ignored.
	 */
	public final void setFrame(InteractiveCameraFrame icf) {
		if (icf == null)
			return;

		frm = icf;
	}

	// 6. OPENGL MATRICES
	
	/**
	 * Convenience function that simply returns {@code getProjectionMatrix(new PMatrix3D())}
	 * 
	 * @see #getProjectionMatrix(PMatrix3D)
	 */
	public PMatrix3D getProjectionMatrix() {
		return getProjectionMatrix(new PMatrix3D());
	}
	
	/**
	 * Fills {@code m} with the Camera projection matrix values and returns it. If
	 * {@code m} is {@code null} a new PMatrix3D will be created. 
	 * <p> 
	 * Calls {@link #computeProjectionMatrix()} to define the Camera projection matrix.
	 * <p>
	 * 
	 * @see #getModelViewMatrix()
	 */
	public PMatrix3D getProjectionMatrix(PMatrix3D m) {
		if (m == null) m = new PMatrix3D();
		
		// May not be needed, but easier and more robust like this.
		computeProjectionMatrix();
		m.set(projectionMat);
		
		return m;
	}	

	/**
	 * Computes the projection matrix associated with the Camera. 
	 * <p> 
	 * If {@link #type()} is PERSPECTIVE, defines a GL_PROJECTION matrix similar to what
	 * would {@code perspective()} do using the {@link #fieldOfView()}, window
	 * {@link #aspectRatio()}, {@link #zNear()} and {@link #zFar()} parameters. 
	 * <p> 
	 * If {@link #type()} is ORTHOGRAPHIC, the projection matrix is as what {@code
	 * ortho()} would do. Frustum's width and height are set using
	 * {@link #getOrthoWidthHeight()}. 
	 * <p> 
	 * Both types use {@link #zNear()} and {@link #zFar()} to place clipping planes. These values
	 * are determined from sceneRadius() and sceneCenter() so that they best fit
	 * the scene size. 
	 * <p> 
	 * Use {@link #getProjectionMatrix()} to retrieve this matrix.
	 * <p> 
	 * <b>Note:</b> You must call this method if your Camera is not associated
	 * with a Scene and is used for offscreen computations
	 * (using {@code projectedCoordinatesOf()} for instance).
	 * 
	 * @see #setProjectionMatrix(PMatrix3D)
	 */
	public void computeProjectionMatrix() {
		float ZNear = zNear();
		float ZFar = zFar();

		switch (type()) {
		case PERSPECTIVE: {
			// #CONNECTION# all non null coefficients were set to 0.0 in
			// constructor.
			float f = 1.0f / PApplet.tan(fieldOfView() / 2.0f);
			projectionMat.m00 = f / aspectRatio();
			projectionMat.m11 = f;
			projectionMat.m22 = (ZNear + ZFar) / (ZNear - ZFar);
			projectionMat.m32 = -1.0f;
			projectionMat.m23 = 2.0f * ZNear * ZFar / (ZNear - ZFar);
			projectionMat.m33 = 0.0f;
			break;
		}
		case ORTHOGRAPHIC: {
			float[] wh = getOrthoWidthHeight();
			projectionMat.m00 = 1.0f / wh[0];
			projectionMat.m11 = 1.0f / wh[1];
			projectionMat.m22 = -2.0f / (ZFar - ZNear);
			projectionMat.m32 = 0.0f;
			projectionMat.m23 = -(ZFar + ZNear) / (ZFar - ZNear);
			projectionMat.m33 = 1.0f;
			break;
		}
		}
	}
	
	/**
	 * Sets the projection matrix associated with the Camera directly from a PCamera.
	 * 
	 * @see #computeProjectionMatrix()
	 */
	public void setProjectionMatrix(PMatrix3D proj) {
		projectionMat.set(proj);
	}

	/**
	 * Convenience function that simply returns
	 * {@code getModelViewMatrix(new PMatrix3D())}
	 */
	public PMatrix3D getModelViewMatrix() {
		return getModelViewMatrix(new PMatrix3D());	
	}
	
	/**
	 * Fills {@code m} with the Camera modelView matrix values and returns it. If
	 * {@code m} is {@code null} a new PMatrix3D will be created.
	 * <p> 
	 * First calls {@link #computeModelViewMatrix()} to define the Camera modelView matrix.
	 *  
	 * @see #getProjectionMatrix(PMatrix3D)
	 */
	public PMatrix3D getModelViewMatrix(PMatrix3D m) {
		if (m == null) m = new PMatrix3D();
		// May not be needed, but easier like this.
		// Prevents from retrieving matrix in stereo mode -> overwrites shifted value.
		computeModelViewMatrix();
		m.set(modelViewMat);
		return m;
	}

	/**
	 * Computes the modelView matrix associated with the Camera's {@link #position()}
	 * and {@link #orientation()}.
	 * <p> 
	 * This matrix converts from the world coordinates system to the Camera
	 * coordinates system, so that coordinates can then be projected on screen
	 * using the projection matrix (see {@link #computeProjectionMatrix()}).
	 * <p>
	 * Use {@link #getModelViewMatrix()} to retrieve this matrix.
	 * <p> 
	 * <b>Note:</b> You must call this method if your Camera is not associated with a
	 * Scene and is used for offscreen computations
	 * (using {@code projectedCoordinatesOf()} for instance).
	 */
	public void computeModelViewMatrix() {
		Quaternion q = frame().orientation();
		
		float q00 = 2.0f * q.x * q.x;
		float q11 = 2.0f * q.y * q.y;
		float q22 = 2.0f * q.z * q.z;
		
		float q01 = 2.0f * q.x * q.y;
		float q02 = 2.0f * q.x * q.z;
		float q03 = 2.0f * q.x * q.w;
		
		float q12 = 2.0f * q.y * q.z;
		float q13 = 2.0f * q.y * q.w;
		float q23 = 2.0f * q.z * q.w;
		
		modelViewMat.m00 = 1.0f  - q11 - q22;
		modelViewMat.m10 =         q01 - q23;
		modelViewMat.m20 =         q02 + q13;
		modelViewMat.m30 = 0.0f;
		
		modelViewMat.m01 =         q01 + q23;
		modelViewMat.m11 = 1.0f  - q22 - q00;
		modelViewMat.m21 =         q12 - q03;
		modelViewMat.m31 = 0.0f;
		
		modelViewMat.m02 =         q02 - q13;
		modelViewMat.m12 =         q12 + q03;
		modelViewMat.m22 = 1.0f - q11 - q00;
		modelViewMat.m32 = 0.0f;
		
		PVector t = q.inverseRotate(frame().position());
		
		modelViewMat.m03 = -t.x;
		modelViewMat.m13 = -t.y;
		modelViewMat.m23 = -t.z;
		modelViewMat.m33 = 1.0f;
	}
	
	/**
	 * Sets the modelview matrix associated with the Camera directly from a PCamera.
	 * 
	 * @see #computeModelViewMatrix()
	 */
	public void setModelViewMatrix(PMatrix3D modelview) {
		modelViewMat.set(modelview);
	}
	
	// 7. WORLD -> CAMERA

	/**
	 * Returns the Camera frame coordinates of a point {@code src} defined in
	 * world coordinates. 
	 * <p> 
	 * {@link #worldCoordinatesOf(PVector)} performs the inverse transformation. 
	 * <p> 
	 * Note that the point coordinates are simply converted in a different
	 * coordinate system. They are not projected on screen. Use
	 * {@link #projectedCoordinatesOf(PVector, Frame)} for that.
	 */
	public final PVector cameraCoordinatesOf(PVector src) {
		return frame().coordinatesOf(src);
	}

	/**
	 * Returns the world coordinates of the point whose position {@code src} is
	 * defined in the Camera coordinate system. 
	 * <p> 
	 * {@link #cameraCoordinatesOf(PVector)} performs the inverse
	 * transformation.
	 */
	public PVector worldCoordinatesOf(final PVector src) {
		return frame().inverseCoordinatesOf(src);
	}

	// 8. 2D -> 3D

	/**
	 * Gives the coefficients of a 3D half-line passing through the Camera eye
	 * and pixel (x,y). 
	 * <p> 
	 * The origin of the half line (eye position) is stored in {@code orig},
	 * while {@code dir} contains the properly oriented and normalized direction
	 * of the half line. 
	 * <p> 
	 * {@code x} and {@code y} are expressed in Processing format (origin in the
	 * upper left corner). Use {@link #screenHeight()} - y to convert to processing scene units. 
	 * <p> 
	 * This method is useful for analytical intersection in a selection method.
	 */
	public void convertClickToLine(final Point pixelInput, PVector orig, PVector dir) {
		Point pixel = new Point(pixelInput);
		if ( frame().coordinateSystemConvention() ==  CoordinateSystemConvention.LEFT_HANDED)
			pixel.y = screenHeight() - pixelInput.y;
		switch (type()) {
		case PERSPECTIVE:
			orig.set(position());
			dir.set(new PVector(( (2.0f * pixel.x / screenWidth()) - 1.0f)
								* PApplet.tan(fieldOfView() / 2.0f)
								* aspectRatio(),
							    ((2.0f * (screenHeight() - pixel.y) / screenHeight()) - 1.0f)
							    * PApplet.tan(fieldOfView() / 2.0f),
								  -1.0f));
			dir.set(PVector.sub(worldCoordinatesOf(dir), orig));
			dir.normalize();
			break;

		case ORTHOGRAPHIC: {
			float[] wh = getOrthoWidthHeight();
			orig.set(new PVector((2.0f * pixel.x / screenWidth() - 1.0f)
					* wh[0], -(2.0f * pixel.y / screenHeight() - 1.0f) * wh[1],
					0.0f));
			orig.set(worldCoordinatesOf(orig));
			dir.set(viewDirection());
			break;
		}
		}
	}

	/**
	 * Convenience function that simply returns
	 * {@code projectedCoordinatesOf(src, null)}
	 * 
	 * @see #projectedCoordinatesOf(PVector, Frame)
	 */
	public final PVector projectedCoordinatesOf(PVector src) {
		return projectedCoordinatesOf(src, null);
	}

	/**
	 * Returns the screen projected coordinates of a point {@code src} defined
	 * in the {@code frame} coordinate system. 
	 * <p> 
	 * When {@code frame} is {@code null}, {@code src} is expressed in the world
	 * coordinate system. See {@link #projectedCoordinatesOf(PVector)}. 
	 * <p> 
	 * The x and y coordinates of the returned PVector are expressed in pixel,
	 * (0,0) being the upper left corner of the window. The z coordinate ranges
	 * between 0.0 (near plane) and 1.0 (excluded, far plane). See the {@code
	 * gluProject} man page for details.
	 * <p>
	 * <b>Attention:</b> This method only uses the intrinsic Camera parameters
	 * (see {@link #getModelViewMatrix()}, {@link #getProjectionMatrix()} and
	 * {@link #getViewport()}) and is completely independent of the processing
	 * matrices. You can hence define a virtual Camera and use this method to
	 * compute projections out of a classical rendering context.
	 * 
	 * @see #unprojectedCoordinatesOf(PVector, Frame)
	 */
	public final PVector projectedCoordinatesOf(PVector src, Frame frame) {		
		float xyz[] = new float[3];		
		viewport = getViewport();
		
		if (frame != null) {
			PVector tmp = frame.inverseCoordinatesOf(src);
			project(tmp.x, tmp.y, tmp.z, modelViewMat, projectionMat, viewport, xyz);
		} else
			project(src.x, src.y, src.z, modelViewMat, projectionMat, viewport, xyz);
		
		if ( frame().coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
			xyz[1] = screenHeight() - xyz[1];

		return new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]);
	}
	
	/**
	 * Convenience function that simply returns {@code return
	 * unprojectedCoordinatesOf(src, null)}
	 * 
	 * #see {@link #unprojectedCoordinatesOf(PVector, Frame)}
	 */
	public final PVector unprojectedCoordinatesOf(PVector src) {
		return this.unprojectedCoordinatesOf(src, null);
	}
	
	/**
	 * Returns the world unprojected coordinates of a point {@code src} defined
	 * in the screen coordinate system. 
	 * <p> 
	 * The {@code src.x} and {@code src.y} input values are expressed in pixels,
	 * (0,0) being the upper left corner of the window. {@code src.z} is a depth
	 * value ranging in [0..1] (near and far plane respectively). See the
	 * {@code gluUnProject} man page for details. 
	 * <p> 
	 * The result is expressed in the {@code frame} coordinate system. When
	 * {@code frame} is {@code null}, the result is expressed in the world
	 * coordinates system. The possible {@code frame}
	 * {@link remixlab.proscene.Frame#referenceFrame()} are taken into account. 
	 * <p> 
	 * {@link #projectedCoordinatesOf(PVector, Frame)} performs the inverse
	 * transformation. 
	 * <p> 
	 * This method only uses the intrinsic Camera parameters (see
	 * {@link #getModelViewMatrix()}, {@link #getProjectionMatrix()} and
	 * {@link #getViewport()}) and is completely independent of the Processing
	 * matrices. You can hence define a virtual Camera and use this method to
	 * compute un-projections out of a classical rendering context. 
	 * <p> 
	 * <b>Attention:</b> However, if your Camera is not attached to a
	 * Scene (used for offscreen computations for instance), make sure the
	 * Camera matrices are updated before calling this method (use
	 * {@link #computeModelViewMatrix()}, {@link #computeProjectionMatrix()}).
	 * <p> 
	 * This method is not computationally optimized. If you call it several
	 * times with no change in the matrices, you should buffer the entire
	 * inverse projection matrix (modelview, projection and then viewport) to
	 * speed-up the queries. See the gluUnProject man page for details.
	 * 
	 * @see #projectedCoordinatesOf(PVector, Frame)
	 * @see #setScreenWidthAndHeight(int, int)
	 */
	public final PVector unprojectedCoordinatesOf(PVector src, Frame frame) {
		float xyz[] = new float[3];
		viewport = getViewport();
		if ( frame().coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
			unproject(src.x, (screenHeight() - src.y), src.z, modelViewMat, projectionMat, viewport, xyz);
		else
			unproject(src.x, src.y, src.z, modelViewMat, projectionMat, viewport, xyz);		
		if (frame != null)
			return frame.coordinatesOf(new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]));
		else
			return new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]);
	}

	// 9. FLYSPEED

	/**
	 * Returns the fly speed of the Camera. 
	 * <p> 
	 * Simply returns {@code frame().flySpeed()}. See the
	 * {@link remixlab.proscene.InteractiveCameraFrame#flySpeed()}
	 * documentation. This value is only meaningful when the MouseAction
	 * bindings is Scene.MOVE_FORWARD or is Scene.MOVE_BACKWARD.
	 * <p>
	 * Set to 0.5% of the {@link #sceneRadius()} by
	 * {@link #setSceneRadius(float)}.
	 * 
	 * @see #setFlySpeed(float)
	 */
	public float flySpeed() {
		return frame().flySpeed();
	}

	/**
	 * Sets the Camera {@link #flySpeed()}.
	 * <p>
	 * <b>Attention:</b> This value is modified by
	 * {@link #setSceneRadius(float)}.
	 */
	public void setFlySpeed(float speed) {
		frame().setFlySpeed(speed);
	}

	// 10. POSITION TOOLS

	/**
	 * Sets the Camera {@link #orientation()}, so that it looks at point
	 * {@code target} (defined in the world coordinate system). 
	 * <p> 
	 * The Camera {@link #position()} is not modified. Simply
	 * {@link #setViewDirection(PVector)}.
	 * 
	 * @see #at()
	 * @see #setUpVector(PVector)
	 * @see #setOrientation(Quaternion)
	 * @see #showEntireScene()
	 * @see #fitSphere(PVector, float)
	 * @see #fitBoundingBox(PVector, PVector)
	 */
	public void lookAt(PVector target) {
		setViewDirection(PVector.sub(target, position()));
	}
	
	/**
	 * Returns a point defined in the world coordinate system where the camera is
	 * pointing at (just in front of {@link #viewDirection()}). Useful for setting the
	 * Processing camera() which uses a similar approach of that found in gluLookAt. 
	 * 
	 * @see #lookAt(PVector)
	 */
	public PVector at() {
		return PVector.add(position(), viewDirection());
	}

	/**
	 * Moves the Camera so that the sphere defined by {@code center} and {@code
	 * radius} is visible and fits the window. 
	 * <p> 
	 * The Camera is simply translated along its {@link #viewDirection()} so
	 * that the sphere fits the screen. Its {@link #orientation()} and its
	 * {@link #fieldOfView()} are unchanged. 
	 * <p> 
	 * You should therefore orientate the Camera before you call this method.
	 * 
	 * @see #lookAt(PVector)
	 * @see #setOrientation(Quaternion)
	 * @see #setUpVector(PVector, boolean)
	 */
	public void fitSphere(PVector center, float radius) {
		float distance = 0.0f;
		switch (type()) {
		case PERSPECTIVE: {
			float yview = radius / PApplet.sin(fieldOfView() / 2.0f);
			float xview = radius / PApplet.sin(horizontalFieldOfView() / 2.0f);
			distance = PApplet.max(xview, yview);
			break;
		}
		case ORTHOGRAPHIC: {
			distance = PVector.dot(PVector.sub(center, revolveAroundPoint()),
									viewDirection())
                       + (radius / orthoCoef);
			break;
		}
		}

		PVector newPos = PVector.sub(center, PVector.mult(viewDirection(),
				distance));
		frame().setPositionWithConstraint(newPos);
	}

	/**
	 * Moves the Camera so that the (world axis aligned) bounding box ({@code
	 * min}, {@code max}) is entirely visible, using
	 * {@link #fitSphere(PVector, float)}.
	 */
	public void fitBoundingBox(PVector min, PVector max) {
		float diameter = PApplet.max(PApplet.abs(max.y - min.y), PApplet
				.abs(max.x - min.x));
		diameter = PApplet.max(PApplet.abs(max.z - min.z), diameter);
		fitSphere(PVector.mult(PVector.add(min, max), 0.5f), 0.5f * diameter);
	}

	/**
	 * Moves the Camera so that the rectangular screen region defined by {@code
	 * rectangle} (pixel units, with origin in the upper left corner) fits the
	 * screen. 
	 * <p> 
	 * The Camera is translated (its orientation() is unchanged) so that
	 * {@code rectangle} is entirely visible. Since the pixel coordinates only
	 * define a <i>frustum</i> in 3D, it's the intersection of this frustum with
	 * a plane (orthogonal to the viewDirection() and passing through the
	 * sceneCenter()) that is used to define the 3D rectangle that is eventually
	 * fitted.
	 */
	public void fitScreenRegion(Rectangle rectangle) {
		PVector vd = viewDirection();
		float distToPlane = distanceToSceneCenter();
		
		Point center = new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY());
		
		PVector orig = new PVector();
		PVector dir = new PVector();
		convertClickToLine( center, orig, dir );		
		PVector newCenter = PVector.add(orig, PVector.mult(dir, (distToPlane / PVector.dot(dir, vd))));
		
		convertClickToLine(new Point(rectangle.x, center.y), orig, dir);
		final PVector pointX  = PVector.add(orig, PVector.mult(dir, (distToPlane / PVector.dot(dir, vd))));
		
		convertClickToLine(new Point(center.x, rectangle.y), orig, dir);
		final PVector pointY  = PVector.add(orig, PVector.mult(dir, (distToPlane / PVector.dot(dir, vd))));
		
		float distance = 0.0f;
		switch (type()) {
		case PERSPECTIVE: {
			final float distX = PVector.dist(pointX, newCenter) / PApplet.sin(horizontalFieldOfView()/2.0f);
            final float distY = PVector.dist(pointY, newCenter) / PApplet.sin(fieldOfView()/2.0f);
            
			distance = PApplet.max(distX, distY);
			break;
			}
		case ORTHOGRAPHIC: {			
			final float dist = PVector.dot(PVector.sub(newCenter, revolveAroundPoint()), vd);			
			final float distX = PVector.dist(pointX, newCenter) / orthoCoef / ((aspectRatio() < 1.0) ? 1.0f : aspectRatio());
            final float distY = PVector.dist(pointY, newCenter) / orthoCoef / ((aspectRatio() < 1.0) ? 1.0f / aspectRatio() : 1.0f);
            
			distance = dist + PApplet.max(distX, distY);
			
			break;
			}
		}
		
		frame().setPositionWithConstraint(PVector.sub(newCenter, PVector.mult(vd, distance)));
	}

	/**
	 * Moves the Camera so that the entire scene is visible. 
	 * <p> 
	 * Simply calls {@link #fitSphere(PVector, float)} on a sphere defined by
	 * {@link #sceneCenter()} and {@link #sceneRadius()}. 
	 * <p> 
	 * You will typically use this method in {@link remixlab.proscene.Scene#init()} after you defined
	 * a new {@link #sceneRadius()}.
	 */
	public void showEntireScene() {
		fitSphere(sceneCenter(), sceneRadius());
	}

	/**
	 * Moves the Camera so that its {@link #sceneCenter()} is projected on the
	 * center of the window. The {@link #orientation()} and
	 * {@link #fieldOfView()} are unchanged. 
	 * <p> 
	 * Simply projects the current position on a line passing through
	 * {@link #sceneCenter()}.
	 * 
	 * @see #showEntireScene()
	 */
	public void centerScene() {
		frame().projectOnLine(sceneCenter(), viewDirection());
	}

	// 11. STEREO PARAMETERS

	/**
	 * Returns the user's inter-ocular distance (in meters). Default value is
	 * 0.062m, which fits most people. 
	 * 
	 * @see #setIODistance(float)
	 */
	public float IODistance() {
		return IODist;
	}

	/**
	 * Sets the {@link #IODistance()}.
	 */
	public void setIODistance(float distance) {
		IODist = distance;
	}

	/**
	 * Returns the physical distance between the user's eyes and the screen (in
	 * meters). 
	 * <p> 
	 * Default value is 0.5m. 
	 * <p>
	 * Value is set using {@link #setPhysicalDistanceToScreen(float)}. 
	 * <p> 
	 * physicalDistanceToScreen() and {@link #focusDistance()} represent the same
	 * distance. The first one is expressed in physical real world units, while
	 * the latter is expressed in processing virtual world units. Use their ratio to
	 * convert distances between these worlds.
	 */
	public float physicalDistanceToScreen() {
		return physicalDist2Scrn;
	}

	/**
	 * Sets the {@link #physicalDistanceToScreen()}.
	 */
	public void setPhysicalDistanceToScreen(float distance) {
		physicalDist2Scrn = distance;
	}

	/**
	 * Returns the physical screen width, in meters. Default value is 0.4m
	 * (average monitor). 
	 * <p> 
	 * Used for stereo display only. Set using
	 * {@link #setPhysicalScreenWidth(float)}.
	 * <p>
	 * See {@link #physicalDistanceToScreen()} for reality center automatic
	 * configuration.
	 */
	public float physicalScreenWidth() {
		return physicalScrnWidth;
	}

	/**
	 * Sets the physical screen (monitor or projected wall) width (in meters).
	 */
	public void setPhysicalScreenWidth(float width) {
		physicalScrnWidth = width;
	}

	/**
	 * Returns the focus distance used by stereo display, expressed in processing
	 * units. 
	 * <p> 
	 * This is the distance in the virtual world between the Camera and the
	 * plane where the horizontal stereo parallax is null (the stereo left and
	 * right images are superimposed). 
	 * <p> 
	 * This distance is the virtual world equivalent of the real-world
	 * {@link #physicalDistanceToScreen()}. 
	 * <p> 
	 * <b>attention:</b> This value is modified by Scene.setSceneRadius(),
	 * setSceneRadius() and {@link #setFieldOfView(float)}. When one of these
	 * values is modified, {@link #focusDistance()} is set to
	 * {@link #sceneRadius()} / tan({@link #fieldOfView()}/2), which provides
	 * good results.
	 */
	public float focusDistance() {
		return focusDist;
	}

	/**
	 * Sets the focusDistance(), in processing scene units.
	 */
	public void setFocusDistance(float distance) {
		focusDist = distance;
	}
	
	// 14. Implementation of glu utility functions
	
	/**
	 * Similar to {@code gluProject}: map object coordinates to window coordinates.
	 * 
	 * @param objx
	 *            Specify the object x coordinate.
	 * @param objy
	 *            Specify the object y coordinate.
	 * @param objz
	 *            Specify the object z coordinate.
	 * @param modelview
	 *            Specifies the current modelview matrix.
	 * @param projection
	 *            Specifies the current projection matrix.
	 * @param viewport
	 *            Specifies the current viewport.
	 * @param windowCoordinate
	 *            Return the computed window coordinates.                
	 */
	public boolean project(float objx, float objy, float objz, PMatrix3D modelview,
                               PMatrix3D projection, int []viewport, float []windowCoordinate) {
		//Transformation vectors
		float in[] = new float[4];
	    float out[] = new float[4];

	    in[0]=objx;
	    in[1]=objy;
	    in[2]=objz;
	    in[3]=1.0f;
	    
	    modelview.mult(in, out);
	    projection.mult(out, in);
	    
	    if (in[3] == 0.0) return false;
	    in[0] /= in[3];
	    in[1] /= in[3];
	    in[2] /= in[3];
	    /* Map x, y and z to range 0-1 */
	    in[0] = in[0] * 0.5f + 0.5f;
	    in[1] = in[1] * 0.5f + 0.5f;
	    in[2] = in[2] * 0.5f + 0.5f;

	    /* Map x,y to viewport */
	    in[0] = in[0] * viewport[2] + viewport[0];
	    in[1] = in[1] * viewport[3] + viewport[1];

	    windowCoordinate[0]=in[0];
	    windowCoordinate[1]=in[1];
	    windowCoordinate[2]=in[2];
	    return true;
	}
	
	/**
	 * Similar to {@code gluUnProject}: map window coordinates to object coordinates.
	 * 
	 * @param winx
	 *            Specify the window x coordinate.
	 * @param winy
	 *            Specify the window y coordinate.
	 * @param winz
	 *            Specify the window z coordinate.
	 * @param modelview
	 *            Specifies the current modelview matrix.
	 * @param projection
	 *            Specifies the current projection matrix.
	 * @param viewport
	 *            Specifies the current viewport.
	 * @param objCoordinate
	 *            Return the computed object coordinates.                
	 */
	boolean unproject(float winx, float winy, float winz, PMatrix3D modelview,
			              PMatrix3D projection, int viewport[], float []objCoordinate)	{
        PMatrix3D finalMatrix = new PMatrix3D(projection);
	    float in[] = new float [4];
	    float out[]  = new float [4];	    
	    
	    finalMatrix.apply(modelview);
	    
	    if(!finalMatrix.invert()) return false;

	    in[0]=winx;
	    in[1]=winy;
	    in[2]=winz;
	    in[3]=1.0f;

	    /* Map x and y from window coordinates */
	    in[0] = (in[0] - viewport[0]) / viewport[2];
	    in[1] = (in[1] - viewport[1]) / viewport[3];

	    /* Map to range -1 to 1 */
	    in[0] = in[0] * 2 - 1;
	    in[1] = in[1] * 2 - 1;
	    in[2] = in[2] * 2 - 1;

	    finalMatrix.mult(in, out);
	    if (out[3] == 0.0) return false;
	    
	    out[0] /= out[3];
	    out[1] /= out[3];
	    out[2] /= out[3];
	    
	    objCoordinate[0] = out[0];
	    objCoordinate[1] = out[1];
	    objCoordinate[2] = out[2];
	    
	    return true;
	}		
}
