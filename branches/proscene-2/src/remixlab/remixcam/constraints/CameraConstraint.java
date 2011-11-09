/**
 *                     ProScene (version 1.2.0)      
 *    Copyright (c) 2010-2011 by National University of Colombia
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

package remixlab.remixcam.constraints;

import remixlab.remixcam.core.*;
import remixlab.remixcam.geom.*;

/**
 * An AxisPlaneConstraint defined in the camera coordinate system.
 * <p>
 * The {@link #translationConstraintDirection()} and
 * {@link #rotationConstraintDirection()} are expressed in the associated
 * {@link #camera()} coordinate system.
 */
public class CameraConstraint extends AxisPlaneConstraint {

	private Camera camera;

	/**
	 * Creates a CameraConstraint, whose constrained directions are defined in the
	 * {@link #camera()} coordinate system.
	 */
	public CameraConstraint(Camera cam) {
		super();
		camera = cam;
	}

	/**
	 * Returns the associated Camera. Set using the CameraConstraint constructor.
	 */
	public Camera camera() {
		return camera;
	}

	/**
	 * Depending on {@link #translationConstraintType()}, {@code constrain}
	 * translation to be along an axis or limited to a plane defined in the
	 * {@link #camera()} coordinate system by
	 * {@link #translationConstraintDirection()}.
	 */
	@Override
	public Vector3D constrainTranslation(Vector3D translation, BasicFrame frame) {
		Vector3D res = new Vector3D(translation.x, translation.y, translation.z);
		Vector3D proj;
		switch (translationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			proj = camera().frame().inverseTransformOf(translationConstraintDirection());
			if (frame.referenceFrame() != null)
				proj = frame.referenceFrame().transformOf(proj);
			res = Vector3D.projectVectorOnPlane(translation, proj);
			break;
		case AXIS:
			proj = camera().frame().inverseTransformOf(translationConstraintDirection());
			if (frame.referenceFrame() != null)
				proj = frame.referenceFrame().transformOf(proj);
			res = Vector3D.projectVectorOnAxis(translation, proj);
			break;
		case FORBIDDEN:
			res = new Vector3D(0.0f, 0.0f, 0.0f);
			break;
		}
		return res;
	}

	/**
	 * When {@link #rotationConstraintType()} is of type AXIS, constrain {@code
	 * rotation} to be a rotation around an axis whose direction is defined in the
	 * {@link #camera()} coordinate system by
	 * {@link #rotationConstraintDirection()}.
	 */
	@Override
	public Quaternion constrainRotation(Quaternion rotation, BasicFrame frame) {
		Quaternion res = rotation.getCopy();
		switch (rotationConstraintType()) {
		case FREE:
			break;
		case PLANE:
			break;
		case AXIS: {
			Vector3D axis = frame.transformOf(camera().frame().inverseTransformOf(
					rotationConstraintDirection()));
			Vector3D quat = new Vector3D(rotation.x, rotation.y, rotation.z);
			quat = Vector3D.projectVectorOnAxis(quat, axis);
			res = new Quaternion(quat, 2.0f * (float) Math.acos(rotation.w));
		}
			break;
		case FORBIDDEN:
			res = new Quaternion(); // identity
			break;
		}
		return res;
	}
}