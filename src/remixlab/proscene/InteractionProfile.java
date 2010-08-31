package remixlab.proscene;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class InteractionProfile {
	/**
	taken from: http://wiki.processing.org/w/Multiple_key_presses
	multiplekeys taken from http://wiki.processing.org/index.php?title=Keep_track_of_multiple_key_presses
	@author Yonas Sandbæk http://seltar.wliia.org
	*/
	
	protected HashMap<Character, Scene.KeyboardAction> keyboardBinding;
	Scene scene;
	 
	// usage: 
	// if(checkKey("ctrl") && checkKey("s")) println("CTRL+S");  
	 
	boolean[] keys = new boolean[526];
	
	boolean checkKey(String k) {
	  for(int i = 0; i < keys.length; i++)
	    if(KeyEvent.getKeyText(i).toLowerCase().equals(k.toLowerCase())) return keys[i];  
	  return false;
	}
	/**
	void keyPressed() { 
	  keys[keyCode] = true;
	  println(KeyEvent.getKeyText(keyCode));
	}
	 
	void keyReleased() { 
	  keys[keyCode] = false; 
	}
	*/
	
	public InteractionProfile(Scene scn) {
		scene = scn;
		keyboardBinding = new HashMap<Character, Scene.KeyboardAction>();
	}
	
	// removes handling
	public void removeShortcut(Character key) {
		setShortcut(key, null);
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
	public void setShortcut(Character key, Scene.KeyboardAction action) {
		if (action != null)
			keyboardBinding.put(key, action);
		else
			keyboardBinding.remove(key);
	}

	/**
	 * Returns the keyboard shortcut associated to a given Keyboard {@code
	 * action}.
	 * <p>
	 * The returned keyboard shortcut may be null (if no Character is defined
	 * for keyboard {@code action}).
	 */
	public Scene.KeyboardAction shortcut(Character key) {
		return keyboardBinding.get(key);
	}
	
	public Character shortcut(Scene.KeyboardAction action) {
		Iterator<Map.Entry<Character, Scene.KeyboardAction>> it = keyboardBinding.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Character, Scene.KeyboardAction> pair = it.next();
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
	
	public boolean handleKeyboardAction(Character key) {
		Scene.KeyboardAction kba = shortcut(key);
		if (kba == null)
			return false;
		else {
			handleKeyboardAction(kba);
			return true;
		}
	}
	
	//TODO: should probably go at the Scene!
	protected void handleKeyboardAction(Scene.KeyboardAction id) {
		/**
		 * enum KeyboardAction { DRAW_AXIS, DRAW_GRID, CAMERA_MODE, CAMERA_TYPE,
		 * CAMERA_KIND, STEREO, ANIMATION, HELP, EDIT_CAMERA_PATH,
		 * FOCUS_INTERACTIVEFRAME, CONSTRAIN_FRAME }
		 */

		switch (id) {
		case DRAW_AXIS:
			scene.toggleAxisIsDrawn();
			break;
		case DRAW_GRID:
			scene.toggleGridIsDrawn();
			break;
		case CAMERA_MODE:
			scene.nextCameraMode();
			break;
		case CAMERA_TYPE:
			scene.toggleCameraType();
			break;
		case CAMERA_KIND:
			scene.toggleCameraKind();
			break;
		case HELP:
			scene.toggleHelpIsDrawn();
			break;
		case EDIT_CAMERA_PATH:
			scene.toggleCameraPathsAreDrawn();
			break;
		case FOCUS_INTERACTIVEFRAME:
			scene.toggleDrawInteractiveFrame();
			break;
		case CONSTRAIN_FRAME:
			scene.toggleDrawInteractiveFrame();
			break;
		}
	}
	
	abstract public void setDefaultShortcuts();
}
