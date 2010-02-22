package proscene;

import processing.core.*;

/**
 * 
 * @author pierre
 * <p>
 * A PSAxisPlaneConstraint defined in the camera coordinate system. 
 * <p> 
 * The {@link #translationConstraintDirection()} and {@link #rotationConstraintDirection()}
 * are expressed in the associated {@link #camera()} coordinate system.
 */
public class PSCameraConstraint extends PSAxisPlaneConstraint {

	private PSCamera camera;

	/**
	 * Creates a PSCameraConstraint, whose constrained directions are defined in
	 * the {@link #camera()} coordinate system.
	 */
	public PSCameraConstraint(PSCamera cam) {
		super();
		camera = cam;
	}

	/**
	 * Returns the associated PSCamera. Set using the PSCameraConstraint constructor.
	 */
	public PSCamera camera() {
		return camera;
	}

	/**
	 * Depending on {@link #translationConstraintType()}, {@code constrain} translation
	 * to be along an axis or limited to a plane defined in the {@link #camera()} coordinate
	 * system by {@link #translationConstraintDirection()}.
	 */
	public PVector constrainTranslation(PVector translation, PSFrame frame) {
		PVector res = new PVector(translation.x, translation.y, translation.z);
		PVector proj;
		switch (translationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			proj = camera().frame().inverseTransformOf(
					translationConstraintDirection());
			if (frame.referenceFrame() != null)
				proj = frame.referenceFrame().transformOf(proj);
			res = PSUtility.projectVectorOnPlane(translation, proj);
			break;
		case AXIS:
			proj = camera().frame().inverseTransformOf(
					translationConstraintDirection());
			if (frame.referenceFrame() != null)
				proj = frame.referenceFrame().transformOf(proj);
			res = PSUtility.projectVectorOnAxis(translation, proj);
			break;
		case FORBIDDEN:
			res = new PVector(0.0f, 0.0f, 0.0f);
			break;
		}
		return res;
	}

	/**
	 * When {@link #rotationConstraintType()} is of type AXIS, constrain {@code rotation}
	 * to be a rotation around an axis whose direction is defined in the {@link #camera()}
	 * coordinate system by {@link #rotationConstraintDirection()}.
	 */
	public PSQuaternion constrainRotation(PSQuaternion rotation, PSFrame frame) {
		PSQuaternion res = new PSQuaternion(rotation);
		switch (rotationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			break;
		case AXIS: {
			PVector axis = frame.transformOf(camera().frame()
					.inverseTransformOf(rotationConstraintDirection()));
			PVector quat = new PVector(rotation.x, rotation.y, rotation.z);
			quat = PSUtility.projectVectorOnAxis(quat, axis);
			res = new PSQuaternion(quat, 2.0f * PApplet.acos(rotation.w));
		}
			break;
		case FORBIDDEN:
			res = new PSQuaternion(); // identity
			break;
		}
		return res;
	}
}
