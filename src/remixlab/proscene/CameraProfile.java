/**
 *                     ProScene (version 1.0.0-beta1)      
 *             Copyright (c) 2010 by RemixLab, DISI-UNAL      
 *            http://www.disi.unal.edu.co/grupos/remixlab/
 *                           
 * This java package provides classes to ease the creation of interactive 3D
 * scenes in Processing.
 * 
 * @author Jean Pierre Charalambos
 * 
 * This source file is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * A copy of the GNU General Public License is available on the World Wide Web
 * at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by
 * writing to the Free Software Foundation, 51 Franklin Street, Suite 500
 * Boston, MA 02110-1335, USA.
 */

package remixlab.proscene;

import java.awt.event.*;
import java.util.Map.Entry;

import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.MouseAction;

/**
 * This class encapsulates a set of camera and keyboard bindings which together
 * represent a "camera mode".
 * <p>
 * Once instantiated, to use a camera profile you need to {@link #register()} it
 * ({@link #unregister()} performs the inverse operation).
 * <p>
 * This class also provide some useful preset bindings which represents some common
 * "camera modes": ARCBALL, WHEELED_ARCBALL, FIRST_PERSON, THIRD_PERSON, and CUSTOM
 * (see {@link #mode()}). A custom camera profiles represents an empty set of camera and
 * keyboard shortcuts. Most of the methods of this class provide means to define
 * (or overwrite) custom (or preset bindings). 
 * <p>
 * The Scene provides proper methods to manage (and also register and unregister)
 * camera profiles, such as {@link remixlab.proscene.Scene#cameraProfile(String)},
 * {@link remixlab.proscene.Scene#currentCameraProfile()},
 * {@link remixlab.proscene.Scene#setCurrentCameraProfile(CameraProfile)}, among others.
 * <p>
 * <b>Attention:</b> Every instantiated scene provides two camera profiles by default:
 * WHEELED_ARCBALL and FIRST_PERSON. If you define an
 * {@link remixlab.proscene.Scene#avatar()} (for instance with 
 * {@link remixlab.proscene.Scene#setAvatar(Trackable)}) then you should register at
 * least one THIRD_PERSON camera profile to your Scene.
 */
public class CameraProfile {
	public enum Mode {ARCBALL, WHEELED_ARCBALL, FIRST_PERSON, THIRD_PERSON, CUSTOM}
	protected String name;
	protected Scene scene;
	protected Mode mode;
	protected Bindings<KeyboardShortcut, Scene.CameraKeyboardAction> keyboard;
	protected Bindings<Integer, Scene.MouseAction> cameraActions;
	protected Bindings<Integer, Scene.MouseAction> iFrameActions;
	// C L I C K A C T I O N S
	protected Bindings<ClickShortcut, ClickAction> clickActions;
	protected Bindings<Integer, Scene.MouseAction> cameraWheelActions;
	protected Bindings<Integer, Scene.MouseAction> iFrameWheelActions;
	
	/**
	 * Convenience constructor that simply calls {@code this(scn, n, Mode.CUSTOM)}.
	 */
	public CameraProfile(Scene scn, String n) {
		this(scn, n, Mode.CUSTOM);
	}

