package proscene;

import processing.core.*;

/**
 * A PSAxisPlaneConstraint defined in the PSFrame local coordinate system. 
 * <p> 
 * The {@link #translationConstraintDirection()} and {@link #rotationConstraintDirection()}
 * are expressed in the PSFrame local coordinate system (see
 * {@link proscene.PSFrame#referenceFrame()}).
 */
public class PSLocalConstraint extends PSAxisPlaneConstraint {

	/**
	 * Depending on {@link #translationConstraintType()}, {@code constrain} translation
	 * to be along an axis or limited to a plane defined in the PSFrame local coordinate
	 * system by {@link #translationConstraintDirection()}.
	 */
	public PVector constrainTranslation(PVector translation, PSFrame frame) {
		PVector res = new PVector(translation.x, translation.y, translation.z);
		PVector proj;
		switch (translationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			proj = frame.rotation().rotate(translationConstraintDirection());
			res = PSUtility.projectVectorOnPlane(translation, proj);
			break;
		case AXIS:
			proj = frame.rotation().rotate(translationConstraintDirection());
			res = PSUtility.projectVectorOnAxis(translation, proj);
			break;
		case FORBIDDEN:
			res = new PVector(0.0f, 0.0f, 0.0f);
			break;
		}
		return res;
	}

	/**
	 * When {@link #rotationConstraintType()} is of Type AXIS, constrain {@code rotation}
	 * to be a rotation around an axis whose direction is defined in the PSFrame local
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
			PVector axis = rotationConstraintDirection();
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
