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

import remixlab.proscene.Scene.CameraKeyboardAction;

public class CameraProfile {
	protected String name;
	protected Scene scene;
	protected ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction> keyboard;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> cameraActions;
	protected ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction> iFrameActions;

	public CameraProfile(Scene scn, String n) {
		scene = scn;
		name = n;
		keyboard = new ShortcutMappings<KeyboardShortcut, Scene.CameraKeyboardAction>(
				scene);
		cameraActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(
				scene);
		iFrameActions = new ShortcutMappings<Shortcut<Scene.Button>, Scene.MouseAction>(
				scene);
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
