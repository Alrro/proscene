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

import java.util.HashMap;

public class Bindings<K, A> {
	protected Scene scene;
	protected HashMap<K, A> map;

	public Bindings(Scene scn) {
		scene = scn;
		map = new HashMap<K, A>();
	}

	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code action}
	 * .
	 * <p>
	 * The returned keyboard shortcut may be null (if no keycombo is defined for
	 * keyboard {@code action}).
	 */
	protected A binding(K key) {
		return map.get(key);
	}

	/**
	 * Defines the {@link #binding(Object)} that triggers a given action.
	 * 
	 * Here are some examples:
	 * 
	 * One or many shortcuts can be assigned to a given Scene action, but a given
	 * shortcut cannot be assigned to more than one action. If a shortcut is
	 * assigned to more than one action, only the last one would be active.
	 */
	protected void setBinding(K key, A action) {
		map.put(key, action);
	}

	protected void removeBinding(K key) {
		map.remove(key);
	}

	protected void removeAllBindings() {
		map.clear();
	}

	protected boolean isShortcutInUse(K key) {
		return map.containsKey(key);
	}

	protected boolean isActionMapped(A action) {
		return map.containsValue(action);
	}
}