	/**
	 * Main constructor.
	 * <p>
	 * This constructor takes the scene instance, the custom name of the camera profile
	 * and an enum type defining its mode which can be: ARCBALL, WHEELED_ARCBALL,
	 * FIRST_PERSON, THIRD_PERSON, CUSTOM.
	 * <p>
	 * If you define a {@link remixlab.proscene.Scene#avatar()} (for instance with 
	 * {@link remixlab.proscene.Scene#setAvatar(Trackable)}) then you should register at
	 * least one THIRD_PERSON camera profile to your Scene. 
	 * <p>
	 * Except for CUSTOM, each camera mode loads some preset camera and keyboard shortcut
	 * bindings. A custom camera profiles represents an empty set of camera and keyboard
	 * shortcuts. 
	 * 
	 * @param scn the scene object
	 * @param n the camera profile name
	 * @param m the camera profile mode
	 */
	public CameraProfile(Scene scn, String n, Mode m) {
	  //TODO: add descriptions for each preset mode!
		scene = scn;		
		name = n;
		mode = m;
		keyboard = new Bindings<KeyboardShortcut, Scene.CameraKeyboardAction>(scene);
		cameraActions = new Bindings<Integer, Scene.MouseAction>(scene);
		iFrameActions = new Bindings<Integer, Scene.MouseAction>(scene);		
		clickActions = new Bindings<ClickShortcut, Scene.ClickAction>(scene);
		
		cameraWheelActions = new Bindings<Integer, Scene.MouseAction>(scene);
		iFrameWheelActions = new Bindings<Integer, Scene.MouseAction>(scene);		
		scene.parent.addMouseWheelListener( scene.dE );
		
		switch (mode) {
		case ARCBALL:
			arcballDefaultShortcuts();
			break;
		case WHEELED_ARCBALL:			
			arcballDefaultShortcuts();
			
			setCameraWheelShortcut( MouseAction.ZOOM );
			/**
			setCameraWheelShortcut( InputEvent.CTRL_DOWN_MASK, MouseAction.MOVE_FORWARD );
			setCameraWheelShortcut( InputEvent.ALT_DOWN_MASK, MouseAction.MOVE_BACKWARD );
			*/
			//should work only iFrame is an instance of drivable
			setIFrameWheelShortcut( MouseAction.ZOOM );
			/**
			setIFrameWheelShortcut( InputEvent.CTRL_DOWN_MASK, MouseAction.MOVE_FORWARD );
			setIFrameWheelShortcut( InputEvent.ALT_DOWN_MASK, MouseAction.MOVE_BACKWARD );
			*/
			
			//scene.parent.addMouseWheelListener( scene.dE );
			break;
		case FIRST_PERSON:
			setCameraShortcut(InputEvent.BUTTON1_DOWN_MASK, Scene.MouseAction.MOVE_FORWARD);
			setCameraShortcut(InputEvent.BUTTON2_DOWN_MASK, Scene.MouseAction.LOOK_AROUND);
			setCameraShortcut(InputEvent.BUTTON3_DOWN_MASK, Scene.MouseAction.MOVE_BACKWARD);
			setCameraShortcut((InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ), Scene.MouseAction.ROLL);
			setCameraShortcut((InputEvent.BUTTON3_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ),	Scene.MouseAction.DRIVE);
			setIFrameShortcut(InputEvent.BUTTON1_DOWN_MASK, Scene.MouseAction.ROTATE);
			setIFrameShortcut(InputEvent.BUTTON2_DOWN_MASK, Scene.MouseAction.ZOOM);
			setIFrameShortcut(InputEvent.BUTTON3_DOWN_MASK, Scene.MouseAction.TRANSLATE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_CAMERA_FLY_SPEED);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_CAMERA_FLY_SPEED);

			setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
			setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
			break;
		case THIRD_PERSON:
			setIFrameShortcut(InputEvent.BUTTON1_DOWN_MASK, Scene.MouseAction.MOVE_FORWARD);
			setIFrameShortcut(InputEvent.BUTTON2_DOWN_MASK, Scene.MouseAction.LOOK_AROUND);
			setIFrameShortcut(InputEvent.BUTTON3_DOWN_MASK, Scene.MouseAction.MOVE_BACKWARD);
			setIFrameShortcut((InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ), Scene.MouseAction.ROLL);
			setIFrameShortcut((InputEvent.BUTTON3_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ), Scene.MouseAction.DRIVE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_AVATAR_FLY_SPEED);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_AVATAR_FLY_SPEED);
			setShortcut('a', Scene.CameraKeyboardAction.INCREASE_AZYMUTH);
			setShortcut('A', Scene.CameraKeyboardAction.DECREASE_AZYMUTH);
			setShortcut('i', Scene.CameraKeyboardAction.INCREASE_INCLINATION);
			setShortcut('I', Scene.CameraKeyboardAction.DECREASE_INCLINATION);
			setShortcut('t', Scene.CameraKeyboardAction.INCREASE_TRACKING_DISTANCE);
			setShortcut('T', Scene.CameraKeyboardAction.DECREASE_TRACKING_DISTANCE);
			break;
		case CUSTOM:
			break;
		}
	}
	
	/**
	 * Internal use. Called by the constructor by ARCBALL and WHEELED_ARCBALL modes.
	 */
	private void arcballDefaultShortcuts() {
		setShortcut(KeyEvent.VK_RIGHT, Scene.CameraKeyboardAction.MOVE_CAMERA_RIGHT);
		setShortcut(KeyEvent.VK_LEFT, Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
		setShortcut(KeyEvent.VK_UP, Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
		setShortcut(KeyEvent.VK_DOWN, Scene.CameraKeyboardAction.MOVE_CAMERA_DOWN);

		setCameraShortcut(InputEvent.BUTTON1_DOWN_MASK, Scene.MouseAction.ROTATE);
		setCameraShortcut(InputEvent.BUTTON2_DOWN_MASK, Scene.MouseAction.ZOOM);
		setCameraShortcut(InputEvent.BUTTON3_DOWN_MASK, Scene.MouseAction.TRANSLATE);
		setIFrameShortcut(InputEvent.BUTTON1_DOWN_MASK, Scene.MouseAction.ROTATE);
		setIFrameShortcut(InputEvent.BUTTON2_DOWN_MASK, Scene.MouseAction.ZOOM);
		setIFrameShortcut(InputEvent.BUTTON3_DOWN_MASK, Scene.MouseAction.TRANSLATE);

		//setCameraShortcut( (InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK), Scene.MouseAction.ZOOM_ON_REGION);
		setCameraShortcut( (InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), Scene.MouseAction.ZOOM_ON_REGION);
		setCameraShortcut( (InputEvent.BUTTON1_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), Scene.MouseAction.SCREEN_ROTATE);

		setShortcut('+', Scene.CameraKeyboardAction.INCREASE_ROTATION_SENSITIVITY);
		setShortcut('-', Scene.CameraKeyboardAction.DECREASE_ROTATION_SENSITIVITY);

		setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
		setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
		
		setClickShortcut(Scene.Button.LEFT, 2, ClickAction.ALIGN_CAMERA);
		setClickShortcut(Scene.Button.MIDDLE, 2, ClickAction.SHOW_ALL);
		setClickShortcut(Scene.Button.RIGHT, 2, ClickAction.ZOOM_TO_FIT);
		//setClickShortcut((InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK), Scene.Button.RIGHT, 2, ClickAction.ZOOM_TO_FIT);
	}
	
  // 1. General stuff
	
	/**
	 * Returns the camera profile mode.
	 * <p>
	 * The camera profile mode is defined at instantiation time and cannot be modified later.
	 * 
	 * @see #CameraProfile(Scene, String, Mode)
	 */
	public Mode mode() {
		return mode;
	}
	
	/**
	 * Returns the camera profile name.
	 * <p>
	 * The camera profile name is defined at instantiation time and cannot be modified later.
	 * 
	 * @see #CameraProfile(Scene, String, Mode)
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns true if the camera profile is registered at the scene and false otherwise.
	 * 
	 * @see #register()
	 * @see #unregister()
	 */
	public boolean isRegistered() {
		return scene.isCameraProfileRegistered(this);
	}

	/**
	 * Registers the camera profile at the scene. Returns true if registration succeeded
	 * (if there's a registered camera profile with the same name, registration will fail). 
	 * <p> 
	 * Convenience wrapper function that simple calls {@code scene.registerCameraProfile(this)}. 
	 */
	public boolean register() {
		return scene.registerCameraProfile(this);
	}

	/**
	 * Unregisters the camera profile from the scene. 
	 * <p> 
	 * Convenience wrapper function that simple calls {@code scene.unregisterCameraProfile(this)}. 
	 */
	public void unregister() {
		scene.unregisterCameraProfile(this);
	}
	
	// 2. AWT input event parsing, i.e., converts events to actions.
	
	/**
	 * Internal method. Parses the event to convert it to a Scene.MouseAction. Returns
	 * {@link remixlab.proscene.Scene.MouseAction#NO_MOUSE_ACTION} if no action was found.
	 * <p>
	 * Called by {@link remixlab.proscene.DesktopEvents#mousePressed(MouseEvent)}.
	 */
	protected MouseAction cameraMouseAction(MouseEvent e) {
		MouseAction camMouseAction = cameraShortcut( e.getModifiersEx() );
		if (camMouseAction == null)
			camMouseAction = MouseAction.NO_MOUSE_ACTION;
		return camMouseAction;
	}
	
	/**
	 * Internal method. Parses the event to convert it to a Scene.MouseAction. Returns
	 * {@link remixlab.proscene.Scene.MouseAction#NO_MOUSE_ACTION} if no action was found.
	 * <p>
	 * Called by {@link remixlab.proscene.DesktopEvents#mousePressed(MouseEvent)}.
	 */
	protected MouseAction iFrameMouseAction(MouseEvent e) {
		MouseAction iFrameMouseAction = iFrameShortcut( e.getModifiersEx() );
		if (iFrameMouseAction == null)
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		return iFrameMouseAction;
	}
	
	/**
	 * Internal method. Parses the event to convert it to a Scene.MouseAction. Returns
	 * {@link remixlab.proscene.Scene.MouseAction#NO_MOUSE_ACTION} if no action was found.
	 * <p>
	 * Called by {@link remixlab.proscene.DesktopEvents#mouseWheelMoved(MouseWheelEvent)}.
	 */
	protected MouseAction cameraWheelMouseAction(MouseWheelEvent e) {
		MouseAction wMouseAction = cameraWheelShortcut(e.getModifiersEx());
		if (wMouseAction == null)
			wMouseAction = MouseAction.NO_MOUSE_ACTION;
		return wMouseAction;
	}
	
	/**
	 * Internal method. Parses the event to convert it to a Scene.MouseAction. Returns
	 * {@link remixlab.proscene.Scene.MouseAction#NO_MOUSE_ACTION} if no action was found.
	 * <p>
	 * Called by {@link remixlab.proscene.DesktopEvents#mouseWheelMoved(MouseWheelEvent)}.
	 */
	protected MouseAction iFrameWheelMouseAction(MouseWheelEvent e) {
		MouseAction fMouseAction = iFrameWheelShortcut( e.getModifiersEx() );
		if (fMouseAction == null)
			fMouseAction = MouseAction.NO_MOUSE_ACTION;
		return fMouseAction;
	}
	
	/**
	 * Returns a String containing the camera mouse bindings' descriptions.
	 */
	public String cameraMouseBindingsDescription() {
		String description = new String();
		for (Entry<Integer, MouseAction> entry : cameraActions.map.entrySet())
      description += KeyEvent.getModifiersExText(entry.getKey()) + " -> " + entry.getValue().description() + "\n";
		return description;
	}
	
	/**
	 * Returns a String containing the interactive frame mouse bindings' descriptions.
	 */
	public String iFrameMouseBindingsDescription() {
		String description = new String();
		for (Entry<Integer, MouseAction> entry : iFrameActions.map.entrySet())
      description += KeyEvent.getModifiersExText(entry.getKey()) + " -> " + entry.getValue().description() + "\n";
		return description;
	}
	
	/**
	 * Returns a String containing the camera mouse-click bindings' descriptions.
	 */
	public String mouseClickBindingsDescription() {
		String description = new String();
		for (Entry<ClickShortcut, ClickAction> entry : clickActions.map.entrySet())
      description += entry.getKey().description() + " -> " + entry.getValue().description() + "\n";
		return description;
	}
	
	/**
	 * Returns a String containing the camera keyboard bindings' descriptions.
	 */
	public String keyboardBindingsDescription() {
		String description = new String();
		for (Entry<KeyboardShortcut, Scene.CameraKeyboardAction> entry : keyboard.map.entrySet())
      description += entry.getKey().description() + " -> " + entry.getValue().description() + "\n";
		return description;
	}
	
	/**
	 * Returns a String containing the camera mouse wheel bindings' descriptions.
	 */
	public String cameraWheelBindingsDescription() {
		String description = new String();
		for (Entry<Integer, Scene.MouseAction> entry : cameraWheelActions.map.entrySet()) {
			if (KeyEvent.getModifiersExText(entry.getKey()).length() != 0 )
				description += "Wheel " + KeyEvent.getModifiersExText(entry.getKey()) + " -> " + entry.getValue().description() + "\n";
			else
				description += "Wheel -> " + entry.getValue().description() + "\n";
		}
		return description;
	}
	
	/**
	 * Returns a String containing the interactive frame mouse wheel bindings' descriptions.
	 */
	public String iFrameWheelBindingsDescription() {
		String description = new String();
		for (Entry<Integer, Scene.MouseAction> entry : iFrameWheelActions.map.entrySet())
			if (KeyEvent.getModifiersExText(entry.getKey()).length() != 0 )
				description += "Wheel " + KeyEvent.getModifiersExText(entry.getKey()) + " -> " + entry.getValue().description() + "\n";
			else
				description += "Wheel -> " + entry.getValue().description() + "\n";
		return description;
	}

	// 3. Bindings
	
	// 3.1 keyboard wrappers
	
	/**
	 * Defines a camera keyboard shortcut to bind the given action.
	 * 
	 * @param key shortcut
	 * @param action action to be binded
	 */
	public void setShortcut(Character key, CameraKeyboardAction action) {
		if ( isKeyInUse(key) ) {
			CameraKeyboardAction a = shortcut(key);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		keyboard.setBinding(new KeyboardShortcut(key), action);
	}
	
	/**
	 * Defines a camera keyboard shortcut to bind the given action.
	 * <p>
	 * High-level version of {@link #setShortcut(Integer, Integer, Scene.CameraKeyboardAction)}.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * @param action action to be binded
	 * 
	 * @see #setShortcut(Integer, Integer, Scene.CameraKeyboardAction)
	 */
	public void setShortcut(Integer mask, Character key, CameraKeyboardAction action) {
		setShortcut(mask, KeyboardShortcut.getVKey(key), action);
	}
	
	/**
	 * Defines a camera keyboard shortcut to bind the given action.
	 * <p>
	 * Low-level version of {@link #setShortcut(Integer, Character, Scene.CameraKeyboardAction)}.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param vKey coded key defining the shortcut
	 * @param action action to be binded
	 * 
	 * @see #setShortcut(Integer, Character, Scene.CameraKeyboardAction)
	 */
	public void setShortcut(Integer mask, Integer vKey, CameraKeyboardAction action) {
		if ( isKeyInUse(mask, vKey) ) {
			CameraKeyboardAction a = shortcut(mask, vKey);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		keyboard.setBinding(new KeyboardShortcut(mask, vKey), action);
	}
	
	/**
	 * Defines a camera keyboard shortcut to bind the given action.
	 * 
	 * @param vKey coded key (such {@link remixlab.proscene.Scene.Arrow#UP}
	 *             that defines the shortcut
	 * @param action action to be binded
	 */
	public void setShortcut(Integer vKey, CameraKeyboardAction action) {
		if ( isKeyInUse(vKey) ) {
			CameraKeyboardAction a = shortcut(vKey);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		keyboard.setBinding(new KeyboardShortcut(vKey), action);
	}

	/**
	 * Removes all camera keyboard shortcuts.
	 */
	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllBindings();
	}
	
	/**
	 * Removes the camera keyboard shortcut.
	 * 
	 * @param key shortcut
	 */
	public void removeShortcut(Character key) {
		keyboard.removeBinding(new KeyboardShortcut(key));
	}
	
	/**
	 * Removes the camera keyboard shortcut.
	 * <p>
	 * High-level version of {@link #removeShortcut(Integer, Integer)}.
	 * 
	 * @param mask modifier mask that defining the shortcut
	 * @param key character (internally converted to a key coded) defining the shortcut
	 * 
	 * @see #removeShortcut(Integer, Integer)
	 */
	public void removeShortcut(Integer mask, Character key) {
		removeShortcut(mask, KeyboardShortcut.getVKey(key));
	}
	
	/**
	 * Removes the camera keyboard shortcut.
	 * <p>
	 * low-level version of {@link #removeShortcut(Integer, Character)}.
	 * 
	 * @param mask modifier mask that defining the shortcut
	 * @param vKey coded key defining the shortcut
	 * 
	 * @see #removeShortcut(Integer, Character)
	 */
	public void removeShortcut(Integer mask, Integer vKey) {
		keyboard.removeBinding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Removes the camera keyboard shortcut.
	 * 
	 * @param vKey coded key (such {@link remixlab.proscene.Scene.Arrow#UP}
	 *             that defines the shortcut
	 */
	public void removeShortcut(Integer vKey) {
		keyboard.removeBinding(new KeyboardShortcut(vKey));
	}

	/**
	 * Returns the action that is binded to the given camera keyboard shortcut.
	 * 
	 * @param key shortcut
	 * @return action
	 */
	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.binding(new KeyboardShortcut(key));
	}
	
  /**
   * Returns the action that is binded to the given camera keyboard shortcut.
   * <p>
   * High-level version of {@link #shortcut(Integer, Integer)}
   * 
   * @param mask modifier mask defining the shortcut
	 * @param key character (internally converted to a coded key) defining the shortcut
   * @return action
   * 
   * @see #shortcut(Integer, Integer)
   */
	public CameraKeyboardAction shortcut(Integer mask, Character key) {
		return shortcut(mask, KeyboardShortcut.getVKey(key));
	}

	/**
   * Returns the action that is binded to the given camera keyboard shortcut.
   * <p>
   * Low-level version of {@link #shortcut(Integer, Character)}
   * 
   * @param mask modifier mask defining the shortcut
	 * @param vKey coded key defining the shortcut
   * @return action
   * 
   * @see #shortcut(Integer, Character)
   */
	public CameraKeyboardAction shortcut(Integer mask, Integer vKey) {
		return keyboard.binding(new KeyboardShortcut(mask, vKey));
	}

	/**
	 * Returns the action that is binded to the given camera keyboard shortcut.
	 * 
	 * @param vKey coded key (such {@link remixlab.proscene.Scene.Arrow#UP}
	 *             that defines the shortcut
	 * @return action
	 */
	public CameraKeyboardAction shortcut(Integer vKey) {
		return keyboard.binding(new KeyboardShortcut(vKey));
	}

	/**
	 * Returns true if the given camera keyboard shortcut binds an action.
	 * 
	 * @param key shortcut
	 */
	public boolean isKeyInUse(Character key) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(key));
	}
	
	/**
	 * Returns true if the given camera keyboard shortcut binds an action.
	 * <p>
	 * High-level version of {@link #isKeyInUse(Integer, Integer)}.
   * 
   * @param mask modifier mask defining the shortcut
	 * @param key character (internally converted to a coded key) defining the shortcut
	 * 
	 * @see #isKeyInUse(Integer, Integer)
	 */
	public boolean isKeyInUse(Integer mask, Character key) {
		return isKeyInUse(mask, KeyboardShortcut.getVKey(key));
	}
	
	/**
	 * Returns true if the given camera keyboard shortcut binds an action.
	 * <p>
	 * Low-level version of {@link #isKeyInUse(Integer, Character)}.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param vKey coded key defining the shortcut
	 * 
	 * @see #isKeyInUse(Integer, Character)
	 */
	public boolean isKeyInUse(Integer mask, Integer vKey) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(mask, vKey));
	}
	
	/**
	 * Returns true if the given camera keyboard shortcut binds an action.
	 * 
	 * @param vKey coded key (such {@link remixlab.proscene.Scene.Arrow#UP}
	 *             that defines the shortcut
	 */
	public boolean isKeyInUse(Integer vKey) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(vKey));
	}

	/**
	 * Returns true if there is a camera keyboard shortcut for the given action.
	 */
	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionMapped(action);
	}

	// camera wrappers:
	
	/**
	 * Removes all camera mouse shortcuts.
	 */
	public void removeAllCameraShortcuts() {
		cameraActions.removeAllBindings();
	}

	/**
	 * Returns true if the given camera mouse shortcut binds an action.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 */
	public boolean isCameraKeyInUse(Integer mask) {
		return cameraActions.isShortcutInUse(mask);
	}

	/**
	 * Returns true if there is a camera mouse shortcut for the given action.
	 */
	public boolean isActionBindedToCamera(Scene.MouseAction action) {
		return cameraActions.isActionMapped(action);
	}

	/**
	 * Defines a camera mouse shortcut to bind the given action.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 * @param action action to be binded
	 */
	public void setCameraShortcut(Integer mask,	Scene.MouseAction action) {
		if ( isCameraKeyInUse(mask) ) {
			MouseAction a = cameraShortcut(mask);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		cameraActions.setBinding(mask, action);
	}

	/**
	 * Removes the camera mouse shortcut.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 */
	public void removeCameraShortcut(Integer mask) {
		cameraActions.removeBinding(mask);
	}
	
	/**
	 * Returns the action that is binded to the given camera mouse shortcut.
	 * 
	 * @param mask  modifier mask that defines the shortcut
	 */
	public Scene.MouseAction cameraShortcut(Integer mask) {
		return cameraActions.binding(mask);
	}

	// iFrame wrappers:
	
	/**
	 * Removes all interactive-frame mouse shortcuts.
	 */
	public void removeAllIFrameShortcuts() {
		iFrameActions.removeAllBindings();
	}

	/**
	 * Returns true if the given interactive-frame mouse shortcut binds an action.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 */
	public boolean isIFrameKeyInUse(Integer mask) {
		return iFrameActions.isShortcutInUse(mask);
	}

	/**
	 * Returns true if there is a interactive-frame mouse shortcut for the given action.
	 */
	public boolean isActionBindedToIFrame(Scene.MouseAction action) {
		return iFrameActions.isActionMapped(action);
	}
	
	/**
	 * Defines a interactive-frame mouse shortcut to bind the given action.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 * @param action action to be binded
	 */
	public void setIFrameShortcut(Integer mask, Scene.MouseAction action) {
		if ( isIFrameKeyInUse(mask) ) {
			MouseAction a = iFrameShortcut(mask);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		iFrameActions.setBinding(mask, action);
	}

	/**
	 * Removes the interactive-frame mouse shortcut.
	 * 
	 * @param mask modifier mask that defines the shortcut
	 */
	public void removeIFrameShortcut(Integer mask) {
		iFrameActions.removeBinding(mask);
	}

	/**
	 * Returns the action that is binded to the given interactive-frame mouse shortcut.
	 * 
	 * @param mask  modifier mask that defines the shortcut
	 */
	public Scene.MouseAction iFrameShortcut(Integer mask) {
		return iFrameActions.binding(mask);
	}
	
	// click wrappers:
	
	/**
	 * Removes all camera mouse-click shortcuts.
	 */
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllBindings();
	}
	
	/**
	 * Returns true if the given camera mouse-click shortcut binds an action.
	 * 	
	 * @param button shortcut
	 */
	public boolean isClickKeyInUse(Scene.Button button) {
		return clickActions.isShortcutInUse(new ClickShortcut(button));
	}
	
	/**
	 * Returns true if the given camera mouse-click shortcut binds an action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button button defining the shortcut
	 */
	public boolean isClickKeyInUse(Integer mask, Scene.Button button) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask, button));
	}
	
	/**
	 * Returns true if the given camera mouse-click shortcut binds an action.
	 * 
	 * @param button button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public boolean isClickKeyInUse(Scene.Button button, Integer nc) {
		return clickActions.isShortcutInUse(new ClickShortcut(button, nc));
	}

	/**
	 * Returns true if the given camera mouse-click shortcut binds an action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public boolean isClickKeyInUse(Integer mask, Scene.Button button, Integer nc) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask, button, nc)); 
	}

	/**
	 * Returns true if there is a camera mouse-click shortcut for the given action.
	 */
	public boolean isClickActionBinded(Scene.ClickAction action) {
		return clickActions.isActionMapped(action);
	}
	
	/**
	 * Defines a camera mouse-click shortcut to bind the given action.
	 * 
	 * @param button shortcut
	 * @param action action to be binded
	 */
	public void setClickShortcut(Scene.Button button, Scene.ClickAction action) {
		if ( isClickKeyInUse(button) ) {
			ClickAction a = clickShortcut(button);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		clickActions.setBinding(new ClickShortcut(button), action);
	}

	/**
	 * Defines a camera mouse-click shortcut to bind the given action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 * @param action action to be binded
	 */
	public void setClickShortcut(Integer mask, Scene.Button button, Scene.ClickAction action) {
		if ( isClickKeyInUse(mask, button) ) {
			ClickAction a = clickShortcut(mask, button);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		clickActions.setBinding(new ClickShortcut(mask, button), action);
	}
	
	/**
	 * Defines a camera mouse-click shortcut to bind the given action.
	 * 
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks that defines the shortcut
	 * @param action action to be binded
	 */
	public void setClickShortcut(Scene.Button button, Integer nc, Scene.ClickAction action) {
		if ( isClickKeyInUse(button, nc) ) {
			ClickAction a = clickShortcut(button, nc);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		clickActions.setBinding(new ClickShortcut(button, nc), action);
	}

	/**
	 * Defines a camera mouse-click shortcut to bind the given action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks that defines the shortcut
	 * @param action action to be binded
	 */
	public void setClickShortcut(Integer mask, Scene.Button button, Integer nc, Scene.ClickAction action) {
		if ( isClickKeyInUse(mask, button, nc) ) {
			ClickAction a = clickShortcut(mask, button, nc);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		clickActions.setBinding(new ClickShortcut(mask, button, nc), action);
	}
	
	/**
	 * Removes the camera mouse-click shortcut.
	 * 
	 * @param button shortcut
	 */
	public void removeClickShortcut(Scene.Button button) {
		clickActions.removeBinding(new ClickShortcut(button));
	}

	/**
	 * Removes the camera mouse-click shortcut.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 */
	public void removeClickShortcut(Integer mask, Scene.Button button) {
		clickActions.removeBinding(new ClickShortcut(mask, button));
	}
	
	/**
	 * Removes the camera mouse-click shortcut.
	 * 
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public void removeClickShortcut(Scene.Button button, Integer nc) {
		clickActions.removeBinding(new ClickShortcut(button, nc));
	}
	
	/**
	 * Removes the camera mouse-click shortcut.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public void removeClickShortcut(Integer mask, Scene.Button button, Integer nc) {
		clickActions.removeBinding(new ClickShortcut(mask, button, nc));
	}
	
	/**
	 * Returns the action that is binded to the given camera mouse-click shortcut.
	 * 
	 * @param button shortcut
	 */
	public Scene.ClickAction clickShortcut(Scene.Button button) {
		return clickActions.binding(new ClickShortcut(button));
	}

	/**
	 * Returns the action that is binded to the given camera mouse-click shortcut.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 */
	public Scene.ClickAction clickShortcut(Integer mask, Scene.Button button) {
		return clickActions.binding(new ClickShortcut(mask, button));
	}
	
	/**
	 * Returns the action that is binded to the given camera mouse-click shortcut.
	 * 
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public Scene.ClickAction clickShortcut(Scene.Button button, Integer nc) {
		return clickActions.binding(new ClickShortcut(button, nc));
	}

	/**
	 * Returns the action that is binded to the given camera mouse-click shortcut.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * @param button mouse button defining the shortcut
	 * @param nc number of clicks defining the shortcut
	 */
	public Scene.ClickAction clickShortcut(Integer mask, Scene.Button button, Integer nc) {
		return clickActions.binding(new ClickShortcut(mask, button, nc));
	}
	
	// wheel
	//Camera Wheel
	
	/**
	 * Removes all camera mouse-wheel shortcuts.
	 */
	public void removeAllCameraWheelShortcuts() {
		cameraWheelActions.removeAllBindings();
	}
	
	/**
	 * Returns true if the given camera mouse-wheel shortcut binds an action.
	 * 
	 * @param mask shortcut
	 */
	public boolean isCameraWheelKeyInUse(Integer mask) {
		return cameraWheelActions.isShortcutInUse(mask);
	}

	/**
	 * Returns true if there is a camera mouse-wheel shortcut for the given action.
	 * 
	 * @param action
	 */
	public boolean isWheelActionBindedToCamera(Scene.MouseAction action) {
		return cameraWheelActions.isActionMapped(action);
	}
	
	/**
	 * Convenience function that simply calls {@code setCameraWheelShortcut(0, action)}
	 * 
	 * @see #setCameraWheelShortcut(Integer, Scene.MouseAction)
	 */
	public void setCameraWheelShortcut(Scene.MouseAction action) {
		setCameraWheelShortcut(0, action);
	}

	/**
	 * Defines a camera mouse-wheel shortcut to bind the given action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * 
	 * @see #setCameraWheelShortcut(Scene.MouseAction)
	 */
	public void setCameraWheelShortcut(Integer mask, Scene.MouseAction action) {
		if ( isCameraWheelKeyInUse(mask) ) {
			MouseAction a = cameraWheelShortcut(mask);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		cameraWheelActions.setBinding(mask, action);
	}
	
	/**
	 * Convenience function that simply calls {@code removeCameraWheelShortcut(0)}.
	 * 
	 * @see #removeCameraWheelShortcut(Integer)
	 */
	public void removeCameraWheelShortcut() {
		removeCameraWheelShortcut(0);
	}

	/**
	 * Removes the camera mouse-wheel shortcut.
	 * 
	 * @param mask shortcut
	 * 
	 * @see #removeCameraWheelShortcut()
	 */
	public void removeCameraWheelShortcut(Integer mask) {
		cameraWheelActions.removeBinding(mask);
	}

	/**
	 * Convenience function that simply returns {@code cameraWheelActions.binding(0)}.
	 * 
	 * @see #cameraWheelShortcut(Integer)
	 */
	public Scene.MouseAction cameraWheelShortcut() {
		return cameraWheelActions.binding(0);
	}
	
	/**
	 * Returns the action that is binded to the given camera mouse-wheel shortcut.
	 * 
	 * @param mask shortcut
	 * 
	 * @see #cameraWheelShortcut()
	 */
	public Scene.MouseAction cameraWheelShortcut(Integer mask) {
		return cameraWheelActions.binding(mask);
	}
	
  //Frame Wheel
	
	/**
	 * Removes all interactive-frame mouse-wheel shortcuts.
	 */
	public void removeAllIFrameWheelShortcuts() {
		iFrameWheelActions.removeAllBindings();
	}

	/**
	 * Returns true if the given interactive-frame mouse-wheel shortcut binds an action.
	 * 
	 * @param mask shortcut
	 */
	public boolean isIFrameWheelKeyInUse(Integer mask) {
		return iFrameWheelActions.isShortcutInUse(mask);
	}

	/**
	 * Returns true if there is an interactive-frame mouse-wheel shortcut for the given action.
	 * 
	 * @param action
	 */
	public boolean isWheelActionBindedToIFrame(Scene.MouseAction action) {
		return iFrameWheelActions.isActionMapped(action);
	}

	/**
	 * Convenience function that simply calls {@code setFrameWheelShortcut(0, action)}
	 * 
	 * @see #setCameraWheelShortcut(Integer, Scene.MouseAction)
	 */
	public void setIFrameWheelShortcut(Scene.MouseAction action) {
		setIFrameWheelShortcut(0, action);
	}
	
	/**
	 * Defines an interactive-frame mouse-wheel shortcut to bind the given action.
	 * 
	 * @param mask modifier mask defining the shortcut
	 * 
	 * @see #setIFrameWheelShortcut(Scene.MouseAction)
	 */
	public void setIFrameWheelShortcut(Integer mask, Scene.MouseAction action) {
		if ( isIFrameWheelKeyInUse(mask) ) {
			MouseAction a = iFrameWheelShortcut(mask);
			System.out.println("Warning: overwritting shortcut which was previously binded to " + a);
		}
		iFrameWheelActions.setBinding(mask, action);
	}
	
	/**
	 * Convenience function that simply calls {@code removeFrameWheelShortcut(0)}.
	 * 
	 * @see #removeIFrameWheelShortcut(Integer)
	 */
	public void removeIFrameWheelShortcut() {
		removeIFrameWheelShortcut(0);
	}

	/**
	 * Removes the interactive-frame mouse-wheel shortcut.
	 * 
	 * @param mask shortcut
	 * 
	 * @see #removeIFrameWheelShortcut()
	 */
	public void removeIFrameWheelShortcut(Integer mask) {
		iFrameWheelActions.removeBinding(mask);
	}
	
	/**
	 * Convenience function that simply returns {@code cameraFrameActions.binding(0)}.
	 * 
	 * @see #iFrameWheelShortcut(Integer)
	 */
	public Scene.MouseAction iFrameWheelShortcut() {
		return iFrameWheelShortcut(0);
	}
	
	/**
	 * Returns the action that is binded to the given interactive-frame mouse-wheel shortcut.
	 * 
	 * @param mask shortcut
	 * 
	 * @see #iFrameWheelShortcut()
	 */
	public Scene.MouseAction iFrameWheelShortcut(Integer mask) {
		return iFrameWheelActions.binding(mask);
	}
}