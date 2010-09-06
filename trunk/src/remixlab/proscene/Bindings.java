package remixlab.proscene;

public abstract class Bindings<T> {
	public final class KeyCombination<K> {
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
			KeyCombination<?> other = (KeyCombination<?>) obj;
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
		public KeyCombination( K vKey, Scene.Modifier myModifier ) {
			modifier = myModifier;			
			virtualKey = vKey;
		}
		public final K virtualKey; //could be: 1. Integer(for virtual keys), 2. Scene.ClickActionButton, or 3. Scene.MouseActionButton
		public final Scene.Modifier modifier;
		private Bindings<T> getOuterType() {
			return Bindings.this;
		}
	}
	
	protected Scene scene;
	
	public Bindings(Scene scn) {
		scene = scn;
	}
	
	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no keycombo is defined
	 * for keyboard {@code action}).
	 */
	abstract protected Object binding(Object keyCombo);
	
	/**
	 * Defines the {@link #binding(Object)} that triggers a given action.
	 * 
	 * Here are some examples:
	 * 
	 * One or many shortcuts can be assigned to a given Scene action,
	 * but a given shortcut cannot be assigned to more than one action.
	 * If a shortcut is assigned to more than one action, only the last one
	 * would be active.
	 */
	abstract protected void setBinding(Object keyCombo, T action);
	
	abstract protected void removeBinding(Object keyCombo);
	
	abstract protected void removeAllBindings();
	
	abstract protected boolean isKeyInUse(Object key);
	
	abstract protected boolean isActionBinded(T action);
}