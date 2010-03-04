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

package proscene;

import processing.core.*;

/**
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
