package remixlab.proscene;
import java.util.HashMap;

public class Bindings<K, A> {	
	protected Scene scene;
	protected HashMap<K, A> bindings;
	
	public Bindings(Scene scn) {
		scene = scn;
		bindings = new HashMap<K, A>();
	}
	
	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no keycombo is defined
	 * for keyboard {@code action}).
	 */
	protected A binding(K key) {
		return bindings.get(key);
	}
	
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
	protected void setBinding(K key, A action) {
		bindings.put(key, action);
	}
	
	protected void removeBinding(K key) {
		bindings.remove(key);
	}
	
	protected void removeAllBindings() {
		bindings.clear();
	}
	
	protected boolean isKeyInUse(K key) {
		return bindings.containsKey(key);
	}
	
	protected boolean isActionBinded(A action) {
		return bindings.containsValue(action);
	}
}