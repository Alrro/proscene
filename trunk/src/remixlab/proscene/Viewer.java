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

import processing.core.*;

@SuppressWarnings("serial")
public class Viewer extends PApplet {
	// Must be set by WindowOwner 'owning' this PApplet
	public WindowOwner owner;
	// The applet width and height
	public int appWidth, appHeight;
	// applet graphics mode e.g. JAVA2D, P3D etc.
	public String mode;
	
	public Scene scene;
	
	public boolean config;
	
	public Viewer(String mode){
		super();
		this.mode = mode;
		config = true;
	}
	
	/**
	 * INTERNAL USE ONLY <br>
	 * The PApplet setup method to intialise the drawing surface
	 */
	public void setup() {
		size(appWidth, appHeight, mode);
		scene = new Scene(this);
	}
	
	/**
	 * INTERNAL USE ONLY <br>
	 * Use addDrawHandler in GWindow to activate this method
	 */
	public void draw() {
		// /**
		if (owner.getSceneConfigHandlerObject() != null && config) {
			try {
				owner.getSceneConfigHandlerMethod().invoke(owner.getSceneConfigHandlerObject(), new Object[] { scene } );
				config = false;
			} catch (Exception e) {
				
			}
		}
		// */
		
		if(owner.getDrawHandlerObject() != null){
			try {
				owner.getDrawHandlerMethod().invoke(owner.getDrawHandlerObject(), new Object[] { this } );
			} catch (Exception e) {
				
			}
		}
	}
	
	public Scene getScene() {
		return scene;
	}	
}
