/**
 *                     ProScene (version 1.0.0-alpha1)      
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
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

public final class Shortcut<V> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modifier == null) ? 0 : modifier.hashCode());
		result = prime * result
				+ ((virtualKey == null) ? 0 : virtualKey.hashCode());
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
		Shortcut<?> other = (Shortcut<?>) obj;
		if (modifier == null) {
			if (other.modifier != null)
				return false;
		} else if (!modifier.equals(other.modifier))
			return false;
		if (virtualKey == null) {
			if (other.virtualKey != null)
				return false;
		} else if (!virtualKey.equals(other.virtualKey))
			return false;
		return true;
	}

	public Shortcut(V vKey, Scene.Modifier myModifier) {
		modifier = myModifier;
		virtualKey = vKey;
	}

	public Shortcut(V vKey) {
		modifier = null;
		virtualKey = vKey;
	}

	public final V virtualKey; // could be: 1. Integer(for virtual keys, seek
															// KeyboardShortcut), 2. Scene.Button
	public final Scene.Modifier modifier;
}