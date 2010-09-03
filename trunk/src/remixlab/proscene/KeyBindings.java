package remixlab.proscene;

import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;

public class KeyBindings {
	public enum Arrow { UP, DOWN, LEFT, RIGHT }
	public enum Modifier { ALT, SHIFT, CONTROL, ALT_GRAPH }
	
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
		private KeyBindings getOuterType() {
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
		private KeyBindings getOuterType() {
			return KeyBindings.this;
		}
	}
	
	Scene scene;
	protected HashMap<KeyboardShortcut, Scene.KeyboardAction> keyboardBinding;
	
	/**
	private final HashMap<Integer, Modifier> modifierBindings;
	private final HashMap<Integer, Arrow> arrowBindings;
	private final HashMap<Character, Integer> characterBindings;
	*/
	
	public KeyBindings(Scene scn) {
		scene = scn;
		keyboardBinding = new HashMap<KeyboardShortcut, Scene.KeyboardAction>();
		/**
		modifierBindings = new HashMap<Integer, Modifier>();
		modifierBindings.put(PApplet.ALT, Modifier.ALT);
		modifierBindings.put(PApplet.CONTROL, Modifier.CONTROL);
		modifierBindings.put(PApplet.SHIFT, Modifier.SHIFT);		
		arrowBindings = new HashMap<Integer, Arrow>();
		arrowBindings.put(PApplet.UP, Arrow.UP);
		arrowBindings.put(PApplet.DOWN, Arrow.DOWN);
		arrowBindings.put(PApplet.LEFT, Arrow.LEFT);
		arrowBindings.put(PApplet.RIGHT, Arrow.RIGHT);
		characterBindings = new HashMap<Character, Integer>();
		characterBindings.put('0', KeyEvent.VK_0);
		characterBindings.put('1', KeyEvent.VK_1);
		characterBindings.put('2', KeyEvent.VK_2);
		characterBindings.put('3', KeyEvent.VK_3);
		characterBindings.put('4', KeyEvent.VK_4);
		characterBindings.put('5', KeyEvent.VK_5);
		characterBindings.put('6', KeyEvent.VK_6);
		characterBindings.put('7', KeyEvent.VK_7);
		characterBindings.put('8', KeyEvent.VK_8);
		characterBindings.put('9', KeyEvent.VK_9);
		characterBindings.put(' ', KeyEvent.VK_SPACE);
		characterBindings.put('a', KeyEvent.VK_A);
		characterBindings.put('b', KeyEvent.VK_B);		
		characterBindings.put('c', KeyEvent.VK_C);
		characterBindings.put('d', KeyEvent.VK_D);
		characterBindings.put('e', KeyEvent.VK_E);
		characterBindings.put('f', KeyEvent.VK_F);
		characterBindings.put('g', KeyEvent.VK_G);
		characterBindings.put('h', KeyEvent.VK_H);
		characterBindings.put('i', KeyEvent.VK_I);
		characterBindings.put('j', KeyEvent.VK_J);
		characterBindings.put('k', KeyEvent.VK_K);
		characterBindings.put('l', KeyEvent.VK_L);
		characterBindings.put('m', KeyEvent.VK_M);
		characterBindings.put('n', KeyEvent.VK_N);
		characterBindings.put('o', KeyEvent.VK_O);
		characterBindings.put('p', KeyEvent.VK_P);
		characterBindings.put('w', KeyEvent.VK_Q);
		characterBindings.put('r', KeyEvent.VK_R);
		characterBindings.put('s', KeyEvent.VK_S);
		characterBindings.put('t', KeyEvent.VK_T);
		characterBindings.put('u', KeyEvent.VK_U);
		characterBindings.put('v', KeyEvent.VK_V);
		characterBindings.put('w', KeyEvent.VK_W);
		characterBindings.put('x', KeyEvent.VK_X);
		characterBindings.put('y', KeyEvent.VK_Y);
		characterBindings.put('z', KeyEvent.VK_Z);		
		*/
	}
	
	//1.
	
	/**
	public void setShortcut(Character vKey, Integer modifier, Scene.KeyboardAction action) {		
		setShortcut(new KeyboardShortcut(characterBindings.get(Character.toLowerCase(vKey)), modifierBindings.get(modifier)), action);
	}
	*/
	
	public void setShortcut(Integer vKey, Modifier modifier, Scene.KeyboardAction action) {
		setShortcut(new KeyboardShortcut(vKey, modifier), action);
	}
	
	//2.
	/**
	public void setShortcut(Integer arrow, Scene.KeyboardAction action) {
		setShortcut(new KeyboardShortcut(arrowBindings.get(arrow)), action);
	}
	*/
	
