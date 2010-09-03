package remixlab.proscene;

import java.util.HashMap;

public class KeyBindings<T> {
	public enum Arrow { UP, DOWN, LEFT, RIGHT }
	public enum Modifier { ALT, SHIFT, CONTROL, ALT_GRAPH }
	
	/**
	 * Internal class.
	 */
	public final class KeyCombination {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((modifier == null) ? 0 : modifier.hashCode());
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
			KeyCombination other = (KeyCombination) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
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
		public KeyCombination( Integer vKey, Modifier myModifier ) {
			modifier = myModifier;			
			virtualKey = vKey;
		}
		public final Integer virtualKey;
		public final Modifier modifier;
		private KeyBindings<T> getOuterType() {
			return KeyBindings.this;
		}
	}
	
	/**
	 * Internal class.
	 */
	public final class KeyboardShortcut {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((arrow == null) ? 0 : arrow.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime
					* result
					+ ((keyCombination == null) ? 0 : keyCombination.hashCode());
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
			if (!getOuterType().equals(other.getOuterType()))
				return false;
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
			if (keyCombination == null) {
				if (other.keyCombination != null)
					return false;
			} else if (!keyCombination.equals(other.keyCombination))
				return false;
			return true;
		}
		public KeyboardShortcut( Character myKey ) {
			key = myKey;
			arrow = null;
			keyCombination = null;
		}
		public KeyboardShortcut( Arrow myArrow ) {
			key = null;
			arrow = myArrow;
			keyCombination = null;
		}
		public KeyboardShortcut( Integer vKey, Modifier myModifier ) {
			key = null;
			arrow = null;
			keyCombination = new KeyCombination(vKey, myModifier); 
		}		
		public final Character key;
		public final Arrow arrow;
		public final KeyCombination keyCombination;
		private KeyBindings<T> getOuterType() {
			return KeyBindings.this;
		}
	}
	
	Scene scene;
	protected HashMap<KeyboardShortcut, T> keyboardBinding;
		
	public KeyBindings(Scene scn) {
		scene = scn;
		keyboardBinding = new HashMap<KeyboardShortcut, T>();
	}
	
	//1.
	public void setShortcut(Integer vKey, Modifier modifier, T action) {
		setShortcut(new KeyboardShortcut(vKey, modifier), action);
	}
	
	//2.
	public void setShortcut(Arrow arrow, T action) {
		setShortcut(new KeyboardShortcut(arrow), action);
	}
	
	//3.
	public void setShortcut(Character key, T action) {
		setShortcut(new KeyboardShortcut(key), action);
	}
	
	//1.	
	public void removeAllShortcuts() {
		keyboardBinding.clear();
	}
	
	public void removeShortcut(Integer vKey, Modifier modifier) {
		removeShortcut(new KeyboardShortcut(vKey, modifier) );
	}
	
	//2.
	
	public void removeShortcut(Arrow arrow) {
		removeShortcut(new KeyboardShortcut(arrow));
	}
	
	//3.
	public void removeShortcut(Character key) {
		removeShortcut(new KeyboardShortcut(key));
	}
	
	// removes handling
	public void removeShortcut(KeyboardShortcut keyCombo) {
		setShortcut(keyCombo, null);
	}

	/**
	 * Defines the shortcut() that triggers a given KeyboardAction.
	 * 
	 * Here are some examples: \code // Press 'Q' to exit application
	 * setShortcut(EXIT_VIEWER, Qt::Key_Q);
	 * 
	 * // Alt+M toggles camera mode setShortcut(CAMERA_MODE, Qt::ALT+Qt::Key_M);
	 * 
	 * // The DISPLAY_FPS action is disabled setShortcut(DISPLAY_FPS, 0);
	 * \endcode
	 * 
	 * Only one shortcut can be assigned to a given QGLViewer::KeyboardAction
	 * (new bindings replace previous ones). If several KeyboardAction are
	 * binded to the same shortcut, only one of them is active.
	 */	
	private void setShortcut(KeyboardShortcut keyCombo, T action) {
		if (action != null)
			keyboardBinding.put(keyCombo, action);
		else
			keyboardBinding.remove(keyCombo);
	}
	
	public T shortcut(Character key) {
		return shortcut(new KeyboardShortcut(key));
	}
	
	public T shortcut(Integer vKey, Modifier modifier) {
		return shortcut(new KeyboardShortcut(vKey, modifier));
	}
	
	public T shortcut(Arrow arrow) {
		return shortcut(new KeyboardShortcut(arrow));
	}

	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no Character is defined
	 * for keyboard {@code action}).
	 */
	private T shortcut(KeyboardShortcut keyCombo) {
		return keyboardBinding.get(keyCombo);
	}
	
	public boolean isKeyInUse(KeyboardShortcut key) {
		return keyboardBinding.containsKey(key);
	}
	
	public boolean isActionBinded(Scene.KeyboardAction action) {
		return keyboardBinding.containsValue(action);
	}
}
