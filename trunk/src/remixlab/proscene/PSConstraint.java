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

/**
 * An interface class for Frame constraints. 
 * <p> 
 * This class defines the interface for the PSConstraints that can be applied
 * to a PSFrame to limit its motion. Use
 * {@link remixlab.proscene.PSFrame#setConstraint(PSConstraint)} to
 * associate a PSConstraint to a PSFrame (default is a {@code null}
 * {@link remixlab.proscene.PSFrame#constraint()}.
 */
public class PSConstraint {
	/**
	 * Filters the translation applied to the PSframe. This default implementation
	 * is empty (no filtering). 
	 * <p> 
	 * Overload this method in your own PSConstraint class to define a new 
	 * translation constraint. {@code frame} is the PSFrame to which is applied the
	 * translation. Use its {@link remixlab.proscene.PSFrame#position()} and
	 * update the translation accordingly instead. 
	 * <p> 
	 * {@code translation} is expressed in the local PSFrame coordinate system.
	 * Use {@link remixlab.proscene.PSFrame#inverseTransformOf(PVector)} to 
	 * express it in the world coordinate system if needed. 
	 */
    public PVector constrainTranslation(PVector translation, PSFrame frame) {
    	return new PVector(translation.x, translation.y, translation.z);
    }
	
    /**
     * Filters the rotation applied to the {@code frame}. This default implementation
     * is empty (no filtering). 
     * <p> 
     * Overload this method in your own PSConstraint class to define a new rotation
     * constraint. See {@link #constrainTranslation(PVector, PSFrame)} for details. 
     * <p> 
     * Use {@link remixlab.proscene.PSFrame#inverseTransformOf(PVector)} on the
     * {@code rotation} {@link remixlab.proscene.PSQuaternion#axis()} to express
     * {@code rotation} in the world coordinate system if needed.
     */
	public PSQuaternion constrainRotation(PSQuaternion rotation, PSFrame frame) {
		return new PSQuaternion(rotation);
	}
}