	public void setShortcut(Arrow arrow, Scene.KeyboardAction action) {
		setShortcut(new KeyboardShortcut(arrow), action);
	}
	
	//3.
	public void setShortcut(Character key, Scene.KeyboardAction action) {
		setShortcut(new KeyboardShortcut(key), action);
	}
	
	//1.
	/**
	public void removeShortcut(Character vKey, Integer modifier) {		
		removeShortcut(new KeyboardShortcut(characterBindings.get(Character.toLowerCase(vKey)), modifierBindings.get(modifier)) );
	}	
	*/
	
	public void removeAllShortcuts() {
		keyboardBinding.clear();
	}
	
	public void removeShortcut(Integer vKey, Modifier modifier) {
		removeShortcut(new KeyboardShortcut(vKey, modifier) );
	}
	
	//2.
	/**
	public void removeShortcut(Integer arrow) {
		removeShortcut(new KeyboardShortcut(arrowBindings.get(arrow)));
	}
	*/
	
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
	private void setShortcut(KeyboardShortcut keyCombo, Scene.KeyboardAction action) {
		if (action != null)
			keyboardBinding.put(keyCombo, action);
		else
			keyboardBinding.remove(keyCombo);
	}
	
	public Scene.KeyboardAction shortcut(Character key) {
		return shortcut(new KeyboardShortcut(key));
	}
	
	public Scene.KeyboardAction shortcut(Integer vKey, Modifier modifier) {
		return shortcut(new KeyboardShortcut(vKey, modifier));
	}
	
	public Scene.KeyboardAction shortcut(Arrow arrow) {
		return shortcut(new KeyboardShortcut(arrow));
	}

	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no Character is defined
	 * for keyboard {@code action}).
	 */
	private Scene.KeyboardAction shortcut(KeyboardShortcut keyCombo) {
		return keyboardBinding.get(keyCombo);
	}
	
	public boolean isKeyInUse(KeyboardShortcut key) {
		return keyboardBinding.containsKey(key);
	}
	
	public boolean isActionBinded(Scene.KeyboardAction action) {
		return keyboardBinding.containsValue(action);
	}
	
	//reverse methods:	
	/**
	public KeyboardShortcut shortcut(Scene.KeyboardAction action) {
		Iterator<Map.Entry<KeyboardShortcut, Scene.KeyboardAction>> it = keyboardBinding.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<KeyboardShortcut, Scene.KeyboardAction> pair = it.next();
			if( pair.getValue() == action )
				return pair.getKey();    
	    }
		return null;
	}
	// */
	
	/**
	public Character shortcutCharacter(Scene.KeyboardAction action) {
		KeyboardShortcut cc = shortcut(action);
		if (cc != null)
			return shortcut(action).key;
		return null;
	}
	*/
	
	/**
	public KeyCombination shortcutModifier(Scene.KeyboardAction action) {
		KeyboardShortcut cc = shortcut(action);
		if (cc != null)
			return shortcut(action).keyCombination;
		return null;
	}
	*/
	
	/**
	public Integer shortcutArrow(Scene.KeyboardAction action) {
		KeyboardShortcut cc = shortcut(action);
		if (cc != null)
			return shortcut(action).arrow;
		return null;
	}
	*/
	
	/**
	public Arrow shortcutArrow(Scene.KeyboardAction action) {
		KeyboardShortcut cc = shortcut(action);
		if (cc != null)
			return shortcut(action).arrow;
		return null;
	}
	*/
	
	/**
	 private final HashMap<Integer, Modifier> modifierBindings;
	private final HashMap<Integer, Arrow> arrowBindings;
	private final HashMap<Character, Integer> characterBindings;
	 */
	
	/**
	public Integer modifierKey(Modifier modifier) {
		Iterator<Map.Entry<Integer, Modifier>> it = modifierBindings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Modifier> pair = it.next();
			if( pair.getValue() == modifier )
				return pair.getKey();    
	    }
		return null;
	}
	
	public Integer arrowKey(Arrow arrow) {
		Iterator<Map.Entry<Integer, Arrow>> it = arrowBindings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Arrow> pair = it.next();
			if( pair.getValue() == arrow )
				return pair.getKey();    
	    }
		return null;
	}
	
	public Character characterKey(Integer character) {
		Iterator<Map.Entry<Character, Integer>> it = characterBindings.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Character, Integer> pair = it.next();
			if( pair.getValue() == character )
				return pair.getKey();    
	    }
		return null;
	}
	*/
}
