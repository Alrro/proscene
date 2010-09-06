package remixlab.proscene;

import java.util.HashMap;

public class MouseBindings<B,T> extends Bindings<T> {
	public final class ButtonKeyCombination {		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((button == null) ? 0 : button.hashCode());
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
			ButtonKeyCombination other = (ButtonKeyCombination) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (button == null) {
				if (other.button != null)
					return false;
			} else if (!button.equals(other.button))
				return false;
			if (modifier == null) {
				if (other.modifier != null)
					return false;
			} else if (!modifier.equals(other.modifier))
				return false;
			return true;
		}
		public ButtonKeyCombination( B myButton, Scene.Modifier myModifier ) {
			button = myButton;
			modifier = myModifier;		
		}
		public ButtonKeyCombination( B myButton ) {						
			button = myButton;
			modifier = null;
		}
		public final B button;
		public final Scene.Modifier modifier;
		private MouseBindings<B, T> getOuterType() {
			return MouseBindings.this;
		}
	}
	
	protected HashMap<ButtonKeyCombination, T> mouseBinding;
	
	public MouseBindings(Scene scn) {
		super(scn);		
		mouseBinding = new HashMap<ButtonKeyCombination, T>();
	}
	
	// /**
	@Override
	protected T binding(Object keyCombo) {
		return mouseBinding.get((ButtonKeyCombination)keyCombo);
	}
	
	@Override
	protected void setBinding(Object keyCombo, T action) {
		if (action != null)
			mouseBinding.put((ButtonKeyCombination)keyCombo, action);
		else
			mouseBinding.remove(keyCombo);
	}
	
	@Override
	protected void removeBinding(Object keyCombo) {
		setBinding((ButtonKeyCombination)keyCombo, null);
	}
	// */
	
	@Override
	protected void removeAllBindings() {
		mouseBinding.clear();
	}
	
	@Override
	protected boolean isKeyInUse(Object key) {
		return mouseBinding.containsKey((ButtonKeyCombination)key);
	}
	
	@Override
	protected boolean isActionBinded(T action) {
		return mouseBinding.containsValue(action);
	}
	
    /**
	protected T shortcut(ButtonKeyCombination keyCombo) {
		return mouseBinding.get(keyCombo);
	}
	
	protected void setShortcut(ButtonKeyCombination keyCombo, T action) {
		if (action != null)
			mouseBinding.put(keyCombo, action);
		else
			mouseBinding.remove(keyCombo);
	}
	
	protected void removeShortcut(ButtonKeyCombination keyCombo) {
		setShortcut(keyCombo, null);
	}
	// */
	
	//---
	
	public void setShortcut(B button, Scene.Modifier modifier, T action) {
		setBinding(new ButtonKeyCombination(button, modifier), action);
	}
	
	public void setShortcut(B button, T action) {
		setBinding(new ButtonKeyCombination(button), action);
	}
	
	public void removeShortcut(B button, Scene.Modifier modifier) {
		removeBinding(new ButtonKeyCombination(button, modifier));
	}
	
	public void removeShortcut(B button) {
		removeBinding(new ButtonKeyCombination(button));
	}
	
	public T shortcut(B button, Scene.Modifier modifier) {
		return binding(new ButtonKeyCombination(button, modifier));
	}
	
	public T shortcut(B button) {
		return binding(new ButtonKeyCombination(button));
	}
}
