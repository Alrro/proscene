package proscene;

import processing.core.*;

/**
 * 
 * @author pierre
 * <p>
 * A PSAxisPlaneConstraint defined in the world coordinate system. 
 * <p> 
 * The {@link #translationConstraintDirection()} and {@link #rotationConstraintDirection()}
 * are expressed in the PSFrame world coordinate system (see
 * {@link proscene.PSFrame#referenceFrame()}). 
 */
public class PSWorldConstraint extends PSAxisPlaneConstraint {

	/**
	 * Depending on {@link #translationConstraintType()}, {@code constrain} translation
	 * to be along an axis or limited to a plane defined in the PSFrame world coordinate
	 * system by {@link #translationConstraintDirection()}.
	 */
	public PVector constrainTranslation(PVector translation, PSFrame frame) {
		PVector res = new PVector(translation.x, translation.y, translation.z);
		PVector proj;
		switch (translationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			if (frame.referenceFrame() != null) {
				proj = frame.referenceFrame().transformOf(
						translationConstraintDirection());
				res = PSUtility.projectVectorOnPlane(translation, proj);
			} else				
				res = PSUtility.projectVectorOnPlane(translation,
						translationConstraintDirection());
			break;
		case AXIS:
			if (frame.referenceFrame() != null) {
				proj = frame.referenceFrame().transformOf(
						translationConstraintDirection());				
				res = PSUtility.projectVectorOnAxis(translation, proj);
			} else				
				res = PSUtility.projectVectorOnAxis(translation,
						translationConstraintDirection());
			break;
		case FORBIDDEN:
			res = new PVector(0.0f, 0.0f, 0.0f);
			break;
		}
		return res;
	}

	/**
	 * When {@link #rotationConstraintType()} is of type AXIS, constrain {@code rotation}
	 * to be a rotation around an axis whose direction is defined in the PSFrame world
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
			PVector quat = new PVector(rotation.x, rotation.y, rotation.z);
			PVector axis = frame.transformOf(rotationConstraintDirection());
			quat = PSUtility.projectVectorOnAxis(quat, axis);
			res = new PSQuaternion(quat, 2.0f * PApplet.acos(rotation.w));
			break;
		}
		case FORBIDDEN:
			res = new PSQuaternion(); // identity
			break;
		}
		return res;
	}
}
