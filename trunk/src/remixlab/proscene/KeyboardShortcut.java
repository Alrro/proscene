package remixlab.proscene;

public final class KeyboardShortcut {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrow == null) ? 0 : arrow.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((keyCombo == null) ? 0 : keyCombo.hashCode());
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
	public KeyboardShortcut( Character myKey ) {
		key = myKey;
		arrow = null;
		keyCombo = null; 
	}
	public KeyboardShortcut( Scene.Arrow myArrow ) {
		key = null;
		arrow = myArrow;
		keyCombo = null;
	}
	public KeyboardShortcut( Integer vKey, Scene.Modifier myModifier ) {
		key = null;
		arrow = null;
		keyCombo = new Shortcut<Integer>(vKey, myModifier); 
	}		
	public final Character key;
	public final Scene.Arrow arrow;
	public final Shortcut<Integer> keyCombo;
}