/**
 *                     ProScene (version 1.0.0-alpha1)      
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
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.ClickAction;
import remixlab.proscene.Scene.MouseAction;

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
	protected Bindings<Integer, Scene.MouseAction> frameWheelActions;
	
	public CameraProfile(Scene scn, String n) {
		this(scn, n, Mode.CUSTOM);
	}

	public CameraProfile(Scene scn, String n, Mode m) {
		scene = scn;		
		name = n;
		mode = m;
		keyboard = new Bindings<KeyboardShortcut, Scene.CameraKeyboardAction>(scene);
		cameraActions = new Bindings<Integer, Scene.MouseAction>(scene);
		iFrameActions = new Bindings<Integer, Scene.MouseAction>(scene);		
		clickActions = new Bindings<ClickShortcut, Scene.ClickAction>(scene);
		
		/**
		scene.parent.addMouseWheelListener( scene.dE );
		cameraWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		frameWheelActions = new ShortcutMappings<Shortcut<Integer>, Scene.MouseAction>(scene);
		*/
		
		switch (mode) {
		case ARCBALL:
			arcballDefaultShortcuts();
			break;
		case WHEELED_ARCBALL:
			cameraWheelActions = new Bindings<Integer, Scene.MouseAction>(scene);
			frameWheelActions = new Bindings<Integer, Scene.MouseAction>(scene);
			
			arcballDefaultShortcuts();
			
			setCameraWheelShortcut( MouseAction.ZOOM );			
			setCameraWheelShortcut( InputEvent.CTRL_DOWN_MASK, MouseAction.MOVE_FORWARD );
			setCameraWheelShortcut( InputEvent.ALT_DOWN_MASK, MouseAction.MOVE_BACKWARD );
			//should work only iFrame is an instance of drivable
			setFrameWheelShortcut( MouseAction.ZOOM );
			setFrameWheelShortcut( InputEvent.CTRL_DOWN_MASK, MouseAction.MOVE_FORWARD );
			setFrameWheelShortcut( InputEvent.ALT_DOWN_MASK, MouseAction.MOVE_BACKWARD );
			
			scene.parent.addMouseWheelListener( scene.dE );
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
	
	public Mode mode() {
		return mode;
	}
	
	protected MouseAction iFrameMouseAction(MouseEvent e) {
		MouseAction iFrameMouseAction = iFrameShortcut( e.getModifiersEx() );
		if (iFrameMouseAction == null)
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		return iFrameMouseAction;
	}
	
	protected MouseAction cameraMouseAction(MouseEvent e) {
		MouseAction camMouseAction = cameraShortcut( e.getModifiersEx() );
		if (camMouseAction == null)
			camMouseAction = MouseAction.NO_MOUSE_ACTION;
		return camMouseAction;
	}

	public String name() {
		return name;
	}

	public boolean isRegistered() {
		return scene.isCameraProfileRegistered(this);
	}

	public boolean register() {
		return scene.registerCameraProfile(this);
	}

	public void unregister() {
		scene.unregisterCameraProfile(this);
	}

	public Bindings<KeyboardShortcut, Scene.CameraKeyboardAction> keyBindings() {
		return keyboard;
	}

	// keyboard wrappers:
	public void setShortcut(Character key, CameraKeyboardAction action) {
		keyboard.setBinding(new KeyboardShortcut(key), action);
	}
	
	//high level call
	public void setShortcut(Integer mask, Character key, CameraKeyboardAction action) {
		setShortcut(mask, MathUtils.getVKey(key), action);
	}
	
	//low level call
	public void setShortcut(Integer mask, Integer vKey, CameraKeyboardAction action) {
		keyboard.setBinding(new KeyboardShortcut(mask, vKey), action);
	}
	
	public void setShortcut(Integer vKey, CameraKeyboardAction action) {
		keyboard.setBinding(new KeyboardShortcut(vKey), action);
	}

	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllBindings();
	}
	
	public void removeShortcut(Character key) {
		keyboard.removeBinding(new KeyboardShortcut(key));
	}
	
	//high level call
	public void removeShortcut(Integer mask, Character key) {
		removeShortcut(mask, MathUtils.getVKey(key));
	}
	
	//low level call
	public void removeShortcut(Integer mask, Integer vKey) {
		keyboard.removeBinding(new KeyboardShortcut(mask, vKey));
	}

	public void removeShortcut(Integer vKey) {
		keyboard.removeBinding(new KeyboardShortcut(vKey));
	}

	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.binding(new KeyboardShortcut(key));
	}
	
  //high level call
	public CameraKeyboardAction shortcut(Integer mask, Character key) {
		return shortcut(mask, MathUtils.getVKey(key));
	}

	//low level call
	public CameraKeyboardAction shortcut(Integer mask, Integer vKey) {
		return keyboard.binding(new KeyboardShortcut(mask, vKey));
	}

	public CameraKeyboardAction shortcut(Integer vKey) {
		return keyboard.binding(new KeyboardShortcut(vKey));
	}

	public boolean isKeyInUse(Character key) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(key));
	}
	
  //high level call
	public boolean isKeyInUse(Integer mask, Character key) {
		return isKeyInUse(mask, MathUtils.getVKey(key));
	}
	
  //low level call
	public boolean isKeyInUse(Integer mask, Integer vKey) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(mask, vKey));
	}
	
	public boolean isKeyInUse(Integer vKey) {
		return keyboard.isShortcutInUse(new KeyboardShortcut(vKey));
	}

	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionMapped(action);
	}

	// camera wrappers:
	public void removeAllCameraShortcuts() {
		cameraActions.removeAllBindings();
	}

	public boolean isCameraKeyInUse(Integer mask) {
		return cameraActions.isShortcutInUse(mask);
	}

	public boolean isActionBindedToCamera(Scene.MouseAction action) {
		return cameraActions.isActionMapped(action);
	}

	public void setCameraShortcut(Integer mask,	Scene.MouseAction action) {
		cameraActions.setBinding(mask, action);
	}

	public void removeCameraShortcut(Integer mask) {
		cameraActions.removeBinding(mask);
	}

	public Scene.MouseAction cameraShortcut(Integer mask) {
		return cameraActions.binding(mask);
	}

	// iFrame wrappers:
	public void removeAllIFrameShortcuts() {
		iFrameActions.removeAllBindings();
	}

	public boolean isIFrameKeyInUse(Integer mask) {
		return iFrameActions.isShortcutInUse(mask);
	}

	public boolean isActionBindedToIFrame(Scene.MouseAction action) {
		return iFrameActions.isActionMapped(action);
	}
	
	public void setIFrameShortcut(Integer mask, Scene.MouseAction action) {
		iFrameActions.setBinding(mask, action);
	}

	public void removeIFrameShortcut(Integer mask) {
		iFrameActions.removeBinding(mask);
	}

	public Scene.MouseAction iFrameShortcut(Integer mask) {
		return iFrameActions.binding(mask);
	}
	
	// click wrappers:
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllBindings();
	}
	
	public boolean isClickKeyInUse(Scene.Button button) {
		return clickActions.isShortcutInUse(new ClickShortcut(button));
	}
	
	public boolean isClickKeyInUse(Integer mask, Scene.Button button) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask, button));
	}
	
	public boolean isClickKeyInUse(Scene.Button button, Integer nc) {
		return clickActions.isShortcutInUse(new ClickShortcut(button, nc));
	}

	public boolean isClickKeyInUse(Integer mask, Scene.Button button, Integer nc) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask, button, nc)); 
	}

	public boolean isClickActionBinded(Scene.ClickAction action) {
		return clickActions.isActionMapped(action);
	}
	
	public void setClickShortcut(Scene.Button button, Scene.ClickAction action) {
		clickActions.setBinding(new ClickShortcut(button), action);
	}

	public void setClickShortcut(Integer mask, Scene.Button button, Scene.ClickAction action) {
		clickActions.setBinding(new ClickShortcut(mask, button), action);
	}
	
	public void setClickShortcut(Scene.Button button, Integer nc, Scene.ClickAction action) {
		clickActions.setBinding(new ClickShortcut(button, nc), action);
	}

	public void setClickShortcut(Integer mask, Scene.Button button, Integer nc, Scene.ClickAction action) {
		clickActions.setBinding(new ClickShortcut(mask, button, nc), action);
	}
	
	public void removeClickShortcut(Scene.Button button) {
		clickActions.removeBinding(new ClickShortcut(button));
	}

	public void removeClickShortcut(Integer mask, Scene.Button button) {
		clickActions.removeBinding(new ClickShortcut(mask, button));
	}
	
	public void removeClickShortcut(Scene.Button button, Integer nc) {
		clickActions.removeBinding(new ClickShortcut(button, nc));
	}
		
	public void removeClickShortcut(Integer mask, Scene.Button button, Integer nc) {
		clickActions.removeBinding(new ClickShortcut(mask, button, nc));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button) {
		return clickActions.binding(new ClickShortcut(button));
	}

	public Scene.ClickAction clickShortcut(Integer mask, Scene.Button button) {
		return clickActions.binding(new ClickShortcut(mask, button));
	}
	
	public Scene.ClickAction clickShortcut(Scene.Button button, Integer nc) {
		return clickActions.binding(new ClickShortcut(button, nc));
	}

	public Scene.ClickAction clickShortcut(Integer mask, Scene.Button button, Integer nc) {
		return clickActions.binding(new ClickShortcut(mask, button, nc));
	}
	
	/**
	public boolean isClickKeyInUse(Integer mask) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask));
	}

	public boolean isClickKeyInUse(Integer mask, Integer nc) {
		return clickActions.isShortcutInUse(new ClickShortcut(mask, nc));
	}

	public boolean isClickActionBinded(Scene.ClickAction action) {
		return clickActions.isActionMapped(action);
	}

	public void setClickShortcut(Integer mask, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(mask), action);
	}

	public void setClickShortcut(Integer mask, Integer nc, Scene.ClickAction action) {
		clickActions.setMapping(new ClickShortcut(mask, nc), action);
	}

	public void removeClickShortcut(Integer mask) {
		clickActions.removeMapping(new ClickShortcut(mask));
	}
		
	public void removeClickShortcut(Integer mask, Integer nc) {
		clickActions.removeMapping(new ClickShortcut(mask, nc));
	}

	public Scene.ClickAction clickShortcut(Integer mask) {
		return clickActions.mapping(new ClickShortcut(mask));
	}

	public Scene.ClickAction clickShortcut(Integer mask, Integer nc) {
		return clickActions.mapping(new ClickShortcut(mask, nc));
	}
	*/
	
	// wheel
	//Camera Wheel
	public void removeAllCameraWheelShortcuts() {
		cameraWheelActions.removeAllBindings();
	}

	public boolean isCameraWheelKeyInUse(Integer mask) {
		return cameraWheelActions.isShortcutInUse(mask);
	}

	public boolean isWheelActionBindedToCamera(Scene.MouseAction action) {
		return cameraWheelActions.isActionMapped(action);
	}
	
	public void setCameraWheelShortcut(Scene.MouseAction action) {
		setCameraWheelShortcut(0, action);
	}

	public void setCameraWheelShortcut(Integer mask, Scene.MouseAction action) {
		cameraWheelActions.setBinding(mask, action);
	}
	
	public void removeCameraWheelShortcut() {
		removeCameraWheelShortcut(0);
	}

	public void removeCameraWheelShortcut(Integer mask) {
		cameraWheelActions.removeBinding(mask);
	}

	public Scene.MouseAction cameraWheelShortcut() {
		return cameraWheelActions.binding(0);
	}
	
	public Scene.MouseAction cameraWheelShortcut(Integer mask) {
		return cameraWheelActions.binding(mask);
	}
	
	protected MouseAction cameraWheelMouseAction(MouseWheelEvent e) {
		MouseAction wMouseAction = cameraWheelShortcut(e.getModifiersEx());
		if (wMouseAction == null)
			wMouseAction = MouseAction.NO_MOUSE_ACTION;
		return wMouseAction;
	}
	
  //Frame Wheel
	public void removeAllFrameWheelShortcuts() {
		frameWheelActions.removeAllBindings();
	}

	public boolean isFrameWheelKeyInUse(Integer mask) {
		return frameWheelActions.isShortcutInUse(mask);
	}

	public boolean isWheelActionBindedToFrame(Scene.MouseAction action) {
		return frameWheelActions.isActionMapped(action);
	}

	public void setFrameWheelShortcut(Scene.MouseAction action) {
		setFrameWheelShortcut(0, action);
	}
	
	public void setFrameWheelShortcut(Integer mask, Scene.MouseAction action) {
		frameWheelActions.setBinding(mask, action);
	}
	
	public void removeFrameWheelShortcut() {
		removeFrameWheelShortcut(0);
	}

	public void removeFrameWheelShortcut(Integer mask) {
		frameWheelActions.removeBinding(mask);
	}
	
	public Scene.MouseAction frameWheelShortcut() {
		return frameWheelShortcut(0);
	}
	
	public Scene.MouseAction frameWheelShortcut(Integer mask) {
		return frameWheelActions.binding(mask);
	}
	
	protected MouseAction frameWheelMouseAction(MouseWheelEvent e) {
		MouseAction fMouseAction = frameWheelShortcut( e.getModifiersEx() );
		if (fMouseAction == null)
			fMouseAction = MouseAction.NO_MOUSE_ACTION;
		return fMouseAction;
	}
}
