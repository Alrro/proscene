package remixlab.proscene;

public final class Shortcut<V> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
	public Shortcut( V vKey, Scene.Modifier myModifier ) {
		modifier = myModifier;			
		virtualKey = vKey;
	}
	public Shortcut( V vKey ) {
		modifier = null;			
		virtualKey = vKey;
	}
	public final V virtualKey; //could be: 1. Integer(for virtual keys), 2. Scene.Button
	public final Scene.Modifier modifier;
}