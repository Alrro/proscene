package remixlab.proscene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map; 

public abstract class InteractionProfile {
	public enum Arrow {LEFT, RIGHT, UP, DOWN}
	public enum Modifier {ALT, SHIFT, CONTROL, ALT_GRAPH}
	/**
	 * Internal class.
	 */
	public final class CharacterCombination {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((arrow == null) ? 0 : arrow.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result
					+ ((modifier == null) ? 0 : modifier.hashCode());
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
			CharacterCombination other = (CharacterCombination) obj;
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
			if (modifier == null) {
				if (other.modifier != null)
					return false;
			} else if (!modifier.equals(other.modifier))
				return false;
			return true;
		}
		public CharacterCombination( Arrow myArrow ) {
			key = null;
			modifier = null;
			arrow = myArrow;
		}
		public CharacterCombination( Character myKey ) {
			key = myKey;
			modifier = null;
			arrow = null;
		}
		public CharacterCombination( Character myKey, Modifier myModifier ) {
			key = myKey;
			modifier = myModifier;
			arrow = null;
		}
		public final Arrow arrow;
		public final Character key;
		public final Modifier modifier;
		private InteractionProfile getOuterType() {
			return InteractionProfile.this;
		}
	}
	
	protected HashMap<CharacterCombination, Scene.KeyboardAction> keyboardBinding;
	Scene scene;	 
	
	public InteractionProfile(Scene scn) {
		scene = scn;
		keyboardBinding = new HashMap<CharacterCombination, Scene.KeyboardAction>();
	}
	
	public void setShortcut(Character key, Modifier modifier, Scene.KeyboardAction action) {
		setShortcut(new CharacterCombination(key, modifier), action);
	}
	
	public void setShortcut(Arrow arrow, Scene.KeyboardAction action) {
		setShortcut(new CharacterCombination(arrow), action);
	}
	
	public void setShortcut(Character key, Scene.KeyboardAction action) {
		setShortcut(new CharacterCombination(key), action);
	}
	
	public void removeShortcut(Character key, Modifier modifier) {
		removeShortcut(new CharacterCombination(key, modifier));
	}
	
	public void removeShortcut(Arrow arrow) {
		removeShortcut(new CharacterCombination(arrow));
	}
	
	public void removeShortcut(Character key) {
		removeShortcut(new CharacterCombination(key));
	}
	
	// removes handling
	public void removeShortcut(CharacterCombination keyCombo) {
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
	private void setShortcut(CharacterCombination keyCombo, Scene.KeyboardAction action) {
		if (action != null)
			keyboardBinding.put(keyCombo, action);
		else
			keyboardBinding.remove(keyCombo);
	}
	
	public Scene.KeyboardAction shortcut(Character key) {
		return shortcut(new CharacterCombination(key));
	}
	
	public Scene.KeyboardAction shortcut(Character key, Modifier modifier) {
		return shortcut(new CharacterCombination(key, modifier));
	}
	
	public Scene.KeyboardAction shortcut(Arrow arrow) {
		return shortcut(new CharacterCombination(arrow));
	}

	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no Character is defined
	 * for keyboard {@code action}).
	 */
	private Scene.KeyboardAction shortcut(CharacterCombination keyCombo) {
		return keyboardBinding.get(keyCombo);
	}
	
	public Character shortcutCharacter(Scene.KeyboardAction action) {
		CharacterCombination cc = shortcut(action);
		if (cc != null)
			return shortcut(action).key;
		return null;
	}
	
	public Modifier shortcutModifier(Scene.KeyboardAction action) {
		CharacterCombination cc = shortcut(action);
		if (cc != null)
			return shortcut(action).modifier;
		return null;
	}
	
	public Arrow shortcutArrow(Scene.KeyboardAction action) {
		CharacterCombination cc = shortcut(action);
		if (cc != null)
			return shortcut(action).arrow;
		return null;
	}
	
	public CharacterCombination shortcut(Scene.KeyboardAction action) {
		Iterator<Map.Entry<CharacterCombination, Scene.KeyboardAction>> it = keyboardBinding.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<CharacterCombination, Scene.KeyboardAction> pair = it.next();
			if( pair.getValue() == action )
				return pair.getKey();    
	    }
		return null;
	}
	
	public boolean isBinded(Scene.KeyboardAction action) {
		if(shortcut(action) != null)
			return true;
		return false;
	}	
	
	abstract public void setDefaultShortcuts();
}
