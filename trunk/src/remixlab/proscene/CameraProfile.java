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

import java.awt.event.MouseEvent;

import remixlab.proscene.Scene.Button;
import remixlab.proscene.Scene.CameraKeyboardAction;
import remixlab.proscene.Scene.Modifier;
import remixlab.proscene.Scene.MouseAction;

public class CameraProfile {
	public enum Mode {ARCBALL, FIRST_PERSON, THIRD_PERSON, CUSTOM}	
	protected String name;
	protected Scene scene;
	protected Mode mode;
	protected ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction> keyboard;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> cameraActions;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> iFrameActions;
	
	public CameraProfile(Scene scn, String n) {
		this(scn, n, Mode.CUSTOM);
	}

	public CameraProfile(Scene scn, String n, Mode m) {
		scene = scn;		
		name = n;
		keyboard = new ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction>(
				scene);
		cameraActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(
				scene);
		iFrameActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(
				scene);
		mode = m;
		
		switch (mode) {
		case ARCBALL:
			setShortcut(Scene.Arrow.RIGHT, Scene.CameraKeyboardAction.MOVE_CAMERA_RIGHT);
			setShortcut(Scene.Arrow.LEFT, Scene.CameraKeyboardAction.MOVE_CAMERA_LEFT);
			setShortcut(Scene.Arrow.UP, Scene.CameraKeyboardAction.MOVE_CAMERA_UP);
			setShortcut(Scene.Arrow.DOWN, Scene.CameraKeyboardAction.MOVE_CAMERA_DOWN);

			setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
			setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
			setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);
			setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
			setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);

			setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.CONTROL,
					Scene.MouseAction.ZOOM_ON_REGION);
			setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
					Scene.MouseAction.SCREEN_ROTATE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_ROTATION_SENSITIVITY);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_ROTATION_SENSITIVITY);

			setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
			setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
			break;
		case FIRST_PERSON:
			// setShortcut('a', CameraKeyboardAction.SHOW_ALL);
			setCameraShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD);
			setCameraShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND);
			setCameraShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD);
			setCameraShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
					Scene.MouseAction.ROLL);
			setCameraShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT,
					Scene.MouseAction.DRIVE);
			setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.ROTATE);
			setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.ZOOM);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.TRANSLATE);

			setShortcut('+', Scene.CameraKeyboardAction.INCREASE_CAMERA_FLY_SPEED);
			setShortcut('-', Scene.CameraKeyboardAction.DECREASE_CAMERA_FLY_SPEED);

			setShortcut('s', Scene.CameraKeyboardAction.INTERPOLATE_TO_FIT_SCENE);
			setShortcut('S', Scene.CameraKeyboardAction.SHOW_ALL);
			break;
		case THIRD_PERSON:
			setIFrameShortcut(Scene.Button.LEFT, Scene.MouseAction.MOVE_FORWARD);
			setIFrameShortcut(Scene.Button.MIDDLE, Scene.MouseAction.LOOK_AROUND);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.MouseAction.MOVE_BACKWARD);
			setIFrameShortcut(Scene.Button.LEFT, Scene.Modifier.SHIFT,
					Scene.MouseAction.ROLL);
			setIFrameShortcut(Scene.Button.RIGHT, Scene.Modifier.SHIFT,
					Scene.MouseAction.DRIVE);

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
	
	protected MouseAction iFrameMouseAction(MouseEvent e) {
		MouseAction iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
		Button button = scene.getButton(e);

		if (button == null) {
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;
			return iFrameMouseAction;
		}

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.ALT);
			if (e.isAltGraphDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.ALT_GRAPH);
			if (e.isControlDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.CONTROL);
			if (e.isShiftDown())
				iFrameMouseAction = iFrameShortcut(button, Modifier.SHIFT);
			if (iFrameMouseAction != null)
				return iFrameMouseAction;
		}

		iFrameMouseAction = iFrameShortcut(button);

		if (iFrameMouseAction == null)
			iFrameMouseAction = MouseAction.NO_MOUSE_ACTION;

		return iFrameMouseAction;
	}
	
	public Mode mode() {
		return mode;
	}
	
	protected MouseAction cameraMouseAction(MouseEvent e) {
		MouseAction camMouseAction = MouseAction.NO_MOUSE_ACTION;
		Button button = scene.getButton(e);

		if (button == null) {
			camMouseAction = MouseAction.NO_MOUSE_ACTION;
			return camMouseAction;
		}

		if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown()
				|| e.isShiftDown()) {
			if (e.isAltDown())
				camMouseAction = cameraShortcut(button, Modifier.ALT);
			if (e.isAltGraphDown())
				camMouseAction = cameraShortcut(button, Modifier.ALT_GRAPH);
			if (e.isControlDown())
				camMouseAction = cameraShortcut(button, Modifier.CONTROL);
			if (e.isShiftDown())
				camMouseAction = cameraShortcut(button, Modifier.SHIFT);
			if (camMouseAction != null)
				return camMouseAction;
		}

		camMouseAction = cameraShortcut(button);

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

	public ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction> keyBindings() {
		return keyboard;
	}

	// keyboard wrappers:
	public void setShortcut(Integer vKey, Scene.Modifier modifier,
			CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(vKey, modifier), action);
	}

	public void setShortcut(Scene.Arrow arrow, CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(arrow), action);
	}

	public void setShortcut(Character key, CameraKeyboardAction action) {
		keyboard.setMapping(new KeyboardShortcut(key), action);
	}

	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllMappings();
	}

	public void removeShortcut(Integer vKey, Scene.Modifier modifier) {
		keyboard.removeMapping(new KeyboardShortcut(vKey, modifier));
	}

	public void removeShortcut(Scene.Arrow arrow) {
		keyboard.removeMapping(new KeyboardShortcut(arrow));
	}

	public void removeShortcut(Character key) {
		keyboard.removeMapping(new KeyboardShortcut(key));
	}

	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.mapping(new KeyboardShortcut(key));
	}

	public CameraKeyboardAction shortcut(Integer vKey, Scene.Modifier modifier) {
		return keyboard.mapping(new KeyboardShortcut(vKey, modifier));
	}

	public CameraKeyboardAction shortcut(Scene.Arrow arrow) {
		return keyboard.mapping(new KeyboardShortcut(arrow));
	}

	public boolean isKeyInUse(KeyboardShortcut key) {
		return keyboard.isShortcutInUse(key);
	}

	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionMapped(action);
	}

	// camera wrappers:
	public void removeAllCameraShortcuts() {
		cameraActions.removeAllMappings();
	}

	public boolean isCameraKeyInUse(Shortcut<Scene.Button> key) {
		return cameraActions.isShortcutInUse(key);
	}

	public boolean isActionBindedToCamera(Scene.MouseAction action) {
		return cameraActions.isActionMapped(action);
	}

	public void setCameraShortcut(Scene.Button button, Scene.Modifier modifier,
			Scene.MouseAction action) {
		cameraActions.setMapping(new Shortcut<Scene.Button>(button, modifier),
				action);
	}

	public void setCameraShortcut(Scene.Button button, Scene.MouseAction action) {
		cameraActions.setMapping(new Shortcut<Scene.Button>(button), action);
	}

	public void removeCameraShortcut(Scene.Button button, Scene.Modifier modifier) {
		cameraActions.removeMapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public void removeCameraShortcut(Scene.Button button) {
		cameraActions.removeMapping(new Shortcut<Scene.Button>(button));
	}

	public Scene.MouseAction cameraShortcut(Scene.Button button,
			Scene.Modifier modifier) {
		return cameraActions.mapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public Scene.MouseAction cameraShortcut(Scene.Button button) {
		return cameraActions.mapping(new Shortcut<Scene.Button>(button));
	}

	// iFrame wrappers:
	public void removeAllIFrameShortcuts() {
		iFrameActions.removeAllMappings();
	}

	public boolean isIFrameKeyInUse(Shortcut<Scene.Button> key) {
		return iFrameActions.isShortcutInUse(key);
	}

	public boolean isActionBindedToIFrame(Scene.MouseAction action) {
		return iFrameActions.isActionMapped(action);
	}

	public void setIFrameShortcut(Scene.Button button, Scene.Modifier modifier,
			Scene.MouseAction action) {
		iFrameActions.setMapping(new Shortcut<Scene.Button>(button, modifier),
				action);
	}

	public void setIFrameShortcut(Scene.Button button, Scene.MouseAction action) {
		iFrameActions.setMapping(new Shortcut<Scene.Button>(button), action);
	}

	public void removeIFrameShortcut(Scene.Button button, Scene.Modifier modifier) {
		iFrameActions.removeMapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public void removeIFrameShortcut(Scene.Button button) {
		iFrameActions.removeMapping(new Shortcut<Scene.Button>(button));
	}

	public Scene.MouseAction iFrameShortcut(Scene.Button button,
			Scene.Modifier modifier) {
		return iFrameActions.mapping(new Shortcut<Scene.Button>(button, modifier));
	}

	public Scene.MouseAction iFrameShortcut(Scene.Button button) {
		return iFrameActions.mapping(new Shortcut<Scene.Button>(button));
	}
}
