package proscene;

import processing.core.*;
import proscene.PSInteractiveFrame.CoordinateSystemConvention;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * A perspective or orthographic camera.
 * <p>
 * A PSCamera defines some intrinsic parameters ({@link #fieldOfView()},
 * {@link #position()}, {@link #viewDirection()}, {@link #upVector()}...)
 * and useful positioning tools that ease its placement
 * ({@link #showEntireScene()}, {@link #fitSphere(PVector, float)},
 * {@link #lookAt(PVector)}...). It exports its associated projection and
 * modelview matrices and can interactively be modified using the mouse.
 */
public class PSCamera implements Cloneable {
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
	private PSInteractiveCameraFrame frm;

	// C a m e r a p a r a m e t e r s
	private int scrnWidth, scrnHeight; // size of the window, in pixels
	private float fldOfView; // in radians
	private PVector scnCenter;
	private float scnRadius; // processing scene units
	private float zNearCoef;
	private float zClippingCoef;
	private float orthoCoef;
	private Type tp; // PERSPECTIVE or ORTHOGRAPHIC
	private float []modelViewMat; // [16] Buffered model view matrix.
	private float []projectionMat; // [16] Buffered projection matrix.

	// S t e r e o p a r a m e t e r s
	float IODist; // inter-ocular distance, in meters
	float focusDist; // in scene units
	float physicalDist2Scrn; // in meters
	float physicalScrnWidth; // in meters

	/**
	 * Default constructor. 
	 * <p> 
	 * {@link #sceneCenter()} is set to (0,0,0) and {@link #sceneRadius()} is
	 * set to 1.0. {@link #type()} PSCamera.PERSPECTIVE, with a {@code PI/4}
	 * {@link #fieldOfView()}. 
	 * <p> 
	 * See {@link #IODistance()}, {@link #physicalDistanceToScreen()},
	 * {@link #physicalScreenWidth()} and {@link #focusDistance()}
	 * documentations for default stereo parameter values.
	 */
	public PSCamera() {		
		fldOfView = PSQuaternion.PI / 4.0f;
		setFrame(new PSInteractiveCameraFrame());

		// Requires fieldOfView() to define focusDistance()
		setSceneRadius(1.0f);

		// Initial value (only scaled after this)
		orthoCoef = PApplet.tan(fieldOfView() / 2.0f);

		// Also defines the revolveAroundPoint(), which changes orthoCoef.
		setSceneCenter(new PVector(0.0f, 0.0f, 0.0f));

		// Requires fieldOfView() when called with ORTHOGRAPHIC. Attention to
		// projectionMat below.
		setType(PSCamera.Type.PERSPECTIVE);

		setZNearCoefficient(0.005f);
		setZClippingCoefficient(PApplet.sqrt(3.0f));

		// Dummy values
		setScreenWidthAndHeight(600, 400);

		// Stereo parameters
		setIODistance(0.062f);
		setPhysicalDistanceToScreen(0.5f);
		setPhysicalScreenWidth(0.4f);
		// focusDistance is set from setFieldOfView()

		modelViewMat = new float[16];
		projectionMat = new float[16];
		for (int j = 0; j < 16; ++j) {
			modelViewMat[j] = ((j % 5 == 0) ? 1.0f : 0.0f);
			// #CONNECTION# computeProjectionMatrix() is lazy and assumes 0.0
			// almost everywhere.
			projectionMat[j] = 0.0f;
		}
		computeProjectionMatrix();
	}

	/**
	 * Implementation of the clone method. 
	 * <p> 
	 * Calls {@link proscene.PSFrame#clone()} and makes a deep
	 * copy of the remaining object attributes except for
	 * {@code prevConstraint} (which is shallow copied).
	 * 
	 * @see proscene.PSFrame#clone()
	 */
	public PSCamera clone() {
		try {
			PSCamera clonedCam = (PSCamera) super.clone();
			clonedCam.scnCenter = new PVector(scnCenter.x, scnCenter.y,
					scnCenter.z);
			clonedCam.frm = frm.clone();
			for (int i = 0; i < 16; i++) {
				clonedCam.modelViewMat[i] = modelViewMat[i];
				clonedCam.projectionMat[i] = projectionMat[i];
			}

			return clonedCam;
		} catch (CloneNotSupportedException e) {
			throw new Error("Is too");
		}
	}

	// 1. POSITION AND ORIENTATION

	/**
	 * Returns the PSCamera position (the eye), defined in the world coordinate
	 * system. 
	 * <p> 
	 * Use {@link #setPosition(PVector)} to set the Camera position. Other
	 * convenient methods are showEntireScene() or fitSphere(). Actually returns
	 * {@link proscene.PSFrame#position()}. 
	 * <p> 
	 * This position corresponds to the projection center of a
	 * PSCamera.PERSPECTIVE camera. It is not located in the image plane, which
	 * is at a zNear() distance ahead.
	 */
	public final PVector position() {
		return frame().position();
	}

	/**
	 * Sets the PSCamera {@link #position()} (the eye), defined in the world
	 * coordinate system.
	 */
	public void setPosition(PVector pos) {
		frame().setPosition(pos);
	}

	/**
	 * Returns the normalized up vector of the PSCamera, defined in the world
	 * coordinate system. 
	 * <p> 
	 * Set using {@link #setUpVector(PVector)} or {@link #setOrientation(PSQuaternion)}.
	 * It is orthogonal to {@link #viewDirection()} and to {@link #rightVector()}. 
	 * <p> 
	 * It corresponds to the Y axis of the associated {@link #frame()} (actually returns
	 * {@link proscene.PSFrame#inverseTransformOf(PVector)})
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
	 * Rotates the PSCamera so that its {@link #upVector()} becomes {@code up}
	 * (defined in the world coordinate system). 
	 * <p> 
	 * The PSCamera is rotated around an axis orthogonal to {@code up} and to
	 * the current {@link #upVector()} direction. 
	 * <p> 
	 * Use this method in order to define the PSCamera horizontal plane.
	 * <p> 
	 * When {@code noMove} is set to {@code false}, the orientation modification
	 * is compensated by a translation, so that the
	 * {@link #revolveAroundPoint()} stays projected at the same position on
	 * screen. This is especially useful when the PSCamera is an observer of the
	 * scene (default mouse binding). 
	 * <p> 
	 * When {@code noMove} is true, the PSCamera {@link #position()} is left
	 * unchanged, which is an intuitive behavior when the PSCamera is in a
	 * walkthrough fly mode.
	 * 
	 * See also setViewDirection(), lookAt() and setOrientation().
	 */
	public void setUpVector(PVector up, boolean noMove) {
		PSQuaternion q = new PSQuaternion(new PVector(0.0f, 1.0f, 0.0f),
				frame().transformOf(up));

		if (!noMove)
			frame().setPosition(
					PVector.sub(revolveAroundPoint(), (PSQuaternion.multiply(
							frame().orientation(), q)).rotate(frame()
							.coordinatesOf(revolveAroundPoint()))));

		frame().rotate(q);

		// Useful in fly mode to keep the horizontal direction.
		frame().updateFlyUpVector();
	}

	/**
	 * Returns the normalized view direction of the PSCamera, defined in the
	 * world coordinate system. 
	 * <p> 
	 * Change this value using {@link #setViewDirection(PVector)}, {@link #lookAt(PVector)}
	 * or {@link #setOrientation(PSQuaternion)}. It is orthogonal to {@link #upVector()} and
	 * to {@link #rightVector()}.
	 * <p> 
	 * This corresponds to the negative Z axis of the {@link #frame()} ( {@code
	 * frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f))} ).
	 */
	public PVector viewDirection() {
		return frame().inverseTransformOf(new PVector(0.0f, 0.0f, -1.0f));
	}

	/**
	 * Rotates the PSCamera so that its {@link #viewDirection()} is {@code
	 * direction} (defined in the world coordinate system). 
	 * <p> 
	 * The PSCamera {@link #position()} is not modified. The PSCamera is rotated
	 * so that the horizon (defined by its {@link #upVector()}) is preserved.
	 * 
	 * @see #lookAt(PVector)
	 * @see #setUpVector(PVector)
	 */
	public void setViewDirection(PVector direction) {
		if (PSUtility.squaredNorm(direction) < 1E-10)
			return;

		PVector xAxis = direction.cross(upVector());
		if (PSUtility.squaredNorm(xAxis) < 1E-10) {
			// target is aligned with upVector, this means a rotation around X
			// axis
			// X axis is then unchanged, let's keep it !
			xAxis = frame().inverseTransformOf(new PVector(1.0f, 0.0f, 0.0f));
		}

		PSQuaternion q = new PSQuaternion();
		q.fromRotatedBasis(xAxis, xAxis.cross(direction), PVector.mult(
				direction, -1));
		frame().setOrientationWithConstraint(q);
	}

	/**
	 * Returns the normalized right vector of the PSCamera, defined in the world
	 * coordinate system. 
	 * <p> 
	 * This vector lies in the PSCamera horizontal plane, directed along the X
	 * axis (orthogonal to {@link #upVector()} and to {@link #viewDirection()}.
	 * Set using {@link #setUpVector(PVector)}, {@link #lookAt(PVector)} or
	 * {@link #setOrientation(PSQuaternion)}.
	 * <p> 
	 * Simply returns {@code frame().inverseTransformOf(new PVector(1.0f, 0.0f,
	 * 0.0f))}.
	 */
	public PVector rightVector() {
		return frame().inverseTransformOf(new PVector(1.0f, 0.0f, 0.0f));
	}

	/**
	 * Returns the PSCamera orientation, defined in the world coordinate system.
	 * <p> 
	 * Actually returns {@code frame().orientation()}. 
	 * Use {@link #setOrientation(PSQuaternion)}, {@link #setUpVector(PVector)}
	 * or {@link #lookAt(PVector)} to set the PSCamera orientation.
	 */
	public PSQuaternion orientation() {
		return frame().orientation();
	}

	/**
	 * Sets the {@link #orientation()} of the PSCamera using polar coordinates. 
	 * <p> 
	 * {@code theta} rotates the PSCamera around its Y axis, and then {@code
	 * phi} rotates it around its X axis. 
	 * <p> 
	 * The polar coordinates are defined in the world coordinates system:
	 * {@code theta = phi = 0} means that the PSCamera is directed towards the
	 * world Z axis. Both angles are expressed in radians.
	 * <p> 
	 * The {@link #position()} of the PSCamera is unchanged, you may want to call
	 * {@link #showEntireScene()} after this method to move the PSCamera.
	 * 
	 * @see #setUpVector(PVector)
	 */
	public void setOrientation(float theta, float phi) {
		//TODO: check coordinate system convention
		PVector axis = new PVector(0.0f, 1.0f, 0.0f);
		PSQuaternion rot1 = new PSQuaternion(axis, theta);
		axis.set(-PApplet.cos(theta), 0.0f, PApplet.sin(theta));
		PSQuaternion rot2 = new PSQuaternion(axis, phi);
		setOrientation(PSQuaternion.multiply(rot1, rot2));
	}

	/**
	 * Sets the PSCamera {@link #orientation()}, defined in the world coordinate
	 * system.
	 */
	public void setOrientation(PSQuaternion q) {
		frame().setOrientation(q);
		frame().updateFlyUpVector();
	}
	
	/**
	 * Sets the PSCamera's {@link #position()} and {@link #orientation()} from an OpenGL
	 * ModelView matrix. 
	 * <p> 
	 * This enables a PSCamera initialisation from other application based on OpenGL.
	 * {@code modelView} is a 16 float vector representing a valid OpenGL ModelView matrix,
	 * <p> 
	 * After this method has been called, {@link #getModelViewMatrix()} returns a matrix equivalent
	 * to {@code modelView}. 
	 * <p> 
	 * Only the {@link #orientation()} and {@link #position()} of the PSCamera are modified.
	 */
	public void fromModelViewMatrix(float []modelViewMatrix) {
		// Get upper left (rotation) matrix
		float upperLeft[][] = new float[3][3];
		for (int i=0; i<3; ++i)
			for (int j=0; j<3; ++j)
				upperLeft[i][j] = modelViewMatrix[i*4+j];
		
		// Transform upperLeft into the associated PSQuaternion
		PSQuaternion q = new PSQuaternion();
		q.fromRotationMatrix(upperLeft);
		
		setOrientation(q);
		setPosition(PVector.mult(q.rotate(new PVector(modelViewMatrix[12], modelViewMatrix[13], modelViewMatrix[14])), -1));
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
	 * Returns the PSCamera.Type of the PSCamera. 
	 * <p> 
	 * Set by {@link #setType(Type)}. 
	 * <p> 
	 * A {@link proscene.PSCamera.Type#PERSPECTIVE} PSCamera uses a classical projection
	 * mainly defined by its {@link #fieldOfView()}. 
	 * <p> 
	 * With a {@link proscene.PSCamera.Type#ORTHOGRAPHIC} {@link #type()}, the {@link #fieldOfView()} is
	 * meaningless and the width and height of the PSCamera frustum are inferred
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
	 * Defines the PSCamera {@link #type()}. 
	 * <p> 
	 * Changing the PSCamera Type alters the viewport and the objects' size can
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
		if ((type == PSCamera.Type.ORTHOGRAPHIC)
				&& (type() == PSCamera.Type.PERSPECTIVE))
			orthoCoef = PApplet.tan(fieldOfView() / 2.0f);

		this.tp = type;
	}

	/**
	 * Returns the vertical field of view of the PSCamera (in radians). 
	 * <p> 
	 * Value is set using {@link #setFieldOfView(float)}. Default value is pi/4
	 * radians. This value is meaningless if the PSCamera {@link #type()} is
	 * {@link proscene.PSCamera.Type#ORTHOGRAPHIC}. 
	 * <p> 
	 * The field of view corresponds the one used in {@code gluPerspective} (see
	 * manual). It sets the Y (vertical) aperture of the PSCamera. The X
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
	 * Sets the vertical {@link #fieldOfView()} of the PSCamera (in radians). 
	 * <p> 
	 * Note that {@link #focusDistance()} is set to {@link #sceneRadius()} /
	 * tan( {@link #fieldOfView()}/2) by this method.
	 */
	public void setFieldOfView(float fov) {
		fldOfView = fov;
		setFocusDistance(sceneRadius() / PApplet.tan(fov / 2.0f));
	}

	/**
	 * Changes the PSCamera {@link #fieldOfView()} so that the entire scene
	 * (defined by {@link proscene.PScene#sceneCenter()} and
	 * {@link proscene.PScene#sceneRadius()} is visible from the PSCamera
	 * {@link #position()}. 
	 * <p> 
	 * The {@link #position()} and {@link #orientation()} of the PSCamera are
	 * not modified and you first have to orientate the PSCamera in order to
	 * actually see the scene (see {@link #lookAt(PVector)},
	 * {@link #showEntireScene()} or {@link #fitSphere(PVector, float)}). 
	 * <p> 
	 * This method is especially useful for <i>shadow maps</i> computation. Use
	 * the PSCamera positioning tools ({@link #setPosition(PVector)},
	 * {@link #lookAt(PVector)}) to position a PSCamera at the light position.
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
	 * happens when the PSCamera is at a distance lower than sqrt(2.0) *
	 * sceneRadius() from the sceneCenter(). It optimizes the shadow map
	 * resolution, although it may miss some parts of the scene.
	 */
	public void setFOVToFitScene() {
		if (distanceToSceneCenter() > PApplet.sqrt(2.0f) * sceneRadius())
			setFieldOfView(2.0f * PApplet.asin(sceneRadius()
					/ distanceToSceneCenter()));
		else
			setFieldOfView(PSQuaternion.PI / 2.0f);
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
	 * of the PSCamera orthographic frustum and returns it. 
	 * <p> 
	 * While {@code target[0]} holds {@code halfWidth}, {@code target[1]} holds
	 * {@code halfHeight}. 
	 * <p> 
	 * These values are only valid and used when the PSCamera is of {@link #type()}
	 * ORTHOGRAPHIC and they are expressed in processing scene units.
	 * <p> 
	 * These values are proportional to the PSCamera (z projected) distance to
	 * the {@link #revolveAroundPoint()}. When zooming on the object, the
	 * PSCamera is translated forward \e and its frustum is narrowed, making the
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
	 * Returns the horizontal field of view of the PSCamera (in radians). 
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
	 * Sets the {@link #horizontalFieldOfView()} of the PSCamera (in radians). 
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
	 * Returns the PSCamera aspect ratio defined by {@link #screenWidth()} /
	 * {@link #screenHeight()}. 
	 * <p> 
	 * When the PSCamera is attached to a PSCene, these values and hence the
	 * aspectRatio() are automatically fitted to the viewer's window aspect
	 * ratio using setScreenWidthAndHeight().
	 */
	public float aspectRatio() {
		return (float) scrnWidth / (float) scrnHeight;
	}

	/**
	 * Defines the PSCamera {@link #aspectRatio()}. 
	 * <p> 
	 * This value is actually inferred from the {@link #screenWidth()} /
	 * {@link #screenHeight()} ratio. You should use
	 * {@link #setScreenWidthAndHeight(int, int)} instead. 
	 * <p> 
	 * This method might however be convenient when the PSCamera is not
	 * associated with a PScene. It actually sets the
	 * {@link #screenHeight()} to 100 and the {@link #screenWidth()}
	 * accordingly. See also {@link #setFOVToFitScene()}.
	 */
	public void setAspectRatio(float aspect) {
		setScreenWidthAndHeight((int) (100.0 * aspect), 100);
	}

	/**
	 * Sets PSCamera {@link #screenWidth()} and {@link #screenHeight()}
	 * (expressed in pixels). 
	 * <p> 
	 * You should not call this method when the PSCamera is associated with a
	 * PScene, since the latter automatically updates these values when it
	 * is resized (hence overwriting your values). 
	 * <p> 
	 * Non-positive dimension are silently replaced by a 1 pixel value to ensure
	 * frustrum coherence. 
	 * <p> 
	 * If your PSCamera is used without a PScene (offscreen rendering,
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
	 * Returns the width (in pixels) of the PSCamera screen. 
	 * <p> 
	 * Set using {@link #setScreenWidthAndHeight(int, int)}. This value is automatically fitted
	 * to the PScene's window dimensions when the PSCamera is attached to a PScene.
	 */
	public final int screenWidth() {
		return scrnWidth;
	}

	/**
	 * Returns the height (in pixels) of the PSCamera screen. 
	 * <p> 
	 * Set using {@link #setScreenWidthAndHeight(int, int)}. This value is automatically fitted
	 * to the PScene's window dimensions when the PSCamera is attached to a PScene.
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
	 * Fills {@code viewport} with the PSCamera viewport and returns it.
	 * If viewport is null (or not the correct size), a new array will be
	 * created. 
	 * <p>
	 * This method is mainly used in conjunction with {@code gluProject}, which
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
	 * Returns the near clipping plane distance used by the PSCamera projection
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
	 * PSCamera is inside the {@link #sceneRadius()} sphere:
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
	 * Camera and use {@link proscene.PScene#setCamera(PSCamera)}. 
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
	 * PSCamera is inside the sphere defined by {@link #sceneCenter()} and
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
	 * Only meaningful when PSCamera type is PERSPECTIVE.
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
	 * A line of {@code n * pixelPSRatio()} processing scene units, located at {@code position} in
	 * the world coordinates system, will be projected with a length of {@code n} pixels on screen. 
	 * <p> 
	 * Use this method to scale objects so that they have a constant pixel size on screen.
	 * The following code will draw a 20 pixel line, starting at {@link #sceneCenter()} and
	 * always directed along the screen vertical direction:
	 * <p>
	 * {@code beginShape(LINES);}<br>
	 * {@code vertex(sceneCenter().x, sceneCenter().y, sceneCenter().z);}<br>
	 * {@code PVector v = PVector.add(sceneCenter(), PVector.mult(upVector(), 20 * pixelPSRatio(sceneCenter())));}<br>
	 * {@code vertex(v.x, v.y, v.z);}<br>
	 * {@code endShape();}<br>
	 */
	public float pixelPSRatio(PVector position) {
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
	
	/**
	 * Returns the 6 plane equations of the PSCamera frustum. 
	 * <p> 
	 * The six 4-component vectors of {@code coef} respectively correspond to the left,
	 * right, near, far, top and bottom PSCamera frustum planes. Each vector holds a plane
	 * equation of the form:
	 * <p>
	 * {@code a*x + b*y + c*z + d = 0}
	 * <p>
	 * where {@code a}, {@code b}, {@code c} and {@code d} are the 4 components of each vector,
	 * in that order. 
	 * <p> 
	 * This format is compatible with the {@code glClipPlane()} OpenGL function. One camera frustum
	 * plane can hence be applied in an other viewer to visualize the culling results:
	 * <p>
	 * {@code // Retrieve place equations}<br>
	 * {@code float [][] coef = mainViewer.camera().getFrustumPlanesCoefficients();}<br>
	 * {@code // These two additional clipping planes (which must have been enabled)}<br>
	 * {@code // will reproduce the mainViewer's near and far clipping.}<br>
	 * {@code glClipPlane(GL_CLIP_PLANE0, coef[2]);}<br>
	 * {@code glClipPlane(GL_CLIP_PLANE1, coef[3]);}<br>
	 */
	public float[][] getFrustumPlanesCoefficients() {
		// Computed once and for all
		float [][] coef = new float [6][4];
		PVector pos          = position();
		PVector viewDir      = viewDirection();
		PVector up           = upVector();
		PVector right        = rightVector();
		float posViewDir = PVector.dot(pos, viewDir);
		
		switch (type()) {
		case PERSPECTIVE : {
			float hhfov = horizontalFieldOfView() / 2.0f;
			float chhfov = PApplet.cos(hhfov);
			float shhfov = PApplet.sin(hhfov);
			normal[0] = PVector.mult(viewDir, -shhfov);
			normal[1] = PVector.add(normal[0], PVector.mult(right, chhfov));
			normal[0] = PVector.sub(normal[0], PVector.mult(right, chhfov));
			
			normal[2] = PVector.mult(viewDir, -1);
			normal[3] =  viewDir;
			
			float hfov = fieldOfView() / 2.0f;
			float chfov = PApplet.cos(hfov);
			float shfov = PApplet.sin(hfov);
			normal[4] = PVector.mult(viewDir, -shfov);
			normal[5] = PVector.sub(normal[4], PVector.mult(up, chfov));
			normal[4] = PVector.add(normal[4], PVector.mult(up, chfov));
			
			for (int i=0; i<2; ++i)
				dist[i] = PVector.dot(pos, normal[i]);
			for (int j=4; j<6; ++j)
				dist[j] = PVector.dot(pos, normal[j]);
			
			// Natural equations are:
			// dist[0,1,4,5] = pos * normal[0,1,4,5];
			// dist[2] = (pos + zNear() * viewDir) * normal[2];
			// dist[3] = (pos + zFar()  * viewDir) * normal[3];
			
			// 2 times less computations using expanded/merged equations. Dir vectors are normalized.
			
			float posRightCosHH = PVector.dot(PVector.mult(pos, chhfov), right);
			dist[0] = -shhfov * posViewDir;
			dist[1] = dist[0] + posRightCosHH;
			dist[0] = dist[0] - posRightCosHH;
			float posUpCosH = PVector.dot(PVector.mult(pos, chfov), up);
			dist[4] = - shfov * posViewDir;
			dist[5] = dist[4] - posUpCosH;
			dist[4] = dist[4] + posUpCosH;
			
			break;
			}
		
		case ORTHOGRAPHIC :
			normal[0] = PVector.mult(right, -1);
			normal[1] =  right;
			normal[4] =  up;
			normal[5] = PVector.mult(up, -1);
			
			float [] wh = getOrthoWidthHeight();
			
			dist[0] = PVector.dot(PVector.sub(pos, PVector.mult(right, wh[0])), normal[0]);
			dist[1] = PVector.dot(PVector.add(pos, PVector.mult(right, wh[0])), normal[1]);
			dist[4] = PVector.dot(PVector.add(pos, PVector.mult(up, wh[1])), normal[4]);
			dist[5] = PVector.dot(PVector.sub(pos, PVector.mult(up, wh[1])), normal[5]);
			break;
			}
		
		// Front and far planes are identical for both camera types.
		normal[2] = PVector.mult(viewDir, -1);
		normal[3] =  viewDir;
		dist[2] = -posViewDir - zNear();
		dist[3] =  posViewDir + zFar();
		
		for (int i=0; i<6; ++i) {
			coef[i][0] = normal[i].x;
			coef[i][1] = normal[i].y;
			coef[i][2] = normal[i].z;
			coef[i][3] = dist[i];
		}
		
		return coef;
	}


	// 3. SCENE RADIUS AND CENTER

	/**
	 * Returns the radius of the scene observed by the Camera. 
	 * <p> 
	 * You need to provide such an approximation of the scene dimensions so that
	 * the Camera can adapt its {@link #zNear()} and {@link #zFar()} values. See the
	 * {@link #sceneCenter()} documentation. 
	 * <p> 
	 * Note that PScene.sceneRadius() (resp. PScene.setSceneRadius()) simply call this
	 * method on its associated PSCamera.
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
			// TODO: tell a warning:
			// "Scene radius must be positive - Ignoring value"
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
	 * The scene observed by the PSCamera should be roughly centered on this
	 * position, and included in a {@link #sceneRadius()} sphere. This approximate
	 * description of the scene permits a {@link #zNear()} and {@link #zFar()}
	 * clipping planes definition, and allows convenient positioning methods such as
	 * {@link #showEntireScene()}. 
	 * <p> 
	 * Note that {@link proscene.PScene#sceneCenter()} (resp.
	 * proscene.PScene{@link #setSceneCenter(PVector)})
	 * simply call this method (resp. {@link #setSceneCenter(PVector)}) on its associated
	 * {@link proscene.PScene#camera()}. Default value is (0,0,0) (world origin). Use
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
	 * The point the PSCamera revolves around its
	 * {@link proscene.PSInteractiveCameraFrame#revolveAroundPoint()}. 
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

	// 5. ASSOCIATED FRAME

	/**
	 * Returns the PSInteractiveCameraFrame attached to the PSCamera. 
	 * <p> 
	 * This PSInteractiveCameraFrame defines its {@link #position()} and
	 * {@link #orientation()} and can translate mouse events into PSCamera
	 * displacement. Set using {@link #setFrame(PSInteractiveCameraFrame)}.
	 */
	public PSInteractiveCameraFrame frame() {
		return frm;
	}

	/**
	 * Sets the PSCamera {@link #frame()}. 
	 * <p> 
	 * If you want to move the PSCamera, use {@link #setPosition(PVector)} and
	 * {@link #setOrientation(PSQuaternion)} or one of the PSCamera positioning
	 * methods ({@link #lookAt(PVector)}, {@link #fitSphere(PVector, float)},
	 * {@link #showEntireScene()}...) instead.
	 * <p> 
	 * This method is actually mainly useful if you derive the
	 * PSInteractiveCameraFrame class and want to use an instance of your new
	 * class to move the PSCamera. 
	 * <p> 
	 * A {@code null} {@code icf} reference will silently be ignored.
	 */
	public final void setFrame(PSInteractiveCameraFrame icf) {
		if (icf == null)
			return;

		frm = icf;
	}

	// 6. OPENGL MATRICES
	
	/**
	 * Convenience function that simply returns {@code return getProjectionMatrix(new float[16])}
	 * 
	 * @see #getProjectionMatrix(float[])
	 */
	public float[] getProjectionMatrix() {
		return getProjectionMatrix(new float[16]);
	}
	
	/**
	 * Fills {@code m} with the PSCamera projection matrix values and returns it. If
	 * {@code m} is {@code null} (or not the correct size), a new array will created. 
	 * <p> 
	 * Calls {@link #computeProjectionMatrix()} to define the PSCamera projection matrix.
	 * <p>
	 * The result is an OpenGL 4x4 matrix, which is given in column-major order (see
	 * {@code glMultMatrix} documentation for details).
	 * 
	 * @see #getModelViewMatrix()
	 */
	public float[] getProjectionMatrix(float[] m) {
		if ((m == null) || (m.length != 16)) {
			m = new float[16];
		}
		
		// May not be needed, but easier and more robust like this.
		computeProjectionMatrix();
		for (int i=0; i<16; ++i)
			m[i] = projectionMat[i];
		
		return m;
	}	

	/**
	 * Computes the projection matrix associated with the PSCamera. 
	 * <p> 
	 * If {@link #type()} is PERSPECTIVE, defines a GL_PROJECTION matrix similar to what
	 * would {@code gluPerspective()} do using the {@link #fieldOfView()}, window
	 * {@link #aspectRatio()}, {@link #zNear()} and {@link #zFar()} parameters. 
	 * <p> 
	 * If {@link #type()} is ORTHOGRAPHIC, the projection matrix is as what {@code
	 * glOrtho()} would do. Frustum's width and height are set using
	 * {@link #getOrthoWidthHeight()}. 
	 * <p> 
	 * Both types use {@link #zNear()} and {@link #zFar()} to place clipping planes. These values
	 * are determined from sceneRadius() and sceneCenter() so that they best fit
	 * the scene size. 
	 * <p> 
	 * Use {@link #getProjectionMatrix()} to retrieve this matrix.
	 * <p> 
	 * <b>Note:</b> You must call this method if your PSCamera is not associated
	 * with a PScene and is used for offscreen computations (using
	 * (un)projectedCoordinatesOf() for instance).
	 * 
	 * @see #setProjectionfromPCamera(PMatrix3D)
	 */
	public void computeProjectionMatrix() {
		float ZNear = zNear();
		float ZFar = zFar();

		switch (type()) {
		case PERSPECTIVE: {
			// #CONNECTION# all non null coefficients were set to 0.0 in
			// constructor.
			float f = 1.0f / PApplet.tan(fieldOfView() / 2.0f);
			projectionMat[0] = f / aspectRatio();
			projectionMat[5] = f;
			projectionMat[10] = (ZNear + ZFar) / (ZNear - ZFar);
			projectionMat[11] = -1.0f;
			projectionMat[14] = 2.0f * ZNear * ZFar / (ZNear - ZFar);
			projectionMat[15] = 0.0f;
			// same as gluPerspective( 180.0*fieldOfView()/M_PI, aspectRatio(),
			// zNear(), zFar() );
			break;
		}
		case ORTHOGRAPHIC: {
			float[] wh = getOrthoWidthHeight();
			projectionMat[0] = 1.0f / wh[0];
			projectionMat[5] = 1.0f / wh[1];
			projectionMat[10] = -2.0f / (ZFar - ZNear);
			projectionMat[11] = 0.0f;
			projectionMat[14] = -(ZFar + ZNear) / (ZFar - ZNear);
			projectionMat[15] = 1.0f;
			// same as glOrtho( -w, w, -h, h, zNear(), zFar() );
			break;
		}
		}
	}
	
	/**
	 * Sets the projection matrix associated with the PSCamera directly from a PCamera.
	 * 
	 * @see #computeProjectionMatrix()
	 */
	public void setProjectionfromPCamera(PMatrix3D proj) {
		PMatrix3D temp = new PMatrix3D(proj);
		temp.transpose();
		temp.get(projectionMat);
	}

	/**
	 * Convenience function that simply returns
	 * {@code return this.getModelViewMatrix(new float[16])}
	 */
	public float[] getModelViewMatrix() {
		return this.getModelViewMatrix(new float[16]);	
	}
	
	/**
	 * Fills {@code m} with the Camera modelView matrix values and returns it. If
	 * {@code m} is {@code null} (or not the correct size), a new array will be created.
	 * <p> 
	 * First calls {@link #computeModelViewMatrix()} to define the PSCamera modelView matrix. 
	 * <p> 
	 * The result is an OpenGL 4x4 matrix, which is given in column-major order (see
	 * {@code glMultMatrix} documentation for details).
	 *  
	 * @see #getProjectionMatrix(float[])
	 * @see #fromModelViewMatrix(float[])
	 */
	public float[] getModelViewMatrix(float[] m) {
		/**
		 * Note that this matrix is usually not the one you would get from a
	     * {@code glGetFloatv(GL_MODELVIEW_MATRIX, m)}. It converts from the world to the
	     * PSCamera coordinate system, but as soon as you modify the GL_MODELVIEW in your
	     * drawing code, the two matrices differ.
		 */
		if ((m == null) || (m.length != 16)) {
			m = new float[16];
		}
		// May not be needed, but easier like this.
		// Prevents from retrieving matrix in stereo mode -> overwrites shifted value.
		computeModelViewMatrix();
		for (int i=0; i<16; ++i)
			m[i] = modelViewMat[i];
	  
	  return m;
	}

	/**
	 * Computes the modelView matrix associated with the Camera's {@link #position()}
	 * and {@link #orientation()}.
	 * <p> 
	 * This matrix converts from the world coordinates system to the PSCamera
	 * coordinates system, so that coordinates can then be projected on screen
	 * using the projection matrix (see {@link #computeProjectionMatrix()}).
	 * <p>
	 * Use {@link #getModelViewMatrix()} to retrieve this matrix.
	 * <p> 
	 * <b>Note:</b> You must call this method if your PSCamera is not associated with a
	 * PScene and is used for offscreen computations (using (un)projectedCoordinatesOf()
	 * for instance).
	 */
	public void computeModelViewMatrix() {
		PSQuaternion q = frame().orientation();
		
		float q00 = 2.0f * q.x * q.x;
		float q11 = 2.0f * q.y * q.y;
		float q22 = 2.0f * q.z * q.z;
		
		float q01 = 2.0f * q.x * q.y;
		float q02 = 2.0f * q.x * q.z;
		float q03 = 2.0f * q.x * q.w;
		
		float q12 = 2.0f * q.y * q.z;
		float q13 = 2.0f * q.y * q.w;
		float q23 = 2.0f * q.z * q.w;
		
		modelViewMat[0] = 1.0f  - q11 - q22;
		modelViewMat[1] =         q01 - q23;
		modelViewMat[2] =         q02 + q13;
		modelViewMat[3] = 0.0f;
		
		modelViewMat[4] =         q01 + q23;
		modelViewMat[5] = 1.0f  - q22 - q00;
		modelViewMat[6] =         q12 - q03;
		modelViewMat[7] = 0.0f;
		
		modelViewMat[8] =         q02 - q13;
		modelViewMat[9] =         q12 + q03;
		modelViewMat[10] = 1.0f - q11 - q00;
		modelViewMat[11] = 0.0f;
		
		PVector t = q.inverseRotate(frame().position());
		
		modelViewMat[12] = -t.x;
		modelViewMat[13] = -t.y;
		modelViewMat[14] = -t.z;
		modelViewMat[15] = 1.0f;
	}
	
	/**
	 * Sets the modelview matrix associated with the PSCamera directly from a PCamera.
	 * 
	 * @see #computeModelViewMatrix()
	 */
	public void setModelViewfromPCamera(PMatrix3D modelview) {
		PMatrix3D temp = new PMatrix3D(modelview);
		temp.transpose();
		temp.get(modelViewMat);
	}
	
	/**
	 * Convenience function that simply returns
	 * {@code return getModelViewProjectionMatrix(new float[16])}
	 */
	public float[] getModelViewProjectionMatrix() {
		return getModelViewProjectionMatrix(new float[16]);	
	}
	
	/**
	 * Fills {@code m} m with the product of the ModelView and Projection matrices and returns it.
	 * If {@code m} is {@code null} (or not the correct size), a new array will created.  
	 * <p>  
	 * Calls {@link #getModelViewMatrix()} and {@link #getProjectionMatrix()} and then fills
	 * {@code m} with the product of these two matrices.
	 */
	public float[] getModelViewProjectionMatrix(float[] m) {
		float mv[] = getModelViewMatrix(); 
		float proj[] = getProjectionMatrix();
		
		for (int i=0; i<4; ++i)	{
			for (int j=0; j<4; ++j) {
				float sum = 0.0f;
				for (int k=0; k<4; ++k)
					sum += proj[i+4*k]*mv[k+4*j];
				m[i+4*j] = sum;
			}
		}
		return m;
	}

	// 7. WORLD -> CAMERA

	/**
	 * Returns the PSCamera frame coordinates of a point {@code src} defined in
	 * world coordinates. 
	 * <p> 
	 * {@link #worldCoordinatesOf(PVector)} performs the inverse transformation. 
	 * <p> 
	 * Note that the point coordinates are simply converted in a different
	 * coordinate system. They are not projected on screen. Use
	 * {@link #projectedCoordinatesOf(PVector, PSFrame)} for that.
	 */
	public final PVector cameraCoordinatesOf(PVector src) {
		return frame().coordinatesOf(src);
	}

	/**
	 * Returns the world coordinates of the point whose position {@code src} is
	 * defined in the PSCamera coordinate system. 
	 * <p> 
	 * {@link #cameraCoordinatesOf(PVector)} performs the inverse
	 * transformation.
	 */
	public PVector worldCoordinatesOf(final PVector src) {
		return frame().inverseCoordinatesOf(src);
	}

	// 8. 2D -> 3D

	/**
	 * Gives the coefficients of a 3D half-line passing through the PSCamera eye
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
	 * Convenience function that simply returns {@code return
	 * projectedCoordinatesOf(src, null)}
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
	 * {@link #unprojectedCoordinatesOf(PVector, PSFrame)} performs the inverse
	 * transformation.
	 * <p> 
	 * <b>Attention:</b> This method only uses the intrinsic PSCamera parameters
	 * (see {@link #getModelViewMatrix()}, {@link #getProjectionMatrix()} and
	 * {@link #getViewport()}) and is completely independent of the OpenGL
	 * GL_MODELVIEW, GL_PROJECTION and viewport matrices. You can hence define
	 * a virtual PSCamera and use this method to compute projections out of a
	 * classical rendering context.
	 */
	public final PVector projectedCoordinatesOf(PVector src, PSFrame frame) {		
		float xyz[] = new float[3];		
		viewport = getViewport();
		
		if (frame != null) {
			PVector tmp = frame.inverseCoordinatesOf(src);
			gluProjectf(tmp.x, tmp.y, tmp.z, modelViewMat, projectionMat, viewport, xyz);
		} else
			gluProjectf(src.x, src.y, src.z, modelViewMat, projectionMat, viewport, xyz);
		
		if ( frame().coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
			xyz[1] = screenHeight() - xyz[1];

		return new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]);
	}
	
	/**
	 * Convenience function that simply returns {@code return
	 * unprojectedCoordinatesOf(src, null)}
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
	 * {@link proscene.PSFrame#referenceFrame()} are taken into
	 * account. 
	 * <p> 
	 * {@link #projectedCoordinatesOf(PVector, PSFrame)} performs the inverse
	 * transformation. 
	 * <p> 
	 * This method only uses the intrinsic PSCamera parameters (see
	 * {@link #getModelViewMatrix()}, {@link #getProjectionMatrix()} and
	 * {@link #getViewport()}) and is completely independent of the OpenGL
	 * GL_MODELVIEW, GL_PROJECTION and viewport matrices. You can hence
	 * define a virtual PSCamera and use this method to compute un-projections
	 * out of a classical rendering context. 
	 * <p> 
	 * <b>Attention:</b> However, if your PSCamera is not attached to a
	 * PScene (used for offscreen computations for instance), make sure the
	 * PSCamera matrices are updated before calling this method (use
	 * {@link #computeModelViewMatrix()}, {@link #computeProjectionMatrix()}).
	 * <p> 
	 * This method is not computationally optimized. If you call it several
	 * times with no change in the matrices, you should buffer the entire
	 * inverse projection matrix (modelview, projection and then viewport) to
	 * speed-up the queries. See the gluUnProject man page for details.
	 * 
	 * @see #setScreenWidthAndHeight(int, int)
	 */
	public final PVector unprojectedCoordinatesOf(PVector src, PSFrame frame) {
		//Warning:
		//it is responsible of the caller to check coordinateSystemConvention on src
		float xyz[] = new float[3];
		viewport = getViewport();
		if ( frame().coordinateSystemConvention() == CoordinateSystemConvention.LEFT_HANDED)
			gluUnProjectf(src.x, (screenHeight() - src.y), src.z, modelViewMat, projectionMat, viewport, xyz);
		else
			gluUnProjectf(src.x, src.y, src.z, modelViewMat, projectionMat, viewport, xyz);		
		if (frame != null)
			return frame.coordinatesOf(new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]));
		else
			return new PVector((float) xyz[0], (float) xyz[1], (float) xyz[2]);
	}	

	// 9. FLYSPEED

	/**
	 * Returns the fly speed of the PSCamera. 
	 * <p> 
	 * Simply returns {@code frame().flySpeed()}. See the
	 * {@link proscene.PSInteractiveCameraFrame#flySpeed()}
	 * documentation. This value is only meaningful when the MouseAction
	 * bindings is PScene.MOVE_FORWARD or is PScene.MOVE_BACKWARD.
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
	 * Sets the PSCamera {@link #flySpeed()}.
	 * <p>
	 * <b>Attention:</b> This value is modified by
	 * {@link #setSceneRadius(float)}.
	 */
	public void setFlySpeed(float speed) {
		frame().setFlySpeed(speed);
	}

	// 10. POSITION TOOLS

	/**
	 * Sets the PSCamera {@link #orientation()}, so that it looks at point
	 * {@code target} (defined in the world coordinate system). 
	 * <p> 
	 * The Camera {@link #position()} is not modified. Simply
	 * {@link #setViewDirection(PVector)}.
	 * 
	 * @see #at()
	 * @see #setUpVector(PVector)
	 * @see #setOrientation(PSQuaternion)
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
	 * Moves the PSCamera so that the sphere defined by {@code center}, {@code
	 * radius}) is visible and fits the window. 
	 * <p> 
	 * The PSCamera is simply translated along its {@link #viewDirection()} so
	 * that the sphere fits the screen. Its {@link #orientation()} and its
	 * {@link #fieldOfView()} are unchanged. 
	 * <p> 
	 * You should therefore orientate the PSCamera before you call this method.
	 * 
	 * @see #lookAt(PVector)
	 * @see #setOrientation(PSQuaternion)
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
	 * Moves the PSCamera so that the (world axis aligned) bounding box ({@code
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
	 * The PSCamera is translated (its orientation() is unchanged) so that
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
	 * Moves the PSCamera so that the entire scene is visible. 
	 * <p> 
	 * Simply calls {@link #fitSphere(PVector, float)} on a sphere defined by
	 * {@link #sceneCenter()} and {@link #sceneRadius()}. 
	 * <p> 
	 * You will typically use this method in {@link proscene.PScene#init()} after you defined
	 * a new {@link #sceneRadius()}.
	 */
	public void showEntireScene() {
		fitSphere(sceneCenter(), sceneRadius());
	}

	/**
	 * Moves the PSCamera so that its {@link #sceneCenter()} is projected on the
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
	 * <b>attention:</b> This value is modified by PScene.setSceneRadius(),
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
	
	// 13. Implementation of glu utility functions
	/**
	 * Utility function that does the same as {@code gluProject()} using float precision
	 * numbers instead of doubles. See the {@code gluProject()} documentation for details. 
	 * <p> 
	 * Code adapted from mesa: http://www.mesa3d.org/
	 */
	protected boolean gluProjectf(float objx, float objy, float objz, float []modelview,
			                  float []projection, int []viewport, float []windowCoordinate) {
		//Transformation vectors
		float in[] = new float[4];
	    float out[] = new float[4];

	    in[0]=objx;
	    in[1]=objy;
	    in[2]=objz;
	    in[3]=1.0f;
	    gluMultMatrixVecf(modelview, in, out);
	    gluMultMatrixVecf(projection, out, in);
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
	 * Utility function that does the same as {@code gluUnProject()} using float precision
	 * numbers instead of doubles. See the {@code gluUnProject()} documentation for details. 
	 * <p> 
	 * Code adapted from mesa: http://www.mesa3d.org/
	 */	
	boolean gluUnProjectf(float winx, float winy, float winz, float modelview[],
			             float projection[], int viewport[], float []objCoordinate)	{		
        float finalMatrix[] = new float [16];
	    float in[] = new float [4];
	    float out[]  = new float [4];

	    gluMultMatricesf(modelview, projection, finalMatrix);
	    if (!gluInvertMatrixf(finalMatrix, finalMatrix)) return false;

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

	    gluMultMatrixVecf(finalMatrix, in, out);
	    if (out[3] == 0.0) return false;
	    
	    out[0] /= out[3];
	    out[1] /= out[3];
	    out[2] /= out[3];
	    
	    objCoordinate[0] = out[0];
	    objCoordinate[1] = out[1];
	    objCoordinate[2] = out[2];
	    
	    return true;
	}
	
	/**
	 * utility function need for
	 * {@link #gluProjectf(float, float, float, float[], float[], int[], float[])} and
	 * {@link #gluUnProject(float, float, float, float[], float[], int[], float[])} 
	 * <p> 
	 * Code adapted from mesa: http://www.mesa3d.org/
	 */
	protected void gluMultMatricesf(float a[], float b[], float r[]) {
		//a, b, and r should be [16]!
		int i, j;
		
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				r[i*4+j] =	a[i*4+0]*b[0*4+j] +
				            a[i*4+1]*b[1*4+j] +
				            a[i*4+2]*b[2*4+j] +
				            a[i*4+3]*b[3*4+j];
			}
		}
	}
	
	/**
	 * utility function need for
	 * {@link #gluProjectf(float, float, float, float[], float[], int[], float[])} and
	 * {@link #gluUnProject(float, float, float, float[], float[], int[], float[])} 
	 * <p> 
	 * Code adapted from mesa: http://www.mesa3d.org/
	 */
	protected boolean gluInvertMatrixf(float m[], float invOut[]) {
		//m and invOut should be [16]!
		float inv[] = new float[16]; 
	    float det;
	    int i;

	    inv[0] =   m[5]*m[10]*m[15] - m[5]*m[11]*m[14] - m[9]*m[6]*m[15]
	             + m[9]*m[7]*m[14] + m[13]*m[6]*m[11] - m[13]*m[7]*m[10];
	    inv[4] =  -m[4]*m[10]*m[15] + m[4]*m[11]*m[14] + m[8]*m[6]*m[15]
	             - m[8]*m[7]*m[14] - m[12]*m[6]*m[11] + m[12]*m[7]*m[10];
	    inv[8] =   m[4]*m[9]*m[15] - m[4]*m[11]*m[13] - m[8]*m[5]*m[15]
	             + m[8]*m[7]*m[13] + m[12]*m[5]*m[11] - m[12]*m[7]*m[9];
	    inv[12] = -m[4]*m[9]*m[14] + m[4]*m[10]*m[13] + m[8]*m[5]*m[14]
	             - m[8]*m[6]*m[13] - m[12]*m[5]*m[10] + m[12]*m[6]*m[9];
	    inv[1] =  -m[1]*m[10]*m[15] + m[1]*m[11]*m[14] + m[9]*m[2]*m[15]
	             - m[9]*m[3]*m[14] - m[13]*m[2]*m[11] + m[13]*m[3]*m[10];
	    inv[5] =   m[0]*m[10]*m[15] - m[0]*m[11]*m[14] - m[8]*m[2]*m[15]
	             + m[8]*m[3]*m[14] + m[12]*m[2]*m[11] - m[12]*m[3]*m[10];
	    inv[9] =  -m[0]*m[9]*m[15] + m[0]*m[11]*m[13] + m[8]*m[1]*m[15]
	             - m[8]*m[3]*m[13] - m[12]*m[1]*m[11] + m[12]*m[3]*m[9];
	    inv[13] =  m[0]*m[9]*m[14] - m[0]*m[10]*m[13] - m[8]*m[1]*m[14]
	             + m[8]*m[2]*m[13] + m[12]*m[1]*m[10] - m[12]*m[2]*m[9];
	    inv[2] =   m[1]*m[6]*m[15] - m[1]*m[7]*m[14] - m[5]*m[2]*m[15]
	             + m[5]*m[3]*m[14] + m[13]*m[2]*m[7] - m[13]*m[3]*m[6];
	    inv[6] =  -m[0]*m[6]*m[15] + m[0]*m[7]*m[14] + m[4]*m[2]*m[15]
	             - m[4]*m[3]*m[14] - m[12]*m[2]*m[7] + m[12]*m[3]*m[6];
	    inv[10] =  m[0]*m[5]*m[15] - m[0]*m[7]*m[13] - m[4]*m[1]*m[15]
	             + m[4]*m[3]*m[13] + m[12]*m[1]*m[7] - m[12]*m[3]*m[5];
	    inv[14] = -m[0]*m[5]*m[14] + m[0]*m[6]*m[13] + m[4]*m[1]*m[14]
	             - m[4]*m[2]*m[13] - m[12]*m[1]*m[6] + m[12]*m[2]*m[5];
	    inv[3] =  -m[1]*m[6]*m[11] + m[1]*m[7]*m[10] + m[5]*m[2]*m[11]
	             - m[5]*m[3]*m[10] - m[9]*m[2]*m[7] + m[9]*m[3]*m[6];
	    inv[7] =   m[0]*m[6]*m[11] - m[0]*m[7]*m[10] - m[4]*m[2]*m[11]
	             + m[4]*m[3]*m[10] + m[8]*m[2]*m[7] - m[8]*m[3]*m[6];
	    inv[11] = -m[0]*m[5]*m[11] + m[0]*m[7]*m[9] + m[4]*m[1]*m[11]
	             - m[4]*m[3]*m[9] - m[8]*m[1]*m[7] + m[8]*m[3]*m[5];
	    inv[15] =  m[0]*m[5]*m[10] - m[0]*m[6]*m[9] - m[4]*m[1]*m[10]
	             + m[4]*m[2]*m[9] + m[8]*m[1]*m[6] - m[8]*m[2]*m[5];

	    det = m[0]*inv[0] + m[1]*inv[4] + m[2]*inv[8] + m[3]*inv[12];
	    if (det == 0)
	        return false;

	    det = 1.0f / det;

	    for (i = 0; i < 16; i++)
	        invOut[i] = inv[i] * det;

	    return true;
	}
	
	/**
	 * utility function need for
	 * {@link #gluProjectf(float, float, float, float[], float[], int[], float[])} and
	 * {@link #gluUnProject(float, float, float, float[], float[], int[], float[])} 
	 * <p> 
	 * Code adapted from mesa: http://www.mesa3d.org/
	 */
	protected void gluMultMatrixVecf(float matrix[], float in[], float out[]) {
		//matrix should be [16]; in and out [4]
		int i;
		for (i=0; i<4; i++) {
			out[i] = in[0] * matrix[0*4+i] +
			         in[1] * matrix[1*4+i] +
			         in[2] * matrix[2*4+i] +
			         in[3] * matrix[3*4+i];
		}
	}	
}
