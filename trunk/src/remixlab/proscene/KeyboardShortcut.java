/**
 *                     ProScene (version 1.0.0-beta2)      
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

import java.awt.event.KeyEvent;

/**
 * This class represents keyboard shortcuts.
 * <p>
 * Keyboard shortcuts can be of one out of three forms: 1. Characters (e.g., 'a');
 * 2. Virtual keys (e.g., right arrow key); or, 3. Key combinations (e.g., 'a' + CTRL key).
 */
public final class KeyboardShortcut {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((mask == null) ? 0 : mask.hashCode());
		result = prime * result + ((vKey == null) ? 0 : vKey.hashCode());
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
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (mask == null) {
			if (other.mask != null)
				return false;
		} else if (!mask.equals(other.mask))
			return false;
		if (vKey == null) {
			if (other.vKey != null)
				return false;
		} else if (!vKey.equals(other.vKey))
			return false;
		return true;
	}

	/**
	 * Internal use only. Maps characters to virtual keys.
	 */
	protected static int getVKey(char key) {
	  if(key == '0') return KeyEvent.VK_0;
	  if(key == '1') return KeyEvent.VK_1;
	  if(key == '2') return KeyEvent.VK_2;
	  if(key == '3') return KeyEvent.VK_3;
	  if(key == '4') return KeyEvent.VK_4;
	  if(key == '5') return KeyEvent.VK_5;
	  if(key == '6') return KeyEvent.VK_6;
	  if(key == '7') return KeyEvent.VK_7;
	  if(key == '8') return KeyEvent.VK_8;
	  if(key == '9') return KeyEvent.VK_9;		
	  if((key == 'a')||(key == 'A')) return KeyEvent.VK_A;
	  if((key == 'b')||(key == 'B')) return KeyEvent.VK_B;
	  if((key == 'c')||(key == 'C')) return KeyEvent.VK_C;
	  if((key == 'd')||(key == 'D')) return KeyEvent.VK_D;
	  if((key == 'e')||(key == 'E')) return KeyEvent.VK_E;
	  if((key == 'f')||(key == 'F')) return KeyEvent.VK_F;
	  if((key == 'g')||(key == 'G')) return KeyEvent.VK_G;
	  if((key == 'h')||(key == 'H')) return KeyEvent.VK_H;
	  if((key == 'i')||(key == 'I')) return KeyEvent.VK_I;
	  if((key == 'j')||(key == 'J')) return KeyEvent.VK_J;
	  if((key == 'k')||(key == 'K')) return KeyEvent.VK_K;
	  if((key == 'l')||(key == 'L')) return KeyEvent.VK_L;
	  if((key == 'm')||(key == 'M')) return KeyEvent.VK_M;
	  if((key == 'n')||(key == 'N')) return KeyEvent.VK_N;
	  if((key == 'o')||(key == 'O')) return KeyEvent.VK_O;
	  if((key == 'p')||(key == 'P')) return KeyEvent.VK_P;
	  if((key == 'q')||(key == 'Q')) return KeyEvent.VK_Q;
	  if((key == 'r')||(key == 'R')) return KeyEvent.VK_R;
	  if((key == 's')||(key == 'S')) return KeyEvent.VK_S;
	  if((key == 't')||(key == 'T')) return KeyEvent.VK_T;
	  if((key == 'u')||(key == 'U')) return KeyEvent.VK_U;
	  if((key == 'v')||(key == 'V')) return KeyEvent.VK_V;
	  if((key == 'w')||(key == 'W')) return KeyEvent.VK_W;
	  if((key == 'x')||(key == 'X')) return KeyEvent.VK_X;
	  if((key == 'y')||(key == 'Y')) return KeyEvent.VK_Y;
	  if((key == 'z')||(key == 'Z')) return KeyEvent.VK_Z;
	  return -1;
	}

	/**
	 * Defines a keyboard shortcut from the given character.
	 *  
	 * @param k the character that defines the keyboard shortcut.
	 */
	public KeyboardShortcut(Character k) {
		this.key = k;
		this.mask = null;
		this.vKey = null;
	}
	
	/**
	 * Defines a keyboard shortcut from the given virtual key.
	 * 
	 * @param vk the virtual key that defines the keyboard shortcut.
	 */
	public KeyboardShortcut(Integer vk) {
		this(0, vk);
	}

	/**
	 * Defines a keyboard shortcut from the given modifier mask and virtual key combination.
	 * 
	 * @param m the mask 
	 * @param vk the virtual key that defines the keyboard shortcut.
	 */
	public KeyboardShortcut(Integer m, Integer vk) {
		this.mask = m;
		this.vKey = vk;
		this.key = null;
	}	
	
	/**
	 * Returns a textual description of this keyboard shortcut.
	 *  
	 * @return description
	 */
	public String description() {
		String description = new String();
		if(key != null)
			description = key.toString();
		else {
			if(mask == 0)
				description = KeyEvent.getKeyText(vKey);
			else
				description = KeyEvent.getModifiersExText(mask) + "+" + KeyEvent.getKeyText(vKey);
		}			
		return description;
	}

	private final Integer mask;
	private final Integer vKey;
	private final Character key;		
}