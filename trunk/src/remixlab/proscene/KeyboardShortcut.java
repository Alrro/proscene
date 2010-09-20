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

public final class KeyboardShortcut {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrow == null) ? 0 : arrow.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((keyCombo == null) ? 0 : keyCombo.hashCode());
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
		KeyboardShortcut other = (KeyboardShortcut) obj;
		if (arrow == null) {
			if (other.arrow != null)
				return false;
		} else if (!arrow.equals(other.arrow))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (keyCombo == null) {
			if (other.keyCombo != null)
				return false;
		} else if (!keyCombo.equals(other.keyCombo))
			return false;
		return true;
	}

	public KeyboardShortcut(Character myKey) {
		key = myKey;
		arrow = null;
		keyCombo = null;
	}

	public KeyboardShortcut(Scene.Arrow myArrow) {
		key = null;
		arrow = myArrow;
		keyCombo = null;
	}

	public KeyboardShortcut(Integer vKey, Scene.Modifier myModifier) {
		key = null;
		arrow = null;
		keyCombo = new Shortcut<Integer>(vKey, myModifier);
	}

	public final Character key;
	public final Scene.Arrow arrow;
	public final Shortcut<Integer> keyCombo;
}