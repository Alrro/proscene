/**
 *                     ProScene (version 1.2.0)      
 *    Copyright (c) 2010-2013 by National University of Colombia
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

package remixlab.proscene;

import processing.core.PApplet;
import processing.event.*;

/**
 * This class represents mouse shortcuts.
 * <p>
 * Mouse shortcuts can be of one out of two forms: 1. Mouse buttons (e.g., 'LEFT');
 * 2. Mouse button + Key combinations (e.g., 'RIGHT' + CTRL key).
 */
public final class MouseShortcut {	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((button == null) ? 0 : button.hashCode());
		result = prime * result + ((mask == null) ? 0 : mask.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MouseShortcut other = (MouseShortcut) obj;
		if (button == null) {
			if (other.button != null)
				return false;
		} else if (!button.equals(other.button))
			return false;
		if (mask == null) {
			if (other.mask != null)
				return false;
		} else if (!mask.equals(other.mask))
			return false;
		return true;
	}

	/**
	 * Defines a mouse shortcut from the given mouse button.
	 * 
	 * @param b mouse button
	 */
	public MouseShortcut(Integer b) {
		this(0, b);
	}

	/**
	 * Defines a mouse shortcut from the given modifier mask and mouse button combination.
	 * 
	 * @param m the mask 
	 * @param b mouse button
	 */
	public MouseShortcut(Integer m, Integer b) {
		this.button = b;
	  //this.mask = m;
		// /**
	  //TODO HACK see issue: https://github.com/processing/processing/issues/1693
		//ALT
		if(button == PApplet.CENTER) {
			mask = (Event.ALT | m);
		}
		//META
		else if(button == PApplet.RIGHT) {
    	mask = (Event.META | m);
		}
		else
			mask = m;
		// */
	}	
	
	/**
	 * Returns a textual description of this mouse shortcut.
	 *  
	 * @return description
	 */
	public String description() {
		return description(button);
	}	
	
	/**
	 * Internal. Low-level description() function.
	 */
	protected String description(Integer b) {
		String r = DesktopEvents.getModifiersText(mask);
		switch (b) {
		case PApplet.LEFT:
			r += (r.length() > 0) ? "+LEFT_BUTTON" : "LEFT_BUTTON";
			break;
		case PApplet.CENTER:
			r += (r.length() > 0) ? "+MIDDLE_BUTTON" : "MIDDLE_BUTTON";
			break;
		case PApplet.RIGHT:
			r += (r.length() > 0) ? "+RIGHT_BUTTON" : "RIGHT_BUTTON";
			break;			
		default:
			r += (r.length() > 0) ? "+NO_MOUSE_BUTTON" : "NO_MOUSE_BUTTON";
			break;
		}		
		return r;
	}
	
	/**
	 * Internal convenience funtion.
	 */
	protected String description(MouseEvent e) {
		return description(e.getButton());
	}	

	protected final Integer mask;
	protected final Integer button;
}