package remixlab.proscene;

import remixlab.proscene.Scene.CameraKeyboardAction;

public abstract class CameraProfile {
	protected String name;
	protected Scene scene;
	protected KeyBindings<Scene.CameraKeyboardAction> keyboard;
	protected MouseBindings<Scene.ClickActionButton, Scene.ClickAction> clickActions;
	protected MouseBindings<Scene.MouseActionButton, Scene.MouseAction> mouseActions;
	
	public CameraProfile(Scene scn, String n) {
		scene = scn;
		name = n;
		keyboard = new KeyBindings<Scene.CameraKeyboardAction>(scene);
		clickActions = new MouseBindings<Scene.ClickActionButton, Scene.ClickAction>(scene);
		mouseActions = new MouseBindings<Scene.MouseActionButton, Scene.MouseAction>(scene);
	}
	
	public String name() {
		return name;
	}
	
	public boolean isRegistered() {
		return scene.isCameraProfileRegistered(this);
	}
	
	public boolean register() {
		return scene.registerCameraProfile(this, true);
	}
	
	public void unregister() {
		scene.unregisterCameraProfile(this);
	}
	
	public KeyBindings<Scene.CameraKeyboardAction> keyBindings() {
		return keyboard;
	}
	
	//keyboard wrappers:
	public void setShortcut(Integer vKey, Scene.Modifier modifier, CameraKeyboardAction action) {
		keyboard.setShortcut(vKey, modifier, action);
	}
	
	public void setShortcut(Scene.Arrow arrow, CameraKeyboardAction action) {
		keyboard.setShortcut(arrow, action);
	}
	
	public void setShortcut(Character key, CameraKeyboardAction action) {
		keyboard.setShortcut(key, action);
	}
	
	public void removeAllKeyboardShortcuts() {
		keyboard.removeAllBindings();
	}
	
	public void removeShortcut(Integer vKey, Scene.Modifier modifier) {
		keyboard.removeShortcut(vKey, modifier);
	}	
	
	public void removeShortcut(Scene.Arrow arrow) {
		keyboard.removeShortcut(arrow);
	}
	
	public void removeShortcut(Character key) {
		keyboard.removeShortcut(key);
	}
	
	public CameraKeyboardAction shortcut(Character key) {
		return keyboard.shortcut(key);
	}
	
	public CameraKeyboardAction shortcut(Integer vKey, Scene.Modifier modifier) {
		return keyboard.shortcut(vKey, modifier);
	}
	
	public CameraKeyboardAction shortcut(Scene.Arrow arrow) {
		return keyboard.shortcut(arrow);
	}
	
	public boolean isKeyInUse(KeyBindings<CameraKeyboardAction>.KeyboardShortcut key) {
		return keyboard.isKeyInUse(key);
	}
	
	public boolean isActionBinded(CameraKeyboardAction action) {
		return keyboard.isActionBinded(action);
	}
	
	//click wrappers:
	public void removeAllClickActionShortcuts() {
		clickActions.removeAllBindings();
	}
	
	public boolean isClickKeyInUse(MouseBindings<Scene.ClickActionButton, Scene.ClickAction>.ButtonKeyCombination key) {
		return clickActions.isKeyInUse(key);
	}
	
	public boolean isActionBinded(Scene.ClickAction action) {
		return clickActions.isActionBinded(action);
	}
	
	public void setShortcut(Scene.ClickActionButton button, Scene.Modifier modifier, Scene.ClickAction action) {
		clickActions.setShortcut(button, modifier, action);
	}
	
	public void setShortcut(Scene.ClickActionButton button, Scene.ClickAction action) {
		clickActions.setShortcut(button, action);
	}
	
	public void removeShortcut(Scene.ClickActionButton button, Scene.Modifier modifier) {
		clickActions.removeShortcut(button, modifier);
	}
	
	public void removeShortcut(Scene.ClickActionButton button) {
		clickActions.removeShortcut(button);
	}
	
	public Scene.ClickAction shortcut(Scene.ClickActionButton button, Scene.Modifier modifier) {
		return clickActions.shortcut(button, modifier);
	}
	
	public Scene.ClickAction shortcut(Scene.ClickActionButton button) {
		return clickActions.shortcut(button);
	}
	
	//mouse wrappers:
	// /**
	public void removeAllMouseActionShortcuts() {
		mouseActions.removeAllBindings();
	}
	
	public boolean isMouseKeyInUse(MouseBindings<Scene.MouseActionButton, Scene.MouseAction>.ButtonKeyCombination key) {
		return mouseActions.isKeyInUse(key);
	}
	
	public boolean isActionBinded(Scene.MouseAction action) {
		return mouseActions.isActionBinded(action);
	}
	
	public void setShortcut(Scene.MouseActionButton button, Scene.Modifier modifier, Scene.MouseAction action) {
		mouseActions.setShortcut(button, modifier, action);
	}
	
	public void setShortcut(Scene.MouseActionButton button, Scene.MouseAction action) {
		mouseActions.setShortcut(button, action);
	}
	
	public void removeShortcut(Scene.MouseActionButton button, Scene.Modifier modifier) {
		mouseActions.removeShortcut(button, modifier);
	}
	
	public void removeShortcut(Scene.MouseActionButton button) {
		mouseActions.removeShortcut(button);
	}
	
	public Scene.MouseAction shortcut(Scene.MouseActionButton button, Scene.Modifier modifier) {
		return mouseActions.shortcut(button, modifier);
	}
	
	public Scene.MouseAction shortcut(Scene.MouseActionButton button) {
		return mouseActions.shortcut(button);
	}
}
