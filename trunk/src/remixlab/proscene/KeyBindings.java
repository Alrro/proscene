package remixlab.proscene;

import java.util.HashMap;

public class KeyBindings<T> extends Bindings<T> {
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
		public KeyboardShortcut( Scene.Arrow myArrow ) {
			key = null;
			arrow = myArrow;
			keyCombination = null;
		}
		public KeyboardShortcut( Integer vKey, Scene.Modifier myModifier ) {
			key = null;
			arrow = null;
			keyCombination = new KeyCombination<Integer>(vKey, myModifier); 
		}		
		public final Character key;
		public final Scene.Arrow arrow;
		public final KeyCombination<Integer> keyCombination;
		private KeyBindings<T> getOuterType() {
			return KeyBindings.this;
		}
	}	
	
	protected HashMap<KeyboardShortcut, T> keyboardBinding;
		
	public KeyBindings(Scene scn) {
		super(scn);
		keyboardBinding = new HashMap<KeyboardShortcut, T>();
	}
	
	// /**
	@Override
	protected T binding(Object keyCombo) {
		return keyboardBinding.get((KeyboardShortcut)keyCombo);
	}
	
	@Override
	protected void setBinding(Object keyCombo, T action) {
		if (action != null)
			keyboardBinding.put((KeyboardShortcut)keyCombo, action);
		else
			keyboardBinding.remove((KeyboardShortcut)keyCombo);
	}
	
	@Override
	protected void removeBinding(Object keyCombo) {
		setBinding((KeyboardShortcut)keyCombo, null);
	}
	// */
	
	@Override
	protected void removeAllBindings() {
		keyboardBinding.clear();
	}
	
	@Override
	protected boolean isKeyInUse(Object key) {
		return keyboardBinding.containsKey((KeyboardShortcut)key);
	}
	
	@Override
	protected boolean isActionBinded(T action) {
		return keyboardBinding.containsValue(action);
	}
	
	/**
	protected T shortcut(KeyboardShortcut keyCombo) {
		return keyboardBinding.get(keyCombo);
	} 
	
	protected void setShortcut(KeyboardShortcut keyCombo, T action) {
		if (action != null)
			keyboardBinding.put(keyCombo, action);
		else
			keyboardBinding.remove(keyCombo);
	}
	
	protected void removeShortcut(KeyboardShortcut keyCombo) {
		setShortcut(keyCombo, null);
	}
	// */
	
	//---
	
	public void setShortcut(Integer vKey, Scene.Modifier modifier, T action) {
		setBinding(new KeyboardShortcut(vKey, modifier), action);
	}
	
	public void setShortcut(Scene.Arrow arrow, T action) {
		setBinding(new KeyboardShortcut(arrow), action);
	}
	
	public void setShortcut(Character key, T action) {
		setBinding(new KeyboardShortcut(key), action);
	}
		
	public void removeShortcut(Integer vKey, Scene.Modifier modifier) {
		removeBinding(new KeyboardShortcut(vKey, modifier) );
	}
	
	public void removeShortcut(Scene.Arrow arrow) {
		removeBinding(new KeyboardShortcut(arrow));
	}
	
	public void removeShortcut(Character key) {
		removeBinding(new KeyboardShortcut(key));
	}
	
	public T shortcut(Character key) {
		return binding(new KeyboardShortcut(key));
	}
	
	public T shortcut(Integer vKey, Scene.Modifier modifier) {
		return binding(new KeyboardShortcut(vKey, modifier));
	}
	
	public T shortcut(Scene.Arrow arrow) {
		return binding(new KeyboardShortcut(arrow));
	}	
}