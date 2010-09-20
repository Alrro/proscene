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

public class ClickShortcut {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((buttonCombo == null) ? 0 : buttonCombo.hashCode());
		result = prime * result
				+ ((numberOfClicks == null) ? 0 : numberOfClicks.hashCode());
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
		ClickShortcut other = (ClickShortcut) obj;
		if (buttonCombo == null) {
			if (other.buttonCombo != null)
				return false;
		} else if (!buttonCombo.equals(other.buttonCombo))
			return false;
		if (numberOfClicks == null) {
			if (other.numberOfClicks != null)
				return false;
		} else if (!numberOfClicks.equals(other.numberOfClicks))
			return false;
		return true;
	}

	public ClickShortcut(Scene.Button button) {
		this(button, 1);
	}

	public ClickShortcut(Scene.Button button, Scene.Modifier myModifier) {
		this(button, myModifier, 1);
	}

	public ClickShortcut(Scene.Button button, Integer clicks) {
		// TODO 0 < numberOfClicks < 3(?)
		buttonCombo = new Shortcut<Scene.Button>(button);
		numberOfClicks = clicks;
	}

	public ClickShortcut(Scene.Button button, Scene.Modifier myModifier,
			Integer clicks) {
		// TODO 0 < numberOfClicks < 3(?)
		buttonCombo = new Shortcut<Scene.Button>(button, myModifier);
		numberOfClicks = clicks;
	}

	public final Shortcut<Scene.Button> buttonCombo;
	public final Integer numberOfClicks;
}
