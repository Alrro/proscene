/**
 * This java package provides classes to ease the creation of
 * interactive 3D scenes in Processing.
 * @author Jean Pierre Charalambos, A/Prof. National University of Colombia
 * (http://disi.unal.edu.co/profesores/pierre/, http://www.unal.edu.co/).
 * @version 0.9.0
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

import processing.core.PVector;

/**
 * Interface for InteractiveFrame objects that are to be tracked by a Camera.
 */

public interface Trackable {
	/**
	 * Returns the position of the Camera that tracks the InteractiveFrame object
	 * in the world coordinate system.
	 *   
	 * @return PVector holding the camera position defined in the world coordinate system. 
	 */
	public PVector cameraPosition();
	
	public PVector upVector();
	
	public PVector target();
	
	/**
	 * Computes the camera position according to some specific InteractiveFrame
	 * parameters which depends on the type of interaction that is to be implemented.
	 * <p>
	 * It is responsibility of the object implementing this interface to update the
	 * camera position by properly calling this method. 
	 */
	public void computeCameraPosition();
}
