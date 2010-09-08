package remixlab.proscene;
import java.util.HashMap;

public class ShortcutMappings<K, A> {	
	protected Scene scene;
	protected HashMap<K, A> map;
	
	public ShortcutMappings(Scene scn) {
		scene = scn;
		map = new HashMap<K, A>();
	}
	
	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no keycombo is defined
	 * for keyboard {@code action}).
	 */
	protected A binding(K key) {
		return map.get(key);
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
	protected void setMapping(K key, A action) {
		map.put(key, action);
	}
	
	protected void removeMapping(K key) {
		map.remove(key);
	}
	
	protected void removeAllMappings() {
		map.clear();
	}
	
	protected boolean isShortcutInUse(K key) {
		return map.containsKey(key);
	}
	
	protected boolean isActionMapped(A action) {
		return map.containsValue(action);
	}
}